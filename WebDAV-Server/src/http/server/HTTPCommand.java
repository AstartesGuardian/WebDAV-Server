package http.server;

import http.server.message.HTTPMessage;
import http.server.message.HTTPResponse;
import http.ExtendableByteBuffer;
import http.FileSystemPath;
import http.server.exceptions.UnexpectedException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import webdav.server.resource.IResource;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
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
    public abstract HTTPResponse.Builder Compute(HTTPEnvRequest environment) throws UserRequiredException, NotFoundException, UnexpectedException;
    
    public void Continue(HTTPEnvRequest environment) throws UserRequiredException, NotFoundException
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
        return obj instanceof HTTPCommand && this.getName().equals(((HTTPCommand)obj).getName())
                || obj instanceof String && this.getName().equals(obj.toString());
    }

    @Override
    public int hashCode()
    {
        return getName().hashCode();
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
        return new ExtendableByteBuffer()
                .write(content)
                .toXML();
    }
    
    
    
    
    
    
    
    protected String getHostPath(String path, String host)
    {
        return "http://" + (host.replace("/", "") + path.replace("\\", "/")).trim().replace("//", "/").replace(" ", "%20");
    }
    
    
    private final Map<FileSystemPath, IResource> openedResources;
    /*
    protected String getPath(String path, HTTPEnvironment environment)
    {
        return path.replace("/", "\\").trim();
    }
    private IResource getResource_(String path, HTTPEnvRequest environment)
    {
        return environment.getResourceFromPath(getPath(path, environment));
    }*/
    private IResource getNonBufferedResource(FileSystemPath path, HTTPEnvRequest environment)
    {
        return environment.getSettings()
                .getFileManager()
                .getResourceFromPath(path, environment);
    }
    protected IResource getResource(FileSystemPath path, HTTPEnvRequest environment)
    {
        if(environment.getSettings().getUseResourceBuffer())
        {
            if(openedResources.containsKey(path))
                return openedResources.get(path);
            else
            {
                IResource rs = getNonBufferedResource(path, environment);
                openedResources.put(path, rs);
                return rs;
            }
        }
        else
            return getNonBufferedResource(path, environment);
    }
    protected IResource getResource(String path, HTTPEnvRequest environment)
    {
        return getResource(environment.getSettings()
                .getFileSystemPathManager()
                .createFromString(path)
                , environment);
    }
    protected void closeResource(String path, HTTPEnvRequest environment)
    {
        closeResource(environment.getSettings()
                .getFileSystemPathManager()
                .createFromString(path)
                , environment);
    }
    protected void closeResource(FileSystemPath path, HTTPEnvRequest environment)
    {
        if(environment.getSettings().getUseResourceBuffer())
            openedResources.remove(path);
    }
}
