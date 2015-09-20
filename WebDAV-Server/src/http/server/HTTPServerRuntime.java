package http.server;

import http.server.message.HTTPRequest;
import http.server.message.HTTPResponse;
import http.ExtendableByteBuffer;
import http.server.exceptions.AlreadyExistingException;
import http.server.exceptions.DeadResourceException;
import http.server.exceptions.UnexpectedException;
import http.server.exceptions.UnimplementedMethodException;
import webdav.server.tools.Helper;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UserRequiredException;
import http.server.exceptions.WrongResourceTypeException;
import http.server.message.HTTPEnvRequest;

public class HTTPServerRuntime implements Runnable
{
    private final int FULL_REQUEST_TIMEOUT = 50; // ms
    
    public HTTPServerRuntime(Socket socket, HTTPServerSettings settings) throws IOException
    {
        this.socket = socket;
        
        this.settings = settings;
        
        this.in = new BufferedInputStream(socket.getInputStream());
        this.out = new BufferedOutputStream(socket.getOutputStream());
    }
    
    private final Socket socket;
    private final BufferedInputStream in;
    private final BufferedOutputStream out;
    private final HTTPServerSettings settings;
    
    
    
    
    
    
    
    
    boolean finishedToRead = false;
    boolean firstTime = true;
    HTTPCommand cmd = null;
    HTTPRequest inputMsg = null;
    
    
    protected byte[] computeRequest(byte[] input, HTTPEnvRequest.Builder envBuilder) throws UserRequiredException, NotFoundException, UnimplementedMethodException, UnexpectedException
    {
        if(firstTime || cmd == null)
        { // First time
            inputMsg = HTTPRequest.parseHTTPRequest(input);
            
            final HTTPRequest inputMsgFinal = inputMsg;

            cmd = settings
                    .getAllowedCommands()
                    .stream()
                    .filter(c -> c.equals(inputMsgFinal.getCommand()))
                    .findFirst()
                    .orElseThrow(UnimplementedMethodException::new);

            HTTPEnvRequest env = envBuilder
                    .setRequest(inputMsg)
                    .setCommand(cmd)
                    .setBytesReceived(input)
                    .build();
            
            HTTPResponse.Builder outputMsg = settings.getRequestFilters()
                    .stream()
                    .map(f -> f.filter(env))
                    .filter(r -> r != null)
                    .findFirst()
                    .orElseGet(() -> cmd.Compute(env));

            outputMsg.setHeader("Server", settings.getServer())
                     .setHeader("Date", Helper.toString(new Date()))
                     .setHeader("Keep-Alive", "timeout=" + settings.getTimeout() + ", max=" + settings.getMaxNbRequests());

            firstTime = false;
            return outputMsg.build().toBytes();
        }
        else
        { // Continue
            HTTPEnvRequest env = envBuilder
                    .setBytesReceived(input)
                    .build();

            cmd.Continue(env);
            return null;
        }
    }
    
    protected byte[] ExceptionManager(byte[] input, HTTPEnvRequest.Builder envBuilder)
    {
        try
        {
            return computeRequest(input, envBuilder);
        }
        catch(UserRequiredException ex)
        {
            settings.onUserRequiredException().accept(ex);
            return HTTPResponse.create()
                    .setCode(401)
                    .setMessage("Unauthorized")
                    .setHeader("WWW-Authenticate", "Digest realm=\"" + settings.getAuthenticationManager().getRealm() + "\", qop=\"auth,auth-int\", nonce=\"" + settings.getAuthenticationManager().generateNonce() + "\", opaque=\"" + settings.getAuthenticationManager().generateNonce() + "\"")
                    .build()
                    .toBytes();
        }
        catch(NotFoundException ex)
        {
            settings.onNotFoundException().accept(ex);
            return NotFoundException.getHTTPResponse()
                    .build()
                    .toBytes();
        }
        catch(UnimplementedMethodException ex)
        {
            settings.onUnimplementedMethodException().accept(ex);
            return HTTPResponse.create()
                    .setCode(501)
                    .setMessage("Not Implemented")
                    .build()
                    .toBytes();
        }
        catch(UnexpectedException ex)
        {
            settings.onUnexpectedException().accept(ex);
            return HTTPResponse.create()
                    .setCode(500)
                    .setMessage("Internal Server Error")
                    .build()
                    .toBytes();
        }
        catch(AlreadyExistingException ex)
        {
            settings.onAlreadyExistingException().accept(ex);
            return HTTPResponse.create()
                    .setCode(412)
                    .setMessage("Precondition Failed")
                    .build()
                    .toBytes();
        }
        catch(DeadResourceException ex)
        {
            settings.onDeadResourceException().accept(ex);
            throw ex;
        }
        catch(WrongResourceTypeException ex)
        {
            settings.onWrongResourceTypeException().accept(ex);
            throw ex;
        }
    }
    
    
    @Override
    public void run()
    {
        try
        {
            HTTPEnvRequest.Builder envBuilder = HTTPEnvRequest.create()
                    .setSettings(settings);
            
            final int maxBufferSize = settings.getMaxBufferSize();
            final int bufferStep = settings.getStepBufferSize();
            
            for(int nbRequest = 0; nbRequest < settings.getMaxNbRequests(); nbRequest++)
            {
                finishedToRead = false;
                firstTime = true;
                cmd = null;
                inputMsg = null;
                
                byte[] output = null;
                
                do
                {
                    ExtendableByteBuffer ebb = new ExtendableByteBuffer()
                            .setInternalBufferSize(bufferStep);
                    
                    do
                    {
                        socket.setSoTimeout(settings.getTimeout() * 1000);

                        ebb.writeOnce(in);
                        
                        socket.setSoTimeout(FULL_REQUEST_TIMEOUT);
                        try
                        {
                            ebb.write(in, maxBufferSize);
                        }
                        catch (Exception ex)
                        {
                            finishedToRead = true;
                            break;
                        }
                    } while(ebb.length() == 0);
                    
                    byte[] input = ebb.toBytes();

                    if(settings.getPrintRequests())
                    {
                        settings.println("***** REQUEST *****")
                                .println(firstTime ? 
                                        new String(input, 0, Math.min(input.length, 2000), "UTF-8")
                                        : "[CONTINUE...] : " + input.length)
                                .println("*** END REQUEST ***")
                                .println();
                    }
                    
                    if(cmd != null || firstTime)
                    {
                        byte[] result = ExceptionManager(input, envBuilder);
                        if(result != null)
                            output = result;
                    }
                } while(!finishedToRead);
                
                if(output != null)
                {
                    if(settings.getPrintResponses())
                    {
                        settings.println("***** RESPONSE *****")
                                .println(new String(output, 0, Math.min(output.length, 4000), "UTF-8"))
                                .println("*** END RESPONSE ***")
                                .println();
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
            if(settings.getPrintErrors())
                ex.printStackTrace();
            
            settings.getOnError()
                    .accept(ex);
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
