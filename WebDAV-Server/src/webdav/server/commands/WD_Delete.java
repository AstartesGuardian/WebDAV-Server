package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.IResource;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;

public class WD_Delete extends HTTPCommand
{
    public WD_Delete()
    {
        super("delete");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) throws UserRequiredException, NotFoundException 
    {
        HTTPMessage msg = new HTTPMessage(200, "OK");
        
        getResource(input.getPath(), environment).delete(environment.getUser());
        
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
