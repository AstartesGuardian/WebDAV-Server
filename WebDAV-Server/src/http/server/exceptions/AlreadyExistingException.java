package http.server.exceptions;

public class AlreadyExistingException extends HTTPException
{
    public AlreadyExistingException()
    {
        super("Resource already existing");
    }
}
