package http.server;

import java.util.ArrayList;
import java.util.List;

public abstract class HTTPCommand
{
    public HTTPCommand(String command)
    {
        this.name = command.trim().toUpperCase();
        
        commands.add(this);
    }
    
    private final String name;
    
    public abstract HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment);
    
    private static final List<HTTPCommand> commands = new ArrayList<>();
    public static HTTPCommand getFrom(String command)
    {
        return commands.stream()
                .filter(c -> c.name.equals(command.trim().toUpperCase()))
                .findFirst()
                .orElse(null);
    }
    
    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof HTTPCommand && this.getName().equals(((HTTPCommand)obj).getName());
    }

    @Override
    public String toString()
    {
        return name;
    }
}
