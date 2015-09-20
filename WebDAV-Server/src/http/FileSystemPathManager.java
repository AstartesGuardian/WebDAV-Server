package http;

import java.util.Collection;

public class FileSystemPathManager
{
    public FileSystemPathManager(Collection<String> fileSeparators, String standardFileSeparator)
    {
        this.fileSeparators = fileSeparators;
        this.standardFileSeparator = standardFileSeparator;
    }
    
    private final Collection<String> fileSeparators;
    protected final String standardFileSeparator;
    
    public FileSystemPath createFromString(String path)
    {
        for(String sep : fileSeparators)
            path = path.replace(sep, standardFileSeparator);
        
        String[] files = path.split(standardFileSeparator);
        
        if(files.length == 0)
            return new FileSystemPath(path, this);
        
        FileSystemPath fsp = new FileSystemPath(files[0], this);
        
        for(int i = 1; i < files.length; i++)
        {
            fsp = new FileSystemPath(files[i], fsp);
        }
        
        return fsp;
    }
}
