package webdav.server.commands;

import http.server.authentication.HTTPUser;
import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.IResource;
import http.server.exceptions.NotFoundException;
import webdav.server.ResourceType;
import http.server.exceptions.UserRequiredException;

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
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) throws UserRequiredException, NotFoundException 
    {
        HTTPUser user = environment.getUser();
        
        IResource f = getResource(input.getPath(), environment);
           
        HTTPMessage msg = new HTTPMessage(200, "OK");
        
        ResourceType type = f.getResourceType(user);
        switch(type)
        {
            case File:
                msg.setContent(f.getContent(user));
                msg.setHeader("Content-Type", f.getMimeType(user));
                break;
                
            case Directory:
                String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><resources>";

                for(IResource sf : f.listResources(user))
                    switch(type)
                    {
                        case File:
                            content += "<directory>" + sf.getWebName(user) + "</directory>";
                            break;

                        case Directory:
                            content += "<file><name>" + sf.getWebName(user) + "</name><size>" + sf.getSize(user) + "</size><type>" + sf.getMimeType(user) + "</type></file>";
                            break;
                    }
                content += "</resources>";

                msg.setContent(content);
                break;
        }
        
        return msg;
    }
    
}
