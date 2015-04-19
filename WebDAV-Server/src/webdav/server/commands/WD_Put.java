package webdav.server.commands;

import http.server.authentication.HTTPUser;
import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.IResource;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;

public class WD_Put extends HTTPCommand
{
    public WD_Put()
    {
        super("put");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) throws UserRequiredException, NotFoundException
    {
        HTTPUser user = environment.getUser();
        
        if(input.getHeader("content-length").equals("0"))
        { // Content-Length == 0
            environment.getResourceManager().createFile(getPath(input.getPath(), environment), user);
        }
        else
        { // Content-Length != 0
            getResource(input.getPath(), environment).setContent(input.getContent(), user);
        }
        
        HTTPMessage msg = new HTTPMessage(200, "OK");
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
    @Override
    public void Continue(HTTPMessage input, byte[] data, HTTPEnvironment environment) throws UserRequiredException, NotFoundException
    {
        if(data.length > 0)
        {
            HTTPUser user = environment.getUser();
        
            getResource(input.getPath(), environment).appendContent(data, user);
        }
    }
}
