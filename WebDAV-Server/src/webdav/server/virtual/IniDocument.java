package webdav.server.virtual;

import http.ExtendableByteBuffer;
import http.StringJoiner;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IniDocument
{
    public IniDocument(byte[] data) throws IOException
    {
        this.entries = new HashMap<>();
        
        String section = "default";

        for(String line : new ExtendableByteBuffer().write(data).toLines())
        {
            Matcher m = regexSection.matcher(line);
            if(m.matches())
            {
                section = m.group(1).trim().toLowerCase();
            }
            else
                if(section != null)
                {
                    m = regexKey.matcher(line);
                    if(m.matches())
                    {
                        String key = m.group(1).trim().toLowerCase();
                        String value = m.group(2).trim();

                        Map<String, String> kv = entries.get(section);
                        if(kv == null)
                            entries.put(section, kv = new HashMap<>());

                        kv.put(key, value);
                    }
                }
        }
    }
    private IniDocument(Map<String, Map<String, String>> entries)
    {
        this.entries = entries;
    }

    private final static Pattern regexSection = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
    private final static Pattern regexKey = Pattern.compile("\\s*([^=]*)=(.*)");

    private final Map<String, Map<String, String>> entries;
    
    
    public static Builder create()
    {
        return new Builder();
    }
    public static class Builder
    {
        public Builder()
        { }
        
        private Map<String, Map<String, String>> entries = new HashMap<>();
        
        public Builder setEntry(String key, Object value)
        {
            setEntry("default", key, value);
            return this;
        }
        public Builder setEntry(String section, String key, Object value)
        {
            if(section == null)
            {
                setEntry(key, value);
            }
            else
            {
                section = section.trim().toLowerCase();
                key = key.trim().toLowerCase();
                
                if(!entries.containsKey(section))
                    entries.put(section, new HashMap<>());

                entries.get(section).put(key, value.toString());
            }
            return this;
        }
        
        public IniDocument build()
        {
            return new IniDocument(entries);
        }
    }
    
    


    public String getString(String section, String key, String defaultvalue)
    {
         Map<String, String> kv = entries.get(section.trim().toLowerCase());
         if(kv == null)
            return defaultvalue;

         return kv.getOrDefault(key.trim().toLowerCase(), defaultvalue);
    }
    public int getInt(String section, String key, int defaultvalue)
    {
        return Integer.parseInt(getString(section, key, String.valueOf(defaultvalue)));
    }
    public double getDouble(String section, String key, double defaultvalue)
    {
        return Double.parseDouble(getString(section, key, String.valueOf(defaultvalue)));
    }
    public byte getByte(String section, String key, byte defaultvalue)
    {
        return Byte.parseByte(getString(section, key, String.valueOf(defaultvalue)));
    }

    @Override
    public String toString()
    {
        return entries.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e ->
                        e.getValue()
                                .entrySet()
                                .stream()
                                .map(kv -> kv.getKey() + "=" + kv.getValue())
                                .reduce("", StringJoiner.join("\r\n"))
                ))
                .entrySet()
                .stream()
                .map(e -> "[" + e.getKey() + "]\r\n" + e.getValue())
                .reduce("", StringJoiner.join("\r\n\r\n"));
    }
    public byte[] toBytes()
    {
        return toString().getBytes();
    }
}
