package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.message.HTTPResponse;
import webdav.server.resource.IResource;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;

public class WD_Head extends HTTPCommand
{
    public WD_Head()
    {
        super("head");
    }
    
    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws UserRequiredException, NotFoundException 
    {
        IResource f = getResource(environment.getPath(), environment);
        
        return HTTPResponse.create()
                .setCode(200)
                .setMessage("OK")
                .setHeader("Content-Length", String.valueOf(f.getSize(environment)))
                .setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
    }
    
}
