package http.server.authentication;

public class HTTPDefaultAuthentication extends HTTPAuthenticationManager
{
    public HTTPDefaultAuthentication(String realm)
    {
        super(realm);
    }

    @Override
    protected HTTPUser getDefaultUser()
    {
        HTTPUser user = new HTTPUser("Guest", "");
        user.giveAllRights(true);
        return user;
    }

    @Override
    protected HTTPUser getUser(String username)
    {
        HTTPUser user = new HTTPUser(username, "");
        user.giveAllRights(true);
        return user;
    }
}
