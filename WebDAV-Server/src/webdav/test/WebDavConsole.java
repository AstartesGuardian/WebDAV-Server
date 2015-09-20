package webdav.test;

import console.Console;
import console.FileNavigationConsole;
import http.FileSystemPath;
import http.FileSystemPathManager;
import http.server.HTTPServer;

public class WebDavConsole extends FileNavigationConsole
{
    public WebDavConsole(FileSystemPathManager manager, FileSystemPath root, String prefix)
    {
        super(manager, root, prefix);
    }
    public WebDavConsole(HTTPServer server, FileSystemPath root)
    {
        super(server, root);
    }
    
    @Command(name="exit")
    public boolean exit(String[] params)
    {
        return true;
    }
}
