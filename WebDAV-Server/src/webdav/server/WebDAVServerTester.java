package webdav.server;

import webdav.server.standard.StandardResourceManager;
import http.server.HTTPCommand;
import http.server.HTTPServer;
import http.server.HTTPServerSettings;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class WebDAVServerTester implements Runnable
{
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException
    {
        new WebDAVServerTester().run();
    }

    @Override
    public void run()
    {
        HTTPServerSettings settings = new HTTPServerSettings("WebDav Server (Windows 8.1)",
                HTTPCommand.getStandardCommands(),
                StandardResourceManager.class,
                "D:\\Documents\\FTP_TEST");
        
        HTTPServer s = new HTTPServer(1703, settings, false, true);
        
        s.run();
    }
}
