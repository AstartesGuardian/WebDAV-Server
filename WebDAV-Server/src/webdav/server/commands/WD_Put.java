package webdav.server.commands;

import http.FileSystemPath;
import http.server.HTTPCommand;
import http.server.message.HTTPResponse;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import webdav.server.resource.IResource;
import webdav.server.resource.ResourceType;

public class WD_Put extends HTTPCommand
{
    public WD_Put()
    {
        super("put");
    }
    
    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws UserRequiredException, NotFoundException
    {
        FileSystemPath path = environment.getPath();
        IResource resource = getResource(path, environment);
        
        if(environment.getRequest().getHeader("content-length").equals("0"))
        { // Content-Length == 0
            getResource(path.getParent(), environment)
                    .addChild(resource.creates(ResourceType.File, environment), environment);
        }
        else
        { // Content-Length != 0
            resource.setContent(environment.getRequest().getContent(), environment);
        }
        
        return HTTPResponse.create()
                .setCode(200)
                .setMessage("OK")
                .setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
    }
    
    @Override
    public void Continue(HTTPEnvRequest environment) throws UserRequiredException, NotFoundException
    {
        System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXX");
        if(environment.getBytesReceived().length > 0)
            getResource(environment.getPath(), environment).appendContent(environment.getBytesReceived(), environment);
    }
}
