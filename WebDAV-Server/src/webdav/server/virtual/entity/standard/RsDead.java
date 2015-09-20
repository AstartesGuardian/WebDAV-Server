package webdav.server.virtual.entity.standard;

import http.FileSystemPath;
import http.server.exceptions.DeadResourceException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import webdav.server.resource.IResource;
import webdav.server.resource.Lock;
import webdav.server.resource.LockKind;
import webdav.server.resource.ResourceType;

public class RsDead implements IResource
{
    private RsDead()
    { }
    
    private static RsDead deadResource = null;
    public static RsDead getInstance()
    {
        if(deadResource == null)
            deadResource = new RsDead();
        return deadResource;
    }

    @Override
    public boolean isVisible(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public ResourceType getResourceType(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public String getWebName(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public String getMimeType(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public long getSize(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public Instant getCreationTime(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public Instant getLastModified(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public Collection<IResource> listResources(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public byte[] getContent(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public void setContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public void appendContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public boolean delete(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public boolean moveTo(FileSystemPath oldPath, FileSystemPath newPath, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public boolean rename(String newName, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public boolean exists(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public IResource creates(ResourceType resourceType, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public IResource creates(IResource resource, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public boolean isOnTheSameFileSystemWith(IResource resource)
    {
        throw new DeadResourceException();
    }

    @Override
    public IResource getResource(LinkedList<FileSystemPath> reversedPath, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public boolean equals(FileSystemPath path, HTTPEnvRequest env)
    {
        throw new DeadResourceException();
    }

    @Override
    public boolean addChild(IResource resource, HTTPEnvRequest env)
    {
        throw new DeadResourceException();
    }

    @Override
    public boolean removeChild(IResource resource, HTTPEnvRequest env)
    {
        throw new DeadResourceException();
    }

    @Override
    public void setProperty(String namespace, String name, String value, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public void removeProperty(String name, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public void removeProperty(String namespace, String name, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public String getProperty(String name, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public String getProperty(String namespace, String name, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public Map<Pair<String, String>, String> getProperties(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public Collection<LockKind> getAvailableLocks() throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public List<Lock> getLocks(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public List<Lock> getLocks(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public boolean setLock(Lock lock, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public boolean setLock(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public void removeLock(Lock lock, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public void removeLock(String uuid, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }

    @Override
    public boolean canLock(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new DeadResourceException();
    }
    
    @Override
    public boolean isInstanceOf(Class<?> c)
    {
        return c.isInstance(this);
    }
}
