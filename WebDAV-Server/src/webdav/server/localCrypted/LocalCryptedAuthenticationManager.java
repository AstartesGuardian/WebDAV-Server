package webdav.server.localCrypted;

import http.server.HTTPAuthentication;
import http.server.HTTPAuthenticationManager;

public class LocalCryptedAuthenticationManager extends HTTPAuthenticationManager
{
    public LocalCryptedAuthenticationManager(String realm)
    {
        super(realm);
    }

    @Override
    protected HTTPAuthentication getUser(String username)
    {
        if(username.trim().toLowerCase().equals("username"))
            return new HTTPAuthentication("username", "password");
        
        return null;
    }
}
