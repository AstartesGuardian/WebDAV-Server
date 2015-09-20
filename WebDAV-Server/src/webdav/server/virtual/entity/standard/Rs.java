package webdav.server.virtual.entity.standard;

import http.FileSystemPath;
import http.server.exceptions.AlreadyExistingException;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import java.util.LinkedList;
import webdav.server.resource.IResource;
import webdav.server.virtual.entity.local.RsRoot;

public abstract class Rs implements IResource
{
    protected abstract IResource generateUndefinedResource(FileSystemPath path, HTTPEnvRequest env) throws UserRequiredException;
    
    
    @Override
    public IResource getResource(LinkedList<FileSystemPath> reversedPath, HTTPEnvRequest env) throws UserRequiredException
    {
        if(reversedPath.isEmpty())
            return this;
        
        FileSystemPath childPath = reversedPath.removeFirst();
        
        IResource resource = listResources(env).stream()
                .filter(r -> r.equals(childPath, env))
                .findFirst()
                .orElseGet(() -> generateUndefinedResource(childPath, env))
                .getResource(reversedPath, env);
        
        if(!(resource instanceof ResourceInterface))
            return new ResourceInterface(resource);
        else
            return resource;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof ResourceInterface)
            return o.equals(this);
        if(o instanceof IResource)
            return o == this;
        
        return false;
    }

    @Override
    public boolean equals(FileSystemPath path, HTTPEnvRequest env)
    {
        return this.getWebName(env).equals(path.getName());
    }

    @Override
    public boolean isInstanceOf(Class<?> c)
    {
        return c.isInstance(this);
    }
}
