package console;

import http.FileSystemPath;
import http.FileSystemPathManager;
import http.server.HTTPServer;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileNavigationConsole extends Console
{
    public FileNavigationConsole(FileSystemPathManager manager, FileSystemPath root, String prefix)
    {
        super();
        
        this.prefix = prefix;
        this.current = root;
        this.manager = manager;
    }
    public FileNavigationConsole(HTTPServer server, FileSystemPath root)
    {
        super();
        
        this.prefix = "\\\\localhost@" + server.getPort();
        this.current = root;
        this.manager = server.getSettings().getFileSystemPathManager();
    }
    
    private final String prefix;
    private final FileSystemPathManager manager;
    private FileSystemPath current;
    
    public FileSystemPathManager getManager()
    {
        return manager;
    }
    public String getPrefix()
    {
        return prefix;
    }
    public FileSystemPath getCurrent()
    {
        return current;
    }
    
    @Override
    protected String getInputSymbol()
    {
        return current.toString() + " $";
    }
    
    protected File getFile(FileSystemPath path)
    {
        return new File(prefix + path);
    }
    protected File getFile(String path)
    {
        return getFile(getPath(path));
    }
    protected FileSystemPath getPath(String path)
    {
        if(path.startsWith("/") || path.startsWith("\\"))
            return manager.createFromString(path);
        else
            return current.createChild(path);
    }
    
    @Override
    public void run()
    {
        super.run();
    }
    
    
    @Command(name="cd")
    public void cd(String[] params)
    {
        if(params.length == 0)
            current = getPath("/");
        else
            current = getPath(params[0]);
    }
    
    @Command(name="ls")
    public void ls(String[] params)
    {
        File f;
        if(params.length > 0)
            f = getFile(params[0]);
        else
            f = getFile(current);
        
        if(!f.exists())
        {
            println("Resource doesn't exist.");
            return;
        }
        
        if(!f.isDirectory())
        {
            println("Resource is not a directory.");
            return;
        }
        
        String[] fs = f.list();
        if(fs.length == 0)
            println(":: Empty folder");
        else
            Stream.of(fs)
                    .map(s -> "  " + s)
                    .forEach(this::println);
    }
    
    @Command(name="rm")
    public void remove(String[] params)
    {
        Collection<String> fileName = new LinkedList<>();
        boolean recursive = false;
        for(String s : params)
        {
            if(s.toLowerCase() == "-r")
                recursive = true;
            else // end of options
                fileName.add(s);
        }
        
        if(fileName.isEmpty())
        {
            println("File name(s) missing.");
            return;
        }
        
        for(File f : fileName.stream()
                .map(this::getFile)
                .collect(Collectors.toList()))
        {
            if(!f.exists())
                println("Resource \"" + f.getPath() + "\" doesnt' exist.");
            else if(f.isDirectory())
            { // folder
                File[] sub = f.listFiles();
                if(sub.length > 0)
                { // not empty folder
                    if(!recursive)
                    {
                        println("Folder \"" + f.getPath() + "\" is not empty.");
                        continue;
                    }
                    
                    Collection<File> fs = new LinkedList<>(Arrays.asList(sub));
                    while(!fs.isEmpty())
                    {
                        
                    }
                }
                // empty folder
                
            }
            else
            { // file
                
            }
        }
        /*
        fileName.stream()
                .map(this::getFile)
                .filter(f -> f.isFile() || (f.isDirectory() && (f.list().length == 0 || f.list().length > 0 && recursive)))
                .forEach();
        
        if(!f.exists())
        {
            println("Resource doesn't exist." + f.getPath());
            return;
        }
        
        if(f.isDirectory())
        {
            println("Resource is a directory.");
            return;
        }
        
        String[] fs = f.list();
        if(fs.length == 0)
            println(":: Empty folder");
        else
            Stream.of(fs)
                    .map(s -> "  " + s)
                    .forEach(this::println);*/
    }
}
