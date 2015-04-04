package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;
import webdav.server.Lock;
import webdav.server.Locker;

public class WD_Lock extends HTTPCommand
{
    public WD_Lock()
    {
        super("lock");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        String resource = input.getPath();
        
        String content = new String(input.getContent());
        
        String owner = content.substring(content.indexOf("<D:owner><D:href>") + "<D:owner><D:href>".length());
        owner = owner.substring(0, owner.indexOf("</D:href>"));
        
        String scope = content.substring(content.indexOf("<D:lockscope><D:") + "<D:lockscope><D:".length());
        scope = scope.substring(0, scope.indexOf("/></D:lockscope>")).toLowerCase();
        
        String type = content.substring(content.indexOf("<D:locktype><D:") + "<D:locktype><D:".length());
        type = type.substring(0, type.indexOf("/></D:locktype>")).toLowerCase();
        
        Lock.LockScope lockScope = Lock.LockScope.Exclusive;
        switch(scope)
        {
            case "exclusive":
                lockScope = Lock.LockScope.Exclusive;
                break;
            case "shared":
                lockScope = Lock.LockScope.Shared;
                break;
        }
        
        Lock.LockType lockType = Lock.LockType.write;
        switch(type)
        {
            case "write":
                lockType = Lock.LockType.write;
                break;
        }
        
        HTTPMessage msg;
        if(!Locker.addLock(owner, resource, lockScope, lockType))
        {
            msg = new HTTPMessage(423, "Locked");
        }
        else
        {
            msg = new HTTPMessage(200, "OK");
            
            Lock lock = Locker.getExisting(resource, lockScope, lockType);

            String uuid = lock.getUUID();
            String timeout = lock.getTimeout().toString();

            msg.setContent(("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \r\n" +
    "  <D:prop xmlns:D=\"DAV:\"> \r\n" +
    "    <D:lockdiscovery> \r\n" +
    "      <D:activelock> \r\n" +
    "        <D:locktype><D:write/></D:locktype> \r\n" +
    "        <D:lockscope><D:exclusive/></D:lockscope> \r\n" +
    "        <D:depth>infinity</D:depth> \r\n" +
    "        <D:owner> \r\n" +
    "          <D:href>%OWNER%</D:href> \r\n" +
    "        </D:owner> \r\n" +
    "        <D:timeout>Second-%TIMEOUT%</D:timeout> \r\n" +
    "        <D:locktoken> \r\n" +
    "          <D:href>%UUID%</D:href>\r\n" +
    "        </D:locktoken> \r\n" +
    "        <D:lockroot> \r\n" +
    "          <D:href>%ROOT%</D:href>\r\n" +
    "        </D:lockroot> \r\n" +
    "      </D:activelock> \r\n" +
    "    </D:lockdiscovery> \r\n" +
    "  </D:prop>")
            .replace("%ROOT%", resource)
            .replace("%OWNER%", owner)
            .replace("%UUID%", uuid)
            .replace("%TIMEOUT%", timeout)
            );

            msg.setHeader("Lock-Token", "<" + uuid + ">");
        }
        
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
