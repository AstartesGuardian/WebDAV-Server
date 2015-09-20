package webdav.server.commands.light;

import http.FileSystemPath;
import http.server.HTTPCommand;
import http.server.message.HTTPResponse;
import http.server.exceptions.UnexpectedException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;

public class WDL_Rename extends HTTPCommand
{
    public WDL_Rename()
    {
        super("lightrename");
    }
    
    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws UserRequiredException, NotFoundException, UnexpectedException 
    {
        getResource(environment.getPath(), environment)
                .rename(environment.getRequest().getHeader("Resource-Name")
                        , environment);
        
        return HTTPResponse.create()
                .setCode(201)
                .setMessage("Renamed")
                .setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
    }
    
}
