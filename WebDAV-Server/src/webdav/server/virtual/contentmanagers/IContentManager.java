package webdav.server.virtual.contentmanagers;

public interface IContentManager
{
    public byte[] getContent(Object path);
    public long setContent(Object path, byte[] data);
    public long appendContent(Object path, byte[] data);
}
