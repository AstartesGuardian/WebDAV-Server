package webdav.server.localCrypted;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class ICrypter
{
    public enum Algorithm
    {
        AES_CBC_NoPadding("AES", "AES/CBC/NoPadding", 128/8),
        AES_CBC_PKCS5Padding("AES", "AES/CBC/PKCS5Padding", 128/8),
        AES_ECB_NoPadding("AES", "AES/ECB/NoPadding", 128/8),
        AES_ECB_PKCS5Padding("AES", "AES/ECB/PKCS5Padding", 128/8),
        DES_CBC_NoPadding("DES", "DES/CBC/NoPadding", 56/8),
        DES_CBC_PKCS5Padding("DES", "DES/CBC/PKCS5Padding", 56/8),
        DES_ECB_NoPadding("DES", "DES/ECB/NoPadding", 56/8),
        DES_ECB_PKCS5Padding("DES", "DES/ECB/PKCS5Padding", 56/8),
        DESede_CBC_NoPadding("DESede", "DESede/CBC/NoPadding", 168/8),
        DESede_CBC_PKCS5Padding("DESede", "DESede/CBC/PKCS5Padding", 168/8),
        DESede_ECB_NoPadding("DESede", "DESede/ECB/NoPadding", 168/8),
        DESede_ECB_PKCS5Padding("DESede", "DESede/ECB/PKCS5Padding", 168/8);
        
        private final String algoName;
        private final String transformationName;
        private final int keySize;
        Algorithm(String algoName, String transformationName, int keySize)
        {
            this.algoName = algoName;
            this.transformationName = transformationName;
            this.keySize = keySize;
        }
        
        public String getAlgoName()
        {
            return this.algoName;
        }
        public String getTransformationName()
        {
            return this.transformationName;
        }
        public int getKeySize()
        {
            return this.keySize;
        }
    }
    
    protected static byte[] sha256(String str) throws NoSuchAlgorithmException
    {
        try
        {
            return sha256(str.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
            return null;
        }
    }
    protected static byte[] sha256(byte[] data) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data);
        return md.digest();
    }
    
    public abstract void setKey(String secret);
    public abstract byte[] encrypt(byte[] data) throws Exception;
    public abstract byte[] decrypt(byte[] cryptedData) throws Exception;
}
