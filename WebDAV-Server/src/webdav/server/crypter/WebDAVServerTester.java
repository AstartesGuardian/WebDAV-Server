package webdav.server.crypter;

import java.io.IOException;

public class WebDAVServerTester implements Runnable
{
    public static void main(String[] args) throws IOException
    {
        new WebDAVServerTester().run();
    }

    @Override
    public void run()
    {/*
        LocalCryptedResourceManager.setAlgorithm(ICrypter.Algorithm.AES_CBC_PKCS5Padding);
        
        HTTPServerSettings settings = new HTTPServerSettings("WebDAV Server (Windows 8.1)",
                HTTPCommand.getStandardCommands(),
                LocalCryptedResourceManager.class,
                "D:\\Documents\\FTP_TEST",
                5,
                100,
                new LocalCryptedAuthenticationManager("Crypted WebDAV Server")
        );
        
        HTTPServer s = new HTTPServer(1702, settings, false, true);
        
        s.run();*/
    }
}
