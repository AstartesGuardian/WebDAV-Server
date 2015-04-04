package webdav.server;

import webdav.server.standard.StandardResourceManager;
import http.server.HTTPCommand;
import http.server.HTTPServer;
import http.server.HTTPServerSettings;
import java.io.IOException;

public class WebDAVServerTester
{
    public static void main(String[] args) throws IOException
    {
        HTTPServerSettings settings = new HTTPServerSettings("WebDav Server (Windows 8.1)",
                HTTPCommand.getStandardCommands(),
                StandardResourceManager.class,
                "D:\\Documents\\FTP_TEST");
        HTTPServer s = new HTTPServer(1700, settings);
        
        s.run();
    }
}
