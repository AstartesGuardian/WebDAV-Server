package webdav.server;

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
}
