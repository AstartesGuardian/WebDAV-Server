package http.server.message;

import http.FileSystemPath;
import http.server.HTTPCommand;
import http.server.HTTPServerSettings;
import http.server.authentication.HTTPUser;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import webdav.server.virtual.IResourceMutation;

public class HTTPEnvRequest
{
    public static Builder create()
    {
        return new Builder();
    }
    public static class Builder
    {
        public Builder()
        { }
        
        private HTTPRequest request;
        private HTTPCommand command;
        private HTTPServerSettings settings;
        private Collection<IResourceMutation> mutations = null;
        private byte[] bytesReceived = null;
        
        public Builder setRequest(HTTPRequest request)
        {
            this.request = request;
            return this;
        }
        
        public Builder setCommand(HTTPCommand command)
        {
            this.command = command;
            return this;
        }
        
        public Builder setBytesReceived(byte[] bytesReceived)
        {
            this.bytesReceived = bytesReceived;
            return this;
        }
        
        public Builder setSettings(HTTPServerSettings settings)
        {
            this.settings = settings;
            return this;
        }
        
        public Builder setMutations(Collection<IResourceMutation> mutations)
        {
            this.mutations = mutations;
            return this;
        }
        public Builder addMutations(IResourceMutation[] mutations)
        {
            this.mutations.addAll(Arrays.asList(mutations));
            return this;
        }
        public Builder addMutations(Collection<IResourceMutation> mutations)
        {
            this.mutations.addAll(mutations);
            return this;
        }
        public Builder addMutation(IResourceMutation mutation)
        {
            this.mutations.add(mutation);
            return this;
        }
        
        public HTTPEnvRequest build()
        {
            if(mutations == null)
                mutations = Collections.EMPTY_LIST;
            
            return new HTTPEnvRequest(
                    bytesReceived,
                    request,
                    command,
                    settings.getAuthenticationManager().checkAuth(request),
                    settings,
                    settings.getFileManager().getResourceMutations());
        }
    }
    
    public HTTPEnvRequest(
            byte[] bytesReceived,
            HTTPRequest request,
            HTTPCommand command,
            HTTPUser user,
            HTTPServerSettings settings,
            Collection<IResourceMutation> mutations)
    {
        this.bytesReceived = bytesReceived;
        this.request = request;
        this.command = command;
        this.user = user;
        this.settings = settings;
        this.mutations = mutations;
    }
    
    private byte[] bytesReceived;
    private FileSystemPath path = null;
    
    private final HTTPRequest request;
    private final HTTPCommand command;
    private final HTTPUser user;
    private final HTTPServerSettings settings;
    private final Collection<IResourceMutation> mutations;
    
    public FileSystemPath getPath()
    {
        if(path == null)
            path = settings.getFileSystemPathManager()
                    .createFromString(request.getDecodedPath());
        
        return path;
    }
    
    public Collection<IResourceMutation> getMutations()
    {
        return mutations;
    }
    
    public byte[] getBytesReceived()
    {
        if(bytesReceived == null)
            bytesReceived = request.toBytes();
        return bytesReceived;
    }
    
    public HTTPRequest getRequest()
    {
        return request;
    }
    
    public HTTPCommand getCommand()
    {
        return command;
    }
    
    public HTTPUser getUser()
    {
        return user;
    }
    
    public HTTPServerSettings getSettings()
    {
        return settings;
    }
}
