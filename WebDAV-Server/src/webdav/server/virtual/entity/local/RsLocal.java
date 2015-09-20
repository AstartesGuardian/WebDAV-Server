package webdav.server.virtual.entity.local;

import http.FileSystemPath;
import http.server.exceptions.AlreadyExistingException;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.exceptions.WrongResourceTypeException;
import http.server.message.HTTPEnvRequest;
import java.time.Instant;
import webdav.server.resource.IResource;
import webdav.server.resource.ResourceType;
import webdav.server.virtual.contentmutation.IContentMutation;
import webdav.server.virtual.entity.standard.IRsLocksProperties;

public abstract class RsLocal extends IRsLocksProperties
{
    public RsLocal(String name)
    {
        this.name = name;
        this.creationDate = Instant.now();
    }
    public RsLocal(IResource resource, HTTPEnvRequest env)
    {
        this.name = resource.getWebName(env);
        this.creationDate = resource.getCreationTime(env);
    }
    
    private String name;
    private final Instant creationDate;
    private boolean isVisible = true;
    
    
    
    public static PropertyBuilder createProperties()
    {
        return new PropertyBuilder();
    }
    public static class PropertyBuilder extends webdav.server.virtual.entity.PropertyBuilder
    {
        public PropertyBuilder()
        {
            super("local");
        }
        
        @Property(name="path")
        protected String path = "";
        
        @Property(name="content::mutation")
        protected IContentMutation contentMutation = null;
        
        public PropertyBuilder setContentMutation(IContentMutation contentMutation)
        {
            this.contentMutation = contentMutation;
            return this;
        }
        
        public PropertyBuilder setPath(String path)
        {
            this.path = path;
            return this;
        }
    }
    
    
    
    
    
    @Override
    public boolean isVisible(HTTPEnvRequest env) throws UserRequiredException
    {
        return isVisible;
    }
    public void setVisible(boolean isVisible)
    {
        this.isVisible = isVisible;
    }

    @Override
    public String getWebName(HTTPEnvRequest env) throws UserRequiredException
    {
        return name;
    }

    @Override
    public Instant getCreationTime(HTTPEnvRequest env) throws UserRequiredException
    {
        return creationDate;
    }

    @Override
    public boolean rename(String newName, HTTPEnvRequest env) throws UserRequiredException
    {
        this.name = newName;
        return true;
    }

    @Override
    public boolean exists(HTTPEnvRequest env) throws UserRequiredException
    {
        return true;
    }

    @Override
    public boolean isOnTheSameFileSystemWith(IResource resource)
    {
        return resource.isInstanceOf(RsLocal.class);
    }

    @Override
    protected IResource generateUndefinedResource(FileSystemPath path, HTTPEnvRequest env) throws UserRequiredException
    {
        return new RsLocalGhost(path);
    }

    @Override
    public IResource creates(ResourceType resourceType, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }

    @Override
    public IResource creates(IResource resource, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }
    
    @Override
    public boolean moveTo(FileSystemPath oldPath, FileSystemPath newPath, HTTPEnvRequest env) throws UserRequiredException
    {
        if(!this.exists(env))
            throw new NotFoundException();
        
        IResource oldParent = env.getSettings().getFileManager().getResourceFromPath(oldPath.getParent(), env);
        IResource newParent = env.getSettings().getFileManager().getResourceFromPath(newPath.getParent(), env);
        
        IResource dest = env.getSettings().getFileManager().getResourceFromPath(newPath, env);
        if(dest.exists(env))
        {
            if(env.getRequest().getHeader("overwrite").toLowerCase().equals("t"))
            { // overwrite
                newParent.removeChild(dest, env);
                dest.delete(env);
                dest = env.getSettings().getFileManager().getResourceFromPath(newPath, env);
            }
            else // no right to overwrite
                throw new AlreadyExistingException();
        }
        
        if((dest = dest.creates(this, env)) != null)
        {
            oldParent.removeChild(dest, env);
            newParent.addChild(dest, env);
            return true;
        }
        else
            return false;
    }
}
