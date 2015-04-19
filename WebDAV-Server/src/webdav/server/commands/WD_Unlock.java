package webdav.server.commands;

import http.server.authentication.HTTPUser;
import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.IResource;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import webdav.server.virtual.entity.VEntity;

public class WD_Unlock extends HTTPCommand
{
    public WD_Unlock()
    {
        super("unlock");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) throws NotFoundException, UserRequiredException 
    {
        String uuid = input.getHeader("Lock-Token").replace("<", "").replace(">", "");
        
        IResource f = getResource(input.getPath(), environment);
        HTTPUser user = environment.getUser();
        
        if(!(f instanceof VEntity))
            throw new NotFoundException();
        VEntity entity = (VEntity)f;
        
        entity.removeLock(uuid, user);
        
        HTTPMessage msg = new HTTPMessage(200, "OK");
        msg.setHeader("Lock-Token", "<" + uuid + ">");
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
