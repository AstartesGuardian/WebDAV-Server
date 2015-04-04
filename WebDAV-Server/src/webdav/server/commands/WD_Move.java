package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import java.net.URLDecoder;
import webdav.server.IResource;

public class WD_Move extends HTTPCommand
{
    public WD_Move()
    {
        super("move");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        HTTPMessage msg = new HTTPMessage(201, "Created");
        
        try
        {
            String dest = input.getHeader("destination");
            String host = input.getHeader("host");
            String shortDest = URLDecoder.decode(dest.substring(dest.indexOf(host) + host.length()), "UTF-8");

            IResource fsrc = environment.createFromPath(environment.getRoot() + input.getPath().replace("/", "\\").trim());
            IResource fdest = environment.createFromPath(environment.getRoot() + shortDest.replace("/", "\\").trim());
            
            fsrc.renameTo(fdest);
        }
        catch (Exception ex)
        { }
        
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
