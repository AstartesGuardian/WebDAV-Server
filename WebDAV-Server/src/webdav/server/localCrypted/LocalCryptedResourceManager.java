package webdav.server.localCrypted;

import webdav.server.IResource;
import webdav.server.IResourceManager;

public class LocalCryptedResourceManager implements IResourceManager
{
    @Override
    public IResource createFromPath(String path)
    {
        return new LocalCryptedResource(path, crypter);
    }
    
    private static ICrypter crypter;
    public static void loadCipherCrypter(ICrypter.Algorithm algo)
    {
        try
        {
            crypter = new CipherCrypter(algo);
        }
        catch (Exception ex)
        {
            crypter = null;
        }
    }
    
    public static void setKey(String key)
    {
        crypter.setKey(key);
    }
}
