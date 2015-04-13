package webdav.server.standard;

import http.server.HTTPAuthentication;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.stream.Stream;
import webdav.server.Helper;
import webdav.server.IResource;

public class StandardResource implements IResource
{
    public StandardResource(String path)
    {
        this.file = new File(path);
    }
    
    protected final File file;
    
    private BasicFileAttributes fa;
    protected BasicFileAttributes getFA()
    {
        if(fa == null)
            try
            {
                this.fa = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            }
            catch (IOException ex)
            {
                this.fa = null;
            }
        return fa;
    }
    
    @Override
    public boolean isVisible()
    {
        return !file.isHidden();
    }

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
        return Helper.toUTF8(file.getName()).trim();
    }

    @Override
    public String getPath(String start)
    {
        try
        {
            String result = file.getAbsolutePath().substring(start.length()).replace("\\", "/");
            if(!result.startsWith("/"))
                return "/" + result;
            else
                return result;
        }
        catch(Exception ex)
        {
            return null;
        }
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
        return getFA().size();
    }

    @Override
    public FileTime getCreationTime()
    {
        return getFA().creationTime();
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
                .filter(f -> f.isVisible())
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
        boolean leave = false;
        for(int i = 0; !leave && i < 1000; i++)
            try
            {
                Files.write(file.toPath(), content);
                leave = true;
            }
            catch (IOException ex)
            { }
    }

    @Override
    public void appendContent(byte[] content)
    {
        boolean leave = false;
        for(int i = 0; !leave && i < 1000; i++)
            try
            {
                Files.write(file.toPath(), content, StandardOpenOption.APPEND);
                leave = true;
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

    @Override
    public boolean needsAuthentification(HTTPAuthentication user)
    {
        return false;
    }
}
