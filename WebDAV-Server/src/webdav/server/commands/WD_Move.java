package webdav.server.commands;

import http.server.authentication.HTTPUser;
import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;

public class WD_Move extends HTTPCommand
{
    public WD_Move()
    {
        super("move");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) throws UserRequiredException, NotFoundException 
    {
        try
        {
            HTTPUser user = environment.getUser();
        
            String dest = input.getHeader("destination");
            String host = input.getHeader("host");
            String shortDest = URLDecoder.decode(dest.substring(dest.indexOf(host) + host.length()), "UTF-8");

            getResource(input.getPath(), environment)
                    .moveTo(getPath(shortDest, environment), user);
        }
        catch (UnsupportedEncodingException ex)
        { }
        
        HTTPMessage msg = new HTTPMessage(201, "Created");
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
