package http.server;

import http.server.authentication.HTTPUser;
import http.server.authentication.HTTPAuthenticationManager;
import webdav.server.Helper;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
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
            final int maxBufferSize = environment.getServerSettings().getMaxBufferSize();
            final int bufferStep = environment.getServerSettings().getStepBufferSize();
            
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
                            break;
                        }
                    } while(read == 0);

                    byte[] input = new byte[read];
                    int index = 0;
                    for (byte[] buff : buffers)
                    {
                        System.arraycopy(buff, 0, input, index, buff.length);
                        index += buff.length;
                    }

                    if(environment.getServerSettings().getPrintRequests())
                    {
                        System.out.println("***** REQUEST *****");
                        if(firstTime)
                            System.out.println(new String(input, 0, Math.min(input.length, 2000), "UTF-8"));
                        else
                            System.out.println("[CONTINUE...] : " + read);
                        System.out.println("*** END REQUEST ***");
                        System.out.println();
                    }
                    
                    if(!firstTime && cmd != null || firstTime)
                    {
                        try
                        {
                            if(firstTime || cmd == null)
                            { // First time
                                inputMsg = new HTTPMessage(input, socket, environment.getServerSettings().getAllowedCommands());

                                HTTPUser user = null;
                                if(userManager != null)
                                {
                                    user = userManager.checkAuth(inputMsg);
                                    if(user != null)
                                        environment.setUser(user);
                                }

                                cmd = inputMsg.getCommand();

                                HTTPMessage outputMsg = cmd.Compute(inputMsg, environment);

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
                        catch(UserRequiredException ex)
                        {
                            HTTPMessage outputMsg = new HTTPMessage(401, "Unauthorized");
                            outputMsg.setHeader("WWW-Authenticate", "Digest realm=\"" + userManager.getRealm() + "\", qop=\"auth,auth-int\", nonce=\"" + userManager.generateNonce() + "\", opaque=\"" + userManager.generateNonce() + "\"");
                            
                            output = outputMsg.toBytes();
                        }
                        catch(NotFoundException ex)
                        {
                            HTTPMessage outputMsg = new HTTPMessage(404, "Not found");
                            
                            output = outputMsg.toBytes();
                        }
                    }
                } while(!finishedToRead);
                
                if(output != null)
                {
                    if(environment.getServerSettings().getPrintResponses())
                    {
                        System.out.println("***** RESPONSE *****");
                        System.out.println(new String(output, 0, Math.min(output.length, 2000), "UTF-8"));
                        System.out.println("*** END RESPONSE ***");
                        System.out.println();
                    }
                    
                    out.write(output, 0, output.length);
                    out.flush();
                }
            }
        }
        catch (SocketTimeoutException | SocketException ex)
        { }
        catch (Exception ex)
        {
            if(environment.getServerSettings().getPrintErrors())
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
