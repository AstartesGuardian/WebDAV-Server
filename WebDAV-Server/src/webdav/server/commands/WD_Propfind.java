package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.Helper;
import webdav.server.IResource;

public class WD_Propfind extends HTTPCommand
{
    public WD_Propfind()
    {
        super("propfind");
    }
    
    private String getInfo(IResource f, HTTPEnvironment environment)
    {
        if(!f.exists())
            return null;
        
        if(f.isFile())
            return getInfoFile(f, environment);
        else
            return getInfoFolder(f, environment);
    }
    private String getInfoFolder(IResource f, HTTPEnvironment environment)
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

        return pattern
                .replace("%PATH%", f.getName())
                .replace("%CREATION-DATE%", f.getCreationTime().toString().substring(0, "0000-00-00T00:00:00".length()) + "-00:00")
                .replace("%DISPLAY-NAME%", f.getName());
    }
    private String getInfoFile(IResource f, HTTPEnvironment environment)
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
        
        return pattern
                .replace("%PATH%", f.getName())
                .replace("%CREATION-DATE%", f.getCreationTime().toString().substring(0, "0000-00-00T00:00:00".length()) + "-00:00")
                .replace("%LAST-MODIFIED%", Helper.toString(f.getLastModified()))
                .replace("%DISPLAY-NAME%", f.getName())
                .replace("%LENGTH%", String.valueOf(f.getSize()))
                .replace("%TYPE%", f.getMimeType())
                .replace("%ENTITY-TAG%", "zzyzx");
    }

    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        StringBuilder content = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n<D:multistatus xmlns:D=\"DAV:\">\r\n");
        
        IResource f = environment.createFromPath(environment.getRoot() + input.getPath().replace("/", "\\").trim());
        
        if(!f.exists())
            return new HTTPMessage(404, "Not found");
        
        content.append(getInfo(f, environment));
        
        if(input.getHeader("depth").trim().equals("0"))
        { // d = 0
        }
        else
        { // d = 1
            for(IResource subFile : f.listResources())
                content.append(getInfo(subFile, environment));
        }
        
        content.append("</D:multistatus>");
        
        HTTPMessage msg = new HTTPMessage(207, "Multi-Status");
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        msg.setContent(content.toString());
        return msg;
    }
    
}
