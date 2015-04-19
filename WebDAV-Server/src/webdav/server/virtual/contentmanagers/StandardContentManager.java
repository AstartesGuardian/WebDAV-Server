package webdav.server.virtual.contentmanagers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class StandardContentManager implements IContentManager
{
    @Override
    public byte[] getContent(Object path)
    {
        try
        {
            return Files.readAllBytes(new File(path.toString()).toPath());
        }
        catch (IOException ex)
        {
            return new byte[0];
        }
    }

    @Override
    public long setContent(Object path, byte[] data)
    {
        try
        {
            Files.write(new File(path.toString()).toPath(), data);
            return data.length;
        }
        catch (IOException ex)
        {
            return 0;
        }
    }

    @Override
    public long appendContent(Object path, byte[] data)
    {
        try
        {
            Files.write(new File(path.toString()).toPath(), data, StandardOpenOption.APPEND);
            return data.length;
        }
        catch (IOException ex)
        {
            return 0;
        }
    }
    
}
