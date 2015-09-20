package webdav.server.virtual.entity.standard;

import http.server.exceptions.UserRequiredException;
import http.server.message.HTTPEnvRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.util.Pair;
import webdav.server.resource.Lock;
import webdav.server.resource.LockKind;

public abstract class IRsLocksProperties extends Rs
{
    // <editor-fold defaultstate="collapsed" desc="Locks">
    private transient List<Lock> locks = null;
    
    @Override
    public boolean canLock(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        if(!getAvailableLocks().contains(lockKind))
            return false;
        
        Stream<Lock> flocks = getLocks(env)
                .stream()
                .filter(l -> lockKind.getType().equals(l.getLockKind().getType()));

        switch(lockKind.getScope())
        {
            default:
            case Exclusive:
                return flocks
                        .noneMatch(l -> LockKind.LockScope.Exclusive == l.getLockKind().getScope());

            case Shared:
                return flocks
                        .noneMatch(l -> LockKind.LockScope.Exclusive == l.getLockKind().getScope());
        }
    }
    @Override
    public void removeLock(String uuid, HTTPEnvRequest env) throws UserRequiredException
    {
        getLocks(env).removeIf(l -> uuid.equals(l.getUUID()));
    }
    @Override
    public void removeLock(Lock lock, HTTPEnvRequest env) throws UserRequiredException
    {
        getLocks(env).remove(lock);
    }
    @Override
    public boolean setLock(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        if(!canLock(lockKind, env))
            return false;
        
        getLocks(env).add(new Lock(lockKind));
        return true;
    }
    @Override
    public boolean setLock(Lock lock, HTTPEnvRequest env) throws UserRequiredException
    {
        if(!canLock(lock.getLockKind(), env))
            return false;
        
        getLocks(env).add(lock);
        return true;
    }
    @Override
    public List<Lock> getLocks(LockKind lockKind, HTTPEnvRequest env) throws UserRequiredException
    {
        getLocks(env).removeIf(l -> l.expired());
        return getLocks(env)
                .stream()
                .filter(l -> lockKind.equals(l.getLockKind()))
                .collect(Collectors.toList());
    }
    @Override
    public List<Lock> getLocks(HTTPEnvRequest env) throws UserRequiredException
    {
        if(locks == null)
            locks = new ArrayList<>();
        else
            locks.removeIf(l -> l.expired());
        return locks;
    }
    @Override
    public Collection<LockKind> getAvailableLocks() throws UserRequiredException
    {
        return Arrays.asList(new LockKind[]
        {
            new LockKind(LockKind.LockScope.Exclusive, LockKind.LockType.write),
            new LockKind(LockKind.LockScope.Shared, LockKind.LockType.write)
        });
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Properties">
    private Map<Pair<String, String>, String> properties = new HashMap<>();
    
    @Override
    public void setProperty(String namespace, String name, String value, HTTPEnvRequest env) throws UserRequiredException
    {
        properties.put(new Pair<>(namespace, name), value);
    }
    @Override
    public void removeProperty(String name, HTTPEnvRequest env) throws UserRequiredException
    {
        properties = getProperties(env)
                .entrySet()
                .stream()
                .filter(e -> !name.equals(e.getKey().getValue()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }
    @Override
    public void removeProperty(String namespace, String name, HTTPEnvRequest env) throws UserRequiredException
    {
        properties.remove(new Pair<>(namespace, name));
    }
    @Override
    public String getProperty(String name, HTTPEnvRequest env) throws UserRequiredException
    {
        return getProperties(env)
                .entrySet()
                .stream()
                .filter(e -> name.equals(e.getKey().getValue()))
                .map(e -> e.getValue())
                .findFirst()
                .orElse(null);
    }
    @Override
    public String getProperty(String namespace, String name, HTTPEnvRequest env) throws UserRequiredException
    {
        return getProperties(env)
                .entrySet()
                .stream()
                .filter(e -> name.equals(e.getKey().getValue()) && namespace.equals(e.getKey().getKey()))
                .map(e -> e.getValue())
                .findFirst()
                .orElse(null);
    }
    @Override
    public Map<Pair<String, String>, String> getProperties(HTTPEnvRequest env) throws UserRequiredException
    {
        return properties;
    }
    // </editor-fold>
}
