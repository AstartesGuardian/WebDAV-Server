package webdav.server.commands.light;

import http.FileSystemPath;
import http.StringJoiner;
import http.server.HTTPCommand;
import http.server.message.HTTPResponse;
import java.time.Instant;
import webdav.server.resource.IResource;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;

public class WDL_Propfind extends HTTPCommand
{
    public WDL_Propfind()
    {
        super("lightpropfind");
    }
    
    private static String addString(String name, String value)
    {
        return "<" + name + ">" + value + "</" + name + ">";
    }
    private static String addString(String name, long value)
    {
        return addString(name, String.valueOf(value));
    }
    
    private String getInfo(IResource resource, FileSystemPath path, String host, HTTPEnvRequest environment) throws UserRequiredException
    {
        if(!resource.isVisible(environment))
            return null;
        
        String displayName = resource.getWebName(environment);
        Instant creationTime = resource.getCreationTime(environment);

        if(displayName == null || creationTime == null)
            return null;
        
        String content = "";
        
        content += addString("href", getHostPath(path.toString(), host)); // href
        content += addString("path", path.toString()); // path
        
        content += addString("creationdate", creationTime.getEpochSecond()); // creation date
        content += addString("displayname", displayName); // display name
        
        content += addString("resourcetype", resource.getResourceType(environment).toString()); // resourcetype

        content += addString("locks",
                resource.getAvailableLocks()
                        .stream()
                        .map(ik -> ik.getType().name() + " / " + ik.getScope().name())
                        .reduce("", StringJoiner.join("\r\n")));
        
        content += addString("properties",
                resource.getProperties(environment)
                        .entrySet()
                        .stream()
                        .map(p -> p.getKey().getKey() + "::" + p.getKey().getValue() + "=" + p.getValue())
                        .reduce("", StringJoiner.join("\r\n")));

        switch(resource.getResourceType(environment))
        {
            case File:
                content += getInfoFile(resource, host, environment);
                break;

            case Directory:
                content += getInfoFolder(resource, host, environment);
                break;
        }
        
        return content;
    }
    private String getInfoFolder(IResource f, String host, HTTPEnvRequest environment) throws UserRequiredException
    {
        return "";
    }
    private String getInfoFile(IResource f, String host, HTTPEnvRequest environment) throws UserRequiredException
    {
        Instant lastModified = f.getLastModified(environment);
        String mimeType = f.getMimeType(environment);
        if(mimeType == null)
            mimeType = "text/binary";
        
        if(lastModified == null)
            return "";
        
        String content = "";
        
        content += addString("contentsize", String.valueOf(f.getSize(environment))); // content size
        content += addString("mimetype", mimeType); // mime type
        content += addString("size", f.getSize(environment)); // mime type
        content += addString("lastmodified", lastModified.getEpochSecond()); // last modified
        
        return content;
    }

    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws UserRequiredException, NotFoundException 
    {
        FileSystemPath path = environment.getPath();
        IResource resource = getResource(path, environment);
        
        String host = environment.getRequest().getHeader("host");
        
        String current = "<current>" + getInfo(resource, path, host, environment) + "</current>";
        if(current == null)
            throw new NotFoundException();

        if(environment.getRequest().getHeader("depth").trim().equals("0"))
        { // depth = 0
        }
        else
        { // depth = 1
            for(IResource subFile : resource.listResources(environment))
            {
                String value = getInfo(subFile, path.createChild(subFile.getWebName(environment)), host, environment);
                if(value != null && !value.isEmpty())
                {
                    current += "<child>";
                    current += value;
                    current += "</child>";
                }
            }
        }

        return HTTPResponse.create()
                .setCode(207)
                .setMessage("Multi-Status")
                .setHeader("Content-Type", "text/xml; charset=\"utf-8\"")
                .setContent("<response>" + current + "</response>");
    }
}
