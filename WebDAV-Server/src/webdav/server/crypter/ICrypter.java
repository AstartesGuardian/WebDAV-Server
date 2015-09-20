package webdav.server.crypter;

public interface ICrypter
{
    public void setKey(String secret);
    public byte[] encrypt(byte[] data, byte[] keyAddition) throws Exception;
    public byte[] decrypt(byte[] cryptedData, byte[] keyAddition) throws Exception;
}
