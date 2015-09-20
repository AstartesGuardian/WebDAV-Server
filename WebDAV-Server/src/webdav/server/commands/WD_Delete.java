package webdav.server.commands;

import http.FileSystemPath;
import http.server.HTTPCommand;
import http.server.message.HTTPResponse;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import webdav.server.resource.IResource;

public class WD_Delete extends HTTPCommand
{
    public WD_Delete()
    {
        super("delete");
    }
    
    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws UserRequiredException, NotFoundException 
    {
        FileSystemPath path = environment.getPath();
        IResource resource = getResource(path, environment);
        IResource resourceParent = getResource(path.getParent(), environment);
        resourceParent.removeChild(resource, environment);
        resource.delete(environment);
        
        return HTTPResponse.create()
                .setCode(200)
                .setMessage("OK")
                .setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
    }
    
}
