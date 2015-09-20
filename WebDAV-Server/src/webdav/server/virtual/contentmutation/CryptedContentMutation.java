package webdav.server.virtual.contentmutation;

import http.server.message.HTTPEnvRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import webdav.server.tools.Helper;
import webdav.server.crypter.CipherCrypter;
import webdav.server.crypter.AbstractCrypter;
import webdav.server.resource.IResource;

public class CryptedContentMutation implements IContentMutation
{
    public CryptedContentMutation(
            AbstractCrypter.Algorithm algo,
            String username,
            String password,
            BiFunction<IResource, HTTPEnvRequest, byte[]> keyAdditionSupplier) throws Exception
    {
        try
        {
            this.crypter = new CipherCrypter(algo);
            this.crypter.setKey(Helper.toHex(CipherCrypter.sha256(username + ":" + password)));
            this.keyAdditionSupplier = keyAdditionSupplier;
        }
        catch (Exception ex)
        {
            ex.printStackTrace(); throw ex;}
    }
    
    public static Builder create()
    {
        return new Builder();
    }
    public static class Builder
    {
        public Builder()
        { }
        
        private AbstractCrypter.Algorithm algorithm = AbstractCrypter.Algorithm.AES_CBC_PKCS5Padding;
        private String login = null;
        private String password = null;
        private BiFunction<IResource, HTTPEnvRequest, byte[]> keyAdditionSupplier = null;
        
        public Builder setAlgorithm(AbstractCrypter.Algorithm algorithm)
        {
            this.algorithm = algorithm;
            return this;
        }
        public Builder setLogin(String login)
        {
            this.login = login;
            return this;
        }
        public Builder setPassword(String password)
        {
            this.password = password;
            return this;
        }
        public Builder setKeyAdditionSupplier(BiFunction<IResource, HTTPEnvRequest, byte[]> keyAdditionSupplier)
        {
            this.keyAdditionSupplier = keyAdditionSupplier;
            return this;
        }
        
        public CryptedContentMutation build() throws Exception
        {
            if(login == null)
                throw new IllegalStateException("Login parameter not specified ; call "+Builder.class.getName()+".setLogin(...)");
            if(password == null)
                throw new IllegalStateException("Password parameter not specified ; call "+Builder.class.getName()+".setPassword(...)");
            if(keyAdditionSupplier == null)
                keyAdditionSupplier = (x,y) -> new byte[0];
            
            return new CryptedContentMutation(
                    algorithm,
                    login,
                    password,
                    keyAdditionSupplier);
        }
    }
    
    private transient BiFunction<IResource, HTTPEnvRequest, byte[]> keyAdditionSupplier;
    private transient AbstractCrypter crypter;
    protected AbstractCrypter getCrypter()
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
    public byte[] transform(byte[] decryptedContent, IResource resource, HTTPEnvRequest env)
    {
        try
        {
            byte[] cryptedContent = getCrypter().encrypt(decryptedContent, keyAdditionSupplier.apply(resource, env));
            byte[] crypted = new byte[cryptedContent.length + 4];
            
            System.arraycopy(getLen(cryptedContent.length), 0, crypted, 0, 4);
            System.arraycopy(cryptedContent, 0, crypted, 4, cryptedContent.length);
            
            return crypted;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public byte[] reverse(byte[] cryptedContent, IResource resource, HTTPEnvRequest env)
    {
        if(cryptedContent.length == 0)
            return new byte[0];
        
        try
        {
            List<byte[]> data = new ArrayList<>();
            byte[] temp;
            int ptr = 0;
            int tempSize;
            int totalSize = 0;
            
            do
            {
                tempSize = getLen(cryptedContent, ptr);
                ptr += 4;
                
                temp = new byte[tempSize];
                System.arraycopy(cryptedContent, ptr, temp, 0, tempSize);
                
                byte[] d = getCrypter().decrypt(temp, keyAdditionSupplier.apply(resource, env));
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
            ex.printStackTrace();
            return new byte[0];
        }
    }
/*
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
    }*/
    
}
