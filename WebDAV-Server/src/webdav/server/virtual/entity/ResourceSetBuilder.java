package webdav.server.virtual.entity;

import webdav.server.resource.IResource;

public class ResourceSetBuilder
{
    public static ResourceSetBuilderFolder create(IResource rootResource)
    {
        return new ResourceSetBuilderFolder(rootResource, null);
    }
    
    public static class ResourceSetBuilderFolder
    {
        private ResourceSetBuilderFolder(IResource folderResource, ResourceSetBuilderFolder parent)
        {
            this.folderResource = folderResource;
            this.parent = parent;
        }

        private final ResourceSetBuilderFolder parent;
        private final IResource folderResource;
        
        public ResourceSetBuilderFolder close()
        {
            return parent;
        }
        public ResourceSetBuilderFolder addFile(IResource fileResource)
        {
            this.folderResource.addChild(fileResource, null);
            return this;
        }
        public ResourceSetBuilderFolder addFolder(IResource folderResource)
        {
            this.folderResource.addChild(folderResource, null);
            return new ResourceSetBuilderFolder(folderResource, this);
        }
        public ResourceSetBuilderFolder addEmptyFolder(IResource folderResource)
        {
            this.folderResource.addChild(folderResource, null);
            return this;
        }
    }
}
