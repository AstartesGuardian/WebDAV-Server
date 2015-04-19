package http.server.exceptions;

public class NotFoundException extends HTTPException
{
    public NotFoundException()
    {
        super("Resource not found");
    }
}
