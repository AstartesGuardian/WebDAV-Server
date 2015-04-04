package webdav.server.standard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.stream.Stream;
import webdav.server.IResource;

public class StandardResource implements IResource
{
    public StandardResource(String path)
    {
        this.file = new File(path);
        try
        {
            this.fa = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        }
        catch (IOException ex)
        {
            this.fa = null;
        }
    }
    
    protected final File file;
    protected BasicFileAttributes fa;
    

    @Override
    public boolean isFile()
    {
        return file.isFile();
    }

    @Override
    public boolean isDirectory()
    {
        return file.isDirectory();
    }

    @Override
    public String getWebName()
    {
        return file.getName();
    }

    @Override
    public String getName()
    {
        return file.getName();
    }

    @Override
    public String getMimeType()
    {
        try
        {
            return Files.probeContentType(file.toPath());
        }
        catch (IOException ex)
        {
            return null;
        }
    }

    @Override
    public long getSize()
    {
        return fa.size();
    }

    @Override
    public FileTime getCreationTime()
    {
        return fa.creationTime();
    }
    
    @Override
    public Date getLastModified()
    {
        return new Date(file.lastModified());
    }

    @Override
    public IResource[] listResources()
    {
        return Stream.of(file.listFiles())
                .map(f -> new StandardResource(f.getPath()))
                .toArray(IResource[]::new);
    }

    @Override
    public boolean exists()
    {
        return file.exists();
    }

    @Override
    public byte[] getContent()
    {
        try
        {
            return Files.readAllBytes(file.toPath());
        }
        catch (IOException ex)
        {
            return new byte[0];
        }
    }

    @Override
    public void setContent(byte[] content)
    {
        try
        {
            Files.write(file.toPath(), content);
        }
        catch (IOException ex)
        { }
    }

    @Override
    public boolean delete()
    {
        return file.delete();
    }

    @Override
    public boolean renameTo(IResource resource)
    {
        return file.renameTo(((StandardResource)resource).file);
    }

    @Override
    public boolean createDirectory()
    {
        return file.mkdir();
    }

    @Override
    public boolean createFile()
    {
        try
        {
            return file.createNewFile();
        }
        catch (IOException ex)
        {
            return false;
        }
    }
}
