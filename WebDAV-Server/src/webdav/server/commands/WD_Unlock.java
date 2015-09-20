package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.message.HTTPResponse;
import webdav.server.resource.IResource;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;

public class WD_Unlock extends HTTPCommand
{
    public WD_Unlock()
    {
        super("unlock");
    }
    
    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws NotFoundException, UserRequiredException 
    {
        String uuid = environment.getRequest().getHeader("Lock-Token").replace("<", "").replace(">", "");
        
        IResource resource = getResource(environment.getPath(), environment);
        
        resource.removeLock(uuid, environment);
        
        return HTTPResponse.create()
                .setCode(204)
                .setMessage("No Content")
                .setHeader("Lock-Token", "<" + uuid + ">")
                .setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
    }
    
}
