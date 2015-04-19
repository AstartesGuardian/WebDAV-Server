package webdav.server.commands;

import http.server.authentication.HTTPUser;
import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.IResource;
import http.server.exceptions.UserRequiredException;

public class WD_Mkcol extends HTTPCommand
{
    public WD_Mkcol()
    {
        super("mkcol");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) throws UserRequiredException 
    {
        HTTPUser user = environment.getUser();
        
        environment.getResourceManager().createDirectory(getPath(input.getPath(), environment), user);
        
        HTTPMessage msg = new HTTPMessage(201, "Created");
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
