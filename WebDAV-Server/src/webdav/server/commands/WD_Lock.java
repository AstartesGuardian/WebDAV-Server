package webdav.server.commands;

import http.FileSystemPath;
import http.server.HTTPCommand;
import http.server.message.HTTPMessage;
import http.server.message.HTTPResponse;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import webdav.server.resource.IResource;
import webdav.server.resource.Lock;
import webdav.server.resource.LockKind;
import webdav.server.resource.LockKind.LockScope;
import webdav.server.resource.LockKind.LockType;
import webdav.server.tools.NodeListWrap;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import org.w3c.dom.Node;
import webdav.server.resource.ResourceType;

public class WD_Lock extends HTTPCommand
{
    public WD_Lock()
    {
        super("lock");
    }
    
    
    private Lock getRequestedLock(HTTPMessage input)
    {
        try
        {
            Document doc = createDocument(input);
            
            
            LockScope lockscope = NodeListWrap
                    .getStream(doc.getElementsByTagNameNS("*", "lockscope"))
                    .filter(Node::hasChildNodes)
                    .map(Node::getFirstChild)
                    .map(Node::getLocalName)
                    .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1).toLowerCase())
                    .map(LockScope::valueOf)
                    .findFirst()
                    .orElse(null);
            
            LockType locktype = NodeListWrap
                    .getStream(doc.getElementsByTagNameNS("*", "locktype"))
                    .filter(Node::hasChildNodes)
                    .map(Node::getFirstChild)
                    .map(Node::getLocalName)
                    .map(String::toLowerCase)
                    .map(LockType::valueOf)
                    .findFirst()
                    .orElse(null);
            
            String owner = NodeListWrap
                    .getStream(doc.getElementsByTagNameNS("*", "owner"))
                    .filter(Node::hasChildNodes)
                    .map(Node::getFirstChild)
                    .filter(n -> "href".equals(n.getLocalName().toLowerCase()))
                    .map(Node::getTextContent)
                    .filter(t -> !t.trim().isEmpty())
                    .findFirst()
                    .orElse(null);
            
            if(lockscope == null || locktype == null)
                return null;
            
            LockKind lk = new LockKind(lockscope, locktype);
            
            if(owner == null || owner.trim().isEmpty())
                return new Lock(lk);
            else
                return new Lock(lk, owner);
        }
        catch (ParserConfigurationException | SAXException | IOException ex)
        {
            return null;
        }
    }
    
    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws NotFoundException, UserRequiredException
    {
        FileSystemPath path = environment.getPath();
        IResource resource = getResource(path, environment);
        
        if(!resource.exists(environment))
        {
            getResource(path.getParent(), environment)
                    .addChild(resource.creates(ResourceType.File, environment), environment);
        }
        
        Lock lock = getRequestedLock(environment.getRequest());
        
        if(lock == null)
            throw new NotFoundException();
        
        LockKind lockKind = lock.getLockKind();
        
        if(!resource.canLock(lockKind, environment))
        {
            return HTTPResponse.create()
                    .setCode(432)
                    .setMessage("Locked")
                    .setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        }
        
        resource.setLock(lock, environment);
        
        try
        {
            Document doc = createDocument();
            
            Element value;
            
        
            Element prop = doc.createElementNS("DAV:", "prop");
            doc.appendChild(prop);
            
            Element lockdiscovery = doc.createElementNS("DAV:", "lockdiscovery");
            prop.appendChild(lockdiscovery);
            
            Element activelock = doc.createElementNS("DAV:", "activelock");
            lockdiscovery.appendChild(activelock);
            
            Element depth = doc.createElementNS("DAV:", "depth");
            depth.setTextContent("infinity");
            activelock.appendChild(depth);
            
            Element timeout = doc.createElementNS("DAV:", "timeout");
            timeout.setTextContent("Second-" + lockKind.getTimeout());
            activelock.appendChild(timeout);
            
            if(lock.getOwner() != null)
            {
                Element owner = doc.createElementNS("DAV:", "owner");
                value = doc.createElementNS("DAV:", "href");
                value.setTextContent(lock.getOwner());
                owner.appendChild(value);
                activelock.appendChild(owner);
            }
            
            Element locktoken = doc.createElementNS("DAV:", "locktoken");
            value = doc.createElementNS("DAV:", "href");
            value.setTextContent(lock.getUUID());
            locktoken.appendChild(value);
            activelock.appendChild(locktoken);
            
            Element lockroot = doc.createElementNS("DAV:", "lockroot");
            value = doc.createElementNS("DAV:", "href");
            value.setTextContent(environment.getRequest().getPath());
            lockroot.appendChild(value);
            activelock.appendChild(lockroot);
            
            Element locktype = doc.createElementNS("DAV:", "locktype");
            value = doc.createElementNS("DAV:", lockKind.getType().name().toLowerCase());
            locktype.appendChild(value);
            activelock.appendChild(locktype);
            
            Element lockscope = doc.createElementNS("DAV:", "lockscope");
            value = doc.createElementNS("DAV:", lockKind.getScope().name().toLowerCase());
            lockscope.appendChild(value);
            activelock.appendChild(lockscope);
            
            
            
            
            return HTTPResponse.create()
                    .setCode(200)
                    .setMessage("OK")
                    .setHeader("Lock-Token", "<" + lock.getUUID() + ">")
                    .setHeader("Content-Type", "text/xml; charset=\"utf-8\"")
                    .setContent(NodeListWrap.getBytes(doc));
        }
        catch (ParserConfigurationException ex)
        {
            throw new NotFoundException();
        }
    }
    
}
