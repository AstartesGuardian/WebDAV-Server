package http.server;

import org.omg.CORBA.Environment;
import webdav.server.IResource;
import webdav.server.IResourceManager;

public class HTTPEnvironment
{
    // <editor-fold defaultstate="collapsed" desc="Constructor(s)">
    public HTTPEnvironment(HTTPServerSettings serverSettings, String root)
    {
        this.serverSettings = serverSettings;
        this.root = root;
    }
    public HTTPEnvironment(HTTPEnvironment parent)
    {
        this(parent.serverSettings, parent.root);
        
        this.iResourceManager = serverSettings.generateResourceManager();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Server settings">
    private final HTTPServerSettings serverSettings;
    public HTTPServerSettings getServerSettings()
    {
        return serverSettings;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Root">
    private final String root;
    public String getRoot()
    {
        return root;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Resource manager">
    private IResourceManager iResourceManager;
    public IResourceManager getResourceManager()
    {
        return iResourceManager;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Shortcuts">
    public IResource createFromPath(String path)
    {
        return getResourceManager().createFromPath(path);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Authentication Manager">
    HTTPAuthenticationManager getAuthenticationManager()
    {
        return serverSettings.getAuthenticationManager();
    }
    // </editor-fold>
}
