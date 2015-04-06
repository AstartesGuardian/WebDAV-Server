package webdav.server;

import http.server.HTTPAuthentication;

public interface IResourceManager
{
    /**
     * Create a IResource instance from 'path' to be used in different commands 
     * of the server.
     * 
     * @param path Path of the resource
     * @return The generated resource
     */
    public IResource createFromPath(String path);
    
    /**
     * Define the user for the current session. Useful for managers using the
     * information of the users.
     * 
     * @param user User authenticated in the current session
     */
    public void setUser(HTTPAuthentication user);
}
