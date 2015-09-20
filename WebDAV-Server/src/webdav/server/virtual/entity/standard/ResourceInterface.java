package webdav.server.virtual.entity.standard;

import http.FileSystemPath;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javafx.util.Pair;
import webdav.server.virtual.IResourceMutation;
import webdav.server.resource.IResource;
import webdav.server.resource.Lock;
import webdav.server.resource.LockKind;
import webdav.server.resource.ResourceType;

public class ResourceInterface implements IResource
{
    public ResourceInterface(IResource resource)
    {
        this.resource = resource;
    }
    private IResource resource;
    
    protected boolean computeMutation(HTTPEnvRequest env, Function<IResourceMutation, IResourceMutation.MutationResult> mutationFunction)
    {
        IResourceMutation.MutationResult result = env.getMutations().stream()
                .map(mutationFunction::apply)
                .filter(IResourceMutation.MutationResult::computeMutation)
                .filter(r -> r.mutatedResource() != null)
                .findFirst()
                .orElse(IResourceMutation.MutationResult.NoMutation);
        
        if(result.computeMutation())
        {
            resource = result.mutatedResource();
            return result.skipOperation();
        }
        else
            return true;
    }

    @Override
    public boolean isVisible(HTTPEnvRequest env) throws UserRequiredException
    {
        return resource.isVisible(env);
    }

    @Override
    public ResourceType getResourceType(HTTPEnvRequest env) throws UserRequiredException
    {
        return resource.getResourceType(env);
    }

    @Override
    public String getWebName(HTTPEnvRequest env) throws UserRequiredException
    {
        return resource.getWebName(env);
    }

    @Override
    public String getMimeType(HTTPEnvRequest env) throws UserRequiredException
    {
        return resource.getMimeType(env);
    }

    @Override
    public long getSize(HTTPEnvRequest env) throws UserRequiredException
    {
        return resource.getSize(env);
    }

    @Override
    public Instant getCreationTime(HTTPEnvRequest env) throws UserRequiredException
    {
        return resource.getCreationTime(env);
    }

    @Override
    public Instant getLastModified(HTTPEnvRequest env) throws UserRequiredException
    {
        return resource.getLastModified(env);
    }

    @Override
    public Collection<IResource> listResources(HTTPEnvRequest env) throws UserRequiredException
    {
        return resource.listResources(env);
    }

    @Override
    public byte[] getContent(HTTPEnvRequest env) throws UserRequiredException
    {
        return resource.getContent(env);
    }

    @Override
    public void setContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException
    {
        if(computeMutation(env, m -> m.setContentInvolvesMutation(env, resource, content)))
            resource.setContent(content, env);
    }

    @Override
    public void appendContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException
    {
        if(computeMutation(env, m -> m.appendContentInvolvesMutation(env, resource, content)))
            resource.appendContent(content, env);
    }

