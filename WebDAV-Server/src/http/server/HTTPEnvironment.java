package http.server;

import webdav.server.IResource;

public class HTTPEnvironment
{
    // <editor-fold defaultstate="collapsed" desc="Constructor(s)">
    public HTTPEnvironment(HTTPServerSettings serverSettings, String root)
    {
        this.serverSettings = serverSettings;
        this.root = root;
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
    
    // <editor-fold defaultstate="collapsed" desc="Shortcuts">
    public IResource createFromPath(String path)
    {
        return this.serverSettings.generateResourceManager().createFromPath(path);
    }
    // </editor-fold>
}
