package webdav.server.virtual.contentmutation;

import http.server.message.HTTPEnvRequest;
import webdav.server.resource.IResource;

public class NoContentMutation implements IContentMutation
{
    private static NoContentMutation instance = null;
    public static NoContentMutation getInstance()
    {
        if(instance == null)
            instance = new NoContentMutation();
        return instance;
    }

    @Override
    public byte[] transform(byte[] data, IResource resource, HTTPEnvRequest env)
    {
        return data;
    }

    @Override
    public byte[] reverse(byte[] data, IResource resource, HTTPEnvRequest env)
    {
        return data;
    }
    
}
