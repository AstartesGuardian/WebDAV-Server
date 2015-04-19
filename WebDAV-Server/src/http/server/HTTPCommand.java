package http.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import webdav.server.Helper;
import webdav.server.IResource;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import webdav.server.commands.WD_Delete;
import webdav.server.commands.WD_Get;
import webdav.server.commands.WD_Head;
import webdav.server.commands.WD_Lock;
import webdav.server.commands.WD_Mkcol;
import webdav.server.commands.WD_Move;
import webdav.server.commands.WD_Options;
import webdav.server.commands.WD_Post;
import webdav.server.commands.WD_Propfind;
import webdav.server.commands.WD_Proppatch;
import webdav.server.commands.WD_Put;
import webdav.server.commands.WD_Unlock;

public abstract class HTTPCommand
{
    public HTTPCommand(String command)
    {
        this.name = command.trim().toUpperCase();
        openedResources = new HashMap<>();
    }
    
    private final String name;
    
    /**
     * Get the name of the command.
     * 
     * @return String
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Execute the command with the HTTPMessage 'input' and in the 'environment'.
     * 
     * @param input Received HTTP message
     * @param environment Server environment
     * @return HTTPMessage
     * @throws http.server.exceptions.UserRequiredException
     */
    public abstract HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) throws UserRequiredException, NotFoundException;
    
    public void Continue(HTTPMessage input, byte[] data, HTTPEnvironment environment) throws UserRequiredException, NotFoundException
    { }
    
    /**
     * Find a HTTPCommand in 'commands' with the command name 'command'.
     * If no HTTPCommand found, returns null.
     * 
     * @param commands Set of HTTPCommand to search in.
     * @param command Command name to find.
     * @return HTTPCommand
     */
    public static HTTPCommand getFrom(Set<HTTPCommand> commands, String command)
    {
        return commands.stream()
                .filter(c -> c.name.equals(command.trim().toUpperCase()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Generate the command list with : OPTIONS, PROPFIND, PROPPATCH, MKCOL,
     * HEAD, GET, PUT, DELETE, LOCK, UNLOCK and MOVE.
     * 
     * @return HTTPCommand[]
     */
    public static HTTPCommand[] getStandardCommands()
    {
        return new HTTPCommand[]
        {
            new WD_Options(),
            new WD_Propfind(),
            new WD_Proppatch(),
            new WD_Mkcol(),
            new WD_Head(),
            new WD_Post(),
            new WD_Get(),
            new WD_Put(),
            new WD_Delete(),
            new WD_Lock(),
            new WD_Unlock(),
            new WD_Move()
        };
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof HTTPCommand && this.getName().equals(((HTTPCommand)obj).getName());
    }

    @Override
    public String toString()
    {
        return name;
    }
    
    
    
    
    protected Document createDocument() throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder xmlBuilder = factory.newDocumentBuilder();
        return xmlBuilder.newDocument();
    }
    protected Document createDocument(HTTPMessage input) throws ParserConfigurationException, IOException, SAXException
    {
        return createDocument(input.getContent());
    }
    protected Document createDocument(byte[] content) throws ParserConfigurationException, IOException, SAXException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        DocumentBuilder xmlBuilder = factory.newDocumentBuilder();

        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        return xmlBuilder.parse(bais);
    }
    
    
    
    
    
    
    
    protected String getHostPath(String path, String host)
    {
        return "http://" + (host.replace("/", "") + Helper.toUTF8(path.replace("\\", "/"))).replace("//", "/");
    }
    
    
    private final Map<String, IResource> openedResources;
    
    protected String getPath(String path, HTTPEnvironment environment)
    {
        return environment.getRoot() + path.replace("/", "\\").trim();
    }
    private IResource getResource_(String path, HTTPEnvironment environment) throws UserRequiredException, NotFoundException
    {
        return environment.getResourceFromPath(getPath(path, environment));
    }
    protected IResource getResource(String path, HTTPEnvironment environment) throws UserRequiredException, NotFoundException
    {
        if(environment.getServerSettings().getUseResourceBuffer())
        {
            if(openedResources.containsKey(path))
                return openedResources.get(path);
            else
            {
                IResource rs = getResource_(path, environment);
                openedResources.put(path, rs);
                return rs;
            }
        }
        else
            return getResource_(path, environment);
    }
    protected void closeResource(String path)
    {
        openedResources.remove(path);
    }
}
