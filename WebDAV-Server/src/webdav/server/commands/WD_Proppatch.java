package webdav.server.commands;

import http.FileSystemPath;
import http.server.HTTPCommand;
import http.server.exceptions.UnexpectedException;
import http.server.message.HTTPResponse;
import java.io.IOException;
import java.util.stream.Stream;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import webdav.server.resource.IResource;
import webdav.server.tools.NodeListWrap;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import java.util.Collection;
import java.util.stream.Collectors;

public class WD_Proppatch extends HTTPCommand
{
    public WD_Proppatch()
    {
        super("proppatch");
    }
    
    private Collection<Node> getNodes(Document doc, String operationName)
    {
        return NodeListWrap
                .getStream(doc.getElementsByTagNameNS("*", operationName.toLowerCase()))
                .filter(Node::hasChildNodes)
                .map(Node::getFirstChild)
                .map(Node::getChildNodes)
                .flatMap(ns -> NodeListWrap.getStream(ns))
                .collect(Collectors.toList());
    }
    
    
    
    private Document generateResult(Stream<Node> totalStream, IResource resource, FileSystemPath path, String host, HTTPEnvRequest environment) throws UserRequiredException, ParserConfigurationException
    {
        final Document doc = createDocument();

        Element root = doc.createElementNS("DAV:", "multistatus");
        doc.appendChild(root);

        Element response = doc.createElementNS("DAV:", "response");
        root.appendChild(response);

        Element aResponse = doc.createElementNS("DAV:", "href");
        aResponse.setTextContent(getHostPath(path.toString(), host));
        response.appendChild(aResponse);
        
        totalStream
                .map(node ->
                {
                    Element propstat = doc.createElementNS("DAV:", "propstat");

                    Element status = doc.createElementNS("DAV:", "status");
                    status.setTextContent("HTTP/1.1 200 OK");
                    propstat.appendChild(status);

                    Element prop = doc.createElementNS("DAV:", "prop");
                    propstat.appendChild(prop);

                    Element nodeElement = doc.createElementNS(node.getNamespaceURI(), node.getLocalName());
                    prop.appendChild(nodeElement);
                    
                    return propstat;
                })
                .forEach(response::appendChild);
        
        return doc;
    }
    
    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws UserRequiredException, NotFoundException 
    {
        FileSystemPath path = environment.getPath();
        IResource resource = getResource(path, environment);
        String host = environment.getRequest().getHeader("host");
        
        byte[] content = new byte[0];
        
        try
        {
            Document doc = createDocument(environment.getRequest());
            
            doc.getDocumentElement().normalize();
            
            Collection<Node> setStream = getNodes(doc, "set");
            Collection<Node> removeStream = getNodes(doc, "remove");
            
            Stream<Node> totalStream = Stream.concat(setStream.stream(), removeStream.stream());
            
            for(Node n : setStream)
                resource.setProperty(n.getNamespaceURI(), n.getLocalName(), n.getTextContent(), environment);
            for(Node n : removeStream)
                resource.removeProperty(n.getNamespaceURI(), n.getLocalName(), environment);
            
            content = NodeListWrap.getBytes(generateResult(totalStream, resource, path, host, environment));
        }
        catch (ParserConfigurationException | SAXException | IOException | DOMException ex)
        {
            throw new UnexpectedException(ex);
        }
        
        return HTTPResponse.create()
                .setCode(207)
                .setMessage("Multi-Status ")
                .setContent(content)
                .setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
    }
    
}
