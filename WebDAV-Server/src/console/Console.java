package console;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Stream;

public abstract class Console implements Runnable
{
    public Console(InputStream in, OutputStream out)
    {
        this.in = in;
        this.out = new PrintStream(out);
    }
    public Console()
    {
        this(System.in, System.out);
    }
    
    public Thread toThread()
    {
        return new Thread(this);
    }
    
    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Command
    {
        public String name();
    }
    
    private boolean stop;
    private final InputStream in;
    private final PrintStream out;
    
    protected String getInputSymbol()
    {
        return "$";
    }
    
    @Override
    public void run()
    {
        stop = false;
        Scanner scanner = new Scanner(in);
        do
        {
            print(getInputSymbol() + " ");
            
            if(!invoke(scanner.nextLine()))
                println("Unkown command.");
        } while(!stop);
    }
    
    protected boolean invoke(String in)
    {
        in = in.trim();
        
        if(in.length() == 0)
            return false;

        String cmd;
        if(in.contains(" "))
            cmd = in.substring(0, in.indexOf(" "));
        else
            cmd = in;

        int paramIndex = cmd.length() + " ".length();
        String[] params;
        if(in.length() > paramIndex)
        {
            String paramsLine = in.substring(paramIndex);
            
            /*
            0: neutral
            1: wait for "
            2: wait for space
            */
            int state = 0;
            boolean escpageChar = false;
            String current = "";
            Collection<String> ps = new LinkedList<>();
            for(char value : paramsLine.toCharArray())
            {
                if(escpageChar)
                {
                    escpageChar = false;
                    current += value;
                }
                else
                    switch(state)
                    {
                        case 0: // neutral
                            switch(value)
                            {
                                case '\\':
                                    escpageChar = true;
                                    state = 2;
                                    break;
                                    
                                case '"':
                                    state = 1;
                                    current = "";
                                    break;

                                case ' ':
                                    // ignore
                                    break;

                                default:
                                    state = 2;
                                    current = "" + value;
                                    break;
                            }
                            break;

                        case 1: // wait for "
                            switch(value)
                            {
                                case '"':
                                    state = 0;
                                    ps.add(current);
                                    break;

                                default:
                                    current += value;
                                    break;
                            }
                            break;

                        case 2: // wait for space
                            switch(value)
                            {
                                case ' ':
                                    state = 0;
                                    ps.add(current);
                                    break;

                                default:
                                    current += value;
                                    break;
                            }
                            break;
                    }
            }
            if(state != 0 && current.length() > 0)
                ps.add(current);
            params = ps.stream().toArray(String[]::new);
        }
        else
            params = new String[0];
        
        return executeCommand(cmd, params);
    }
    
    protected void print(String value)
    {
        out.print(value);
    }
    protected void println(String value)
    {
        out.println(value);
    }
    
    private boolean executeCommand(String command, String[] parameters)
    {
        try
        {
            Method method = Stream.of(getClass().getMethods())
                    .filter(m -> m.isAnnotationPresent(Command.class))
                    .filter(m -> command.trim().toLowerCase().equals(m.getAnnotation(Command.class).name().trim().toLowerCase()))
                    .findFirst()
                    .orElse(null);
            if(method == null)
                return false;
            
            Object result;
            
            if(method.getParameterCount() == 0)
                result = method.invoke(this);
            else
                result = method.invoke(this, (Object)parameters);
            
            if(result != null && result instanceof Boolean)
                stop = (boolean)result;
        }
        catch (Exception ex)
        {
            manageError(ex);
        }
        
        return true;
    }
    
    protected void manageError(Exception ex)
    {
        manageError(ex.getMessage());
    }
    protected void manageError(String text)
    {
        out.println(" /!\\ Error : " + text);
    }
    
    public void stop()
    {
        stop = true;
    }
}
