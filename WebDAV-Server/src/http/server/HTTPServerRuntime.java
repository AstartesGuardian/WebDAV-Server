package http.server;

import webdav.server.Helper;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HTTPServerRuntime implements Runnable
{
    private final int FULL_REQUEST_TIMEOUT = 50; // ms
    
    public HTTPServerRuntime(Socket socket, HTTPEnvironment environment) throws IOException
    {
        this.socket = socket;
        this.environment = environment;
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
            for(int nbRequest = 0; nbRequest < environment.getServerSettings().getMaxNbRequests(); nbRequest++)
            {
                List<byte[]> buffers = new ArrayList<>();
                byte[] inputBuffer = new byte[5000];
                int nbInput;
                int read = 0;
                
                do
                {
                    socket.setSoTimeout(environment.getServerSettings().getTimeout() * 1000);

                    nbInput = in.read(inputBuffer, 0, inputBuffer.length);
                    read += nbInput;
                    buffers.add(Arrays.copyOf(inputBuffer, nbInput));
                    
                    socket.setSoTimeout(FULL_REQUEST_TIMEOUT);
                    try
                    {
                        while(true)
                        {
                            nbInput = in.read(inputBuffer, 0, inputBuffer.length);
                            read += nbInput;
                            buffers.add(Arrays.copyOf(inputBuffer, nbInput));
                        }
                    }
                    catch (Exception ex)
                    { }
                }
                while(read == 0);

                byte[] input = new byte[read];
                int index = 0;
                for (byte[] buff : buffers)
                {
                    System.arraycopy(buff, 0, input, index, buff.length);
                    index += buff.length;
                }

                System.out.println("*************************");
                System.out.println(new String(input, "UTF-8"));
                System.out.println("*************************");
                HTTPMessage inputMsg = new HTTPMessage(input, socket, environment.getServerSettings().getAllowedCommands());

                HTTPMessage outputMsg = inputMsg.getCommand().Compute(inputMsg, environment);

                outputMsg.setHeader("Server", environment.getServerSettings().getServer());
                outputMsg.setHeader("Date", Helper.toString(new Date()));
                outputMsg.setHeader("Keep-Alive", "timeout=" + environment.getServerSettings().getTimeout() + ", max=" + environment.getServerSettings().getMaxNbRequests());

                byte[] output = outputMsg.toBytes();
                out.write(output, 0, output.length);
                out.flush();
            }
        }
        catch (Exception ex)
        { }
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
