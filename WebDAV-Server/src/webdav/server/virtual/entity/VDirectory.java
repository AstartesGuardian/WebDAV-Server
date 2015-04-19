package webdav.server.virtual.entity;

import http.server.authentication.HTTPUser;
import java.util.ArrayList;
import java.util.List;
import webdav.server.IResource;
import webdav.server.ResourceType;
import http.server.exceptions.UserRequiredException;

public class VDirectory extends VEntity
{
    private static final long serialVersionUID = 3L;
    
    public VDirectory(VDirectory parent, String name, HTTPUser user) throws UserRequiredException
    {
        super(parent, name, ResourceType.Directory, user, "directory::create");
    }
    public VDirectory(VDirectory parent, String name)
    {
        super(parent, name, ResourceType.Directory);
    }
    
    // <editor-fold defaultstate="collapsed" desc="External management">
    // <editor-fold defaultstate="collapsed" desc="listResources">
    @Override
    protected IResource[] listResources()
    {
        synchronized(children)
        {
            return children.stream().toArray(IResource[]::new);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getMimeType">
    @Override
    protected String getMimeType()
    {
        return "Directory";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getSize">
    @Override
    protected long getSize()
    {
        synchronized(children)
        {
            return children.stream().mapToLong(c -> c.getSize()).sum();
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="delete">
    @Override
    protected boolean delete()
    {
        synchronized(children)
        {
            children.forEach(c -> c.delete());
            children.clear();
        }
        
        return ((VDirectory)getParent()).removeChild(this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getContent">
    @Override
    protected byte[] getContent()
    {
        return new byte[0];
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="setContent">
    @Override
    protected void setContent(byte[] content)
    { }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="appendContent">
    @Override
    protected void appendContent(byte[] content)
    { }
    // </editor-fold>
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Internal management">
    // <editor-fold defaultstate="collapsed" desc="Children">
    protected final List<VEntity> children = new ArrayList<>();
    protected void addChild(VEntity entity)
    {
        synchronized(children)
        {
            children.add(entity);
        }
    }
    public boolean removeChild(VEntity entity)
    {
        synchronized(children)
        {
            return children.remove(entity);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="moveInto">
    public boolean moveInto(VEntity entity, String newName)
    {
        VDirectory parent = entity.getParent();
        if(!this.equals(parent))
        {
            if(parent != null)
                parent.removeChild(entity);
            entity.setParent(this);
            this.addChild(entity);
        }
        entity.setWebName(newName);
        
        return true;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getFromPath">
    @Override
    public VEntity getFromPath(List<String> path, int stopIndex)
    {
        String next = path.remove(0).trim().toLowerCase();
        VEntity entity;
        synchronized(children)
        {
            entity = children
                    .stream()
                    .filter(c -> next.equals(c.getWebName().trim().toLowerCase()))
                    .findFirst()
                    .orElse(null);
        }
        
        if(entity == null || path.size() == stopIndex)
            return entity;
        else
            return entity.getFromPath(path, stopIndex);
    }
    // </editor-fold>
    // </editor-fold>
}
