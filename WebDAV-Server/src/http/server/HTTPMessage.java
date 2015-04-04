package http.server;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class HTTPMessage
{
    // <editor-fold defaultstate="collapsed" desc="Constructor(s)">
    public HTTPMessage(int code, String message)
    {
        this.command = null;
        this.path = null;
        this.inetAddress = null;
        this.responseMessage = String.valueOf(code) + " " + message;
        headers = new HashMap<>();
        content = new byte[0];
        
        initialize();
    }
    public HTTPMessage(byte[] byteMessage, Socket clientSocket, Set<HTTPCommand> commands)
    {
        this.responseMessage = null;
        headers = new HashMap<>();
        this.inetAddress = clientSocket.getInetAddress();
        
        String message = null;
        try
        {
            message = new String(byteMessage, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        { }
        
        final String separator = "\r\n\r\n";
        final int header_content_separator_index = message.indexOf(separator);
        
        final int header_index = message.indexOf("\r\n");
        command = HTTPCommand.getFrom(commands, message.substring(0, message.indexOf(" ")));
        
        this.path = message.substring(message.indexOf(" "), message.indexOf(" ", message.indexOf(" ") + 1));
        message = message.substring(header_index + 1);
        
        if(header_content_separator_index + separator.length() < byteMessage.length)
            content = Arrays.copyOfRange(byteMessage, header_content_separator_index + separator.length(), byteMessage.length);
        else
            content = new byte[0];
        
        Stream.of(message.substring(0, header_content_separator_index - header_index).split("\r\n"))
                .forEach(header ->
                {
                    if(header != null && !header.isEmpty())
                    {
                        final int sep = header.indexOf(':');
                        setHeader(header.substring(0, sep), header.substring(sep + 1));
                    }
                });
    }
    
    private void initialize()
    {
        setHeader("Content-Length", String.valueOf(0));
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Properties">
    private final HTTPCommand command;
    private final String path;
    
    private final InetAddress inetAddress;
    private final String responseMessage;
    
    private final Map<String, String> headers;
    private byte[] content;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="From">
    public InetAddress from()
    {
        return this.inetAddress;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Path">
    public String getPath()
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
    
    // <editor-fold defaultstate="collapsed" desc="Formaters">
    private String formatHeaderName(String header)
    {
        return header.trim().replace(' ', '-').toLowerCase();
    }
    private String formatHeaderRaw(String header, String value)
    {
        return header + ": " + value;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Headers">
    public void setHeader(String header, String value)
    {
        headers.put(formatHeaderName(header), value);
    }
    public String getHeader(String header)
    {
        return headers.getOrDefault(formatHeaderName(header), "").trim();
    }
    public boolean containsHeader(String header)
    {
        return headers.containsKey(formatHeaderName(header));
    }
    public Set<String> getHeaders()
    {
        return headers.keySet();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Command">
    public HTTPCommand getCommand()
    {
        return command;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Content">
    public byte[] getContent()
    {
        return content;
    }
    public void setContent(String content)
    {
        try
        {
            this.content = content.getBytes("UTF-8");
            setHeader("Content-Length", String.valueOf(content.length()));
        }
        catch (UnsupportedEncodingException ex)
        { }
    }
    public void setContent(byte[] content)
    {
        this.content = content;
        setHeader("Content-Length", String.valueOf(content.length));
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Converters">
    public byte[] toBytes()
    {
        try
        {
            if(content.length > 0)
            {
                byte[] headers = getHeadersString().getBytes("UTF-8");

                byte[] result = new byte[headers.length + content.length];

                System.arraycopy(headers, 0, result, 0, headers.length);
                System.arraycopy(content, 0, result, headers.length, content.length);

                return result;
            }
            else
                return getHeadersString().getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            return null;
        }
    }

    private String getHeadersString()
    {
        StringBuilder str = new StringBuilder();
        
        if(responseMessage != null)
        {
            str.append("HTTP/1.1");
            str.append(" ");
            str.append(responseMessage);
            str.append("\r\n");
        }
        
        headers.forEach((k, v) ->
        {
            str.append(formatHeaderRaw(k, v));
            str.append("\r\n");
        });
        
        str.append("\r\n");
        
        return str.toString();
    }
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        
        str.append(getHeadersString());
        
        if(content.length > 0)
            str.append(new String(content));
        
        return str.toString();
    }
    // </editor-fold>
}
