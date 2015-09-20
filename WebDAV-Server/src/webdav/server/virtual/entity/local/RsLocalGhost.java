package webdav.server.virtual.entity.local;

import http.FileSystemPath;
import http.server.exceptions.UserRequiredException;
import http.server.exceptions.WrongResourceTypeException;
import http.server.message.HTTPEnvRequest;
import webdav.server.resource.IResource;
import webdav.server.resource.ResourceType;
import webdav.server.virtual.entity.standard.IRsGhost;

public class RsLocalGhost extends IRsGhost
{
    public RsLocalGhost(FileSystemPath path)
    {
        super(path);
    }
    
    @Override
    protected IResource generateUndefinedResource(FileSystemPath path, HTTPEnvRequest env) throws UserRequiredException
    {
        return new RsLocalGhost(path);
    }

    @Override
    public IResource creates(ResourceType resourceType, HTTPEnvRequest env) throws UserRequiredException
    {
        switch(resourceType)
        {
            case Directory:
                return new RsLocalDirectory(path.getName());
                
            case File:
                return new RsLocalFile(path.getName(), env);
                
            default:
                throw new WrongResourceTypeException();
        }
    }

    @Override
    public IResource creates(IResource resource, HTTPEnvRequest env) throws UserRequiredException
    {
        switch(resource.getResourceType(env))
        {
            case Directory:
                return new RsLocalDirectory(resource, env);
                
            case File:
                return new RsLocalFile(resource, env);
                
            default:
                return null;
        }
    }

    @Override
    public boolean isOnTheSameFileSystemWith(IResource resource)
    {
        return resource.isInstanceOf(RsLocal.class) || resource.isInstanceOf(RsLocalGhost.class);
    }
}
