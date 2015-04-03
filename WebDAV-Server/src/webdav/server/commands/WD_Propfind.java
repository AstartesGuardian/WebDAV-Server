package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.Helper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adrien
 */
public class WD_Propfind extends HTTPCommand
{
    public WD_Propfind()
    {
        super("propfind");
    }
    
    private String getInfo(File f, HTTPEnvironment environment)
    {
        if(f.isFile())
            return getInfoFile(f, environment);
        else
            return getInfoFolder(f, environment);
    }
    private String getInfoFolder(File f, HTTPEnvironment environment)
    {
        try
        {
            BasicFileAttributes fa = Files.readAttributes(f.toPath(), BasicFileAttributes.class);

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
                    .replace("%CREATION-DATE%", fa.creationTime().toString().substring(0, "1997-12-01T17:42:21".length()) + "-00:00")
                    .replace("%DISPLAY-NAME%", f.getName());
        }
        catch (IOException ex)
        {
            System.out.println("[err-d] " + ex.getMessage());
            return null;
        }
    }
    private String getInfoFile(File f, HTTPEnvironment environment)
    {
        try
        {
            BasicFileAttributes fa = Files.readAttributes(f.toPath(), BasicFileAttributes.class);

            String pattern =
    "    <D:response>\r\n" +
    "      <D:href>%PATH%</D:href>\r\n" +
    "      <D:propstat>\r\n" +
    "        <D:prop>\r\n" +
    "          <D:creationdate>%CREATION-DATE%</D:creationdate>\r\n" +
    "          <D:displayname>%DISPLAY-NAME%</D:displayname>\r\n" +
    "          <D:getcontentlength>%LENGTH%</D:getcontentlength>\r\n" +
    "          <D:getcontenttype>%TYPE%</D:getcontenttype>\r\n" +
    "          <D:getetag>\"zzyzx\"</D:getetag>\r\n" +
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
                    .replace("%CREATION-DATE%", fa.creationTime().toString().substring(0, "1997-12-01T17:42:21".length()) + "-00:00")
                    .replace("%LAST-MODIFIED%", Helper.toString(new Date(f.lastModified())))
                    .replace("%DISPLAY-NAME%", f.getName())
                    .replace("%LENGTH%", String.valueOf(fa.size()))
                    .replace("%TYPE%", Files.probeContentType(f.toPath()));
        }
        catch (IOException ex)
        {
            System.out.println("[err-f] " + ex.getMessage());
            return null;
        }
    }

    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        HTTPMessage msg = new HTTPMessage(207, "Multi-Status");
        
        StringBuilder content = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n<D:multistatus xmlns:D=\"DAV:\">\r\n");
        
        File f = new File(environment.getRoot() + input.getPath().replace("/", "\\").trim());
        content.append(getInfo(f, environment));
        
        if(input.getHeader("depth").trim().equals("0"))
        { // d = 0
        }
        else
        { // d = 1
            for(File subFile : f.listFiles())
                content.append(getInfo(subFile, environment));
        }
        
        content.append("</D:multistatus>");
        
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        msg.setContent(content.toString());
        return msg;
    }
    
}
