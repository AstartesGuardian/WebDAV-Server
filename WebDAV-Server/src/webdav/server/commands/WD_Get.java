package webdav.server.commands;

import http.server.authentication.HTTPUser;
import http.server.HTTPCommand;
import http.server.message.HTTPResponse;
import webdav.server.resource.IResource;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;

public class WD_Get extends HTTPCommand
{
    public WD_Get()
    {
        super("get");
    }
    WD_Get(String name)
    {
        super(name);
    }
    
    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws UserRequiredException, NotFoundException 
    {
        try
        {
        IResource f = getResource(environment.getPath(), environment);
           
        HTTPResponse.Builder builder = HTTPResponse.create()
                .setCode(200)
                .setMessage("OK");
        
        switch(f.getResourceType(environment))
        {
            case File:
                builder.setContent(f.getContent(environment))
                        .setHeader("Content-Type", f.getMimeType(environment));
                break;
                
            case Directory:
                String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><resources>";

                for(IResource sf : f.listResources(environment))
                    switch(sf.getResourceType(environment))
                    {
                        case File:
                            content += "<directory>" + sf.getWebName(environment) + "</directory>";
                            break;

                        case Directory:
                            content += "<file><name>" + sf.getWebName(environment) + "</name><size>" + sf.getSize(environment) + "</size><type>" + sf.getMimeType(environment) + "</type></file>";
                            break;
                    }
                content += "</resources>";

                builder.setContent(content);
                break;
        }
        
        return builder;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw ex;
        }
    }
    
}
