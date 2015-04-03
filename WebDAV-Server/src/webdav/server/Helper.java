package webdav.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import sun.misc.BASE64Decoder;

/**
 *
 * @author Adrien
 */
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
