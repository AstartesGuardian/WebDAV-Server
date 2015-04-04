package webdav.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

public class Locker
{
    private static Random rnd = null;
    private static Random getRnd()
    {
        if(rnd == null)
            rnd = new Random();
        return rnd;
    }
    
    private static String expand(String str, int nbChars)
    {
        while(str.length() < nbChars)
            str = "0" + str;
        return str;
    }
    
    private static String node = null;
    private static String getNode()
    {
        if(node == null)
        {
            long n = getRnd().nextLong();
            node = expand(Integer.toHexString((int)((n >> 32)&0xFFFF)), 4) + expand(Integer.toHexString((int)((n)&0xFFFF)), 8);
        }
        return node;
    }
    
    public synchronized static String generateUUID()
    {
        
        long timestamp = new Date().getTime();
        int rndNumber = getRnd().nextInt(0x3FFF) + 0x8000;
        return "urn:uuid:" +
                // time_low
                expand(Integer.toHexString((int)(timestamp&0xFFFFFFFF)), 8) + 
                // time_mid
               '-' + expand(Integer.toHexString((int)((timestamp >> 32)&0xFFFF)), 4) +
                // time_hi_and_version
               '-' + expand(Integer.toHexString((int)((timestamp >> (32+16))&0x0FFF) + 0x1000), 4) +
                // clock_seq_hi_and_reserved
               '-' + expand(Integer.toHexString((rndNumber >> 16)&0xFF), 2) +
                // clock_seq_low
               expand(Integer.toHexString(rndNumber&0xFF), 2) +
                // node
               '-' + getNode();
    }
    
    private static Map<String, List<Lock>> resources = new HashMap<>();
    
    
    public static boolean isLocked(String resource, Lock.LockScope scope, Lock.LockType type)
    {
        List<Lock> locks = resources.getOrDefault(resource, null);
        if(locks == null)
            return false;
        
        Stream<Lock> lockStream = locks.stream();
        
        if(lockStream.anyMatch(l -> l.getScope() == Lock.LockScope.Exclusive && l.getType() == type))
            return true;
        return false;
    }
    
    public static Lock getExisting(String uuid)
    {
        try
        {
            return resources
                    .entrySet()
                    // to Set<Entry<String, List<Lock>>>
                    .stream()
                    .map(e -> e.getValue())
                    // to List<List<Lock>>
                    .filter(l -> l.stream().anyMatch(lock -> uuid.equals(lock.getUUID())))
                    .findFirst()
                    .get()
                    // to List<Lock> (containing uuid)
                    .stream()
                    .filter(lock -> uuid.equals(lock.getUUID()))
                    .findFirst()
                    .orElse(null); // to Lock (corresponding to uuid)
        }
        catch (Exception ex)
        {
            return null;
        }
    }
    public static Lock getExisting(String resource, Lock.LockScope scope, Lock.LockType type)
    {
        return resources
                .getOrDefault(resource, new ArrayList<>())
                .stream()
                .filter(l -> l.getScope() == scope && l.getType() == type)
                .findFirst().orElse(null);
    }
    
    public static void clearOwnerLock(String owner)
    {
        resources.forEach((resource, locks) -> locks.removeIf(lock -> owner.equals(lock.getOwner())));
    }
    public static void clearResourceLock(String resource)
    {
        resources.remove(resource);
    }
    
    public static void removeLock(Lock lock)
    {
        removeLock(lock.getUUID());
    }
    public static void removeLock(String uuid)
    {
        resources.forEach((resource, locks) -> locks.removeIf(lock -> uuid.equals(lock.getUUID())));
    }
    
    public static boolean addLock(String owner, String resource, Lock.LockScope scope, Lock.LockType type)
    {
        if(isLocked(resource, scope, type))
            return false;
        
        List<Lock> locks = resources.getOrDefault(resource, null);
        if(locks == null)
        {
            locks = new ArrayList<>();
            resources.put(resource, locks);
        }
        
        locks.add(new Lock(resource, owner, scope, type));
        
        return true;
    }
}
