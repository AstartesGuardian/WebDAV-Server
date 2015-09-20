package webdav.server.virtual.entity.local;

import http.server.exceptions.UnexpectedException;
import http.server.exceptions.UserRequiredException;
import http.server.exceptions.WrongResourceTypeException;
import http.server.message.HTTPEnvRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import webdav.server.resource.IResource;
import webdav.server.resource.ResourceType;
import webdav.server.virtual.contentmutation.IContentMutation;
import webdav.server.virtual.contentmutation.NoContentMutation;

public class RsLocalFile extends RsLocal
{
    public RsLocalFile(String name, HTTPEnvRequest env)
    {
        super(name);
        
        this.mimeType = null;
        this.size = 0;
        this.lastModified = Instant.now();
        this.uid = env.getSettings()
                .getFileManager()
                .generateUID();
    }
    public RsLocalFile(IResource resource, HTTPEnvRequest env)
    {
        super(resource, env);
        
        this.mimeType = resource.getMimeType(env);
        
        if(resource instanceof RsLocalFile)
        {
            RsLocalFile file = (RsLocalFile)resource;
            this.uid = file.uid;
            this.size = file.size;
        }
        else
        {
            this.uid = env.getSettings()
                    .getFileManager()
                    .generateUID();
            
            setContent(resource.getContent(env), env);
        }
        
        this.lastModified = resource.getLastModified(env);
    }
    
    private String mimeType;
    private long size;
    private Instant lastModified;
    private BigInteger uid;
    private transient File physicalFile = null;
    
            
    protected File getPhysicalFile(HTTPEnvRequest env)
    {
        if(physicalFile == null)
            physicalFile = new File(env.getSettings()
                    .getFileManager()
                    .getProperty("local::path")
                    .toString()
                    + env.getSettings().getStandardFileSeparator()
                    + uid
            );
        return physicalFile;
    }
    
    
    @Override
    public ResourceType getResourceType(HTTPEnvRequest env) throws UserRequiredException
    {
        return ResourceType.File;
    }
    
    @Override
    public String getMimeType(HTTPEnvRequest env) throws UserRequiredException
    {
        if(mimeType != null)
            return mimeType;
        
        try
        {
            mimeType = Files.probeContentType(getPhysicalFile(env).toPath());
        }
        catch (IOException | SecurityException ex)
        {
            mimeType = "Unknown";
        }
        
        return mimeType;
    }

    @Override
    public long getSize(HTTPEnvRequest env) throws UserRequiredException
    {
        return size;
    }

    @Override
    public Instant getLastModified(HTTPEnvRequest env) throws UserRequiredException
    {
        return lastModified;
    }

    @Override
    public Collection<IResource> listResources(HTTPEnvRequest env) throws UserRequiredException
    {
        return Collections.EMPTY_LIST;
    }

    @Override
    public byte[] getContent(HTTPEnvRequest env) throws UserRequiredException
    {
        try
        {
            File file = getPhysicalFile(env);
            byte[] content;
            
            if(file.exists())
                content = Files.readAllBytes(file.toPath());
            else
                content = new byte[0];
            
            return env.getSettings()
                    .getFileManager()
                    .getProperty(
                            IContentMutation.class,
                            "local::content::mutation",
                            NoContentMutation.getInstance())
                    .reverse(content, this, env);
        }
        catch (IOException ex)
        {
            throw new UnexpectedException(ex);
        }
    }
    
    private long writeContent(byte[] content, HTTPEnvRequest env, OpenOption... openOptions)
    {
        content = env.getSettings()
                .getFileManager()
                .getProperty(
                        IContentMutation.class,
                        "local::content::mutation",
                        NoContentMutation.getInstance())
                .transform(content, this, env);
        
        try
        {
            Files.write(getPhysicalFile(env).toPath(), content, openOptions);
        }
        catch (IOException ex)
        {
            throw new UnexpectedException(ex);
        }
        
        this.lastModified = Instant.now();
        return content.length;
    }

    @Override
    public void setContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException
    {
        writeContent(content, env);
        this.size += content.length;
    }

    @Override
    public void appendContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException
    {
        writeContent(content, env, StandardOpenOption.APPEND);
        this.size += content.length;
    }

    @Override
    public boolean delete(HTTPEnvRequest env) throws UserRequiredException
    {
        return getPhysicalFile(env).delete();
    }

    @Override
    public boolean addChild(IResource resource, HTTPEnvRequest env)
    {
        throw new WrongResourceTypeException();
    }

    @Override
    public boolean removeChild(IResource resource, HTTPEnvRequest env)
    {
        throw new WrongResourceTypeException();
    }
}
