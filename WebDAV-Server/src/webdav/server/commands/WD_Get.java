package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.IResource;

public class WD_Get extends HTTPCommand
{
    public WD_Get()
    {
        super("get");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        IResource f = getResource(input.getPath(), environment);
        
        if(!f.exists())
            return new HTTPMessage(404, "Not found");
           
        HTTPMessage msg = new HTTPMessage(200, "OK");
        
        if(f.isFile())
        {
            msg.setContent(f.getContent());
            msg.setHeader("Content-Type", f.getMimeType());
        }
        else if(f.isDirectory())
        {
            String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><resources>";

            for(IResource sf : f.listResources())
                if(sf.isDirectory())
                    content += "<directory>" + sf.getWebName() + "</directory>";
                else
                    content += "<file><name>" + sf.getWebName() + "</name><size>" + sf.getSize() + "</size><type>" + sf.getMimeType() + "</type></file>";
            content += "</resources>";

            msg.setContent(content);
        }
        
        return msg;
    }
    
}
