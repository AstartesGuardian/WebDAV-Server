package webdav.server.commands;

import http.FileSystemPath;
import http.server.HTTPCommand;
import http.server.message.HTTPResponse;
import http.server.exceptions.UnexpectedException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;

public class WD_Move extends HTTPCommand
{
    public WD_Move()
    {
        super("move");
    }
    
    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws UserRequiredException, NotFoundException, UnexpectedException 
    {
        String dest = environment.getRequest().getHeader("destination");
        
        try
        {
            FileSystemPath source = environment.getPath();
            String host = environment.getRequest().getHeader("host");
            String localDest = URLDecoder.decode(dest.substring(dest.indexOf(host) + host.length()), "UTF-8");

            getResource(source, environment)
                    .moveTo(source
                            , environment.getSettings()
                                    .getFileSystemPathManager()
                                    .createFromString(localDest)
                            , environment);
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new UnexpectedException(ex);
        }
        
        return HTTPResponse.create()
                .setCode(201)
                .setMessage("Created")
                .setHeader("Location", dest)
                .setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
    }
    
}
