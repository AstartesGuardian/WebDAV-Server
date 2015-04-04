package webdav.server.localCrypted;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CipherCrypter extends ICrypter
{
    public CipherCrypter(Algorithm algo) throws NoSuchAlgorithmException, NoSuchPaddingException
    {
        this(algo.getAlgoName(), algo.getTransformationName(), algo.getKeySize());
    }
    public CipherCrypter(String algoName, String transformationName, int keySize) throws NoSuchAlgorithmException, NoSuchPaddingException
    {
        this.cipher = Cipher.getInstance(algoName);
        this.keySize = keySize;
        this.algoName = algoName;
    }
    
    private SecretKey key;
    private final Cipher cipher;
    private final int keySize;
    private final String algoName;
    
    private byte[] formatSecret(String secret)
    {
        byte[] hash;
        try
        {
            hash = sha256(secret);
        }
        catch (NoSuchAlgorithmException ex)
        { // use another hashing method
            hash = null;
        }
        
        return Arrays.copyOf(hash, keySize);
    }
    
    @Override
    public void setKey(String secret)
    {
        key = new SecretKeySpec(formatSecret(secret), algoName);
    }


    @Override
    public synchronized byte[] encrypt(byte[] data) throws Exception
    {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cryptedData = cipher.doFinal(data);
        return Base64.getEncoder().encode(cryptedData);
    }

    @Override
    public synchronized byte[] decrypt(byte[] cryptedData) throws Exception
    {
        byte[] data64 = Base64.getDecoder().decode(cryptedData);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data64);
    }
}
