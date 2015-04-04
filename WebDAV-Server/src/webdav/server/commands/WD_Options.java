package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;

public class WD_Options extends HTTPCommand
{
    public WD_Options()
    {
        super("options");
    }

    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        HTTPMessage msg = new HTTPMessage(200, "OK");
        
        String cmds = "";
        for(String s : environment.getServerSettings().getAllowedCommands().stream().map(c -> c.getName()).toArray(String[]::new))
            if(cmds.isEmpty())
                cmds += s;
            else
                cmds += ", " + s;
        
        msg.setHeader("Allow", cmds);
        msg.setHeader("Content-Type", "httpd/unix-directory");
        return msg;
    }
    
}
