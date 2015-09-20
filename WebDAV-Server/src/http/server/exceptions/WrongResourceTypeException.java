package http.server.exceptions;

public class WrongResourceTypeException extends HTTPException
{
    public WrongResourceTypeException()
    {
        super("This resource type can't call this method.");
    }
    public WrongResourceTypeException(String description)
    {
        super(description);
    }
}
