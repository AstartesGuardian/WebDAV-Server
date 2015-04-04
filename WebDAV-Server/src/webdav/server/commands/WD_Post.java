package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import webdav.server.IResource;

public class WD_Post extends HTTPCommand
{
    public WD_Post()
    {
        super("post");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        IResource f = environment.createFromPath(environment.getRoot() + input.getPath().replace("/", "\\").trim());
        
        try {
            System.out.println("/////////////////// CONTENT : " + URLDecoder.decode(new String(input.getContent()), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WD_Post.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!f.exists())
        {
            System.out.println("/////////////////// NOT FOUND : " + f.getName());
            return new HTTPMessage(404, "Not found");
        }
        else
            System.out.println("/////////////////// FOUND : " + f.getName());
           
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
                    content += "<directory>" + sf.getName() + "</directory>";
                else
                    content += "<file><name>" + sf.getName() + "</name><size>" + sf.getSize() + "</size><type>" + sf.getMimeType() + "</type></file>";
            content += "</resources>";

            msg.setContent(content);
        }
        
        return msg;
    }
    
}
