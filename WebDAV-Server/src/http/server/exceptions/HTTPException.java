package http.server.exceptions;

public class HTTPException extends RuntimeException
{
    public HTTPException(String name)
    {
        super(name);
    }
    public HTTPException(String name, Throwable ex)
    {
        super(name, ex);
    }
}
