package webdav.server.virtual.entity.remote.webdav;

import http.ExtendableByteBuffer;
import http.FileSystemPath;
import http.FileSystemPathManager;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UnexpectedException;
import http.server.exceptions.UserRequiredException;
import http.server.exceptions.WrongResourceTypeException;
import http.server.message.HTTPEnvRequest;
import http.server.message.HTTPRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.util.Pair;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import webdav.server.resource.IResource;
import webdav.server.resource.Lock;
import webdav.server.resource.LockKind;
import webdav.server.resource.ResourceType;
import webdav.server.tools.NodeListWrap;
import webdav.server.virtual.entity.standard.Rs;

public class RsWebDav extends Rs
{
    public RsWebDav(byte[] ip, int port, Map<String, String> metadata)
    {
        this(ip, port, metadata.get("path"), metadata.get("displayname"));
        
        this.metadata = metadata;
    }
    public RsWebDav(byte[] ip, int port, String path, String name)
    {
        super();
        
        this.ip = ip;
        this.port = port;
        this.path = path;
        this.name = name;
    }
    
    protected String name;
    protected String path;
    protected final byte[] ip;
    protected final int port;
    
    protected ExtendableByteBuffer sendRequest(HTTPRequest.Builder requestBuilder) throws IOException
    {
        return RsWebDavHelper.sendRequest(ip, port, requestBuilder);
    }
    
    private Map<String, String> metadata = null;
    protected Map<String, String> getMetaData(HTTPEnvRequest env)
    {
        if(metadata == null)
        {
            try
            {
                this.metadata = NodeListWrap.getStream(sendRequest(HTTPRequest.create()
                        .setCommand("LIGHTPROPFIND")
                        .setPath(path)
                        .setHeader("Depth", "0")
                        .setHeader("Host", env.getRequest().getHeader("host")))
                        .toXML()
                        .getElementsByTagName("current"))
                        .map(Node::getChildNodes)
                        .map(NodeListWrap::getStream)
                        .map(s -> s.collect(Collectors.toMap(n -> n.getNodeName(), n -> n.getTextContent())))
                        .findFirst()
                        .get();
            }
            catch (ParserConfigurationException | SAXException | IOException ex)
            {
                throw new UnexpectedException(ex);
            }
        }
        return metadata;
    }
    protected String getMetaData(String name, HTTPEnvRequest env)
    {
        if(getMetaData(env) == null)
            throw new NotFoundException();
        if(!getMetaData(env).containsKey(name))
            throw new WrongResourceTypeException("Key can't be found : \"" + name + "\"");
        return getMetaData(env).get(name);
    }
    

    @Override
    protected IResource generateUndefinedResource(FileSystemPath path, HTTPEnvRequest env) throws UserRequiredException
    {
        FileSystemPathManager manager = env.getSettings().getFileSystemPathManager();
        return new RsWebDavGhost(manager.createFromString(this.path).createChild(path.getName()), ip, port);
    }

    @Override
    public boolean isVisible(HTTPEnvRequest env) throws UserRequiredException
    {
        return true;
    }

    @Override
    public ResourceType getResourceType(HTTPEnvRequest env) throws UserRequiredException
    {
        return ResourceType.valueOf(getMetaData("resourcetype", env));
    }

    @Override
    public String getWebName(HTTPEnvRequest env) throws UserRequiredException
    {
        if(name != null)
            return name;
        
        return getMetaData("displayname", env);
    }

    @Override
    public String getMimeType(HTTPEnvRequest env) throws UserRequiredException
    {
        return getMetaData("mimetype", env);
    }

    @Override
    public long getSize(HTTPEnvRequest env) throws UserRequiredException
    {
        return Long.parseLong(getMetaData("size", env));
    }

    @Override
    public Instant getCreationTime(HTTPEnvRequest env) throws UserRequiredException
    {
        return Instant.ofEpochSecond(Long.parseLong(getMetaData("creationdate", env)));
    }

    @Override
    public Instant getLastModified(HTTPEnvRequest env) throws UserRequiredException
    {
        return Instant.ofEpochSecond(Long.parseLong(getMetaData("lastmodified", env)));
    }

