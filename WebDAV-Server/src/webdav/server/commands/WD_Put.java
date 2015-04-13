package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.IResource;

public class WD_Put extends HTTPCommand
{
    public WD_Put()
    {
        super("put");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        HTTPMessage msg = new HTTPMessage(200, "OK");
        
        IResource f = getResource(input.getPath(), environment);
        
        f.createFile();
        
        if(!input.getHeader("content-length").equals("0"))
        {
            f.setContent(input.getContent());
        }
        
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
    @Override
    public void Continue(HTTPMessage input, byte[] data, HTTPEnvironment environment)
    {
        if(data.length > 0)
        {
            IResource f = getResource(input.getPath(), environment);
            f.appendContent(data);
        }
    }
}
