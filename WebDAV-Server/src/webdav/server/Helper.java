package webdav.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.xml.bind.DatatypeConverter;

public class Helper 
{
    private Helper()
    { }
    
    public static String toString(Date date)
    {
        return new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss", Locale.ENGLISH).format(date).replace(".", "") + " GMT";
    }
    
    public static String toBase10(String base64)
    {
        return new String(DatatypeConverter.parseBase64Binary(base64.trim().substring("Basic ".length()).trim()));
    }
}
