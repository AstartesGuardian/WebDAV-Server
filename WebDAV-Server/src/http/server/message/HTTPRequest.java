package http.server.message;

import http.FileSystemPath;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTTPRequest extends HTTPMessage
{
    public HTTPRequest(
            String command,
            String httpVersion,
            String path,
            Map<String, String> headers,
            byte[] content)
    {
        super(httpVersion, headers, content);
        this.command = command;
        this.path = path;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Builder">
    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String command = "GET";
        private String httpVersion = "HTTP/1.1";
        private String path = "/";
        private Map<String, String> headers = new HashMap<>();
        private byte[] content = null;
        
        public Builder setHTTPVersion(double version)
        {
            this.httpVersion = "HTTP/" + version;
            return this;
        }
        public Builder setHTTPVersion(String version)
        {
            version = version.trim();
            
            if(!version.toUpperCase().startsWith("HTTP/"))
                version = "HTTP/" + version;
            
            this.httpVersion = version;
            return this;
        }
        
        public Builder setPath(String path)
        {
            try
            {
                this.path = URLEncoder.encode(path.trim().replace("\\", "/"), "UTF-8").replace("%2F", "/").replace("+", "%20").replace("%25", "%");
            }
            catch (UnsupportedEncodingException ex)
            {
                this.path = path.trim();
            }
            return this;
        }
        public Builder setPath(FileSystemPath path)
        {
            setPath(path.toString());
            return this;
        }
        
        public Builder setContent(byte[] content)
        {
            this.content = content;
            return this;
        }
        public Builder setContent(String content)
        {
            return setContent(content, "UTF-8");
        }
        public Builder setContent(String content, String charset)
        {
            try
            {
                this.content = content.getBytes(charset);
            }
            catch(UnsupportedEncodingException ex)
            { }
            
            return this;
        }
        
        public Builder setHeaders(Map<String, String> headers)
        {
            this.headers.clear();
            addHeaders(headers);
            
            return this;
        }
        public Builder addHeaders(Map<String, String> headers)
        {
            headers.entrySet()
                    .stream()
                    .forEach(e -> this.headers.put(formatHeaderName(e.getKey()), e.getValue()));
            
            return this;
        }
        public Builder setHeader(String name, String value)
        {
            this.headers.put(formatHeaderName(name), value);
            return this;
        }
        
        public Builder setCommand(String command)
        {
            this.command = command.trim().toUpperCase();
            return this;
        }
        
        
        public HTTPRequest build()
        {
            if(content == null)
                content = new byte[0];
            
            setHeader("Content-Length", String.valueOf(content.length));
            
            return new HTTPRequest(
                    command,
                    httpVersion,
                    path,
                    headers,
                    content);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Parser">
    public static HTTPRequest parseHTTPRequest(byte[] byteMessage)
    {
        final Builder builder = HTTPRequest.create();
        
        parseHTTPMessage(byteMessage,
                headLine -> 
                {
                    builder.setCommand(headLine[0]);
                    builder.setPath(headLine[1]);
                    builder.setHTTPVersion(headLine[2]);
                },
                builder::setHeader,
                builder::setContent);
        
        return builder.build();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Properties">
    private final String command;
    private final String path;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Accessors">
    public String getCommand()
    {
        return command;
    }
    public String getPath()
    {
        return path;
    }
    public String getDecodedPath()
    {
        try
        {
            return URLDecoder.decode(path, "UTF-8").trim();
        }
        catch (UnsupportedEncodingException ex)
        {
            return path;
        }
    }
    // </editor-fold>
    
    @Override
    protected String getHeadLine()
    {
        return command + " " + path + " " + httpVersion;
    }
}
