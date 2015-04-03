package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author Adrien
 */
public class WD_Mkcol extends HTTPCommand
{
    public WD_Mkcol()
    {
        super("mkcol");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        HTTPMessage msg = new HTTPMessage(201, "Created");
        
        File f = new File(environment.getRoot() + input.getPath().replace("/", "\\").trim());
        try
        {
            f.mkdir();
        }
        catch (Exception ex)
        { }
        
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
