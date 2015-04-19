package webdav.server;

import http.server.HTTPCommand;
import http.server.HTTPServer;
import http.server.HTTPServerSettings;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import webdav.server.virtual.contentmanagers.StandardContentManager;
import webdav.server.virtual.VirtualManager;
import webdav.server.virtual.VirtualResourceManager;
import webdav.server.virtual.entity.VDirectory;

public class WebDAVServerTester implements Runnable
{
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException
    {
        new WebDAVServerTester().run();
    }

    @Override
    public void run()
    {
        VirtualManager vm;
        try
        {
            vm = VirtualManager.load(new File("data.vm"));
        }
        catch (Exception ex)
        {
            vm = new VirtualManager();
            VDirectory dir = new VDirectory(vm.getRoot(), "public");
        }
        
        vm.setRootDirectory(new File("D:\\Documents\\FTP_TEST"));
        vm.addContentManager("direct", new StandardContentManager());
        
        HTTPServerSettings settings = new HTTPServerSettings();
        settings.setAllowedCommands(HTTPCommand.getStandardCommands());
        settings.setAuthenticationManager(null);
        settings.setHTTPVersion(1.1);
        settings.setMaxNbRequests(100);
        settings.setPrintErrors(true);
        settings.setPrintRequests(true);
        settings.setPrintResponses(true);
        settings.setResourceManager(new VirtualResourceManager(vm));
        settings.setRoot("");
        settings.setServer("WebDAV Server");
        settings.setMaxBufferSize(1048576);
        settings.setStepBufferSize(5000);
        settings.setTimeout(5);
        settings.setUseResourceBuffer(false);
        
        
        HTTPServer s = new HTTPServer(1704, settings, false, true);
        s.run();
    }
}
