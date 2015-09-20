package webdav.server.resource;

import http.FileSystemPath;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public interface IResource
{
    /**
     * Get if the resource is visible.
     * 
     * @param env
     * @return boolean
     * @throws http.server.exceptions.UserRequiredException
     */
    public boolean isVisible(HTTPEnvRequest env) throws UserRequiredException;
    
    /**
     * Get the type of the resource.
     * 
     * @param env
     * @return ResourceType
     * @throws http.server.exceptions.UserRequiredException
     */
    public ResourceType getResourceType(HTTPEnvRequest env) throws UserRequiredException;
    
    /**
     * Get the web name of the resource.
     * 
     * @param env
     * @return String
     * @throws http.server.exceptions.UserRequiredException
     */
    public String getWebName(HTTPEnvRequest env) throws UserRequiredException;
    /**
     * Get the resource name.
     * 
     * @param env
     * @return String
     * @throws http.server.exceptions.UserRequiredException
     */
    //public FileSystemPath getPath(HTTPEnvRequest env) throws UserRequiredException;
    
    /**
     * Get the mime type of the resource.
     * 
     * @param env
     * @return String
     * @throws http.server.exceptions.UserRequiredException
     */
    public String getMimeType(HTTPEnvRequest env) throws UserRequiredException;
    /**
     * Get the size of the resource (byte).
     * 
     * @param env
     * @return long
     * @throws http.server.exceptions.UserRequiredException
     */
    public long getSize(HTTPEnvRequest env) throws UserRequiredException;
    /**
     * Get the creation time.
     * 
     * @param env
     * @return Instant
     * @throws http.server.exceptions.UserRequiredException
     */
    public Instant getCreationTime(HTTPEnvRequest env) throws UserRequiredException;
    /**
     * Get the last modified date.
     * 
     * @param env
     * @return Instant
     * @throws http.server.exceptions.UserRequiredException
     */
    public Instant getLastModified(HTTPEnvRequest env) throws UserRequiredException;
    /**
     * Get the list of the resources contained in this resource.
     * 
     * @param env
     * @return Collection of IResource
     * @throws http.server.exceptions.UserRequiredException
     */
    public Collection<IResource> listResources(HTTPEnvRequest env) throws UserRequiredException;
    
    /**
     * Get the content of the resource.
     * 
     * @param env
     * @return byte[]
     * @throws http.server.exceptions.UserRequiredException
     */
    public byte[] getContent(HTTPEnvRequest env) throws UserRequiredException;
    /**
     * Set the content of the resource.
     * 
     * @param content Content to put in the resource
     * @param env
     * @throws http.server.exceptions.UserRequiredException
     */
    public void setContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException;
    /**
     * Append a content to the resource.
     * 
     * @param content Content to append in the resource
     * @param env
     * @throws http.server.exceptions.UserRequiredException
     */
    public void appendContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException;
    
    /**
     * Delete the resource.
     * 
     * @param env
     * @return boolean
     * @throws http.server.exceptions.UserRequiredException
     */
    public boolean delete(HTTPEnvRequest env) throws UserRequiredException;
    /**
     * Rename or move a resource from the current path to 'resource' path.
     * 
     * @param newPath
     * @param env
     * @return boolean
     * @throws http.server.exceptions.UserRequiredException
     */
    public boolean moveTo(FileSystemPath oldPath, FileSystemPath newPath, HTTPEnvRequest env) throws UserRequiredException;
    
    public boolean rename(String newName, HTTPEnvRequest env) throws UserRequiredException;
    
    /**
     * Define if the resource exists or not.
     * 
     * @param env
     * @return boolean
     * @throws UserRequiredException 
     */
    public boolean exists(HTTPEnvRequest env) throws UserRequiredException;
    
    public IResource creates(ResourceType resourceType, HTTPEnvRequest env) throws UserRequiredException;
    public IResource creates(IResource resource, HTTPEnvRequest env) throws UserRequiredException;
    
    public boolean isOnTheSameFileSystemWith(IResource resource);
    
    public IResource getResource(LinkedList<FileSystemPath> reversedPath, HTTPEnvRequest env) throws UserRequiredException;
    
    
    public boolean equals(FileSystemPath path, HTTPEnvRequest env);
    
    
    public boolean addChild(IResource resource, HTTPEnvRequest env);
    public boolean removeChild(IResource resource, HTTPEnvRequest env);
    
    public boolean isInstanceOf(Class<?> c);
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Properties">
    public void setProperty(String namespace, String name, String value, HTTPEnvRequest env) throws UserRequiredException;
    public void removeProperty(String name, HTTPEnvRequest env) throws UserRequiredException;
    public void removeProperty(String namespace, String name, HTTPEnvRequest env) throws UserRequiredException;
    public String getProperty(String name, HTTPEnvRequest env) throws UserRequiredException;
    public String getProperty(String namespace, String name, HTTPEnvRequest env) throws UserRequiredException;
    public Map<Pair<String, String>, String> getProperties(HTTPEnvRequest env) throws UserRequiredException;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Locks">
    public Collection<LockKind> getAvailableLocks() throws UserRequiredException;
    public List<Lock> getLocks(HTTPEnvRequest env) throws UserRequiredException;
    public List<Lock> getLocks(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException;
    public boolean setLock(Lock lock, HTTPEnvRequest env) throws UserRequiredException;
    public boolean setLock(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException;
    public void removeLock(Lock lock, HTTPEnvRequest env) throws UserRequiredException;
    public void removeLock(String uuid, HTTPEnvRequest env) throws UserRequiredException;
    public boolean canLock(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException;
    // </editor-fold>
}
