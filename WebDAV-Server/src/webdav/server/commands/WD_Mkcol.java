package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.IResource;

public class WD_Mkcol extends HTTPCommand
{
    public WD_Mkcol()
    {
        super("mkcol");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        HTTPMessage msg = new HTTPMessage(201, "Created");
        
        IResource f = getResource(input.getPath(), environment);
        
        f.createDirectory();
        
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