    @Override
    public boolean delete(HTTPEnvRequest env) throws UserRequiredException
    {
        if(!computeMutation(env, m -> m.deleteInvolvesMutation(env, resource)))
            return true;
        
        if(resource.delete(env))
        {
            resource = RsDead.getInstance();
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean moveTo(FileSystemPath oldPath, FileSystemPath newPath, HTTPEnvRequest env) throws UserRequiredException
    {
        if(oldPath.getParent().equals(newPath.getParent()))
        {
            if(!computeMutation(env, m -> m.renameInvolvesMutation(env, resource, newPath.getName())))
                return true;
            
            return rename(newPath.getName(), env);
        }
        else
        {
            if(!computeMutation(env, m -> m.moveInvolvesMutation(env, resource, newPath)))
                return true;
            
            return resource.moveTo(oldPath, newPath, env);
        }
    }

    @Override
    public boolean rename(String newName, HTTPEnvRequest env) throws UserRequiredException
    {
        if(!computeMutation(env, m -> m.renameInvolvesMutation(env, resource, newName)))
            return true;
        
        return resource.rename(newName, env);
    }

    @Override
    public boolean exists(HTTPEnvRequest env) throws UserRequiredException
    {
        return resource.exists(env);
    }

    @Override
    public IResource creates(ResourceType resourceType, HTTPEnvRequest env) throws UserRequiredException
    {
        if(computeMutation(env, m -> m.createInvolvesMutation(env, resource, resourceType)))
            resource = resource.creates(resourceType, env);
        
        return resource;
    }

    @Override
    public IResource creates(IResource resource, HTTPEnvRequest env) throws UserRequiredException
    {
        return this.resource.creates(resource, env);
    }
    
    @Override
    public boolean isOnTheSameFileSystemWith(IResource resource)
    {
        return this.resource.isOnTheSameFileSystemWith(resource);
    }

    @Override
    public IResource getResource(LinkedList<FileSystemPath> reversedPath, HTTPEnvRequest env) throws UserRequiredException
    {
        IResource rs = resource.getResource(reversedPath, env);
        if(rs == resource)
            return this;
        
        if(!(rs instanceof ResourceInterface))
            return new ResourceInterface(resource);
        else
            return rs;
    }

    @Override
    public void setProperty(String namespace, String name, String value, HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("property::set");
        
        computeMutation(env, m -> m.setPropertyInvolvesMutation(env, resource, namespace, name, value));
        
        resource.setProperty(namespace, name, value, env);
    }

    @Override
    public void removeProperty(String name, HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("property::remove");
        
        computeMutation(env, m -> m.removePropertyInvolvesMutation(env, resource, name));
        
        resource.removeProperty(name, env);
    }

    @Override
    public void removeProperty(String namespace, String name, HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("property::remove");
        
        computeMutation(env, m -> m.removePropertyInvolvesMutation(env, resource, namespace, name));
        
        resource.removeProperty(namespace, name, env);
    }

    @Override
    public String getProperty(String name, HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("property::get");
        
        return resource.getProperty(name, env);
    }

    @Override
    public String getProperty(String namespace, String name, HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("property::get");
        
        return resource.getProperty(namespace, name, env);
    }

    @Override
    public Map<Pair<String, String>, String> getProperties(HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("property::get");
        
        return resource.getProperties(env);
    }

    @Override
    public Collection<LockKind> getAvailableLocks() throws UserRequiredException
    {
        return resource.getAvailableLocks();
    }

    @Override
    public List<Lock> getLocks(HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("lock::get");
        
        return resource.getLocks(env);
    }

    @Override
    public List<Lock> getLocks(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("lock::get");
        
        return resource.getLocks(lockKind, env);
    }

    @Override
    public boolean setLock(Lock lock, HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("lock::set");
        
        return resource.setLock(lock, env);
    }

    @Override
    public boolean setLock(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("lock::set");
        
        return resource.setLock(lockKind, env);
    }

    @Override
    public void removeLock(Lock lock, HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("lock::remove");
        
        resource.removeLock(lock, env);
    }

    @Override
    public void removeLock(String uuid, HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("lock::remove");
        
        resource.removeLock(uuid, env);
    }

    @Override
    public boolean canLock(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        env.getUser().checkRight("lock::check");
        
        return resource.canLock(lockKind, env);
    }

    @Override
    public boolean equals(FileSystemPath path, HTTPEnvRequest env)
    {
        return resource.equals(path, env);
    }

    @Override
    public boolean addChild(IResource resource, HTTPEnvRequest env)
    {
        if(resource instanceof ResourceInterface)
            return this.resource.addChild(resource, env);
        else
            return this.resource.addChild(new ResourceInterface(resource), env);
    }

    @Override
    public boolean removeChild(IResource resource, HTTPEnvRequest env)
    {
        return this.resource.removeChild(resource, env);
    }

    @Override
    public boolean isInstanceOf(Class<?> c)
    {
        return c.isInstance(resource);
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof ResourceInterface)
            return o.equals(this.resource);
        if(o instanceof IResource)
            return o == this.resource;
        return false;
    }
    
    public <T> T cast(Class<T> c)
    {
        return (T)resource;
    }
}
