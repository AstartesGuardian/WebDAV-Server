package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import webdav.server.Helper;
import webdav.server.IResource;

public class WD_Propfind extends HTTPCommand
{
    public WD_Propfind()
    {
        super("propfind");
    }
    
    private String getInfo(IResource f, String host, HTTPEnvironment environment)
    {
        if(!f.exists())
            return null;
        
        if(f.isFile())
            return getInfoFile(f, host, environment);
        else
            return getInfoFolder(f, host, environment);
    }
    private String getInfoFolder(IResource f, String host, HTTPEnvironment environment)
    {
        String pattern =
"    <D:response>\r\n" +
"      <D:href>%PATH%</D:href>\r\n" +
"      <D:propstat>\r\n" +
"        <D:prop>\r\n" +
"          <D:creationdate>%CREATION-DATE%</D:creationdate>\r\n" +
"          <D:displayname>%DISPLAY-NAME%</D:displayname>\r\n" +
"          <D:resourcetype><D:collection/></D:resourcetype>\r\n" +
"          <D:supportedlock>\r\n" +
"            <D:lockentry>\r\n" +
"              <D:lockscope><D:exclusive/></D:lockscope>\r\n" +
"              <D:locktype><D:write/></D:locktype>\r\n" +
"            </D:lockentry>\r\n" +
"            <D:lockentry>\r\n" +
"              <D:lockscope><D:shared/></D:lockscope>\r\n" +
"              <D:locktype><D:write/></D:locktype>\r\n" +
"            </D:lockentry>\r\n" +
"          </D:supportedlock>\r\n" +
"        </D:prop>\r\n" +
"        <D:status>HTTP/1.1 200 OK</D:status>\r\n" +
"      </D:propstat>\r\n" +
"    </D:response>\r\n";

        String displayName = f.getWebName();
        String path = f.getPath(environment.getRoot());
        
        return pattern
                .replace("%PATH%", (path == null ? "null" : ("http://" + (host.replace("/", "") + path.replace("\\", "/")).replace("//", "/"))))
                .replace("%CREATION-DATE%", f.getCreationTime().toString().substring(0, "0000-00-00T00:00:00".length()) + "-00:00")
                .replace("%DISPLAY-NAME%", (displayName == null ? "null" : displayName));
    }
    private String getInfoFile(IResource f, String host, HTTPEnvironment environment)
    {
        String pattern =
"    <D:response>\r\n" +
"      <D:href>%PATH%</D:href>\r\n" +
"      <D:propstat>\r\n" +
"        <D:prop>\r\n" +
"          <D:creationdate>%CREATION-DATE%</D:creationdate>\r\n" +
"          <D:displayname>%DISPLAY-NAME%</D:displayname>\r\n" +
"          <D:getcontentlength>%LENGTH%</D:getcontentlength>\r\n" +
"          <D:getcontenttype>%TYPE%</D:getcontenttype>\r\n" +
"          <D:getetag>\"%ENTITY-TAG%\"</D:getetag>\r\n" +
"          <D:getlastmodified>%LAST-MODIFIED%</D:getlastmodified>\r\n" +
"          <D:resourcetype/>\r\n" +
"          <D:supportedlock>\r\n" +
"            <D:lockentry>\r\n" +
"              <D:lockscope><D:exclusive/></D:lockscope>\r\n" +
"              <D:locktype><D:write/></D:locktype>\r\n" +
"            </D:lockentry>\r\n" +
"            <D:lockentry>\r\n" +
"              <D:lockscope><D:shared/></D:lockscope>\r\n" +
"              <D:locktype><D:write/></D:locktype>\r\n" +
"            </D:lockentry>\r\n" +
"          </D:supportedlock>\r\n" +
"        </D:prop>\r\n" +
"        <D:status>HTTP/1.1 200 OK</D:status>\r\n" +
"      </D:propstat>\r\n" +
"    </D:response>\r\n";
        
        String mimeType = f.getMimeType();
        String displayName = f.getWebName();
        
        String path = f.getPath(environment.getRoot());
        
        return pattern
                .replace("%PATH%", (path == null ? "null" : ("http://" + (host.replace("/", "") + path.replace("\\", "/")).replace("//", "/"))))
                .replace("%CREATION-DATE%", f.getCreationTime().toString().substring(0, "0000-00-00T00:00:00".length()) + "-00:00")
                .replace("%LAST-MODIFIED%", Helper.toString(f.getLastModified()))
                .replace("%DISPLAY-NAME%", (displayName == null ? "null" : displayName))
                .replace("%LENGTH%", String.valueOf(f.getSize()))
                .replace("%TYPE%", (mimeType == null ? "text/binary" : mimeType))
                .replace("%ENTITY-TAG%", "zzyzx");
    }

    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        StringBuilder content = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n<D:multistatus xmlns:D=\"DAV:\">\r\n");
        String host = input.getHeader("host");
        
        IResource f = getResource(input.getPath(), environment);
        
        if(!f.exists())
        {
            System.out.println("[FILE] : "+f.getPath(environment.getRoot())+" NOT FOUND");
            return new HTTPMessage(404, "Not found");
        }
        content.append(getInfo(f, host, environment));
        
        if(input.getHeader("depth").trim().equals("0"))
        { // d = 0
        }
        else
        { // d = 1
            for(IResource subFile : f.listResources())
                content.append(getInfo(subFile, host, environment));
        }
        
        content.append("</D:multistatus>");
        System.out.println(content.toString());
        
        HTTPMessage msg = new HTTPMessage(207, "Multi-Status");
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        msg.setContent(content.toString());
        return msg;
    }
    
}
