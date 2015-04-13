package webdav.server;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;

public class Helper 
{
    private Helper()
    { }
    
    public static String toString(Date date)
    {
        return new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss", Locale.ENGLISH).format(date).replace(".", "") + " GMT";
    }
    
    public static String toUTF8(String str)
    {
        try
        {
            return URLEncoder.encode(str, "UTF-8").replace("%2F", "/").replace("+", " ");
        }
        catch (UnsupportedEncodingException ex)
        {
            return str;
        }
    }
    
    public static String toBase10(String base64)
    {
        return new String(DatatypeConverter.parseBase64Binary(base64.trim().substring("Basic ".length()).trim()));
    }
    
    public static String toHex(byte[] data)
    {
        BigInteger bigInt = new BigInteger(1, data);
        String value = bigInt.toString(16);
        
        while(value.length() < 32)
            value = "0" + value;
        
        return value;
    }
}
