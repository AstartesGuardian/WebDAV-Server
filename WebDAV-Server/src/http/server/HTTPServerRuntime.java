package http.server;

import webdav.server.Helper;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import webdav.server.commands.WD_Options;

public class HTTPServerRuntime implements Runnable
{
    private final int FULL_REQUEST_TIMEOUT = 50; // ms
    
    public HTTPServerRuntime(Socket socket, HTTPEnvironment environment) throws IOException
    {
        this.socket = socket;
        this.environment = new HTTPEnvironment(environment);
        this.in = new BufferedInputStream(socket.getInputStream());
        this.out = new BufferedOutputStream(socket.getOutputStream());
    }
    
    private final Socket socket;
    private final BufferedInputStream in;
    private final BufferedOutputStream out;
    private final HTTPEnvironment environment;
    
    
    
    
    
    @Override
    public void run()
    {
        try
        {
            HTTPAuthenticationManager userManager = environment.getAuthenticationManager();
            final int maxBufferSize = environment.getResourceManager().getMaxBufferSize();
            final int bufferStep = environment.getResourceManager().getStepBufferSize();
            
            byte[] inputBuffer = new byte[bufferStep];
            int nbInput;
            
            for(int nbRequest = 0; nbRequest < environment.getServerSettings().getMaxNbRequests(); nbRequest++)
            {
                boolean finishedToRead = false;
                boolean firstTime = true;
                HTTPCommand cmd = null;
                HTTPMessage inputMsg = null;
                
                byte[] output = null;
                
                do
                {
                    List<byte[]> buffers = new ArrayList<>();
                    int read = 0;
                    do
                    {
                        socket.setSoTimeout(environment.getServerSettings().getTimeout() * 1000);

                        nbInput = in.read(inputBuffer, 0, inputBuffer.length);
                        if(nbInput > 0)
                        {
                            read += nbInput;
                            buffers.add(Arrays.copyOf(inputBuffer, nbInput));
                        }
                        
                        socket.setSoTimeout(FULL_REQUEST_TIMEOUT);
                        try
                        {
                            while(read <= maxBufferSize)
                            {
                                nbInput = in.read(inputBuffer, 0, inputBuffer.length);
                                if(nbInput > 0)
                                {
                                    read += nbInput;
                                    buffers.add(Arrays.copyOf(inputBuffer, nbInput));
                                }
                            }
                        }
                        catch (Exception ex)
                        {
                            finishedToRead = true;
                        }
                    } while(read == 0);

                    byte[] input = new byte[read];
                    int index = 0;
                    for (byte[] buff : buffers)
                    {
                        System.arraycopy(buff, 0, input, index, buff.length);
                        index += buff.length;
                    }

                    if(environment.getServerSettings().printRequests())
                    {
                        System.out.println("*************************");
                        if(firstTime)
                            System.out.println(new String(input, "UTF-8"));
                        else
                            System.out.println("[CONTINUE...] : " + read);
                        System.out.println("*************************");
                    }
                    
                    if(!firstTime && cmd != null || firstTime)
                    {
                        if(firstTime)
                        { // First time
                            inputMsg = new HTTPMessage(input, socket, environment.getServerSettings().getAllowedCommands());

                            HTTPMessage outputMsg;

                            HTTPAuthentication user = null;
                            if(userManager != null)
                                user = userManager.checkAuth(inputMsg);

                            environment.getResourceManager().setUser(user);

                            if(userManager != null
                                    && !new WD_Options().equals(inputMsg.getCommand())
                                    && environment.createFromPath(inputMsg.getPath()).needsAuthentification(user))
                            {
                                outputMsg = new HTTPMessage(401, "Unauthorized");
                                outputMsg.setHeader("WWW-Authenticate", "Digest realm=\"" + userManager.getRealm() + "\", qop=\"auth,auth-int\", nonce=\"" + userManager.generateNonce() + "\", opaque=\"" + userManager.generateNonce() + "\"");
                            }
                            else
                            {
                                cmd = inputMsg.getCommand();
                                outputMsg = cmd.Compute(inputMsg, environment);
                            }


                            outputMsg.setHeader("Server", environment.getServerSettings().getServer());
                            outputMsg.setHeader("Date", Helper.toString(new Date()));
                            outputMsg.setHeader("Keep-Alive", "timeout=" + environment.getServerSettings().getTimeout() + ", max=" + environment.getServerSettings().getMaxNbRequests());

                            output = outputMsg.toBytes();
                            firstTime = false;
                        }
                        else
                        { // Continue
                            cmd.Continue(inputMsg, input, environment);
                        }
                    }
                } while(!finishedToRead);
                
                out.write(output, 0, output.length);
                out.flush();
            }
        }
        catch (SocketTimeoutException ex)
        { }
        catch (Exception ex)
        {
            if(environment.getServerSettings().printErrors())
                ex.printStackTrace();
        }
        finally
        {
            try
            {
                socket.close();
            }
            catch (Exception ex)
            { }
        }
    }
}
