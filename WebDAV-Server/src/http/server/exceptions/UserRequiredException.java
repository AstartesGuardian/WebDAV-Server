package http.server.exceptions;

public class UserRequiredException extends HTTPException
{
    public UserRequiredException()
    {
        super("User required");
    }
}
