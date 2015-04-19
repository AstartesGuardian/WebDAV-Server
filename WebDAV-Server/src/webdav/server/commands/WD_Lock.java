package webdav.server.commands;

import http.server.authentication.HTTPUser;
import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import webdav.server.IResource;
import webdav.server.Lock;
import webdav.server.LockKind;
import webdav.server.LockKind.LockScope;
import webdav.server.LockKind.LockType;
import webdav.server.NodeListWrap;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import webdav.server.virtual.entity.VEntity;

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
                    .filter(n -> n.hasChildNodes())
                    .map(n -> n.getFirstChild().getLocalName())
                    .map(t -> t.substring(0, 1).toUpperCase()+ t.substring(1).toLowerCase())
                    .map(t -> LockScope.valueOf(t))
                    .findFirst()
                    .orElse(null);
            
            LockType locktype = NodeListWrap
                    .getStream(doc.getElementsByTagNameNS("*", "locktype"))
                    .filter(n -> n.hasChildNodes())
                    .map(n -> n.getFirstChild().getLocalName())
                    .map(t -> t.toLowerCase())
                    .map(t -> LockType.valueOf(t))
                    .findFirst()
                    .orElse(null);
            
            String owner = NodeListWrap
                    .getStream(doc.getElementsByTagNameNS("*", "owner"))
                    .filter(n -> n.hasChildNodes())
                    .map(n -> n.getFirstChild())
                    .filter(n -> "href".equals(n.getLocalName().toLowerCase()))
                    .map(n -> n.getTextContent())
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
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) throws NotFoundException, UserRequiredException
    {
        IResource f;
        String path = input.getPath();
        HTTPUser user = environment.getUser();
        
        try
        {
            f = getResource(path, environment);
        }
        catch (NotFoundException ex)
        {
            f = environment.getResourceManager().createFile(path, user);
        }
        
        if(!(f instanceof VEntity))
            throw new NotFoundException();
        VEntity entity = (VEntity)f;
        
        Lock lock = getRequestedLock(input);
        
        if(lock == null)
            throw new NotFoundException();
        
        LockKind lockKind = lock.getLockKind();
        
        if(!entity.canLock(lockKind, user))
        {
            HTTPMessage msg = new HTTPMessage(423, "Locked");
            msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
            return msg;
        }
        
        entity.setLock(lock, user);
        
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
            value.setTextContent(input.getPurePath());
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
            
            
            
            
            HTTPMessage msg = new HTTPMessage(200, "OK");
            msg.setHeader("Lock-Token", "<" + lock.getUUID() + ">");
            msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
            msg.setContent(NodeListWrap.getBytes(doc));
            return msg;
        }
        catch (ParserConfigurationException ex)
        {
            throw new NotFoundException();
        }
    }
    
}
