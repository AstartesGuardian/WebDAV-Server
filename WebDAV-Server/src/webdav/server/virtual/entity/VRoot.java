package webdav.server.virtual.entity;

import webdav.server.virtual.VirtualManager;

public class VRoot extends VDirectory
{
    private static final long serialVersionUID = 3L;
    
    public VRoot(VirtualManager virtualManager)
    {
        super(null, "");
        this.virtualManager = virtualManager;
    }
    
    // <editor-fold defaultstate="collapsed" desc="delete">
    @Override
    public boolean delete()
    {
        synchronized(children)
        {
            children.forEach(c -> c.delete());
            children.clear();
        }
        
        return true;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="getManager">
    private final VirtualManager virtualManager;
    @Override
    protected VirtualManager getManager()
    {
        return virtualManager;
    }
    // </editor-fold>
}
