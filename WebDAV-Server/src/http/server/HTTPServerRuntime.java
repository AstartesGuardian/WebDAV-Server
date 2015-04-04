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
    public HTTPServerRuntime(Socket socket, HTTPEnvironment environment) throws IOException
    {
        this.socket = socket;
        socket.setSoTimeout(50);
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
            List<byte[]> buffers = new ArrayList<>();
            int read = 0;
            try
            {
                byte[] input = new byte[5000];
                while(true)
                {
                    int nbInput = in.read(input, 0, input.length);
                    read += nbInput;
                    buffers.add(Arrays.copyOf(input, nbInput));
                }
            }
            catch (Exception ex)
            { }
            
            if(read == 0)
                return;
            
            byte[] input = new byte[read];
            int index = 0;
            for (byte[] buff : buffers)
            {
                System.arraycopy(buff, 0, input, index, buff.length);
                index += buff.length;
            }
            
            System.out.println("*************************");
            System.out.println(new String(input));
            System.out.println("*************************");
            HTTPMessage inputMsg = new HTTPMessage(input, socket, environment.getServerSettings().getAllowedCommands());
            
            HTTPMessage outputMsg = inputMsg.getCommand().Compute(inputMsg, environment);
            
            outputMsg.setHeader("Server", environment.getServerSettings().getServer());
            outputMsg.setHeader("Date", Helper.toString(new Date()));
            outputMsg.setHeader("Keep-Alive", "timeout=" + environment.getServerSettings().getTimeout() + ", max=100");
            
            byte[] output = outputMsg.toBytes();
            out.write(output, 0, output.length);
            out.flush();
            
            socket.close();
        }
        catch (Exception ex)
        { }
    }
}
