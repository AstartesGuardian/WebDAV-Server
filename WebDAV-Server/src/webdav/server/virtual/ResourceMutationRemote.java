package webdav.server.virtual;

import http.server.message.HTTPEnvRequest;
import java.io.IOException;
import java.net.URL;
import webdav.server.resource.IResource;
import webdav.server.virtual.entity.local.RsLocal;
import webdav.server.virtual.entity.local.RsLocalFile;
import webdav.server.virtual.entity.remote.IRemoteInterface;
import webdav.server.virtual.entity.remote.webdav.RsWebDavInterface;
import webdav.server.virtual.entity.standard.ResourceInterface;

public class ResourceMutationRemote implements IResourceMutation
{

    @Override
    public MutationResult renameInvolvesMutation(HTTPEnvRequest env, IResource entity, String newName)
    {
        if(!entity.isInstanceOf(IRemoteInterface.class))
            return MutationResult.NoMutation;
            
        IRemoteInterface remoteInterface;

        if(entity instanceof ResourceInterface)
            remoteInterface = ((ResourceInterface)entity).cast(IRemoteInterface.class);
        else
            remoteInterface = (IRemoteInterface)entity;

        IResource newResource = new RsLocalFile(entity.getWebName(env), env);
        newResource.setContent(remoteInterface.toIniFile().toBytes(), env);
        return new MutationResult(newResource, true);
    }

    @Override
    public MutationResult setContentInvolvesMutation(HTTPEnvRequest env, IResource entity, byte[] content)
    {
        if(!entity.isInstanceOf(RsLocal.class))
            return MutationResult.NoMutation;
        
        try
        {
            IniDocument document = new IniDocument(content);

            String urlStr = document.getString("server", "url", null);

            if(urlStr == null)
            {
                String protocol = document.getString("server", "protocol", null);
                String ip = document.getString("server", "ip", null);
                String port = document.getString("server", "port", "80");
                String path = document.getString("server", "path", "/");
                
                if(protocol == null || ip == null)
                    return MutationResult.NoMutation;
                
                urlStr = protocol.trim() + "://" + ip.trim() + ":" + port.trim() + path.trim();
            }

            URL url = new URL(urlStr);

            switch(url.getProtocol().toLowerCase())
            {
                case "http":
                    return new MutationResult(
                            new RsWebDavInterface(url, entity.getWebName(env))
                            , false);

                default:
                    return MutationResult.NoMutation;
            }
        }
        catch(IOException ex)
        {
            return MutationResult.NoMutation;
        }
    }
}
