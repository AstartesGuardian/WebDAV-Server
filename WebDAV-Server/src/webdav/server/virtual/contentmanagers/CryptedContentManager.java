package webdav.server.virtual.contentmanagers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import webdav.server.Helper;
import webdav.server.crypter.CipherCrypter;
import webdav.server.crypter.ICrypter;

public class CryptedContentManager implements IContentManager
{
    public CryptedContentManager(ICrypter.Algorithm algo, String username, String password)
    {
        try
        {
            crypter = new CipherCrypter(algo);
            crypter.setKey(Helper.toHex(CipherCrypter.sha256(username + ":" + password)));
        }
        catch (Exception ex)
        { }
    }
    
    private transient ICrypter crypter;
    protected ICrypter getCrypter()
    {
        return crypter;
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
    public byte[] getContent(Object path)
    {
        try
        {
            File file = new File(path.toString());
            
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
        catch (Throwable ex)
        {
            return new byte[0];
        }
    }

    @Override
    public long setContent(Object path, byte[] data)
    {
        try
        {
            File file = new File(path.toString());
            
            byte[] cryptedContent = getCrypter().encrypt(data);
            
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
            
            return 8L + 4L + cryptedContent.length;
        }
        catch (Exception ex)
        { }
        
        return 0;
    }

    @Override
    public long appendContent(Object path, byte[] data)
    {
        try
        {
            File file = new File(path.toString());
            
            byte[] cryptedContent = getCrypter().encrypt(data);

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
            
            return 4L + cryptedContent.length;
        }
        catch (Exception ex)
        { }
        
        return 0;
    }
    
}
