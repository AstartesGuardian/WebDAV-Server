package webdav.server.virtual.entity.remote.webdav;

import http.ExtendableByteBuffer;
import http.FileSystemPath;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import http.server.message.HTTPRequest;
import java.io.IOException;
import webdav.server.resource.IResource;
import webdav.server.resource.ResourceType;
import webdav.server.virtual.entity.standard.IRsGhost;

public class RsWebDavGhost extends IRsGhost
{
    public RsWebDavGhost(FileSystemPath path, byte[] ip, int port)
    {
        super(path);
        
        this.ip = ip;
        this.port = port;
    }
    
    private final byte[] ip;
    private final int port;
    
    protected ExtendableByteBuffer sendRequest(HTTPRequest.Builder requestBuilder) throws IOException
    {
        return RsWebDavHelper.sendRequest(ip, port, requestBuilder);
    }

    @Override
    protected IResource generateUndefinedResource(FileSystemPath path, HTTPEnvRequest env) throws UserRequiredException
    {
        return new RsWebDavGhost(this.path.createChild(path.getName()), ip, port);
    }

    @Override
    public IResource creates(ResourceType resourceType, HTTPEnvRequest env) throws UserRequiredException
    {
        try
        {
            sendRequest(HTTPRequest.create()
                    .setCommand("lightcreate")
                    .setPath(path)
                    .setHeader("Resource-Type", resourceType.toString()));
            
            return new RsWebDav(ip, port, path.toString(), path.getName());
        }
        catch (IOException ex)
        {
            return null;
        }
    }

    @Override
    public IResource creates(IResource resource, HTTPEnvRequest env) throws UserRequiredException
    {
        try
        {
            sendRequest(HTTPRequest.create()
                    .setCommand("put")
                    .setPath(path));
            
            sendRequest(HTTPRequest.create()
                    .setCommand("put")
                    .setPath(path)
                    .setHeader("overwrite", "f")
                    .setContent(resource.getContent(env)));
            
            return this;
        }
        catch (IOException ex)
        {
            return null;
        }
    }

    @Override
    public boolean isOnTheSameFileSystemWith(IResource resource)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
