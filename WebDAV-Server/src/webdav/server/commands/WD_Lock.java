package webdav.server.commands;

import http.server.HTTPCommand;
import http.server.HTTPEnvironment;
import http.server.HTTPMessage;

/**
 *
 * @author Adrien
 */
public class WD_Lock extends HTTPCommand
{
    public WD_Lock()
    {
        super("lock");
    }
    
    @Override
    public HTTPMessage Compute(HTTPMessage input, HTTPEnvironment environment) 
    {
        HTTPMessage msg = new HTTPMessage(200, "OK");
        
        String owner = "owner";
        
        msg.setContent("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \r\n" +
"  <D:prop xmlns:D=\"DAV:\"> \r\n" +
"    <D:lockdiscovery> \r\n" +
"      <D:activelock> \r\n" +
"        <D:locktype><D:write/></D:locktype> \r\n" +
"        <D:lockscope><D:exclusive/></D:lockscope> \r\n" +
"        <D:depth>infinity</D:depth> \r\n" +
"        <D:owner> \r\n" +
"          <D:href>%OWNER%</D:href> \r\n" +
"        </D:owner> \r\n" +
"        <D:timeout>Second-604800</D:timeout> \r\n" +
"        <D:locktoken> \r\n" +
"          <D:href\r\n" +
"          >urn:uuid:e71d4fae-5dec-22d6-fea5-00a0c91e6be4</D:href>\r\n" +
"        </D:locktoken> \r\n" +
"        <D:lockroot> \r\n" +
"          <D:href>%ROOT%</D:href>\r\n" +
"        </D:lockroot> \r\n" +
"      </D:activelock> \r\n" +
"    </D:lockdiscovery> \r\n" +
"  </D:prop>"
        .replace("%ROOT%", input.getPath().substring(input.getPath().indexOf(environment.getRoot()) + environment.getRoot().length()))
        .replace("%OWNER%", owner)
        );
        
        msg.setHeader("Lock-Token", "<urn:uuid:e71d4fae-5dec-22d6-fea5-00a0c91e6be4>");
        
        msg.setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        return msg;
    }
    
}
