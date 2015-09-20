package webdav.server.virtual.contentmutation;

import http.server.message.HTTPEnvRequest;
import webdav.server.resource.IResource;

public interface IContentMutation
{
    public byte[] transform(byte[] data, IResource resource, HTTPEnvRequest env);
    public byte[] reverse(byte[] data, IResource resource, HTTPEnvRequest env);
}
