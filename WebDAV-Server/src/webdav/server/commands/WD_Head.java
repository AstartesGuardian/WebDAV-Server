package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.IResource;

public class WD_Head extends HTTPCommand
{
    public WD_Head()
    {
        super("head");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        IResource f = getResource(input.getPath(), environment);
        
        if(!f.exists())
            return new HTTPMessage(404, "Not found");
        
        HTTPMessage msg = new HTTPMessage(200, "OK");
        msg.setHeader("Content-Length", String.valueOf(f.getSize()));
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
