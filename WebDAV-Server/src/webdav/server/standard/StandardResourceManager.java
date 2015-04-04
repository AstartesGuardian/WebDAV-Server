package webdav.server.standard;

import webdav.server.IResource;
import webdav.server.IResourceManager;

public class StandardResourceManager implements IResourceManager
{
    @Override
    public IResource createFromPath(String path)
    {
        return new StandardResource(path);
    }
}
