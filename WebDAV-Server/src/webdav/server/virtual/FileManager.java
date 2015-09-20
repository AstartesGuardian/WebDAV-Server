package webdav.server.virtual;

import http.FileSystemPath;
import http.server.message.HTTPEnvRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import webdav.server.crypter.AbstractCrypter;
import webdav.server.crypter.ICrypter;
import webdav.server.resource.IResource;
import webdav.server.virtual.entity.local.RsRoot;

public class FileManager implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public FileManager(
            Map<String, Object> properties,
            IResource root,
            Collection<IResourceMutation> resourceMutations)
    {
        this.properties = properties;
        this.root = root;
        this.resourceMutations = resourceMutations;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Builder">
    public static Builder create()
    {
        return new Builder();
    }
    public static class Builder
    {
        public Builder()
        { }
        
        private IResource root = null;
        private byte[] source = null;
        private String crypterKey = null;
        private ICrypter crypter = null;
        private final Map<String, Object> properties = new HashMap<>();
        private final Collection<IResourceMutation> resourceMutations = new LinkedList<>();
        
        public Builder addResourceMutation(IResourceMutation resourceMutation)
        {
            this.resourceMutations.add(resourceMutation);
            return this;
        }
        public Builder addResourceMutations(Collection<IResourceMutation> resourceMutations)
        {
            this.resourceMutations.addAll(resourceMutations);
            return this;
        }
        public Builder addResourceMutations(IResourceMutation[] resourceMutations)
        {
            addResourceMutations(Arrays.asList(resourceMutations));
            return this;
        }
        public Builder setResourceMutations(IResourceMutation[] resourceMutations)
        {
            setResourceMutations(Arrays.asList(resourceMutations));
            return this;
        }
        public Builder setResourceMutations(Collection<IResourceMutation> resourceMutations)
        {
            this.resourceMutations.clear();
            this.resourceMutations.addAll(resourceMutations);
            return this;
        }
        
        public Builder addProperty(String name, Object property)
        {
            this.properties.put(name.trim().toLowerCase(), property);
            return this;
        }
        public Builder addProperties(Map<String, Object> properties)
        {
            this.properties.putAll(properties.entrySet()
                    .stream()
                    .filter(e -> e.getKey() != null)
                    .filter(e -> e.getValue() != null)
                    .collect(Collectors.toMap(e -> e.getKey().trim().toLowerCase(), e -> e.getValue())));
            return this;
        }
        public Builder setProperties(Map<String, Object> properties)
        {
            this.properties.clear();
            addProperties(properties);
            return this;
        }
        
        public Builder setSourceFile(File source) throws IOException
        {
            this.source = Files.readAllBytes(source.toPath());
            return this;
        }
        public Builder setSourceData(byte[] source)
        {
            this.source = source;
            return this;
        }
        
        public Builder setLoginPassword(String login, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException
        {
            this.crypterKey = new String(AbstractCrypter.sha256(login + ":" + password), "UTF-8");
            return this;
        }
        
        public Builder setCrypter(ICrypter crypter)
        {
            this.crypter = crypter;
            return this;
        }
        
        public Builder setRoot(IResource root)
        {
            this.root = root;
            return this;
        }
        
        public FileManager build() throws Exception
        {
            if(source != null)
            { // Load from source
                FileManager fm;
                
                if(crypter != null && crypterKey != null)
                { // Load from crypted source
                    fm = load(crypter.decrypt(source, new byte[0]));
                    fm.crypter = crypter;
                }
                else
                { // Load from non-crypted source
                    fm = load(source);
                }
                
                fm.resourceMutations = resourceMutations;
                fm.properties = properties;
                return fm;
            }
            else
            { // Create new
                if(root == null)
                    root = new RsRoot();
                
                return new FileManager(
                        properties,
                        root,
                        resourceMutations);
            }
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Properties">
    private BigInteger uid = BigInteger.ZERO;
    private final IResource root;
    public transient Map<String, Object> properties;
    private transient ICrypter crypter = null;
    private transient Collection<IResourceMutation> resourceMutations;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Accessors">
    public synchronized BigInteger generateUID()
    {
        uid = uid.add(BigInteger.ONE);
        return uid;
    }
    
    public Collection<IResourceMutation> getResourceMutations()
    {
        return resourceMutations;
    }
    
    public IResource getRoot()
    {
        return root;
    }
    
    public IResource getResourceFromPath(FileSystemPath path, HTTPEnvRequest env)
    {
        if(path.isRoot())
            return getRoot();
        else
            return getRoot().getResource(path.toPaths(), env);
    }
    
    private Map<String, Object> getProperties()
    {
        return properties;
    }
    public <T> T getProperty(String name)
    {
        return getProperty(name, (T)null);
    }
    public <T> T getProperty(String name, T defaultValue)
    {
        return (T)getProperties().getOrDefault(name.trim().toLowerCase(), defaultValue);
    }
    public <T> T getProperty(String name, Supplier<T> defaultSupplier)
    {
        return (T)getProperties().getOrDefault(name.trim().toLowerCase(), defaultSupplier.get());
    }
    public <T> T getProperty(Class<T> type, String name)
    {
        return getProperty(name, (T)null);
    }
    public <T> T getProperty(Class<T> type, String name, T defaultValue)
    {
        return (T)getProperties().getOrDefault(name.trim().toLowerCase(), defaultValue);
    }
    public <T> T getProperty(Class<T> type, String name, Supplier<T> defaultSupplier)
    {
        return (T)getProperties().getOrDefault(name.trim().toLowerCase(), defaultSupplier.get());
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Save/Load">
    public boolean save(File destination, ICrypter c)
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(baos))
        {
            out.writeObject(this);
            
            if(c != null)
                Files.write(destination.toPath(), c.encrypt(baos.toByteArray(), new byte[0]));
            else
                Files.write(destination.toPath(), baos.toByteArray());
            
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
        return save(destination, crypter);
    }
    
    private static <T extends FileManager> T load(byte[] data) throws Exception
    {
        try (ByteArrayInputStream baos = new ByteArrayInputStream(data);
                ObjectInputStream out = new ObjectInputStream(baos))
        {
            return (T)out.readObject();
        }
    }
    // </editor-fold>
}
