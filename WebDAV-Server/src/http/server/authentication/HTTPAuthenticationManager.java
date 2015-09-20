package http.server.authentication;

import http.server.message.HTTPMessage;
import http.server.message.HTTPRequest;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import webdav.server.tools.Helper;
import webdav.server.crypter.AbstractCrypter;

public abstract class HTTPAuthenticationManager implements Serializable
{
    private static final long serialVersionUID = -2L;
    
    public HTTPAuthenticationManager(String realm)
    {
        this.realm = realm;
    }
    
    private transient Random rnd = null;
    private final String realm;
    
    protected Random getRandom()
    {
        if(rnd == null)
            rnd = new Random();
        return rnd;
    }
    
    public String getRealm()
    {
        return realm;
    }
    
    protected abstract HTTPUser getDefaultUser();
    protected abstract HTTPUser getUser(String username);
    
    protected HTTPUser getByPassUser(HTTPMessage request)
    {
        try
        {
            if(!request.containsHeader("Authorization"))
                return getDefaultUser();
            
            String authHeader = request.getHeader("Authorization");
            authHeader = authHeader.substring(authHeader.indexOf(" ") + 1);
            
            StringTokenizer token = new StringTokenizer(authHeader, ",");
            Map<String, String> authValues = new HashMap<>();
            while(token.hasMoreTokens())
            {
                String str = token.nextToken();
                String[] sp = str.split("=");
                if(sp.length == 2)
                {
                    sp[1] = sp[1].trim();
                    if(sp[1].startsWith("\"") && sp[1].endsWith("\""))
                        sp[1] = sp[1].substring(1, sp[1].length() - 1);
                    
                    authValues.put(sp[0].trim().toLowerCase(), sp[1]);
                }
            }
            
            if(!authValues.containsKey("username") ||
                    !authValues.containsKey("response"))
                return getDefaultUser();
            
            
            String username = authValues.get("username");
            String password = authValues.get("response");
            
            HTTPUser user = new HTTPUser(username, password);
            user.giveAllRights(true);
            user.setManager(this);
            return user;
        }
        catch (Exception ex)
        { }
        
        return getDefaultUser();
    }
    
    public HTTPUser checkAuth(HTTPRequest request)
    {
        try
        {
            if(!request.containsHeader("Authorization"))
                return getDefaultUser();
            
            String authHeader = request.getHeader("Authorization");
            authHeader = authHeader.substring(authHeader.indexOf(" ") + 1);
            
            StringTokenizer token = new StringTokenizer(authHeader, ",");
            Map<String, String> authValues = new HashMap<>();
            while(token.hasMoreTokens())
            {
                String str = token.nextToken();
                String[] sp = str.split("=");
                if(sp.length == 2)
                {
                    sp[1] = sp[1].trim();
                    if(sp[1].startsWith("\"") && sp[1].endsWith("\""))
                        sp[1] = sp[1].substring(1, sp[1].length() - 1);
                    
                    authValues.put(sp[0].trim().toLowerCase(), sp[1]);
                }
            }
            
            if(!authValues.containsKey("username") ||
                    !authValues.containsKey("nonce") ||
                    !authValues.containsKey("nc") ||
                    !authValues.containsKey("cnonce") ||
                    !authValues.containsKey("qop") ||
                    !authValues.containsKey("response"))
                return getDefaultUser();
            
            
            String username = authValues.get("username");
            HTTPUser user = getUser(username);
            if(user == null)
                return getDefaultUser();
            
            String password = user.getPassword();
            
            String realm = this.getRealm();
            String uri = request.getPath();
            String method = request.getCommand().trim().toUpperCase();
            String nonce = authValues.get("nonce");
            String nonceCount = authValues.get("nc");
            String clientNonce = authValues.get("cnonce");
            String qop = authValues.get("qop");
            
            String ha1 = Helper.toHex(AbstractCrypter.md5(username + ":" + realm + ":" + password));
            String ha2 = Helper.toHex(AbstractCrypter.md5(method + ":" + uri));
            
            String receivedValue = authValues.get("response");
            String resultValue = Helper.toHex(AbstractCrypter.md5(ha1 + ":" + nonce + ":" + nonceCount + ":" + clientNonce + ":" + qop + ":" + ha2));
            
            if(resultValue.equals(receivedValue))
            {
                user.setManager(this);
                return user;
            }
        }
        catch (Exception ex)
        { }
        
        return getDefaultUser();
    }

    public String generateNonce()
    {
        try
        {
            byte[] data = new byte[50];
            getRandom().nextBytes(data);
            return Helper.toHex(AbstractCrypter.md5(data));
        }
        catch (NoSuchAlgorithmException ex)
        {
            return null;
        }
    }
}
