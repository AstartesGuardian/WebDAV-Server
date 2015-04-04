package webdav.server.localCrypted;

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
    
    protected final ICrypter crypter;
    
    @Override
    public long getSize()
    {
        return getContent().length;
    }
    
    @Override
    public IResource[] listResources()
    {
        return Stream.of(file.listFiles())
                .map(f -> new LocalCryptedResource(f.getPath(), this.crypter))
                .toArray(IResource[]::new);
    }

    @Override
    public byte[] getContent()
    {
        try
        {
            return this.crypter.decrypt(Files.readAllBytes(file.toPath()));
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
            Files.write(file.toPath(), this.crypter.encrypt(content));
        }
        catch (Exception ex)
        { }
    }
}
