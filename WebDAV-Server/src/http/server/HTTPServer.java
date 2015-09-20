package http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.function.Supplier;
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
     * @param portAutoScan Use port scan if the 'port' parameter can't be opened
     */
    public HTTPServer(
            int port,
            HTTPServerSettings settings,
            boolean useSSL,
            boolean portAutoScan,
            Supplier<Boolean> continueRunning,
            int checkForExitTime)
    {
        this.port = port;
        this.settings = settings;
        this.portAutoScan = portAutoScan;
        this.useSSL = useSSL;
        this.continueRunning = continueRunning;
        this.checkForExitTime = checkForExitTime;
        
        this.server = null;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Properties">
    private final boolean portAutoScan;
    private final boolean useSSL;
    private final int port;
    private final HTTPServerSettings settings;
    private final Supplier<Boolean> continueRunning;
    private final int checkForExitTime;
    
    private String[] cipherSuites = null;
    private SSLServerSocketFactory factory = null;
    private ServerSocket server;
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="Builder">
    public static Builder create()
    {
        return new Builder();
    }
    public static class Builder
    {
        public Builder()
        { }
        
        private boolean portAutoScan = false;
        private boolean useSSL = false;
        private int port = 2000;
        private HTTPServerSettings settings = null;
        private Supplier<Boolean> continueRunning = null;
        private int checkForExitTime = -1;
        
        public Builder setPort(int port)
        {
            this.port = port;
            return this;
        }
        public Builder setPortAutoScan(boolean portAutoScan)
        {
            this.portAutoScan = portAutoScan;
            return this;
        }
        public Builder setUseSSL(boolean useSSL)
        {
            this.useSSL = useSSL;
            return this;
        }
        public Builder setSettings(HTTPServerSettings settings)
        {
            this.settings = settings;
            return this;
        }
        public Builder setContinueRunningSupplier(Supplier<Boolean> continueRunning)
        {
            this.continueRunning = continueRunning;
            return this;
        }
        public Builder setCheckForExitTime(int checkForExitTime)
        {
            this.checkForExitTime = checkForExitTime;
            return this;
        }
        
        public HTTPServer build()
        {
            if(settings == null)
                settings = HTTPServerSettings.create().build();
            if(continueRunning == null)
                continueRunning = () -> true;
            if(checkForExitTime <= 0)
                checkForExitTime = 5000;
            
            return new HTTPServer(
                    port,
                    settings,
                    useSSL,
                    portAutoScan,
                    continueRunning,
                    checkForExitTime);
        }
    }
    // </editor-fold>
    
    
    
    public Thread toThread()
    {
        return new Thread(this);
    }
    
    
    public HTTPServerSettings getSettings()
    {
        return settings;
    }
    
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
        if(portAutoScan)
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
            
            settings.println("[SERVER] \""
                    + settings.getServer()
                    + "\" [Tout = " + settings.getTimeout()
                    + "s ; Nbmax = " + settings.getMaxNbRequests()
                    + "] started on port <"
                    + this.getPort()
                    + ">");
            
            server.setSoTimeout(checkForExitTime);
            
            do
            {
                try
                {
                    Socket clientSocket = server.accept();
                    if(settings.getSocketFilter().discard(clientSocket))
                        continue;

                    new Thread(new HTTPServerRuntime(clientSocket, settings)).start();
                }
                catch(SocketTimeoutException ex)
                { }
            } while(continueRunning.get());
        }
        catch (Exception ex)
        { }
    }
}
