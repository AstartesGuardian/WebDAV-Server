package http;

import java.util.Collections;
import java.util.LinkedList;

public class FileSystemPath
{
    public FileSystemPath(String fileName, FileSystemPath parent)
    {
        this.fileName = fileName;
        this.parent = parent;
        this.fileSystemPathManager = parent.fileSystemPathManager;
    }
    public FileSystemPath(String fileName, FileSystemPathManager fileSystemPathManager)
    {
        this.fileName = fileName;
        this.parent = null;
        this.fileSystemPathManager = fileSystemPathManager;
    }
    
    private final String fileName;
    private final FileSystemPath parent;
    private final FileSystemPathManager fileSystemPathManager;
    
    public String getName()
    {
        return fileName;
    }
    
    public FileSystemPath getParent()
    {
        return parent;
    }
    
    public boolean isRoot()
    {
        return parent == null;
    }
    
    public FileSystemPath createChild(String childName)
    {
        return new FileSystemPath(childName, this);
    }
    
    
    public LinkedList<FileSystemPath> toPaths()
    {
        LinkedList<FileSystemPath> paths = toReversePaths();
        Collections.reverse(paths);
        return paths;
    }
    public LinkedList<FileSystemPath> toReversePaths()
    {
        LinkedList<FileSystemPath> paths = new LinkedList<>();
        
        FileSystemPath path = this;
        do
        {
            paths.add(path);
        } while((path = path.getParent()) != null && !path.isRoot());
        
        return paths;
    }
    
    public String[] toStrings()
    {
        return toString().split(fileSystemPathManager.standardFileSeparator);
    }

    @Override
    public String toString()
    {
        if(isRoot())
            return getName();
        
        String result = getParent() + fileSystemPathManager.standardFileSeparator + getName();
        if(result.startsWith(fileSystemPathManager.standardFileSeparator + fileSystemPathManager.standardFileSeparator))
            result = result.substring(1);
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof FileSystemPath && o.hashCode() == this.hashCode();
    }
    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }
}
