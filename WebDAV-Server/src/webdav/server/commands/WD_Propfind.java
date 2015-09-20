package webdav.server.commands;

import http.FileSystemPath;
import http.server.HTTPCommand;
import http.server.exceptions.UnexpectedException;
import http.server.message.HTTPResponse;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import webdav.server.tools.Helper;
import webdav.server.resource.IResource;
import webdav.server.tools.NodeListWrap;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import webdav.server.resource.ResourceType;

public class WD_Propfind extends HTTPCommand
{
    public WD_Propfind()
    {
        super("propfind");
    }
    
    private Element getInfo(IResource resource, FileSystemPath path, String host, HTTPEnvRequest environment, Document doc) throws UserRequiredException, UnsupportedEncodingException
    {
        if(!resource.isVisible(environment))
            return null;
        
        String displayName = resource.getWebName(environment);
        Instant creationTime = resource.getCreationTime(environment);

        if(displayName == null || creationTime == null)
            return null;

        Element response = doc.createElementNS("DAV:", "response");

        Element href = doc.createElementNS("DAV:", "href");
        href.setTextContent(getHostPath(path.toString(), host));
        response.appendChild(href);

        Element propstat = doc.createElementNS("DAV:", "propstat");
        response.appendChild(propstat);

        Element status = doc.createElementNS("DAV:", "status");
        status.setTextContent("HTTP/1.1 200 OK");
        propstat.appendChild(status);

        Element prop = doc.createElementNS("DAV:", "prop");
        propstat.appendChild(prop);

        Element creationdate = doc.createElementNS("DAV:", "creationdate");
        creationdate.setTextContent(FileTime.from(creationTime).toString().substring(0, "0000-00-00T00:00:00".length()) + "-00:00");
        prop.appendChild(creationdate);

        Element displayname = doc.createElementNS("DAV:", "displayname");
        displayname.setTextContent(URLDecoder.decode(displayName, "UTF-8"));
        prop.appendChild(displayname);

        Element supportedlock = doc.createElementNS("DAV:", "supportedlock");
        prop.appendChild(supportedlock);

        resource.getAvailableLocks()
                .stream()
                .forEach(lk ->
                {
                    Element value;

                    Element lockentry = doc.createElementNS("DAV:", "lockentry");
                    supportedlock.appendChild(lockentry);

                    Element lockscope = doc.createElementNS("DAV:", "lockscope");
                    value = doc.createElementNS("DAV:", lk.getScope().name().toLowerCase());
                    lockscope.appendChild(value);
                    lockentry.appendChild(lockscope);

                    Element locktype = doc.createElementNS("DAV:", "locktype");
                    value = doc.createElementNS("DAV:", lk.getType().name().toLowerCase());
                    locktype.appendChild(value);
                    lockentry.appendChild(locktype);
                });
        
        resource.getProperties(environment)
                .entrySet()
                .forEach(p ->
                {
                    Element property = doc.createElementNS(p.getKey().getKey(), p.getKey().getValue());
                    property.setTextContent(p.getValue());
                    prop.appendChild(property);
                });

        switch(resource.getResourceType(environment))
        {
            case File:
                if(!getInfoFile(resource, host, environment, doc, prop))
                    return null;
                break;

            case Directory:
                if(!getInfoFolder(resource, host, environment, doc, prop))
                    return null;
                break;
        }
        
        return response;
    }
    private boolean getInfoFolder(IResource resource, String host, HTTPEnvRequest environment, Document doc, Element parent) throws UserRequiredException
    {
        Element resourcetype = doc.createElementNS("DAV:", "resourcetype");
        parent.appendChild(resourcetype);
        
        Element collection = doc.createElementNS("DAV:", "collection");
        resourcetype.appendChild(collection);
        
        return true;
    }
    private boolean getInfoFile(IResource resource, String host, HTTPEnvRequest environment, Document doc, Element parent) throws UserRequiredException
    {
        Instant lastModified = resource.getLastModified(environment);
        String mimeType = resource.getMimeType(environment);
        if(mimeType == null)
            mimeType = "text/binary";
        
        if(lastModified == null)
            return false;
        
        
        Element resourcetype = doc.createElementNS("DAV:", "resourcetype");
        parent.appendChild(resourcetype);
        
        Element getcontentlength = doc.createElementNS("DAV:", "getcontentlength");
        getcontentlength.setTextContent(String.valueOf(resource.getSize(environment)));
        parent.appendChild(getcontentlength);
        
        Element getcontenttype = doc.createElementNS("DAV:", "getcontenttype");
        getcontenttype.setTextContent(mimeType);
        parent.appendChild(getcontenttype);
        
        Element getetag = doc.createElementNS("DAV:", "getetag");
        getetag.setTextContent("zzyzx");
        parent.appendChild(getetag);
        
        Element getlastmodified = doc.createElementNS("DAV:", "getlastmodified");
        getlastmodified.setTextContent(Helper.toString(Date.from(lastModified)));
        parent.appendChild(getlastmodified);
        
        return true;
    }

    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws UserRequiredException, NotFoundException 
    {
        FileSystemPath path = environment.getPath();
        IResource f = getResource(path, environment);
        
        String host = environment.getRequest().getHeader("host");
        
        try
        {
            Document doc = createDocument();
        
            Element multistatus = doc.createElementNS("DAV:", "multistatus");
            doc.appendChild(multistatus);
            
            Element current = getInfo(f, path, host, environment, doc);
            if(current == null)
                throw new NotFoundException();
            multistatus.appendChild(current);
            
            if(environment.getRequest().getHeader("depth").trim().equals("0"))
            { // depth = 0
            }
            else
            { // depth = 1
                for(IResource subFile : f.listResources(environment))
                {
                    try
                    {
                        Element child = getInfo(subFile, path.createChild(subFile.getWebName(environment)), host, environment, doc);
                        if(child != null)
                            multistatus.appendChild(child);
                    }
                    catch(NotFoundException ex)
                    { }
                }
            }
        
            return HTTPResponse.create()
                    .setCode(207)
                    .setMessage("Multi-Status")
                    .setHeader("Content-Type", "text/xml; charset=\"utf-8\"")
                    .setContent(NodeListWrap.getBytes(doc));
        }
        catch (ParserConfigurationException | UnsupportedEncodingException ex)
        {
            throw new UnexpectedException(ex);
        }
    }

    
}
