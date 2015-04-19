package webdav.server.virtual.entity;

import http.server.authentication.HTTPUser;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.util.Pair;
import javax.xml.namespace.QName;
import org.w3c.dom.Node;
import webdav.server.IResource;
import webdav.server.Lock;
import webdav.server.LockKind;
import webdav.server.ResourceType;
import http.server.exceptions.UserRequiredException;
import webdav.server.virtual.VirtualManager;

public abstract class VEntity implements IResource, Serializable
{
    private static final long serialVersionUID = 0L;
    
    public VEntity(VDirectory parent, String name, ResourceType type, HTTPUser user, String rightName) throws UserRequiredException
    {
        if(user != null)
            user.checkRight(rightName);
        
        this.name = name;
        this.parent = parent;
        this.type = type;
        
        if(parent != null)
            parent.addChild(this);
        
        Instant now = Instant.now();
        this.lastModified = now;
        this.creationTime = now;
    }
    public VEntity(VDirectory parent, String name, ResourceType type)
    {
        this.name = name;
        this.parent = parent;
        this.type = type;
        
        if(parent != null)
            parent.addChild(this);
        
        Instant now = Instant.now();
        this.lastModified = now;
        this.creationTime = now;
    }
    
    // <editor-fold defaultstate="collapsed" desc="External management">
    // <editor-fold defaultstate="collapsed" desc="Web name">
    private String name;
    @Override
    public String getWebName(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("webname::get");
        
        return getWebName();
    }
    public String getWebName()
    {
        return name;
    }
    public void setWebName(String name)
    {
        this.name = name;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Visible">
    private boolean isVisible = true;
    @Override
    public boolean isVisible(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("visible::get");
        
        return isVisible();
    }
    public boolean isVisible()
    {
        return isVisible;
    }
    public void setVisible(boolean isVisible)
    {
        this.isVisible = isVisible;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Times">
    // <editor-fold defaultstate="collapsed" desc="Creation time">
    protected final Instant creationTime;
    @Override
    public Instant getCreationTime(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("time::creation::get");
        
        return getCreationTime();
    }
    public Instant getCreationTime()
    {
        return creationTime;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Last modified">
    protected Instant lastModified;
    @Override
    public Instant getLastModified(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("time::lastmodified::get");
        
        return getLastModified();
    }
    public Instant getLastModified()
    {
        return lastModified;
    }
    // </editor-fold>
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Paths">
    // <editor-fold defaultstate="collapsed" desc="getFromPath">
    public abstract VEntity getFromPath(List<String> path, int stopIndex);
    public VEntity getFromPath(List<String> path)
    {
        return getFromPath(path, 0);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="getPath">
    public String getPath()
    {
        if(name.isEmpty())
            return "";
        
        if(parent == null)
            return "/" + name;
        else
            return parent.getPath() + "/" + name;
    }
    @Override
    public String getPath(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("path::get");
        
        return getPath();
    }
    // </editor-fold>
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Rename">
    public boolean moveTo(String newPath)
    {
        List<String> to = new ArrayList<>(Arrays.asList(newPath.replace("\\", "/").split("/")));
        VDirectory dir_to = (VDirectory)getManager().getFromPath(to, 1);
        
        if(dir_to == null)
            return false;
        
        return dir_to.moveInto(this, to.remove(0));
    }
    @Override
    public boolean moveTo(String newPath, HTTPUser user) throws UserRequiredException
    {
        user.checkRight("move");
        
        return moveTo(newPath);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Resource type">
    private final ResourceType type;
    @Override
    public ResourceType getResourceType(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("type::get");
        
        return getResourceType();
    }
    protected ResourceType getResourceType()
    {
        return type;
    }
    // </editor-fold>
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Locks">
    
    
    private transient List<Lock> locks = null;
    public boolean canLock(LockKind lockKind, HTTPUser user) throws UserRequiredException
    {
        user.checkRight("lock::check");
        
        if(!Arrays.asList(getAvailableLocks()).contains(lockKind))
            return false;
        
        synchronized(getLocks(user))
        {
            Stream<Lock> flocks = getLocks(user)
                    .stream()
                    .filter(l -> lockKind.getType().equals(l.getLockKind().getType()));
            
            switch(lockKind.getScope())
            {
                default:
                case Exclusive:
                    return flocks
                            .noneMatch(l -> LockKind.LockScope.Exclusive == l.getLockKind().getScope());
                    
                case Shared:
                    return flocks
                            .noneMatch(l -> LockKind.LockScope.Exclusive == l.getLockKind().getScope());
            }
        }
    }
    public void removeLock(String uuid, HTTPUser user) throws UserRequiredException
    {
        user.checkRight("lock::remove");
        
        synchronized(getLocks(user))
        {
            getLocks(user).removeIf(l -> uuid.equals(l.getUUID()));
        }
    }
    public void removeLock(Lock lock, HTTPUser user) throws UserRequiredException
    {
        user.checkRight("lock::remove");
        
        synchronized(getLocks(user))
        {
            getLocks(user).remove(lock);
        }
    }
    public boolean setLock(LockKind lockKind, HTTPUser user) throws UserRequiredException
    {
        user.checkRight("lock::set");
        
        if(!canLock(lockKind, user))
            return false;
        
        synchronized(getLocks(user))
        {
            getLocks(user).add(new Lock(lockKind));
        }
        return true;
    }
    public boolean setLock(Lock lock, HTTPUser user) throws UserRequiredException
    {
        user.checkRight("lock::set");
        
        if(!canLock(lock.getLockKind(), user))
            return false;
        
        synchronized(getLocks(user))
        {
            getLocks(user).add(lock);
        }
        return true;
    }
    public List<Lock> getLocks(LockKind lockKind, HTTPUser user) throws UserRequiredException
    {
        synchronized(getLocks(user))
        {
            getLocks(user).removeIf(l -> l.expired());
            return getLocks(user)
                    .stream()
                    .filter(l -> lockKind.equals(l.getLockKind()))
                    .collect(Collectors.toList());
        }
    }
    public synchronized List<Lock> getLocks(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("lock::get");
        
        if(locks == null)
            locks = new ArrayList<>();
        else
            locks.removeIf(l -> l.expired());
        return locks;
    }
    public LockKind[] getAvailableLocks() throws UserRequiredException
    {
        return new LockKind[]
        {
            new LockKind(LockKind.LockScope.Exclusive, LockKind.LockType.write),
            new LockKind(LockKind.LockScope.Shared, LockKind.LockType.write)
        };
    }
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="Properties">
    private Map<Pair<String, String>, String> properties = new HashMap<>();
    
    // <editor-fold defaultstate="collapsed" desc="Set">
    public void setProperty(String namespace, String name, String value, HTTPUser user) throws UserRequiredException
    {
        user.checkRight("property::set");
        
        synchronized(properties)
        {
            properties.put(new Pair<>(namespace, name), value);
        }
    }
    public void setProperty(Node node, String value, HTTPUser user) throws UserRequiredException
    {
        setProperty(node.getNamespaceURI(), node.getLocalName(), value, user);
    }
    public void setProperty(QName name, String value, HTTPUser user) throws UserRequiredException
    {
        setProperty(name.getNamespaceURI(), name.getLocalPart(), value, user);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Remove">
    public void removeProperty(String name, HTTPUser user) throws UserRequiredException
    {
        user.checkRight("property::remove");
        
        synchronized(properties)
        {
            properties = getProperties(user)
                    .stream()
                    .filter(e -> !name.equals(e.getKey().getValue()))
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        }
    }
    public void removeProperty(String namespace, String name, HTTPUser user) throws UserRequiredException
    {
        user.checkRight("property::remove");
        
        synchronized(properties)
        {
            properties.remove(new Pair<>(namespace, name));
        }
    }
    public void removeProperty(Node node, HTTPUser user) throws UserRequiredException
    {
        removeProperty(node.getNamespaceURI(), node.getLocalName(), user);
    }
    public void removeProperty(QName name, HTTPUser user) throws UserRequiredException
    {
        removeProperty(name.getNamespaceURI(), name.getLocalPart(), user);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Get">
    public String getProperty(String name, HTTPUser user) throws UserRequiredException
    {
        synchronized(properties)
        {
            return getProperties(user)
                    .stream()
                    .filter(e -> name.equals(e.getKey().getValue()))
                    .map(e -> e.getValue())
                    .findFirst()
                    .orElse(null);
        }
    }
    public String getProperty(String namespace, String name, HTTPUser user) throws UserRequiredException
    {
        synchronized(properties)
        {
            return getProperties(user)
                    .stream()
                    .filter(e -> name.equals(e.getKey().getValue()) && namespace.equals(e.getKey().getKey()))
                    .map(e -> e.getValue())
                    .findFirst()
                    .orElse(null);
        }
    }
    public String getProperty(QName name, HTTPUser user) throws UserRequiredException
    {
        user.checkRight("property::get");
        
        synchronized(properties)
        {
            return properties.getOrDefault(name, "");
        }
    }
    public Set<Entry<Pair<String, String>, String>> getProperties(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("property::get");
        
        synchronized(properties)
        {
            return properties.entrySet();
        }
    }
    // </editor-fold>
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Abstracts">
    protected abstract boolean delete();
    protected abstract long getSize();
    protected abstract void appendContent(byte[] content);
    protected abstract void setContent(byte[] content);
    protected abstract IResource[] listResources();
    protected abstract byte[] getContent();
    protected abstract String getMimeType();
    // </editor-fold>
    // </editor-fold>
    
    
    @Override
    public IResource[] listResources(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("children::get");
        
        return listResources();
    }
    @Override
    public void appendContent(byte[] content, HTTPUser user) throws UserRequiredException
    {
        user.checkRight("content::set");
        
        appendContent(content);
    }
    @Override
    public void setContent(byte[] content, HTTPUser user) throws UserRequiredException
    {
        user.checkRight("content::set");
        
        setContent(content);
        
    }
    @Override
    public byte[] getContent(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("content::get");
        
        return getContent();
    }
    @Override
    public long getSize(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("size::get");
        
        return getSize();
    }
    @Override
    public String getMimeType(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("mimetype::get");
        
        return getMimeType();
    }
    @Override
    public boolean delete(HTTPUser user) throws UserRequiredException
    {
        user.checkRight("delete");
        
        return delete();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Internal management">
    // <editor-fold defaultstate="collapsed" desc="getParent">
    private VDirectory parent;
    public VDirectory getParent()
    {
        return parent;
    }
    protected void setParent(VDirectory parent)
    {
        this.parent = parent;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="getManager">
    protected VirtualManager getManager()
    {
        if(parent == null)
            return null;
        else
            return parent.getManager();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="toString">
    @Override
    public String toString()
    {
        return this.name + " [" + getResourceType().toString() + " : " + getPath() + "]";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Compare">
    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;
        
        if(o instanceof VEntity)
            return ((VEntity)o).hashCode() == this.hashCode();
        if(o instanceof String)
            return ((String)o).replace("\\", "/").toLowerCase().hashCode() == this.hashCode();
        
        return false;
    }
    @Override
    public int hashCode()
    {
        return getPath().replace("\\", "/").toLowerCase().hashCode();
    }
    // </editor-fold>
    // </editor-fold>
}
