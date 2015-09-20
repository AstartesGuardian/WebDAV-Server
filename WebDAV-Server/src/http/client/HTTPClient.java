/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.client;

import http.ExtendableByteBuffer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HTTPClient
{
    public HTTPClient(URL url) throws IOException
    {
        this.url = url;
        this.parameters = new HashMap<>();
        
        initialize();
    }
    public HTTPClient(String url) throws IOException
    {
        this(new URL(url));
    }
    
    public static HTTPClient of(URL url) throws IOException
    {
        return new HTTPClient(url);
    }
    public static HTTPClient of(String url) throws IOException
    {
        return new HTTPClient(url);
    }
    
    protected final URL url;
    protected HttpURLConnection connection;
    
    protected final Map<String, Object> parameters;
    
    protected void initialize() throws IOException
    {
        connection = (HttpURLConnection)url.openConnection();
    }
    
    public HTTPClient setMethod(String method) throws ProtocolException
    {
        connection.setRequestMethod(method.trim().toUpperCase());
        
        connection.setInstanceFollowRedirects(true);
        connection.setChunkedStreamingMode(1500);
        
        return this;
    }
    
    public HTTPClient addParameter(String name, Object value)
    {
        parameters.put(name, value);
        
        return this;
    }
    public HTTPClient addParameters(String entry)
    {
        String[] entries = entry.split("&");
        for(String e : entries)
        {
            String[] splittedEntry = e.split("=");
            parameters.put(splittedEntry[0], (splittedEntry.length > 1 ? e.substring(splittedEntry[0].length() + 1) : ""));
        }
        
        return this;
    }
    
    protected static String encode(String value)
    {
        try
        {
            return URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            return value;
        }
    }
    protected void finalization() throws IOException
    {
        String params = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + encode(e.getValue().toString()))
                .reduce("", (r, s) -> (r.isEmpty() ? s : r + "&" + s));
        connection.addRequestProperty("Content-Length", "" + params.length());
        
        /*
        PrintWriter out = new PrintWriter(connection.getOutputStream());
        
        String params = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + encode(e.getValue().toString()))
                .reduce("", (r, s) -> (r.isEmpty() ? s : r + "&" + s));
        
        out.println(params);
        out.close();*/
    }
    
    public boolean failed()
    {
        return loaded && content == null;
    }
    
    protected boolean loaded = false;
    protected ExtendableByteBuffer content = null;
    public HTTPClient get() throws IOException
    {
        if(failed())
            throw new IOException();
        
        if(loaded)
            return this;
        
        loaded = true;
        
        connection.setDoOutput(true);
        
        finalization();
        
        content = new ExtendableByteBuffer()
                .setInternalBufferSize(5000)
                .write(connection.getInputStream());
        
        connection.disconnect();
        
        return this;
    }
    
    @Override
    public String toString()
    {
        try
        {
            get();
            return content.toString();
        }
        catch(IOException ex)
        {
            return "{error}";
        }
    }
    public String[] toLines()
    {
        return getLines(toString());
    }
    public byte[] toBytes() throws IOException
    {
        get();
        return content.toBytes();
    }
    public char[] toChars() throws IOException
    {
        get();
        return content.toChars();
    }
    
    
    protected String[] getLines(String value)
    {
        while(value.contains("\r\n"))
            value = value.replace("\r\n", "\n");
        
        return value.replace("\r", "\n").split("\n");
    }
    
    
    public String[] orDefault(String[] defaultValue)
    {
        String result = orDefault((String)null);
        
        if(result == null)
            return defaultValue;
        else
            return getLines(result);
    }
    public String orDefault(String defaultValue)
    {
        try
        {
            get();
            return content.toString();
        }
        catch(IOException ex)
        {
            return defaultValue;
        }
    }
    public byte[] orDefault(byte[] defaultValue)
    {
        try
        {
            get();
            return content.toBytes();
        }
        catch(IOException ex)
        {
            return defaultValue;
        }
    }
    public char[] orDefault(char[] defaultValue)
    {
        try
        {
            get();
            return content.toChars();
        }
        catch(IOException ex)
        {
            return defaultValue;
        }
    }
}
