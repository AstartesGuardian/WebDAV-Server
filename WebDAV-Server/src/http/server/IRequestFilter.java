package http.server;

import http.server.message.HTTPEnvRequest;
import http.server.message.HTTPResponse;

public interface IRequestFilter
{
    public HTTPResponse.Builder filter(HTTPEnvRequest input);
}
