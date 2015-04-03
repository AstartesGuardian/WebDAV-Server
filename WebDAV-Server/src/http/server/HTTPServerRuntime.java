package http.server;

import webdav.server.Helper;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.omg.CORBA.Environment;

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
            
            System.out.println("************* " + read + " *************");
            System.out.println(new String(input));
            System.out.println("*******************************");
            HTTPMessage inputMsg = new HTTPMessage(input, socket);
            
            HTTPMessage outputMsg = inputMsg.getCommand().Compute(inputMsg, environment);
            
            outputMsg.setHeader("Server", environment.getServerSettings().getServer());
            outputMsg.setHeader("Date", Helper.toString(new Date()));
            outputMsg.setHeader("Keep-Alive", "timeout=5, max=100");
            
            byte[] output = outputMsg.toBytes();
            out.write(output, 0, output.length);
            out.flush();
            
            socket.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace(System.err);
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }
}
