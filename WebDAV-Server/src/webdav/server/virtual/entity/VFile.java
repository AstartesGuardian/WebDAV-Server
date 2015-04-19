package webdav.server.virtual.entity;

import http.server.authentication.HTTPUser;
import http.server.exceptions.UserRequiredException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import webdav.server.IResource;
import webdav.server.ResourceType;
import webdav.server.virtual.VirtualManager;

public class VFile extends VEntity
{
    private static final long serialVersionUID = 2L;
    
    public VFile(VDirectory parent, String name, HTTPUser user) throws UserRequiredException
    {
        super(parent, name, ResourceType.File, user, "file::create");
        generateUID();
    }
    public VFile(VDirectory parent, String name)
    {
        super(parent, name, ResourceType.File);
        generateUID();
    }
    private void generateUID()
    {
        VirtualManager manager = getManager();
        if(manager != null)
            id = getManager().getNewUID();
        else
            id = BigInteger.ZERO;
    }
    
    // <editor-fold defaultstate="collapsed" desc="External management">
    // <editor-fold defaultstate="collapsed" desc="getMimeType">
    private String mimeType = null;
    @Override
    public String getMimeType()
    {
        if(mimeType != null)
            try
            {
                mimeType = Files.probeContentType(new File(getPhysicalPath()).toPath());
            }
            catch (IOException ex)
            {
                mimeType = "Unknown";
            }
        
        return mimeType;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getSize">
    private long size = 0;
    @Override
    public long getSize()
    {
        return size;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="listResources">
    @Override
    public IResource[] listResources()
    {
        return new IResource[0];
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getContent">
    @Override
    public byte[] getContent()
    {
        return getManager().getContentManager("direct").getContent(getPhysicalPath());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="setContent">
    @Override
    public void setContent(byte[] content)
    {
        size = getManager().getContentManager("direct").setContent(getPhysicalPath(), content);
        lastModified = Instant.now();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="appendContent">
    @Override
    public void appendContent(byte[] content)
    {
        size += getManager().getContentManager("direct").appendContent(getPhysicalPath(), content);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="delete">
    @Override
    public boolean delete()
    {
        new File(getPhysicalPath()).delete();
        
        return ((VDirectory)getParent()).removeChild(this);
    }
    // </editor-fold>
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Internal management">
    // <editor-fold defaultstate="collapsed" desc="getID">
    private BigInteger id;
    protected BigInteger getID()
    {
        return id;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="getPhysicalPath">
    protected String getPhysicalPath()
    {
        String root = getManager().getRootDirectory();
        if(!root.endsWith("/") && !root.endsWith("\\"))
            root += "/";
        return root + getID();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="getFromPath">
    @Override
    public VEntity getFromPath(List<String> path, int stopIndex)
    {
        if(path.size() != 1)
            return null;
        
        String current = path.remove(0).trim().toLowerCase();
        if(current.equals(getWebName().toLowerCase()))
            return this;
        else
            return null;
    }
    // </editor-fold>
    // </editor-fold>
}
