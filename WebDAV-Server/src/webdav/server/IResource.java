package webdav.server;

import http.server.authentication.HTTPUser;
import http.server.exceptions.UserRequiredException;
import java.time.Instant;

public interface IResource
{
    /**
     * Get if the resource is visible.
     * 
     * @param user
     * @return boolean
     * @throws http.server.exceptions.UserRequiredException
     */
    public boolean isVisible(HTTPUser user) throws UserRequiredException;
    
    /**
     * Get the type of the resource.
     * 
     * @param user
     * @return ResourceType
     * @throws http.server.exceptions.UserRequiredException
     */
    public ResourceType getResourceType(HTTPUser user) throws UserRequiredException;
    
    /**
     * Get the web name of the resource.
     * 
     * @param user
     * @return String
     * @throws http.server.exceptions.UserRequiredException
     */
    public String getWebName(HTTPUser user) throws UserRequiredException;
    /**
     * Get the resource name.
     * 
     * @param user
     * @return String
     * @throws http.server.exceptions.UserRequiredException
     */
    public String getPath(HTTPUser user) throws UserRequiredException;
    /**
     * Get the mime type of the resource.
     * 
     * @param user
     * @return String
     * @throws http.server.exceptions.UserRequiredException
     */
    public String getMimeType(HTTPUser user) throws UserRequiredException;
    /**
     * Get the size of the resource (byte).
     * 
     * @param user
     * @return long
     * @throws http.server.exceptions.UserRequiredException
     */
    public long getSize(HTTPUser user) throws UserRequiredException;
    /**
     * Get the creation time.
     * 
     * @param user
     * @return Instant
     * @throws http.server.exceptions.UserRequiredException
     */
    public Instant getCreationTime(HTTPUser user) throws UserRequiredException;
    /**
     * Get the last modified date.
     * 
     * @param user
     * @return Instant
     * @throws http.server.exceptions.UserRequiredException
     */
    public Instant getLastModified(HTTPUser user) throws UserRequiredException;
    /**
     * Get the list of the resources contained in this resource.
     * 
     * @param user
     * @return IResource[]
     * @throws http.server.exceptions.UserRequiredException
     */
    public IResource[] listResources(HTTPUser user) throws UserRequiredException;
    
    /**
     * Get the content of the resource.
     * 
     * @param user
     * @return byte[]
     * @throws http.server.exceptions.UserRequiredException
     */
    public byte[] getContent(HTTPUser user) throws UserRequiredException;
    /**
     * Set the content of the resource.
     * 
     * @param content Content to put in the resource
     * @param user
     * @throws http.server.exceptions.UserRequiredException
     */
    public void setContent(byte[] content, HTTPUser user) throws UserRequiredException;
    /**
     * Append a content to the resource.
     * 
     * @param content Content to append in the resource
     * @param user
     * @throws http.server.exceptions.UserRequiredException
     */
    public void appendContent(byte[] content, HTTPUser user) throws UserRequiredException;
    
    /**
     * Delete the resource.
     * 
     * @param user
     * @return boolean
     * @throws http.server.exceptions.UserRequiredException
     */
    public boolean delete(HTTPUser user) throws UserRequiredException;
    /**
     * Rename or move a resource from the current path to 'resource' path.
     * 
     * @param newPath
     * @param user
     * @return boolean
     * @throws http.server.exceptions.UserRequiredException
     */
    public boolean moveTo(String newPath, HTTPUser user) throws UserRequiredException;
}
