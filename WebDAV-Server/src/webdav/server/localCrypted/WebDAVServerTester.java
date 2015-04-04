package webdav.server.localCrypted;

import http.server.HTTPCommand;
import http.server.HTTPServer;
import http.server.HTTPServerSettings;
import java.io.IOException;

public class WebDAVServerTester implements Runnable
{
    public static void main(String[] args) throws IOException
    {
        new WebDAVServerTester().run();
    }

    @Override
    public void run()
    {
        LocalCryptedResourceManager.loadCipherCrypter(ICrypter.Algorithm.AES_CBC_PKCS5Padding);
        LocalCryptedResourceManager.setKey("Chocolate");
        
        HTTPServerSettings settings = new HTTPServerSettings("WebDav Server (Windows 8.1)",
                HTTPCommand.getStandardCommands(),
                LocalCryptedResourceManager.class,
                "D:\\Documents\\FTP_TEST");
        
        HTTPServer s = new HTTPServer(1700, settings, false, true);
        
        s.run();
    }
}
