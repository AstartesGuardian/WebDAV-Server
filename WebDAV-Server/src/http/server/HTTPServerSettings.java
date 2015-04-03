package http.server;

import java.util.HashSet;
import java.util.Set;

public class HTTPServerSettings
{
    // <editor-fold defaultstate="collapsed" desc="Constructor(s)">
    public HTTPServerSettings(String serverName, HTTPCommand[] cmds)
    {
        timeout = 5;
        maxNumberOfRequest = 100;
        server = serverName;
        httpVersion = 1.0;
        allowedCommands = new HashSet<>();
        
        for(HTTPCommand c : cmds)
            addAllowedCommand(c);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Timeout">
    protected int timeout;
    /**
     * 
     * @return Timeout (sec)
     */
    public int getTimeout()
    {
        return timeout;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Maximum number of request">
    protected int maxNumberOfRequest;
    public int getMaxNumberOfRequest()
    {
        return maxNumberOfRequest;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Server information">
    protected String server;
    public String getServer()
    {
        return server;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="HTTP Version">
    protected double httpVersion;
    public double getHTTPVersion()
    {
        return httpVersion;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Allowed commands">
    protected Set<HTTPCommand> allowedCommands;
    public Set<HTTPCommand> getAllowedCommands()
    {
        return allowedCommands;
    }
    public void addAllowedCommand(HTTPCommand cmd)
    {
        allowedCommands.add(cmd);
    }
    public void addAllowedCommand(Iterable<HTTPCommand> cmds)
    {
        cmds.forEach(cmd -> addAllowedCommand(cmd));
    }
    // </editor-fold>
}
