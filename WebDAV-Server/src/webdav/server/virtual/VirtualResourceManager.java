package webdav.server.virtual;

import http.server.authentication.HTTPUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;
import webdav.server.IResource;
import webdav.server.IResourceManager;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import webdav.server.virtual.entity.VDirectory;
import webdav.server.virtual.entity.VEntity;
import webdav.server.virtual.entity.VFile;

public class VirtualResourceManager implements IResourceManager
{
    public VirtualResourceManager(VirtualManager virtualManager)
    {
        this.virtualManager = virtualManager;
    }
    protected transient VirtualManager virtualManager;
    
    
    @Override
    public IResource getResource(String path, HTTPUser user) throws UserRequiredException, NotFoundException
    {
        VEntity entity = virtualManager.getFromPath(path);
        if(entity == null)
            throw new NotFoundException();
        else
            return entity;
    }
    
    protected Pair<String, VDirectory> getFromPath(String path)
    {
        List<String> paths = new ArrayList<>(Arrays.asList(path.replace("\\", "/").split("/")));
        VDirectory dir = (VDirectory)virtualManager.getFromPath(paths, 1);
        
        return new Pair<>(paths.remove(0), dir);
    }
    
    @Override
    public IResource createFile(String path, HTTPUser user) throws UserRequiredException
    {
        Pair<String, VDirectory> info = getFromPath(path);
        return new VFile(info.getValue(), info.getKey(), user);
    }
    
    @Override
    public IResource createDirectory(String path, HTTPUser user) throws UserRequiredException
    {
        Pair<String, VDirectory> info = getFromPath(path);
        return new VDirectory(info.getValue(), info.getKey(), user);
    }
}
