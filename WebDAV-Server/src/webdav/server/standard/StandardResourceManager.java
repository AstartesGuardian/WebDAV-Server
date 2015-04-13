package webdav.server.standard;

import http.server.HTTPAuthentication;
import webdav.server.IResource;
import webdav.server.IResourceManager;

public class StandardResourceManager implements IResourceManager
{
    @Override
    public IResource createFromPath(String path)
    {
        return new StandardResource(path);
    }

    @Override
    public void setUser(HTTPAuthentication user)
    { }

    @Override
    public int getMaxBufferSize()
    {
        return 1048576;
    }
    @Override
    public int getStepBufferSize()
    {
        return 5000;
    }
}
