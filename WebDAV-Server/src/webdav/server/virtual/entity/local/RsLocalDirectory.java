package webdav.server.virtual.entity.local;

import http.server.exceptions.UserRequiredException;
import http.server.exceptions.WrongResourceTypeException;
import http.server.message.HTTPEnvRequest;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;
import webdav.server.resource.IResource;
import webdav.server.resource.ResourceType;
import webdav.server.virtual.entity.standard.ResourceInterface;

public class RsLocalDirectory extends RsLocal
{
    public RsLocalDirectory(String name)
    {
        super(name);
    }
    public RsLocalDirectory(IResource resource, HTTPEnvRequest env)
    {
        super(resource, env);
        
        children.addAll(resource.listResources(env));
    }
    
    private LinkedList<IResource> children = new LinkedList<>();
    
    @Override
    public ResourceType getResourceType(HTTPEnvRequest env) throws UserRequiredException
    {
        return ResourceType.Directory;
    }

    @Override
    public String getMimeType(HTTPEnvRequest env) throws UserRequiredException
    {
        return "Directory";
    }

    @Override
    public long getSize(HTTPEnvRequest env) throws UserRequiredException
    {
        return listResources(env).stream()
                .mapToLong(r -> r.getSize(env))
                .sum();
    }

    @Override
    public Instant getLastModified(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }

    @Override
    public Collection<IResource> listResources(HTTPEnvRequest env) throws UserRequiredException
    {
        return children.stream()
                .filter(r -> r.isVisible(env))
                .collect(Collectors.toList());
    }

    @Override
    public byte[] getContent(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }

    @Override
    public void setContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }

    @Override
    public void appendContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }

    @Override
    public boolean delete(HTTPEnvRequest env) throws UserRequiredException
    {
        return listResources(env).stream()
                .map(r -> r.delete(env))
                .allMatch(b -> b);
    }
    
    @Override
    public boolean addChild(IResource resource, HTTPEnvRequest env)
    {
        return children.add(resource);
    }
    
    @Override
    public boolean removeChild(IResource resource, HTTPEnvRequest env)
    {
        String webName = resource.getWebName(env);
        
        IResource rs = children.stream()
                .filter(r -> r.getWebName(env).equals(webName))
                .findFirst()
                .orElse(null);
        
        return rs != null && children.remove(rs);
    }
}
