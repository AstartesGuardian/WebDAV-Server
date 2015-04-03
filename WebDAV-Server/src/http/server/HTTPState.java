package http.server;

import java.util.stream.Stream;

public abstract class HTTPState
{
    public HTTPState(HTTPCommand[] commands)
    {
        this.commands = Stream.of(commands);
    }
    
    private final Stream<HTTPCommand> commands;
    
    public HTTPMessage Compute(HTTPMessage message, HTTPEnvironment environment)
    {
        HTTPCommand cmd = commands.filter(c -> c.equals(message.getCommand())).findFirst().orElse(null);
        if(cmd != null)
            return cmd.Compute(message, environment);
        else
            return null;
    }
}
