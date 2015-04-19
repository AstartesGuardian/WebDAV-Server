package webdav.server.commands;

import http.server.authentication.HTTPUser;
import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.IResource;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;

public class WD_Head extends HTTPCommand
{
    public WD_Head()
    {
        super("head");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) throws UserRequiredException, NotFoundException 
    {
        HTTPUser user = environment.getUser();
        
        IResource f = getResource(input.getPath(), environment);
        
        HTTPMessage msg = new HTTPMessage(200, "OK");
        msg.setHeader("Content-Length", String.valueOf(f.getSize(user)));
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
