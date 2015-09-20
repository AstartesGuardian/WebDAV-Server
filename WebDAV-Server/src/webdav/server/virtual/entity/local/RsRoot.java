package webdav.server.virtual.entity.local;

import http.FileSystemPath;
import http.server.exceptions.UserRequiredException;
import http.server.exceptions.WrongResourceTypeException;
import http.server.message.HTTPEnvRequest;
import webdav.server.resource.IResource;
import webdav.server.resource.ResourceType;

public class RsRoot extends RsLocalDirectory
{
    public RsRoot()
    {
        super("");
    }

    @Override
    public boolean delete(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }

    @Override
    public boolean moveTo(FileSystemPath oldPath, FileSystemPath newPath, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }

    @Override
    public boolean rename(String newName, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }

    @Override
    public IResource creates(ResourceType resourceType, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }
    
    @Override
    public IResource creates(IResource resource, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }
}
