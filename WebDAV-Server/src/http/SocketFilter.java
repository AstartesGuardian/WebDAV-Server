package http;

import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;

public class SocketFilter
{
    public static Builder create()
    {
        return new Builder();
    }
    public static class Builder
    {
        public Builder()
        { }
        
        private Collection<Function<Socket, Boolean>> rules = new LinkedList<>();
        public void addRule(Function<Socket, Boolean> rule)
        {
            this.rules.add(rule);
        }
        public void addRules(Collection<Function<Socket, Boolean>> rules)
        {
            this.rules.addAll(rules);
        }
        public void addRules(Function<Socket, Boolean>[] rules)
        {
            this.rules.addAll(Arrays.asList(rules));
        }
        public void setRules(Collection<Function<Socket, Boolean>> rules)
        {
            this.rules = new LinkedList<>(rules);
        }
        public void setRules(Function<Socket, Boolean>[] rules)
        {
            this.rules = new LinkedList<>(Arrays.asList(rules));
        }
        
        public SocketFilter build()
        {
            return new SocketFilter(rules);
        }
    }
    
    public SocketFilter(Collection<Function<Socket, Boolean>> rules)
    {
        this.rules = rules;
    }
    
    private final Collection<Function<Socket, Boolean>> rules;
    
    public boolean discard(Socket socket)
    {
        return rules.stream().anyMatch(r -> r.apply(socket));
    }
}