    @Override
    public Collection<IResource> listResources(HTTPEnvRequest env) throws UserRequiredException
    {
        try
        {
            return NodeListWrap.getStream(sendRequest(HTTPRequest.create()
                        .setCommand("LIGHTPROPFIND")
                        .setPath(path)
                        .setHeader("Depth", "1")
                        .setHeader("Host", env.getRequest().getHeader("host")))
                    .toXML()
                    .getElementsByTagName("child"))
                    .map(Node::getChildNodes)
                    .map(NodeListWrap::getStream)
                    .map(s -> s.collect(Collectors.toMap(n -> n.getNodeName(), n -> n.getTextContent())))
                    .map(d -> new RsWebDav(ip, port, d))
                    .collect(Collectors.toList());
        }
        catch (Exception ex)
        {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public byte[] getContent(HTTPEnvRequest env) throws UserRequiredException
    {
        try
        {
            return sendRequest(HTTPRequest.create()
                    .setCommand("get")
                    .setPath(path))
                    .toBytes();
        }
        catch (IOException ex)
        {
            return new byte[0];
        }
    }

    @Override
    public void setContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException
    {
        try
        {
            sendRequest(HTTPRequest.create()
                    .setCommand("put")
                    .setPath(path)
                    .setHeader("overwrite", env.getRequest().getHeader("overwrite", "f"))
                    .setContent(content));
        }
        catch (IOException ex)
        { }
    }

    @Override
    public void appendContent(byte[] content, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean delete(HTTPEnvRequest env) throws UserRequiredException
    {
        try
        {
            sendRequest(HTTPRequest.create()
                    .setCommand("delete")
                    .setPath(path));
            
            return true;
        }
        catch (IOException ex)
        {
            return false;
        }
    }

    @Override
    public boolean moveTo(FileSystemPath oldPath, FileSystemPath newPath, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    /*
        try
        {
            System.err.println("oldPath : " + oldPath);
            System.err.println("newPath : " + newPath);
            String host = RsWebDavHelper.getHost(ip, port);
            
            sendRequest(HTTPRequest.create()
                    .setCommand("lightrename")
                    .setPath(path)
                    .setHeader("Destination", "http://" + host + newPath)
                    .setHeader("host", host));
            
            return true;
        }
        catch (IOException ex)
        {
            return false;
        }*/
    }

    @Override
    public boolean rename(String newName, HTTPEnvRequest env) throws UserRequiredException
    {
        try
        {
            this.name = newName;
            
            sendRequest(HTTPRequest.create()
                    .setCommand("lightrename")
                    .setPath(path)
                    .setHeader("Resource-Name", newName));
            
            return true;
        }
        catch (IOException ex)
        {
            return false;
        }
    }

    @Override
    public boolean exists(HTTPEnvRequest env) throws UserRequiredException
    {
        return true;
    }

    @Override
    public IResource creates(ResourceType resourceType, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }

    @Override
    public IResource creates(IResource resource, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new WrongResourceTypeException();
    }

    @Override
    public boolean isOnTheSameFileSystemWith(IResource resource)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addChild(IResource resource, HTTPEnvRequest env)
    {
        if(getResourceType(env) != ResourceType.Directory)
            throw new WrongResourceTypeException();
        return true;
    }

    @Override
    public boolean removeChild(IResource resource, HTTPEnvRequest env)
    {
        if(getResourceType(env) != ResourceType.Directory)
            throw new WrongResourceTypeException();
        return true;
    }

    @Override
    public void setProperty(String namespace, String name, String value, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeProperty(String name, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeProperty(String namespace, String name, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProperty(String name, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProperty(String namespace, String name, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Pair<String, String>, String> getProperties(HTTPEnvRequest env) throws UserRequiredException
    {
        return new HashMap<>();
    }

    @Override
    public Collection<LockKind> getAvailableLocks() throws UserRequiredException
    {
        return Arrays.asList(new LockKind[]
        {
            new LockKind(LockKind.LockScope.Exclusive, LockKind.LockType.write),
            new LockKind(LockKind.LockScope.Shared, LockKind.LockType.write)
        });
    }

    @Override
    public List<Lock> getLocks(HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Lock> getLocks(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean setLock(Lock lock, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean setLock(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeLock(Lock lock, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeLock(String uuid, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canLock(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
