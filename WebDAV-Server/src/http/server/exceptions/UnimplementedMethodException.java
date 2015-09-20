package http.server.exceptions;

public class UnimplementedMethodException extends HTTPException
{
    public UnimplementedMethodException()
    {
        super("Command not found");
    }
}
