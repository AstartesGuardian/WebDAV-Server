package webdav.server;

import webdav.server.commands.WD_Move;
import webdav.server.commands.WD_Options;
import webdav.server.commands.WD_Delete;
import webdav.server.commands.WD_Lock;
import webdav.server.commands.WD_Mkcol;
import webdav.server.commands.WD_Put;
import webdav.server.commands.WD_Head;
import webdav.server.commands.WD_Unlock;
import webdav.server.commands.WD_Get;
import webdav.server.commands.WD_Propfind;
import webdav.server.commands.WD_Proppatch;
import http.server.HTTPCommand;
import http.server.HTTPServer;
import http.server.HTTPServerSettings;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Stream;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class WebDAVServer
{
    public static void main(String[] args) throws IOException
    {
        HTTPServerSettings settings = new HTTPServerSettings("Apache/2.2.8 (Unix) mod_ssl/2.2.8 OpenSSL/0.9.8g PHP/5.2.6", new HTTPCommand[]
        {
            new WD_Options(),
            new WD_Propfind(),
            new WD_Proppatch(),
            new WD_Mkcol(),
            new WD_Head(),
            new WD_Get(),
            new WD_Put(),
            new WD_Delete(),
            new WD_Lock(),
            new WD_Unlock(),
            new WD_Move()
        });
        HTTPServer s = new HTTPServer(1700, true, settings);
        
        s.run();
    }
}
