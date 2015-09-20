package http.server.exceptions;

public class UnexpectedException extends HTTPException
{
    public UnexpectedException(Throwable ex)
    {
        super("Not expected error", ex);
    }
}
