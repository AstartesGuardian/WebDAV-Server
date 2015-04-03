package http.server;

public class HTTPEnvironment
{
    // <editor-fold defaultstate="collapsed" desc="Constructor(s)">
    public HTTPEnvironment(HTTPServerSettings serverSettings, String root)
    {
        this.serverSettings = serverSettings;
        this.root = root;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Server settings">
    private final HTTPServerSettings serverSettings;
    public HTTPServerSettings getServerSettings()
    {
        return serverSettings;
    }
    // </editor-fold>

    private String root;
    public String getRoot()
    {
        return root;
    }
}
