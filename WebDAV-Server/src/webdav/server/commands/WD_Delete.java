package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.IResource;

public class WD_Delete extends HTTPCommand
{
    public WD_Delete()
    {
        super("delete");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        HTTPMessage msg = new HTTPMessage(200, "OK");
        
        IResource f = getResource(input.getPath(), environment);
        try
        {
            f.delete();
        }
        catch (Exception ex)
        { }
        
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
