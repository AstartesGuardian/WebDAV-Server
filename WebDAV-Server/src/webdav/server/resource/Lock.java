package webdav.server.resource;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class Lock
{
    public Lock(LockKind lockKind)
    {
        this.lockKind = lockKind;
        
        // generate dead line
        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.from(Instant.now()));
        cal.add(Calendar.SECOND, lockKind.getTimeout());
        deadlineTime = cal.getTime().toInstant();
    }
    public Lock(LockKind lockKind, String owner)
    {
        this(lockKind);
        this.owner = owner;
    }
    
    private final LockKind lockKind;
    public LockKind getLockKind()
    {
        return lockKind;
    }
    
    private final Instant deadlineTime;
    public boolean expired()
    {
        return deadlineTime.compareTo(Instant.now()) >= 0;
    }
    
    private String owner = null;
    public String getOwner()
    {
        return owner;
    }

    private String uuid = null;
    public String getUUID()
    {
        if(uuid == null)
            uuid = generateUUID();
        return uuid;
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="UUID generator">
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
    private synchronized static String generateUUID()
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
    // </editor-fold>

    @Override
    public int hashCode()
    {
        return getUUID().hashCode();
    }
}
