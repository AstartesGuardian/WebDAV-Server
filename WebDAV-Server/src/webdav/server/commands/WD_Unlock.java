package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.Locker;

public class WD_Unlock extends HTTPCommand
{
    public WD_Unlock()
    {
        super("unlock");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        HTTPMessage msg = new HTTPMessage(200, "OK");
        
        String uuid = input.getHeader("Lock-Token").replace("<", "").replace(">", "");
        
        Locker.removeLock(uuid);
        
        msg.setHeader("Lock-Token", "<" + uuid + ">");
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
