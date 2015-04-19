package webdav.server.virtual.entity;

import http.server.authentication.HTTPUser;
import java.util.List;
import webdav.server.IResource;
import webdav.server.ResourceType;
import http.server.exceptions.UserRequiredException;

public class VLink extends VEntity
{
    private static final long serialVersionUID = 6L;
    
    public VLink(VDirectory parent, String name, VFile file, HTTPUser user) throws UserRequiredException
    {
        super(parent, name, file.getResourceType(), user, "link::create");
        
        this.file = file;
    }
    public VLink(VDirectory parent, String name, VFile file)
    {
        super(parent, name, file.getResourceType());
        
        this.file = file;
    }
    
    private final VFile file;
    public VFile getLinkedFile()
    {
        return file;
    }
    
    // <editor-fold defaultstate="collapsed" desc="External management">
    // <editor-fold defaultstate="collapsed" desc="getMimeType">
    @Override
    public String getMimeType()
    {
        return file.getMimeType();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getSize">
    @Override
    public long getSize()
    {
        return file.getSize();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="listResources">
    @Override
    public IResource[] listResources()
    {
        return file.listResources();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getContent">
    @Override
    public byte[] getContent()
    {
        return file.getContent();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="setContent">
    @Override
    public void setContent(byte[] content)
    {
        file.setContent(content);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="appendContent">
    @Override
    public void appendContent(byte[] content)
    {
        file.appendContent(content);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="delete">
    @Override
    public boolean delete()
    {
        return ((VDirectory)getParent()).removeChild(this);
    }
    // </editor-fold>
    // </editor-fold>

    @Override
    public VEntity getFromPath(List<String> path, int stopIndex)
    {
        if(file.getResourceType().equals(ResourceType.File))
        { // File
            return file.getFromPath(path);
        }
        else
        { // Directory
            return file.getFromPath(path);
        }
    }
}
