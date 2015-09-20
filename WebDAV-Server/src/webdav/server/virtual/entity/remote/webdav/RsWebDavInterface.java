package webdav.server.virtual.entity.remote.webdav;

import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import webdav.server.virtual.IniDocument;
import webdav.server.virtual.entity.remote.IRemoteInterface;

public class RsWebDavInterface extends RsWebDav implements IRemoteInterface
{
    public RsWebDavInterface(String path, String name) throws UnknownHostException, MalformedURLException
    {
        this(new URL(path), name);
    }
    public RsWebDavInterface(URL path, String name) throws UnknownHostException
    {
        this(Inet4Address.getByName(path.getHost()).getAddress()
                , path.getPort()
                , path.getPath()
                , name);
    }
    public RsWebDavInterface(byte[] ip, int port, String path, String name)
    {
        super(ip, port, path, name);
    }
    
    @Override
    public boolean rename(String newName, HTTPEnvRequest env) throws UserRequiredException
    {
        super.name = newName;
        return true;
    }

    @Override
    public IniDocument toIniFile()
    {
        return IniDocument.create()
                .setEntry("server", "protocol", "http")
                .setEntry("server", "ip", ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3])
                .setEntry("server", "port", port)
                .setEntry("server", "path", path)
                .build();
    }
}
