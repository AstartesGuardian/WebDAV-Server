package webdav.server.localCrypted;

import http.server.HTTPAuthentication;
import webdav.server.Helper;
import webdav.server.IResource;
import webdav.server.IResourceManager;

public class LocalCryptedResourceManager implements IResourceManager
{
    @Override
    public IResource createFromPath(String path)
    {
        try
        {
            if(user == null)
                return new LocalCryptedResource(path, null);
            
            ICrypter crypter = new CipherCrypter(algo);
            crypter.setKey(Helper.toHex(CipherCrypter.sha256(user.getUserName() + ":" + user.getPassword())));
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
    
    @Override
    public int getMaxBufferSize()
    {
        return 1048576;
    }
    
    @Override
    public int getStepBufferSize()
    {
        return 5000;
    }
}
