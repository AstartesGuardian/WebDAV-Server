package webdav.server;


public class LockKind
{
    public enum LockType
    {
        write
    }
    public enum LockScope
    {
        Shared,
        Exclusive
    }

    public LockKind(LockScope scope, LockType type)
    {
        this.scope = scope;
        this.type = type;
        this.timeout = 60; // 1 minute
    }
    public LockKind(LockScope scope, LockType type, int timeout)
    {
        this.scope = scope;
        this.type = type;
        this.timeout = timeout;
    }
    
    private final int timeout;
    public int getTimeout()
    {
        return timeout;
    }
    
    private final LockScope scope;
    public LockScope getScope()
    {
        return scope;
    }

    private final LockType type;
    public LockType getType()
    {
        return type;
    }

    @Override
    public int hashCode()
    {
        return scope.hashCode() * 31 + scope.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
            
        if(getClass() != obj.getClass())
            return false;
        
        return this.hashCode() == ((LockKind)obj).hashCode();
    }
}
