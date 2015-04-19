package webdav.server.virtual;

import webdav.server.virtual.contentmanagers.IContentManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.NoSuchPaddingException;
import webdav.server.crypter.CipherCrypter;
import webdav.server.crypter.ICrypter;
import webdav.server.virtual.entity.VEntity;
import webdav.server.virtual.entity.VRoot;

public class VirtualManager implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public VirtualManager()
    {
        root = new VRoot(this);
    }
    
    // <editor-fold defaultstate="collapsed" desc="UID manager">
    private BigInteger uid = BigInteger.ZERO;
    public synchronized BigInteger getNewUID()
    {
        uid = uid.add(BigInteger.ONE);
        return uid;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="VRoot">
    private final VRoot root;
    public VRoot getRoot()
    {
        return root;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Root directory">
    private transient String rootDirectory;
    public void setRootDirectory(File rootDirectory)
    {
        this.rootDirectory = rootDirectory.getAbsolutePath();
    }
    public String getRootDirectory()
    {
        return rootDirectory;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Content managers">
    private transient Map<String, IContentManager> contentManagers = null;
    private Map<String, IContentManager> getContentManagers()
    {
        if(contentManagers == null)
            contentManagers = new HashMap<>();
        return contentManagers;
    }
    public void addContentManager(String name, IContentManager contentManager)
    {
        getContentManagers().put(name.toLowerCase(), contentManager);
    }
    public IContentManager getContentManager(String name)
    {
        return getContentManagers().getOrDefault(name.toLowerCase(), null);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="getFromPath">
    public VEntity getFromPath(List<String> path, int stopIndex)
    {
        if(path.isEmpty())
            return null;
        
        String initial = path.remove(0);
        if(initial.trim().length() > 0)
            return null;
        
        if(path.size() <= stopIndex)
            return getRoot();
        
        return getRoot().getFromPath(path, stopIndex);
    }
    public VEntity getFromPath(List<String> path)
    {
        return getFromPath(path, 0);
    }
    public VEntity getFromPath(String path)
    {
        return getFromPath(path, 0);
    }
    public VEntity getFromPath(String path, int stopIndex)
    {
        return getFromPath(new ArrayList<>(Arrays.asList(path.replace("\\", "/").split("/"))), stopIndex);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Save/Load">
    private static ICrypter getCrypter(String login, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException
    {
        CipherCrypter c = new CipherCrypter(ICrypter.Algorithm.AES_CBC_PKCS5Padding);
        c.setKey(new String(ICrypter.sha256(login + ":" + password), "UTF-8"));
        return c;
    }
    
    public boolean save(File destination, String login, String password)
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(baos))
        {
            ICrypter c = getCrypter(login, password);
            
            out.writeObject(this);
            Files.write(destination.toPath(), c.encrypt(baos.toByteArray()));
            
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    public boolean save(File destination)
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(baos))
        {
            out.writeObject(this);
            Files.write(destination.toPath(), baos.toByteArray());
            
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
    
    public static <T extends VirtualManager> T load(byte[] data) throws Exception
    {
        try (ByteArrayInputStream baos = new ByteArrayInputStream(data);
                ObjectInputStream out = new ObjectInputStream(baos))
        {
            return (T)out.readObject();
        }
    }
    public static <T extends VirtualManager> T load(File source) throws Exception
    {
        return load(Files.readAllBytes(source.toPath()));
    }
    public static <T extends VirtualManager> T load(File source, String login, String password) throws Exception
    {
        ICrypter c = getCrypter(login, password);
        return load(c.decrypt(Files.readAllBytes(source.toPath())));
    }
    // </editor-fold>
}
