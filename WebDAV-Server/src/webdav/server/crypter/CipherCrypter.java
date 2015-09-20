package webdav.server.crypter;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CipherCrypter extends AbstractCrypter
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

    protected SecretKey getTemporaryKey(byte[] keyAddition)
    {
        if(keyAddition.length == 0)
            return key;
        
        byte[] encodedKey = key.getEncoded();
        for(int i = 0; i < keyAddition.length; i++)
        {
            encodedKey[i] += keyAddition[(i - encodedKey[i] + Byte.MAX_VALUE) % keyAddition.length];
        }
        return new SecretKeySpec(encodedKey, algoName);
    }

    @Override
    public synchronized byte[] encrypt(byte[] data, byte[] keyAddition) throws Exception
    {
        cipher.init(Cipher.ENCRYPT_MODE, getTemporaryKey(keyAddition));
        byte[] cryptedData = cipher.doFinal(data);
        return Base64.getEncoder().encode(cryptedData);
    }

    @Override
    public synchronized byte[] decrypt(byte[] cryptedData, byte[] keyAddition) throws Exception
    {
        byte[] data64 = Base64.getDecoder().decode(cryptedData);
        cipher.init(Cipher.DECRYPT_MODE, getTemporaryKey(keyAddition));
        return cipher.doFinal(data64);
    }
}
