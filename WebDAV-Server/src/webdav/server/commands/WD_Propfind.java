package webdav.server.commands;

import http.server.authentication.HTTPUser;
import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Stream;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import webdav.server.Helper;
import webdav.server.IResource;
import webdav.server.NodeListWrap;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import webdav.server.virtual.entity.VEntity;

public class WD_Propfind extends HTTPCommand
{
    public WD_Propfind()
    {
        super("propfind");
    }
    
    private Element getInfo(IResource f, String host, HTTPEnvironment environment, Document doc) throws UserRequiredException
    {
        HTTPUser user = environment.getUser();
        
        if(!f.isVisible(user))
            return null;
        
        String displayName = f.getWebName(user);
        String path = f.getPath(user);
        Instant creationTime = f.getCreationTime(user);

        if(displayName == null
                || path == null
                || creationTime == null
                || !(f instanceof VEntity))
            return null;

        VEntity entity = (VEntity)f;

        Element response = doc.createElementNS("DAV:", "response");

        Element href = doc.createElementNS("DAV:", "href");
        href.setTextContent(getHostPath(path, host));
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
        displayname.setTextContent(Helper.toUTF8(displayName));
        prop.appendChild(displayname);

        Element supportedlock = doc.createElementNS("DAV:", "supportedlock");
        prop.appendChild(supportedlock);

        Stream.of(entity.getAvailableLocks())
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
        
        entity.getProperties(user)
                .forEach(p ->
                {
                    Element property = doc.createElementNS(p.getKey().getKey(), p.getKey().getValue());
                    property.setTextContent(p.getValue());
                    prop.appendChild(property);
                });

        switch(f.getResourceType(user))
        {
            case File:
                if(!getInfoFile(f, host, environment, doc, prop))
                    return null;
                break;

            case Directory:
                if(!getInfoFolder(f, host, environment, doc, prop))
                    return null;
                break;
        }
        
        return response;
    }
    private boolean getInfoFolder(IResource f, String host, HTTPEnvironment environment, Document doc, Element parent) throws UserRequiredException
    {
        Element resourcetype = doc.createElementNS("DAV:", "resourcetype");
        parent.appendChild(resourcetype);
        
        Element collection = doc.createElementNS("DAV:", "collection");
        resourcetype.appendChild(collection);
        
        return true;
    }
    private boolean getInfoFile(IResource f, String host, HTTPEnvironment environment, Document doc, Element parent) throws UserRequiredException
    {
        HTTPUser user = environment.getUser();
        
        Instant lastModified = f.getLastModified(user);
        String mimeType = f.getMimeType(user);
        if(mimeType == null)
            mimeType = "text/binary";
        
        if(lastModified == null)
            return false;
        
        
        Element resourcetype = doc.createElementNS("DAV:", "resourcetype");
        parent.appendChild(resourcetype);
        
        Element getcontentlength = doc.createElementNS("DAV:", "getcontentlength");
        getcontentlength.setTextContent(String.valueOf(f.getSize(user)));
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
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) throws UserRequiredException, NotFoundException 
    {
        HTTPUser user = environment.getUser();
        
        IResource f = getResource(input.getPath(), environment);
        
        String host = input.getHeader("host");
        
        try
        {
            Document doc = createDocument();
        
            Element multistatus = doc.createElementNS("DAV:", "multistatus");
            doc.appendChild(multistatus);
            
            Element current = getInfo(f, host, environment, doc);
            if(current == null)
                throw new NotFoundException();
            multistatus.appendChild(current);
            
            if(input.getHeader("depth").trim().equals("0"))
            { // depth = 0
            }
            else
            { // depth = 1
                for(IResource subFile : f.listResources(user))
                {
                    Element child = getInfo(subFile, host, environment, doc);
                    if(child != null)
                        multistatus.appendChild(child);
                }
            }
        
            HTTPMessage msg = new HTTPMessage(207, "Multi-Status");
            msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
            msg.setContent(NodeListWrap.getBytes(doc));
            return msg;
        }
        catch (ParserConfigurationException ex)
        {
            throw new NotFoundException();
        }
    }

    
}
