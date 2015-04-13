package webdav.server.localCrypted;

import http.server.HTTPAuthentication;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import webdav.server.IResource;
import webdav.server.standard.StandardResource;

public class LocalCryptedResource extends StandardResource
{
    public LocalCryptedResource(String path, ICrypter crypter)
    {
        super(path);
        
        this.crypter = crypter;
    }
    
    private ICrypter crypter = null;
    protected ICrypter getCrypter()
    {
        return crypter;
    }
    
    private void updateSize(long size)
    {
        try
        {
            RandomAccessFile f = new RandomAccessFile(file, "rw");
            f.seek(0);
            f.write(toBytes(size));
            f.close();
        }
        catch (Exception ex)
        { }
    }
    
    @Override
    public long getSize()
    {
        try
        {
            RandomAccessFile f = new RandomAccessFile(file, "rw");
            f.seek(0);
            byte[] data = new byte[8];
            if(f.read(data, 0, 8) != 8)
            {
                f.close();
                return 0;
            }
            f.close();
            
            long len = toLong(data);
            if(len == 0)
            {
                len = getContent().length;
                if(len > 0)
                    updateSize(len);
            }
            
            return len;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }
    
    @Override
    public IResource[] listResources()
    {
        return Stream.of(file.listFiles())
                .map(f -> new LocalCryptedResource(f.getPath(), getCrypter()))
                .toArray(IResource[]::new);
    }

    @Override
    public byte[] getContent()
    {
        try
        {
            List<byte[]> data = new ArrayList<>();
            byte[] cryptedContent = Files.readAllBytes(file.toPath());
            byte[] temp;
            int ptr = 8;
            int tempSize;
            int totalSize = 0;
            
            do
            {
                tempSize = getLen(cryptedContent, ptr);
                ptr += 4;
                
                temp = new byte[tempSize];
                System.arraycopy(cryptedContent, ptr, temp, 0, tempSize);
                
                byte[] d = getCrypter().decrypt(temp);
                data.add(d);
                
                System.out.println("S : " + d.length);
                totalSize += d.length;
                ptr += tempSize;
            } while(ptr < cryptedContent.length);
            
            byte[] decryptedContent = new byte[totalSize];
            ptr = 0;
            for(byte[] d : data)
            {
                System.arraycopy(d, 0, decryptedContent, ptr, d.length);
                ptr += d.length;
            }
            
            return decryptedContent;
        }
        catch (Exception ex)
        {
            return new byte[0];
        }
    }
    
    private byte[] toBytes(long length)
    {
        return new byte[]
        {
            (byte)(length & 0xFF),
            (byte)((length >> (8*1)) & 0xFF),
            (byte)((length >> (8*2)) & 0xFF),
            (byte)((length >> (8*3)) & 0xFF),
            (byte)((length >> (8*4)) & 0xFF),
            (byte)((length >> (8*5)) & 0xFF),
            (byte)((length >> (8*6)) & 0xFF),
            (byte)((length >> (8*7)) & 0xFF)
        };
    }
    private long toLong(byte[] length)
    {
        return ((long)(length[0] & 0xFF)) +
                ((long)(length[1] & 0xFF) << (8*1)) +
                ((long)(length[2] & 0xFF) << (8*2)) +
                ((long)(length[3] & 0xFF) << (8*3)) +
                ((long)(length[4] & 0xFF) << (8*4)) +
                ((long)(length[5] & 0xFF) << (8*5)) +
                ((long)(length[6] & 0xFF) << (8*6)) +
                ((long)(length[7] & 0xFF) << (8*7));
    }
    private byte[] getLen(int length)
    {
        return new byte[]
        {
            (byte)(length & 0xFF),
            (byte)((length >> (8*1)) & 0xFF),
            (byte)((length >> (8*2)) & 0xFF),
            (byte)((length >> (8*3)) & 0xFF)
        };
    }
    private int getLen(byte[] length, int index)
    {
        return ((int)(length[index] & 0xFF)) +
                ((int)(length[index + 1] & 0xFF) << (8*1)) +
                ((int)(length[index + 2] & 0xFF) << (8*2)) +
                ((int)(length[index + 3] & 0xFF) << (8*3));
    }

    @Override
    public void setContent(byte[] content)
    {
        try
        {
            byte[] cryptedContent = getCrypter().encrypt(content);
            
            boolean leave = false;
            for(int i = 0; !leave && i < 1000; i++)
                try
                {
                    Files.write(file.toPath(), toBytes(0));
                    leave = true;
                }
                catch (Exception ex)
                { }
            
            leave = false;
            for(int i = 0; !leave && i < 1000; i++)
                try
                {
                    Files.write(file.toPath(), getLen(cryptedContent.length), StandardOpenOption.APPEND);
                    leave = true;
                }
                catch (Exception ex)
                { }
            
            leave = false;
            for(int i = 0; !leave && i < 1000; i++)
                try
                {
                    Files.write(file.toPath(), cryptedContent, StandardOpenOption.APPEND);
                    leave = true;
                }
                catch (Exception ex)
                { }
        }
        catch (Exception ex)
        { }
    }

    @Override
    public void appendContent(byte[] content)
    {
        try
        {
            byte[] cryptedContent = getCrypter().encrypt(content);

            boolean leave = false;
            for(int i = 0; !leave && i < 1000; i++)
                try
                {
                    Files.write(file.toPath(), getLen(cryptedContent.length), StandardOpenOption.APPEND);
                    leave = true;
                }
                catch (Exception ex)
                { }

            leave = false;
            for(int i = 0; !leave && i < 1000; i++)
                try
                {
                    Files.write(file.toPath(), cryptedContent, StandardOpenOption.APPEND);
                    leave = true;
                }
                catch (Exception ex)
                { }
        }
        catch (Exception ex)
        { }
    }
    
    @Override
    public boolean needsAuthentification(HTTPAuthentication user)
    {
        return user == null;
    }
}
