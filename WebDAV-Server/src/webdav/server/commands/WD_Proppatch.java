package webdav.server.commands;

import http.server.authentication.HTTPUser;
import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import webdav.server.IResource;
import webdav.server.NodeListWrap;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import webdav.server.virtual.entity.VEntity;

public class WD_Proppatch extends HTTPCommand
{
    public WD_Proppatch()
    {
        super("proppatch");
    }
    
    private Node[] getNodes(Document doc, String operationName)
    {
        return NodeListWrap
                .getStream(doc.getElementsByTagNameNS("*", operationName.toLowerCase()))
                .filter(n -> n.hasChildNodes())
                .map(n -> n.getFirstChild())
                .map(n -> NodeListWrap
                .getStream(n.getChildNodes()))
                .flatMap(prop -> prop)
                .toArray(Node[]::new);
    }
    
    
    
    private Document generateResult(Stream<Node> totalStream, IResource f, String host, HTTPUser user) throws UserRequiredException, ParserConfigurationException
    {
        final Document doc = createDocument();

        Element root = doc.createElementNS("DAV:", "multistatus");
        doc.appendChild(root);

        Element response = doc.createElementNS("DAV:", "response");
        root.appendChild(response);

        Element aResponse = doc.createElementNS("DAV:", "href");
        aResponse.setTextContent(getHostPath(f.getPath(user), host));
        response.appendChild(aResponse);

        totalStream
                .forEach(node ->
                {
                    Element propstat = doc.createElementNS("DAV:", "propstat");
                    response.appendChild(propstat);

                    Element status = doc.createElementNS("DAV:", "status");
                    status.setTextContent("HTTP/1.1 200 OK");
                    propstat.appendChild(status);

                    Element prop = doc.createElementNS("DAV:", "prop");
                    propstat.appendChild(prop);

                    Element nodeElement = doc.createElementNS(node.getNamespaceURI(), node.getLocalName());
                    prop.appendChild(nodeElement);
                });
        
        return doc;
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) throws UserRequiredException, NotFoundException 
    {
        HTTPUser user = environment.getUser();
        IResource f = getResource(input.getPath(), environment);
        String host = input.getHeader("host");
        
        byte[] content = new byte[0];
        
        try
        {
            VEntity entity = (VEntity)f;
        
            Document doc = createDocument(input);
            
            doc.getDocumentElement().normalize();
            
            Node[] setStream = getNodes(doc, "set");
            Node[] removeStream = getNodes(doc, "remove");
            
            Stream<Node> totalStream = Stream.concat(Stream.of(setStream), Stream.of(removeStream));
            
            for(Node n : setStream)
                entity.setProperty(n, n.getTextContent(), user);
            for(Node n : removeStream)
                entity.removeProperty(n, user);
            
            content = NodeListWrap.getBytes(generateResult(totalStream, f, host, user));
        }
        catch (ParserConfigurationException | SAXException | IOException | DOMException ex)
        { }
        
        HTTPMessage msg = new HTTPMessage(200, "OK");
        msg.setContent(content);
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
