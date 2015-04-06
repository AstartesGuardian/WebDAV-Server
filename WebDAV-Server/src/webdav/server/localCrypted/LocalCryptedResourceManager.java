package webdav.server.localCrypted;

import http.server.HTTPAuthentication;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;
import webdav.server.IResource;
import webdav.server.IResourceManager;

public class LocalCryptedResourceManager implements IResourceManager
{
    public LocalCryptedResourceManager()
    {
        
    }
    
    @Override
    public IResource createFromPath(String path)
    {
        try
        {
            ICrypter crypter = new CipherCrypter(algo);
            crypter.setKey(user.getPassword());
            return new LocalCryptedResource(path, crypter);
        }
        catch (Exception ex)
        {
            return null;
        }
    }
    
    private static ICrypter.Algorithm algo;
    public static void setAlgorithm(ICrypter.Algorithm algo)
    {
        LocalCryptedResourceManager.algo = algo;
    }
    
    private HTTPAuthentication user;
    @Override
    public void setUser(HTTPAuthentication user)
    {
        this.user = user;
    }
}
