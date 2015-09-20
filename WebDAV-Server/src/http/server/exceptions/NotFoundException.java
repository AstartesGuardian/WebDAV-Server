package http.server.exceptions;

import http.server.message.HTTPResponse;

public class NotFoundException extends HTTPException
{
    public NotFoundException()
    {
        super("Resource not found");
    }
    
    public static HTTPResponse.Builder getHTTPResponse()
    {
        return HTTPResponse.create()
                .setCode(404)
                .setMessage("Not found");
    }
}
