package webdav.server.localCrypted;

import http.server.HTTPAuthentication;
import java.nio.file.Files;
import java.util.stream.Stream;
import webdav.server.IResource;
import webdav.server.standard.StandardResource;

public class LocalCryptedResource extends StandardResource
{
    public LocalCryptedResource(String path, ICrypter crypter)
    {
        super(path);
        
        this.crypter = crypter;
    }
    
    private ICrypter crypter = null;
    protected ICrypter getCrypter()
    {
        return crypter;
    }
    
    @Override
    public long getSize()
    {
        return getContent().length;
    }
    
    @Override
    public IResource[] listResources()
    {
        return Stream.of(file.listFiles())
                .map(f -> new LocalCryptedResource(f.getPath(), getCrypter()))
                .toArray(IResource[]::new);
    }

    @Override
    public byte[] getContent()
    {
        try
        {
            return getCrypter().decrypt(Files.readAllBytes(file.toPath()));
        }
        catch (Exception ex)
        {
            return new byte[0];
        }
    }

    @Override
    public void setContent(byte[] content)
    {
        try
        {
            Files.write(file.toPath(), getCrypter().encrypt(content));
        }
        catch (Exception ex)
        {
            ex.printStackTrace(); }
    }
    
    @Override
    public boolean needsAuthentification(HTTPAuthentication user)
    {
        if(user == null)
            return true;
        
        return false;
    }
}
