package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.Helper;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adrien
 */
public class WD_Move extends HTTPCommand
{
    public WD_Move()
    {
        super("move");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        HTTPMessage msg = new HTTPMessage(201, "Created");
        
        try
        {
            String dest = input.getHeader("destination");
            String host = input.getHeader("host");
            String shortDest = URLDecoder.decode(dest.substring(dest.indexOf(host) + host.length()), "UTF-8");

            File fsrc = new File(environment.getRoot() + input.getPath().replace("/", "\\").trim());
            File fdest = new File(environment.getRoot() + shortDest.replace("/", "\\").trim());
        
            fsrc.renameTo(fdest);
        }
        catch (Exception ex)
        { }
        
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
