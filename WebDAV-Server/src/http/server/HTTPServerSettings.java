package http.server;

import http.server.authentication.HTTPAuthenticationManager;
import http.server.authentication.HTTPDefaultAuthentication;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import webdav.server.IResourceManager;

public class HTTPServerSettings
{
    public HTTPServerSettings()
    {
        this.setAllowedCommands(HTTPCommand.getStandardCommands());
        this.setAuthenticationManager(null);
        this.setHTTPVersion(httpVersion);
        this.setMaxBufferSize(maxBufferSize);
        this.setMaxNbRequests(maxNbRequests);
        this.setPrintErrors(true);
        this.setPrintRequests(true);
        this.setResourceManager(null);
        this.setRoot(null);
        this.setServer(null);
        this.setStepBufferSize(stepBufferSize);
        this.setTimeout(timeout);
        this.setUseResourceBuffer(true);
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Constructor(s)">
    /*public HTTPServerSettings(
            String serverName,
            HTTPCommand[] cmds,
            IResourceManager resourceManager,
            String root,
            int timeout,
            int maxNbRequests,
            HTTPAuthenticationManager authenticationManager,
            boolean printErrors,
            boolean printRequests,
            boolean useResourceBuffer)
    {
        this.timeout = timeout;
        this.maxNbRequests = maxNbRequests;
        this.server = serverName;
        this.httpVersion = 1.1;
        this.allowedCommands = new HashSet<>();
        this.authenticationManager = authenticationManager;
        
        for(HTTPCommand c : cmds)
            addAllowedCommand(c);
        
        this.root = root;
        this.resourceManager = resourceManager;
        this.printErrors = printErrors;
        this.printRequests = printRequests;
        this.useResourceBuffer = useResourceBuffer;
    }
    public HTTPServerSettings(String serverName, HTTPCommand[] cmds, IResourceManager resourceManager, String root, int timeout, int maxNbRequests, HTTPAuthenticationManager authenticationManager, boolean printErrors, boolean printRequests)
    {
        this(serverName, cmds, resourceManager, root, timeout, maxNbRequests, authenticationManager, printErrors, printRequests, true);
    }
    public HTTPServerSettings(String serverName, HTTPCommand[] cmds, IResourceManager resourceManager, String root, int timeout, int maxNbRequests, HTTPAuthenticationManager authenticationManager, boolean printErrors)
    {
        this(serverName, cmds, resourceManager, root, timeout, maxNbRequests, authenticationManager, printErrors, false);
    }
    public HTTPServerSettings(String serverName, HTTPCommand[] cmds, IResourceManager resourceManager, String root, int timeout, int maxNbRequests, HTTPAuthenticationManager authenticationManager)
    {
        this(serverName, cmds, resourceManager, root, timeout, maxNbRequests, authenticationManager, false);
    }
    public HTTPServerSettings(String serverName, HTTPCommand[] cmds, IResourceManager resourceManager, String root, int timeout, int maxNbRequests)
    {
        this(serverName, cmds, resourceManager, root, timeout, maxNbRequests, null);
    }
    public HTTPServerSettings(String serverName, HTTPCommand[] cmds, IResourceManager resourceManager, String root, int timeout)
    {
        this(serverName, cmds, resourceManager, root, timeout, 100);
    }
    public HTTPServerSettings(String serverName, HTTPCommand[] cmds, IResourceManager resourceManager, String root)
    {
        this(serverName, cmds, resourceManager, root, 5);
    }
    public HTTPServerSettings(String serverName, HTTPCommand[] cmds, IResourceManager resourceManager)
    {
        this(serverName, cmds, resourceManager, "");
    }
    public HTTPServerSettings(String serverName, HTTPCommand[] cmds)
    {
        this(serverName, cmds, new VirtualResourceManager(new VirtualManager()));
    }
    public HTTPServerSettings(String serverName)
    {
        this(serverName, HTTPCommand.getStandardCommands());
    }
    public HTTPServerSettings()
    {
        this("WebDav Server");
    }*/
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Root">
    private String root;
    /**
     * Get the root string to add on the begining of every path.
     * 
     * @return String
     */
    public String getRoot()
    {
        return root;
    }
    public void setRoot(String root)
    {
        this.root = root;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="IResourceManager::new">
    private IResourceManager resourceManager;
    /**
     * Generate a file manager from 'iResourceManager' specified in the 
     * HTTPServerSettings constructor.
     * 
     * @return IResourceManager
     */
    public IResourceManager getResourceManager()
    {
        return resourceManager;
    }
    public void setResourceManager(IResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
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
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
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
    public void setServer(String server)
    {
        this.server = server;
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
    public void setHTTPVersion(double httpVersion)
    {
        this.httpVersion = httpVersion;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Allowed commands">
    protected Set<HTTPCommand> allowedCommands;
    /**
     * Get the allowed commands.
     * 
     * @return Set of HTTPCommand
     */
    public final Set<HTTPCommand> getAllowedCommands()
    {
        return allowedCommands;
    }
    public final void setAllowedCommands(HTTPCommand[] commands)
    {
        setAllowedCommands(Arrays.asList(commands));
    }
    public final void setAllowedCommands(Collection<HTTPCommand> commands)
    {
        allowedCommands = new HashSet<>(commands);
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

    // <editor-fold defaultstate="collapsed" desc="MaxNbRequests">
    protected int maxNbRequests;
    /**
     * Get the maximum number of requests by TCP connection.
     * 
     * @return int
     */
    public int getMaxNbRequests()
    {
        return maxNbRequests;
    }
    public void setMaxNbRequests(int maxNbRequests)
    {
        this.maxNbRequests = maxNbRequests;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Authentication Manager">
    protected HTTPAuthenticationManager authenticationManager = null;
    protected HTTPAuthenticationManager defaultAuthenticationManager = null;
    /**
     * Get the authentication manager to use.
     * 
     * @return HTTPAuthenticationManager
     */
    public HTTPAuthenticationManager getAuthenticationManager()
    {
        if(authenticationManager == null)
        {
            if(defaultAuthenticationManager == null)
                defaultAuthenticationManager = new HTTPDefaultAuthentication("WebDAV Server Realm");
            authenticationManager = defaultAuthenticationManager;
        }
        return authenticationManager;
    }
    public void setAuthenticationManager(HTTPAuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }
    // </editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc="Print errors">
    private boolean printErrors;
    /**
     * Get if the server has to print errors.
     * 
     * @return boolean
     */
    public boolean getPrintErrors()
    {
        return printErrors;
    }
    public void setPrintErrors(boolean printErrors)
    {
        this.printErrors = printErrors;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Print requests">
    private boolean printRequests;
    /**
     * Get if the server has to print requests.
     * 
     * @return boolean
     */
    public boolean getPrintRequests()
    {
        return printRequests;
    }
    public void setPrintRequests(boolean printRequests)
    {
        this.printRequests = printRequests;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Print Responses">
    private boolean printResponses;
    /**
     * Get if the server has to print requests.
     * 
     * @return boolean
     */
    public boolean getPrintResponses()
    {
        return printResponses;
    }
    public void setPrintResponses(boolean printResponses)
    {
        this.printResponses = printResponses;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Use resource buffer">
    private boolean useResourceBuffer;
    /**
     * Get if the server has to use a resource buffer.
     * With certain kind of resource types, the resource buffer can be a
     * problem (example : a resource type that don't check everytime if the
     * resource exists).
     * 
     * @return boolean
     */
    public boolean getUseResourceBuffer()
    {
        return useResourceBuffer;
    }
    public void setUseResourceBuffer(boolean useResourceBuffer)
    {
        this.useResourceBuffer = useResourceBuffer;
    }
    // </editor-fold>

    
    private int maxBufferSize;
    public int getMaxBufferSize()
    {
        return maxBufferSize;
    }
    public void setMaxBufferSize(int maxBufferSize)
    {
        this.maxBufferSize = maxBufferSize;
    }

    private int stepBufferSize;
    public int getStepBufferSize()
    {
        return stepBufferSize;
    }
    public void setStepBufferSize(int stepBufferSize)
    {
        this.stepBufferSize = stepBufferSize;
    }
}
