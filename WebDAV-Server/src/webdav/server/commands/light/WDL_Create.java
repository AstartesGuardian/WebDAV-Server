package webdav.server.commands.light;

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

public class WDL_Create extends HTTPCommand
{
    public WDL_Create()
    {
        super("lightcreate");
    }
    
    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws NotFoundException, UserRequiredException
    {
        FileSystemPath path = environment.getPath();
        IResource resource = getResource(path, environment);
        
        if(!resource.exists(environment))
        {
            getResource(path.getParent(), environment)
                    .addChild(resource.creates(ResourceType.valueOf(environment.getRequest()
                            .getHeader("Resource-Type", ResourceType.File.toString()))
                            , environment)
                            , environment);
        }
        
        return HTTPResponse.create()
                .setCode(201)
                .setMessage("Created")
                .setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
    }
    
}
