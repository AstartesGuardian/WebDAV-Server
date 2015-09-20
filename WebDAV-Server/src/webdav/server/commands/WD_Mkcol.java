package webdav.server.commands;

import http.FileSystemPath;
import http.server.authentication.HTTPUser;
import http.server.HTTPCommand;
import http.server.message.HTTPResponse;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import webdav.server.resource.ResourceType;

public class WD_Mkcol extends HTTPCommand
{
    public WD_Mkcol()
    {
        super("mkcol");
    }
    
    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws UserRequiredException 
    {
        FileSystemPath path = environment.getPath();
        getResource(path.getParent(), environment)
                .addChild(getResource(environment.getPath(), environment)
                        .creates(ResourceType.Directory, environment)
                        , environment);
        
        return HTTPResponse.create()
                .setCode(201)
                .setMessage("Created");
    }
    
}
