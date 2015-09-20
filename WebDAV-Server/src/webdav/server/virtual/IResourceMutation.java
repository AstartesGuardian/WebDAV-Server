package webdav.server.virtual;

import http.FileSystemPath;
import http.server.message.HTTPEnvRequest;
import webdav.server.resource.IResource;
import webdav.server.resource.ResourceType;

public interface IResourceMutation
{
    public static class MutationResult
    {
        public MutationResult()
        {
            this.computeMutation = false;
            this.mutatedResource = null;
            this.skipOperation = true;
        }
        public MutationResult(IResource mutatedResource, boolean skipOperation)
        {
            this.computeMutation = true;
            this.mutatedResource = mutatedResource;
            this.skipOperation = skipOperation;
        }
        
        private final boolean computeMutation;
        private final IResource mutatedResource;
        private final boolean skipOperation;
        
        public boolean computeMutation()
        {
            return computeMutation;
        }
        public IResource mutatedResource()
        {
            return mutatedResource;
        }
        public boolean skipOperation()
        {
            return skipOperation;
        }
        
        public final static MutationResult NoMutation = new MutationResult();
    }
    
    public default MutationResult renameInvolvesMutation(HTTPEnvRequest env, IResource entity, String newName)
    {
        return MutationResult.NoMutation;
    }
    public default MutationResult moveInvolvesMutation(HTTPEnvRequest env, IResource entity, FileSystemPath newPath)
    {
        return MutationResult.NoMutation;
    }
    public default MutationResult setContentInvolvesMutation(HTTPEnvRequest env, IResource entity, byte[] content)
    {
        return MutationResult.NoMutation;
    }
    public default MutationResult appendContentInvolvesMutation(HTTPEnvRequest env, IResource entity, byte[] content)
    {
        return MutationResult.NoMutation;
    }
    public default MutationResult deleteInvolvesMutation(HTTPEnvRequest env, IResource entity)
    {
        return MutationResult.NoMutation;
    }
    public default MutationResult createInvolvesMutation(HTTPEnvRequest env, IResource entity, ResourceType resourceType)
    {
        return MutationResult.NoMutation;
    }
    
    public default MutationResult setPropertyInvolvesMutation(HTTPEnvRequest env, IResource entity, String namespace, String name, String value)
    {
        return MutationResult.NoMutation;
    }
    
    public default MutationResult removePropertyInvolvesMutation(HTTPEnvRequest env, IResource entity, String namespace, String name)
    {
        return MutationResult.NoMutation;
    }
    public default MutationResult removePropertyInvolvesMutation(HTTPEnvRequest env, IResource entity, String name)
    {
        return MutationResult.NoMutation;
    }
}
