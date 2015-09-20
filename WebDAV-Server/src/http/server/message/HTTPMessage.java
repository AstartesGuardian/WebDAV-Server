package http.server.message;

import http.ExtendableByteBuffer;
import http.StringJoiner;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class HTTPMessage
{
    public HTTPMessage(
            String httpVersion,
            Map<String, String> headers,
            byte[] content)
    {
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.content = content;
    }
    
    protected static String formatHeaderName(String name)
    {
        return name.trim().replace(' ', '-').toLowerCase();
    }
    protected static String formatHeaderRaw(String header, String value)
    {
        return header + ": " + value;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Parser Helper">
    private static int parserHelper_getHeaderSize(byte[] byteMessage)
    {
        int headerLen = 0;
        while(byteMessage[headerLen] != '\r'
                || byteMessage[headerLen + 1] != '\n'
                || byteMessage[headerLen + 2] != '\r'
                || byteMessage[headerLen + 3] != '\n')
        {
            headerLen++;
        }
        return headerLen;
    }
    private static String[] parserHelper_getHeadLineValues(String headLine)
    {
        int secondIndex = headLine.indexOf(" ");
        int thirdIndex = headLine.indexOf(" ", secondIndex + 1);
        
        return new String[]
        {
            headLine.substring(0, secondIndex),
            headLine.substring(secondIndex + 1, thirdIndex),
            headLine.substring(thirdIndex + 1)
        };
    }
    
    private static void parserHelper_parseHeaders(String messageWithoutHeadLine, BiConsumer<String, String> consumer)
    {
        Stream.of(messageWithoutHeadLine.split("\r\n"))
                .filter(s -> !s.isEmpty())
                .forEach(header ->
                {
                    final int sep = header.indexOf(':');
                    consumer.accept(header.substring(0, sep).trim(), header.substring(sep + 1).trim());
                });
    }
    private static void parserHelper_parseContent(byte[] byteMessage, int headerLen, Consumer<byte[]> consumer)
    {
        String separator = "\r\n\r\n";
        
        if(headerLen + separator.length() < byteMessage.length)
            consumer.accept(Arrays.copyOfRange(byteMessage, headerLen + separator.length(), byteMessage.length));
    }
    
    private static String parserHelper_getStringHeader(byte[] byteMessage, int headerLen)
    {
        try
        {
            return new String(byteMessage, 0, headerLen, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            return null;
        }
    }
    
    protected static void parseHTTPMessage(byte[] byteMessage, Consumer<String[]> headLineConsumer, BiConsumer<String, String> headerConsumer, Consumer<byte[]> contentConsumer)
    {
        if(byteMessage.length == 0)
            return;
        
        int headerLen = parserHelper_getHeaderSize(byteMessage);
        
        String message = parserHelper_getStringHeader(byteMessage, headerLen);
        
        int header_index = message.indexOf("\r\n");
        
        String[] head = parserHelper_getHeadLineValues(message.substring(0, header_index));
        headLineConsumer.accept(head);
        
        parserHelper_parseContent(byteMessage, headerLen, contentConsumer);
        parserHelper_parseHeaders(message.substring(header_index + 1), headerConsumer);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Properties">
    protected final String httpVersion;
    protected final Map<String, String> headers;
    protected final byte[] content;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Accessors">
    public String getHTTPVersion()
    {
        return httpVersion;
    }
    
    public Map<String, String> getHeaders()
    {
        return headers;
    }
    public String getHeader(String name)
    {
        return headers.get(formatHeaderName(name));
    }
    public String getHeader(String name, String defaultValue)
    {
        return headers.getOrDefault(formatHeaderName(name), defaultValue);
    }
    public boolean containsHeader(String name)
    {
        return headers.containsKey(formatHeaderName(name));
    }
    public String getHeadersString()
    {
        return headers.entrySet()
                .stream()
                .map(e -> formatHeaderRaw(e.getKey(), e.getValue()) + "\r\n")
                .reduce("", StringJoiner.join());
    }
    
    
    
    public byte[] getContent()
    {
        return content;
    }
    public String getContentString(String charset) throws UnsupportedEncodingException
    {
        return new String(content, charset);
    }
    public String getContentString() throws UnsupportedEncodingException
    {
        return getContentString("UTF-8");
    }
    public ByteArrayInputStream getContentStream()
    {
        return new ByteArrayInputStream(content);
    }
    public ExtendableByteBuffer getContentBuffer()
    {
        return new ExtendableByteBuffer().write(content);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Converters">
    public byte[] toBytes()
    {
        try
        {
            byte[] headers = (getHeadLine() + "\r\n" + getHeadersString() + "\r\n").getBytes("UTF-8");
            
            if(content.length > 0)
            {
                byte[] result = new byte[headers.length + content.length];

                System.arraycopy(headers, 0, result, 0, headers.length);
                System.arraycopy(content, 0, result, headers.length, content.length);
                return result;
            }
            else
                return headers;
        }
        catch (UnsupportedEncodingException ex)
        {
            return null;
        }
    }
    
    @Override
    public String toString()
    {
        String content = "";
        try
        {
            if(this.content.length > 0)
                content = getContentString();
        }
        catch (UnsupportedEncodingException ex)
        { }
        
        return getHeadLine() + "\r\n" + getHeadersString() + "\r\n" + content;
    }
    // </editor-fold>
    
    protected abstract String getHeadLine();
}
