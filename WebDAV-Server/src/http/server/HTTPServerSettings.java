package http.server;

import java.util.HashSet;
import java.util.Set;
import webdav.server.IResourceManager;

public class HTTPServerSettings
{
    // <editor-fold defaultstate="collapsed" desc="Constructor(s)">
    public HTTPServerSettings(String serverName, HTTPCommand[] cmds, Class iResourceManager, String root)
    {
        timeout = 5;
        server = serverName;
        httpVersion = 1.0;
        allowedCommands = new HashSet<>();
        
        for(HTTPCommand c : cmds)
            addAllowedCommand(c);
        
        this.root = root;
        this.iResourceManager = iResourceManager;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Root">
    private final String root;
    /**
     * Get the root string to add on the begining of every path.
     * 
     * @return String
     */
    public String getRoot()
    {
        return root;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="IResourceManager::new">
    private final Class iResourceManager;
    /**
     * Generate a file manager from 'iResourceManager' specified in the 
     * HTTPServerSettings constructor.
     * 
     * @return IResourceManager
     */
    public IResourceManager generateResourceManager()
    {
        try
        {
            return (IResourceManager)iResourceManager.newInstance();
        } catch (Exception ex)
        {
            return null;
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Timeout">
    protected int timeout;
    /**
     * Get the timeout in seconds.
     * 
     * @return Timeout (sec)
     */
    public int getTimeout()
    {
        return timeout;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Server information">
    protected String server;
    /**
     * Get the server information.
     * 
     * @return String
     */
    public String getServer()
    {
        return server;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="HTTP Version">
    protected double httpVersion;
    /**
     * Get the HTTP version.
     * 
     * @return double
     */
    public double getHTTPVersion()
    {
        return httpVersion;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Allowed commands">
    protected final Set<HTTPCommand> allowedCommands;
    /**
     * Get the allowed commands.
     * 
     * @return Set of HTTPCommand
     */
    public final Set<HTTPCommand> getAllowedCommands()
    {
        return allowedCommands;
    }
    /**
     * Add an allowed command.
     * 
     * @param cmd HTTPCommand to add
     */
    public final void addAllowedCommand(HTTPCommand cmd)
    {
        allowedCommands.add(cmd);
    }
    /**
     * Add a list of allowed commands.
     * 
     * @param cmds Commands to add
     */
    public final void addAllowedCommand(Iterable<HTTPCommand> cmds)
    {
        cmds.forEach(cmd -> addAllowedCommand(cmd));
    }
    // </editor-fold>
}
