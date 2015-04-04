package webdav.server;

import static webdav.server.Locker.generateUUID;


public class Lock
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

    public Lock(LockScope scope, LockType type)
    {
        this.scope = scope;
        this.type = type;
    }
    public Lock(String resource, String owner, LockScope scope, LockType type)
    {
        this(scope, type);
        this.resource = resource;
        this.owner = owner;
    }
    
    public Long getTimeout()
    {
        return 604800L;
    }
    
    private String owner = null;
    public String getOwner()
    {
        return owner;
    }
    
    private String resource = null;
    public String getResource()
    {
        return resource;
    }

    private String uuid = null;
    public String getUUID()
    {
        if(uuid == null)
            uuid = generateUUID();
        return uuid;
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
}
