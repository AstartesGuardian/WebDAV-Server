package http.server.exceptions;

public class DeadResourceException extends HTTPException
{
    public DeadResourceException()
    {
        super("This resource is dead");
    }
}
