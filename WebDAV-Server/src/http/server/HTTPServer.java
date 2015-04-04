package http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Stream;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class HTTPServer implements Runnable
{
    /**
     * Create a HTTP Server.
     * 
     * @param port Port to use
     * @param settings Settings of the server
     * @param useSSL Use ANON SSL/TLS
     * @param scanIfNotAvailablePort Use port scan if the 'port' parameter can't be opened
     */
    public HTTPServer(int port, HTTPServerSettings settings, boolean useSSL, boolean scanIfNotAvailablePort)
    {
        this.port = port;
        this.settings = settings;
        this.scanIfNotAvailablePort = scanIfNotAvailablePort;
        this.useSSL = useSSL;
        this.server = null;
    }
    /**
     * Create a HTTP Server.
     * 
     * @param port Port to use
     * @param settings Settings of the server
     * @param useSSL Use ANON SSL/TLS
     */
    public HTTPServer(int port, HTTPServerSettings settings, boolean useSSL)
    {
        this(port, settings, useSSL, false);
    }
    /**
     * Create a HTTP Server.
     * 
     * @param port Port to use
     * @param settings Settings of the server
     */
    public HTTPServer(int port, HTTPServerSettings settings)
    {
        this(port, settings, false, false);
    }
    
    private final boolean scanIfNotAvailablePort;
    private final boolean useSSL;
    private final int port;
    private final HTTPServerSettings settings;
    
    private String[] cipherSuites = null;
    private SSLServerSocketFactory factory = null;
    private ServerSocket server;
    
    /**
     * Get the port used by the server.
     * 
     * @return int
     */
    public int getPort()
    {
        if(server == null)
            return port;
        else
            return server.getLocalPort();
    }
    
    /**
     * Create a ServerSocket. If the 'useSSL' has been specified as true, it
     * will create a SSLServerSocket without certification ("ANON" Cypher Suites).
     * 
     * @param port Port of the ServerSocket
     * @return ServerSocket / SSLServerSocket
     * @throws IOException 
     */
    private ServerSocket createSocket(int port) throws IOException
    {
        if(useSSL)
        {
            if(factory == null)
            {
                factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
                cipherSuites = Stream.of(factory.getSupportedCipherSuites())
                        .filter(c -> c.toLowerCase().contains("anon"))
                        .toArray(String[]::new);
                Stream.of(cipherSuites).forEach(c -> System.out.println(c));
            }
            SSLServerSocket socket = (SSLServerSocket)factory.createServerSocket(port);
            socket.setEnabledCipherSuites(cipherSuites);
            return socket;
        }
        else
            return new ServerSocket(port);
    }
    
    /**
     * Open a ServerSocket on the specified 'port'. If the port can't be opened
     * and 'scanIfNotAvailablePort' has been specified as true, it will loop
     * until an openable port is found.
     * 
     * @return ServerSocket
     * @throws IOException 
     */
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
                    ss = createSocket(p++);
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
            server = openSocket();
            HTTPEnvironment environment = new HTTPEnvironment(settings, settings.getRoot());
            System.out.println("[SERVER] \""
                    + environment.getServerSettings().getServer()
                    + "\" [Tout = " + environment.getServerSettings().getTimeout()
                    + "s ; Nbmax = " + environment.getServerSettings().getMaxNbRequests()
                    + "] started on port <"
                    + this.getPort()
                    + ">");
            
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
