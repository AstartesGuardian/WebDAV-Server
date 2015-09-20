package webdav.server.virtual.entity.standard;

import http.FileSystemPath;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import webdav.server.resource.IResource;
import webdav.server.resource.Lock;
import webdav.server.resource.LockKind;
import webdav.server.resource.ResourceType;

public abstract class IRsGhost extends Rs
{
    public IRsGhost(FileSystemPath path)
    {
        this.path = path;
    }
    
    protected final FileSystemPath path;
    
    
    @Override
    public boolean isVisible(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }

    @Override
    public ResourceType getResourceType(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }

    @Override
    public String getWebName(HTTPEnvRequest env) throws UserRequiredException
    {
        return path.getName();
    }

    @Override
    public String getMimeType(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }

    @Override
    public long getSize(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }

    @Override
    public Instant getCreationTime(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }

    @Override
    public Instant getLastModified(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }

    @Override
    public Collection<IResource> listResources(HTTPEnvRequest env) throws UserRequiredException
    {
        return Collections.EMPTY_LIST;
    }

    @Override
    public byte[] getContent(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }

    @Override
    public void setContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }

    @Override
    public void appendContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }

    @Override
    public boolean delete(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }

    @Override
    public boolean moveTo(FileSystemPath oldPath, FileSystemPath newPath, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }

    @Override
    public boolean rename(String newName, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }

    @Override
    public boolean exists(HTTPEnvRequest env) throws UserRequiredException
    {
        return false;
    }
    
    @Override
    public boolean addChild(IResource resource, HTTPEnvRequest env)
    {
        throw new NotFoundException();
    }
    
    @Override
    public boolean removeChild(IResource resource, HTTPEnvRequest env)
    {
        throw new NotFoundException();
    }
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Locks">
    @Override
    public boolean canLock(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    @Override
    public void removeLock(String uuid, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    @Override
    public void removeLock(Lock lock, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    @Override
    public boolean setLock(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    @Override
    public boolean setLock(Lock lock, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    @Override
    public List<Lock> getLocks(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    @Override
    public List<Lock> getLocks(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    @Override
    public Collection<LockKind> getAvailableLocks() throws UserRequiredException
    {
        throw new NotFoundException();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Properties">
    @Override
    public void setProperty(String namespace, String name, String value, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    @Override
    public void removeProperty(String name, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    @Override
    public void removeProperty(String namespace, String name, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    @Override
    public String getProperty(String name, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    @Override
    public String getProperty(String namespace, String name, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    @Override
    public Map<Pair<String, String>, String> getProperties(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new NotFoundException();
    }
    // </editor-fold>
}
