package http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Stream;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class HTTPServer implements Runnable
{
    public HTTPServer(int port, boolean scanIfNotAvailablePort, boolean useSSL, HTTPServerSettings settings)
    {
        this.port = port;
        this.settings = settings;
        this.scanIfNotAvailablePort = scanIfNotAvailablePort;
        this.useSSL = useSSL;
    }
    public HTTPServer(int port, boolean useSSL, HTTPServerSettings settings)
    {
        this(port, false, useSSL, settings);
    }
    public HTTPServer(int port, HTTPServerSettings settings)
    {
        this(port, false, false, settings);
    }
    
    private final boolean scanIfNotAvailablePort;
    private final boolean useSSL;
    private final int port;
    private final HTTPServerSettings settings;
    
    private String[] cipherSuites = null;
    private SSLServerSocketFactory factory = null;
    private ServerSocket createSocket(int port) throws IOException
    {
        if(useSSL)
        {
            if(factory == null)
            {
                factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
                cipherSuites = Stream.of(factory.getSupportedCipherSuites())/*.filter(c -> c.toLowerCase().contains("dhe"))*/.toArray(String[]::new);
                Stream.of(cipherSuites).forEach(c -> System.out.println(c));
            }
            SSLServerSocket socket = (SSLServerSocket)factory.createServerSocket(port);
            socket.setEnabledCipherSuites(cipherSuites);
            return socket;
        }
        else
            return new ServerSocket(port);
    }
    private ServerSocket openSocket() throws IOException
    {
        if(scanIfNotAvailablePort)
        {
            ServerSocket ss;
            int p = this.port;
            while(true)
            {
                try
                {
                    ss = createSocket(p);
                    break;
                }
                catch (Exception ex)
                { }
            }
            return ss;
        }
        else
            return createSocket(this.port);
    }

    @Override
    public void run()
    {
        try
        {
            ServerSocket server = openSocket();
            HTTPEnvironment environment = new HTTPEnvironment(settings, "C:\\Users\\Adrien\\Documents\\FTP_TEST");
            System.out.println("[SERVER] started on port " + server.getLocalPort());
            
            do
            {
                Socket clientSocket = server.accept();
                
                new Thread(new HTTPServerRuntime(clientSocket, environment)).start();
            } while(true);
        }
        catch (Exception ex)
        { }
    }
}
