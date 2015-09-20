package webdav.server.virtual.entity.remote.webdav;

import http.ExtendableByteBuffer;
import http.StringJoiner;
import http.server.exceptions.NotFoundException;
import http.server.message.HTTPRequest;
import http.server.message.HTTPResponse;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.stream.Stream;

public class RsWebDavHelper
{
    public static ExtendableByteBuffer sendRequest(byte[] ip, int port, HTTPRequest.Builder requestBuilder) throws IOException
    {
        Socket socket = new Socket(Inet4Address.getByAddress(ip), port);

        socket.getOutputStream()
                .write(requestBuilder
                        .build()
                        .toBytes());

        return HTTPResponse.parseHTTPResponse(
                new ExtendableByteBuffer()
                        .setInternalBufferSize(5000)
                        .write(socket.getInputStream())
                        .toBytes())
                .throwCode(404, NotFoundException::new)
                .getContentBuffer();
    }
    
    public static String getHost(byte[] ip, int port)
    {
        String result = null;
        
        for(byte b : ip)
            if(result == null)
                result = String.valueOf(b);
            else
                result += "." + b;
        
        return result + ":" + port;
    }
}
