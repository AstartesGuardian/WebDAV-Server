package webdav.server.commands;

import http.StringJoiner;
import http.server.HTTPCommand;
import http.server.message.HTTPEnvRequest;
import http.server.message.HTTPResponse;

public class WD_Options extends HTTPCommand
{
    public WD_Options()
    {
        super("options");
    }

    @Override
    public HTTPResponse.Builder Compute(HTTPEnvRequest environment) 
    {
        String cmds = environment.getSettings()
                .getAllowedCommands()
                .stream()
                .map(HTTPCommand::getName)
                .reduce("", StringJoiner.join(", "));
        
        return HTTPResponse.create()
                .setCode(200)
                .setMessage("OK")
                .setHeader("Allow", cmds)
                .setHeader("Content-Type", "httpd/unix-directory");
    }
    
}
