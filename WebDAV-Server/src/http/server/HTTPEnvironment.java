package http.server;

import http.server.authentication.HTTPUser;
import http.server.authentication.HTTPAuthenticationManager;
import org.omg.CORBA.Environment;
import webdav.server.IResource;
import webdav.server.IResourceManager;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;

public class HTTPEnvironment
{
    // <editor-fold defaultstate="collapsed" desc="Constructor(s)">
    public HTTPEnvironment(HTTPServerSettings serverSettings, String root)
    {
        this.serverSettings = serverSettings;
        this.root = root;
        this.resourceManager = serverSettings.getResourceManager();
    }
    public HTTPEnvironment(HTTPEnvironment parent)
    {
        this(parent.serverSettings, parent.root);
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
    
    // <editor-fold defaultstate="collapsed" desc="User">
    private HTTPUser user = null;
    public HTTPUser getUser()
    {
        return user;
    }
    public void setUser(HTTPUser user)
    {
        this.user = user;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Resource manager">
    private final IResourceManager resourceManager;
    public IResourceManager getResourceManager()
    {
        return resourceManager;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Shortcuts">
    public IResource getResourceFromPath(String path) throws UserRequiredException, NotFoundException
    {
        return getResourceManager().getResource(path, getUser());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Authentication Manager">
    HTTPAuthenticationManager getAuthenticationManager()
    {
        return serverSettings.getAuthenticationManager();
    }
    // </editor-fold>
}
