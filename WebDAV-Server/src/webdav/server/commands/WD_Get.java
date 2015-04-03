package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.Helper;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.BASE64Decoder;

/**
 *
 * @author Adrien
 */
public class WD_Get extends HTTPCommand
{
    public WD_Get()
    {
        super("get");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        HTTPMessage msg;
        
        System.out.println(input.from());
        
        msg = new HTTPMessage(200, "OK");
        
        File f = new File(environment.getRoot() + input.getPath().replace("/", "\\").trim());
        try
        {
            if(f.isFile())
            {
                msg.setContent(Files.readAllBytes(f.toPath()));
                msg.setHeader("Content-Type", Files.probeContentType(f.toPath())/*"text/xml; charset=\"utf-8\""*/);
            }
            else if(f.isDirectory())
            {
                String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><resources>";
                
                for(File sf : f.listFiles())
                    if(sf.isDirectory())
                        content += "<directory>" + sf.getName() + "</directory>";
                    else
                        content += "<file><name>" + sf.getName() + "</name><size>" + Files.size(sf.toPath()) + "</size><type>" + Files.probeContentType(sf.toPath()) + "</type></file>";
                content += "</resources>";
                
                msg.setContent(content);
            }
        }
        catch (IOException ex)
        { }
        
        return msg;
    }
    
}
