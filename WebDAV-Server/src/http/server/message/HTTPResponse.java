package http.server.message;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class HTTPResponse extends HTTPMessage
{
    public HTTPResponse(
            int code,
            String message,
            String httpVersion,
            Map<String, String> headers,
            byte[] content)
    {
        super(httpVersion, headers, content);
        this.code = code;
        this.message = message;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Builder">
    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private int code = 207;
        private String httpVersion = "HTTP/1.1";
        private String message = "Multi-Status";
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
        
        public Builder setCode(String code)
        {
            this.code = Integer.parseInt(code);
            return this;
        }
        public Builder setCode(int code)
        {
            this.code = code;
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
        
        public Builder setMessage(String message)
        {
            this.message = message.trim();
            return this;
        }
        
        
        public HTTPResponse build()
        {
            if(content == null)
                content = new byte[0];
            
            setHeader("Content-Length", String.valueOf(content.length));
            
            return new HTTPResponse(
                    code,
                    message,
                    httpVersion,
                    headers,
                    content);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Parser">
    public static HTTPResponse parseHTTPResponse(byte[] byteMessage)
    {
        final Builder builder = create();
        
        parseHTTPMessage(byteMessage,
                headLine -> 
                {
                    builder.setHTTPVersion(headLine[0]);
                    builder.setCode(headLine[1]);
                    builder.setMessage(headLine[2]);
                },
                builder::setHeader,
                builder::setContent);
        
        return builder.build();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Properties">
    private final int code;
    private final String message;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Accessors">
    public int getCode()
    {
        return code;
    }
    public String getMessage()
    {
        return message;
    }
    // </editor-fold>
    
    @Override
    protected String getHeadLine()
    {
        return httpVersion + " " + code + " " + message;
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Code manager">
    public <T extends Throwable> HTTPResponse throwCode(int code, Supplier<T> supplier) throws T
    {
        if(this.code == code)
            throw supplier.get();
        return this;
    }
    public <T extends Throwable> HTTPResponse throwCodes(int[] codes, Supplier<T> supplier) throws T
    {
        for(int code : codes)
            if(this.code == code)
                throw supplier.get();
        return this;
    }
    // </editor-fold>
}
