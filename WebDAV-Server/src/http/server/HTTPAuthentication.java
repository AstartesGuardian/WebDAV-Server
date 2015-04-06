package http.server;

public class HTTPAuthentication
{
    public HTTPAuthentication(String username, String password, int rank)
    {
        this.username = username;
        this.rank = rank;
        this.password = password;
    }
    public HTTPAuthentication(String username, String password)
    {
        this(username, password, 0);
    }
    
    private final String username;
    public String getUserName()
    {
        return username;
    }
    
    private final String password;
    public String getPassword()
    {
        return password;
    }
    
    private final int rank;
    public int gerRank()
    {
        return rank;
    }
}
