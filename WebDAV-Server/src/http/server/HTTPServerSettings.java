package http.server;

import http.FileSystemPathManager;
import http.SocketFilter;
import http.server.authentication.HTTPAuthenticationManager;
import http.server.authentication.HTTPDefaultAuthentication;
import http.server.exceptions.AlreadyExistingException;
import http.server.exceptions.DeadResourceException;
import http.server.exceptions.NotFoundException;
import http.server.exceptions.UnexpectedException;
import http.server.exceptions.UnimplementedMethodException;
import http.server.exceptions.UserRequiredException;
import http.server.exceptions.WrongResourceTypeException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import webdav.server.resource.IResourceManager;
import webdav.server.virtual.FileManager;

public class HTTPServerSettings
{
    public HTTPServerSettings(
            Collection<IRequestFilter> requestFilters,
            SocketFilter socketFilter,
            FileSystemPathManager fileSystemPathManager,
            String standardFileSeparator,
            Collection<String> fileSeparators,
            boolean isVerbose,
            PrintStream verboseInput,
            int stepBufferSize,
            int maxBufferSize,
            boolean useResourceBuffer,
            boolean printResponses,
            boolean printRequests,
            boolean printErrors,
            HTTPAuthenticationManager authenticationManager,
            int maxNbRequests,
            Set<HTTPCommand> allowedCommands,
            double httpVersion,
            String server,
            int timeout,
            FileManager resourceManager,
            
            Consumer<Exception> onError,
            Consumer<UserRequiredException> onUserRequiredException,
            Consumer<UnexpectedException> onUnexpectedException,
            Consumer<UnimplementedMethodException> onUnimplementedMethodException,
            Consumer<WrongResourceTypeException> onWrongResourceTypeException,
            Consumer<DeadResourceException> onDeadResourceException,
            Consumer<AlreadyExistingException> onAlreadyExistingException,
            Consumer<NotFoundException> onNotFoundException)
    {
        this.requestFilters = requestFilters;
        this.socketFilter = socketFilter;
        this.fileSystemPathManager = fileSystemPathManager;
        this.standardFileSeparator = standardFileSeparator;
        this.fileSeparators = fileSeparators;
        this.isVerbose = isVerbose;
        this.verboseInput = verboseInput;
        this.stepBufferSize = stepBufferSize;
        this.maxBufferSize = maxBufferSize;
        this.useResourceBuffer = useResourceBuffer;
        this.printResponses = printResponses;
        this.printRequests = printRequests;
        this.printErrors = printErrors;
        this.authenticationManager = authenticationManager;
        this.maxNbRequests = maxNbRequests;
        this.allowedCommands = allowedCommands;
        this.httpVersion = httpVersion;
        this.server = server;
        this.timeout = timeout;
        this.resourceManager = resourceManager;
        
        this.onError = onError;
        this.onUserRequiredException = onUserRequiredException;
        this.onUnexpectedException = onUnexpectedException;
        this.onUnimplementedMethodException = onUnimplementedMethodException;
        this.onWrongResourceTypeException = onWrongResourceTypeException;
        this.onDeadResourceException = onDeadResourceException;
        this.onAlreadyExistingException = onAlreadyExistingException;
        this.onNotFoundException = onNotFoundException;
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Properties">
    private final Collection<IRequestFilter> requestFilters;
    private final SocketFilter socketFilter;
    private final FileSystemPathManager fileSystemPathManager;
    private final String standardFileSeparator;
    private final Collection<String> fileSeparators;
    private final boolean isVerbose;
    private final PrintStream verboseInput;
    private final int stepBufferSize;
    private final int maxBufferSize;
    private final boolean useResourceBuffer;
    private final boolean printResponses;
    private final boolean printRequests;
    private final boolean printErrors;
    private HTTPAuthenticationManager authenticationManager;
    private final int maxNbRequests;
    private final Set<HTTPCommand> allowedCommands;
    private final double httpVersion;
    private final String server;
    private final int timeout;
    private final FileManager resourceManager;
    
    private final Consumer<Exception> onError;
    private final Consumer<UserRequiredException> onUserRequiredException;
    private final Consumer<UnexpectedException> onUnexpectedException;
    private final Consumer<UnimplementedMethodException> onUnimplementedMethodException;
    private final Consumer<WrongResourceTypeException> onWrongResourceTypeException;
    private final Consumer<DeadResourceException> onDeadResourceException;
    private final Consumer<AlreadyExistingException> onAlreadyExistingException;
    private final Consumer<NotFoundException> onNotFoundException;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Builder">
    public static Builder create()
    {
        return new Builder();
    }
    public static class Builder
    {
        public Builder()
        { }
        
        private SocketFilter socketFilter = null;
        private FileSystemPathManager fileSystemPathManager = null;
        private String standardFileSeparator = null;
        private Collection<String> fileSeparators = new ArrayList<>(Arrays.asList(new String[] { "/", "\\" }));
        private boolean isVerbose = false;
        private PrintStream verboseInput = System.out;
        private int stepBufferSize = 5000;
        private int maxBufferSize = 1048576;
        private boolean useResourceBuffer = true;
        private boolean printResponses = false;
        private boolean printRequests = false;
        private boolean printErrors = false;
        private HTTPAuthenticationManager authenticationManager = null;
        private int maxNbRequests = 1;
        private Set<HTTPCommand> allowedCommands = new HashSet<>(Arrays.asList(HTTPCommand.getStandardCommands()));
        private double httpVersion = 1.1;
        private String server = "WebDav Server";
        private int timeout = 5;
        private FileManager resourceManager = null;
        private Consumer<Exception> onError = null;
        private Consumer<UserRequiredException> onUserRequiredException = null;
        private Consumer<UnexpectedException> onUnexpectedException = null;
        private Consumer<UnimplementedMethodException> onUnimplementedMethodException = null;
        private Consumer<WrongResourceTypeException> onWrongResourceTypeException = null;
        private Consumer<DeadResourceException> onDeadResourceException = null;
        private Consumer<AlreadyExistingException> onAlreadyExistingException = null;
        private Consumer<NotFoundException> onNotFoundException = null;
        private Collection<IRequestFilter> requestFilters = new ArrayList<>();
        
        public Builder setVerbose(boolean isVerbose)
        {
            this.isVerbose = isVerbose;
            return this;
        }
        
        public Builder setRequestFilters(Collection<IRequestFilter> requestFilters)
        {
            this.requestFilters = requestFilters;
            return this;
        }
        public Builder setRequestFilters(IRequestFilter[] requestFilters)
        {
            this.requestFilters = new ArrayList<>(Arrays.asList(requestFilters));
            return this;
        }
        public Builder addRequestFilter(IRequestFilter requestFilter)
        {
            this.requestFilters.add(requestFilter);
            return this;
        }
        public Builder addRequestFilters(Collection<IRequestFilter> requestFilters)
        {
            this.requestFilters.addAll(requestFilters);
            return this;
        }
        public Builder addRequestFilters(IRequestFilter[] requestFilters)
        {
            this.requestFilters.addAll(Arrays.asList(requestFilters));
            return this;
        }
        
        public Builder setVerboseInput(PrintStream verboseInput)
        {
            this.verboseInput = verboseInput;
            return this;
        }
        
        public Builder setResourceManager(FileManager resourceManager)
        {
            this.resourceManager = resourceManager;
            return this;
        }

        public Builder setTimeout(int timeout)
        {
            this.timeout = timeout;
            return this;
        }

        public Builder setServer(String server)
        {
            this.server = server;
            return this;
        }

        public Builder setHTTPVersion(double httpVersion)
        {
            this.httpVersion = httpVersion;
            return this;
        }

        public Builder setAllowedCommands(HTTPCommand[] commands)
        {
            return setAllowedCommands(Arrays.asList(commands));
        }
        public Builder setAllowedCommands(Collection<HTTPCommand> commands)
        {
            allowedCommands = new HashSet<>(commands);
            return this;
        }
        /**
         * Add an allowed command.
         * 
         * @param cmd HTTPCommand to add
         */
        public Builder addAllowedCommand(HTTPCommand cmd)
        {
            allowedCommands.add(cmd);
            return this;
        }
        /**
         * Add a list of allowed commands.
         * 
         * @param cmds Commands to add
         */
        public Builder addAllowedCommands(Collection<HTTPCommand> cmds)
        {
            allowedCommands.addAll(cmds);
            return this;
        }
        public Builder addAllowedCommands(HTTPCommand[] cmds)
        {
            addAllowedCommands(Arrays.asList(cmds));
            return this;
        }

        public Builder setMaxNbRequests(int maxNbRequests)
        {
            this.maxNbRequests = maxNbRequests;
            return this;
        }

        public Builder setAuthenticationManager(HTTPAuthenticationManager authenticationManager)
        {
            this.authenticationManager = authenticationManager;
            return this;
        }

        public Builder setPrintErrors(boolean printErrors)
        {
            this.printErrors = printErrors;
            return this;
        }

        public Builder setPrintRequests(boolean printRequests)
        {
            this.printRequests = printRequests;
            return this;
        }

        public Builder setPrintResponses(boolean printResponses)
        {
            this.printResponses = printResponses;
            return this;
        }

        public Builder setUseResourceBuffer(boolean useResourceBuffer)
        {
            this.useResourceBuffer = useResourceBuffer;
            return this;
        }


        public Builder setMaxBufferSize(int maxBufferSize)
        {
            this.maxBufferSize = maxBufferSize;
            return this;
        }

        public Builder setStepBufferSize(int stepBufferSize)
        {
            this.stepBufferSize = stepBufferSize;
            return this;
        }
        
        public Builder setOnError(Consumer<Exception> onError)
        {
            this.onError = onError;
            return this;
        }
        
        
        public Builder setFileSeparators(String[] fileSeparators)
        {
            this.fileSeparators = Arrays.asList(fileSeparators);
            return this;
        }
        public Builder setFileSeparators(Collection<String> fileSeparators)
        {
            this.fileSeparators = fileSeparators;
            return this;
        }
        public Builder addFileSeparator(String fileSeparator)
        {
            this.fileSeparators.add(fileSeparator);
            return this;
        }
        public Builder addFileSeparators(Collection<String> fileSeparators)
        {
            this.fileSeparators.addAll(fileSeparators);
            return this;
        }
        public Builder addFileSeparators(String[] fileSeparators)
        {
            this.fileSeparators.addAll(Arrays.asList(fileSeparators));
            return this;
        }
        
        public Builder setSocketFilter(SocketFilter socketFilter)
        {
            this.socketFilter = socketFilter;
            return this;
        }
        
        
        public Builder onUserRequiredException(Consumer<UserRequiredException> consumer)
        {
            onUserRequiredException = consumer;
            return this;
        }
        public Builder onUnexpectedException(Consumer<UnexpectedException> consumer)
        {
            onUnexpectedException = consumer;
            return this;
        }
        public Builder onUnimplementedMethodException(Consumer<UnimplementedMethodException> consumer)
        {
            onUnimplementedMethodException = consumer;
            return this;
        }
        public Builder onWrongResourceTypeException(Consumer<WrongResourceTypeException> consumer)
        {
            onWrongResourceTypeException = consumer;
            return this;
        }
        public Builder onDeadResourceException(Consumer<DeadResourceException> consumer)
        {
            onDeadResourceException = consumer;
            return this;
        }
        public Builder onAlreadyExistingException(Consumer<AlreadyExistingException> consumer)
        {
            onAlreadyExistingException = consumer;
            return this;
        }
        public Builder onNotFoundException(Consumer<NotFoundException> consumer)
        {
            onNotFoundException = consumer;
            return this;
        }
        
        
        public HTTPServerSettings build()
        {
            if(socketFilter == null)
                socketFilter = new SocketFilter(Collections.EMPTY_LIST);
            
            if(standardFileSeparator == null)
            {
                standardFileSeparator = fileSeparators.stream().findFirst().get();
                fileSeparators.remove(standardFileSeparator);
            }
            
            if(fileSystemPathManager == null)
                fileSystemPathManager = new FileSystemPathManager(fileSeparators, standardFileSeparator);
            
            if(resourceManager == null)
                throw new IllegalStateException("Resource manager has to be defined, please call : "+Builder.class.getName()+".setResourceManager(...)");
            
            if(onError == null)
                onError = (x) -> {};
            if(onUserRequiredException == null)
                onUserRequiredException = (x) -> {};
            if(onUnexpectedException == null)
                onUnexpectedException = (x) -> {};
            if(onUnimplementedMethodException == null)
                onUnimplementedMethodException = (x) -> {};
            if(onWrongResourceTypeException == null)
                onWrongResourceTypeException = (x) -> {};
            if(onDeadResourceException == null)
                onDeadResourceException = (x) -> {};
            if(onAlreadyExistingException == null)
                onAlreadyExistingException = (x) -> {};
            if(onNotFoundException == null)
                onNotFoundException = (x) -> {};
            
            return new HTTPServerSettings(
                    requestFilters,
                    socketFilter,
                    fileSystemPathManager,
                    standardFileSeparator,
                    fileSeparators,
                    isVerbose,
                    verboseInput,
                    stepBufferSize,
                    maxBufferSize,
                    useResourceBuffer,
                    printResponses,
                    printRequests,
                    printErrors,
                    authenticationManager,
                    maxNbRequests,
                    allowedCommands,
                    httpVersion,
                    server,
                    timeout,
                    resourceManager,
                    onError,
                    onUserRequiredException,
                    onUnexpectedException,
                    onUnimplementedMethodException,
                    onWrongResourceTypeException,
                    onDeadResourceException,
                    onAlreadyExistingException,
                    onNotFoundException);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Accessors">
    public boolean isVerbose()
    {
        return isVerbose;
    }
    
    public Collection<IRequestFilter> getRequestFilters()
    {
        return requestFilters;
    }
    
    Consumer<UserRequiredException> onUserRequiredException()
    {
        return onUserRequiredException;
    }
    Consumer<UnexpectedException> onUnexpectedException()
    {
        return onUnexpectedException;
    }
    Consumer<DeadResourceException> onDeadResourceException()
    {
        return onDeadResourceException;
    }
    Consumer<UnimplementedMethodException> onUnimplementedMethodException()
    {
        return onUnimplementedMethodException;
    }
    Consumer<WrongResourceTypeException> onWrongResourceTypeException()
    {
        return onWrongResourceTypeException;
    }
    Consumer<AlreadyExistingException> onAlreadyExistingException()
    {
        return onAlreadyExistingException;
    }
    Consumer<NotFoundException> onNotFoundException()
    {
        return onNotFoundException;
    }
    
    public SocketFilter getSocketFilter()
    {
        return socketFilter;
    }
    
    public FileSystemPathManager getFileSystemPathManager()
    {
        return fileSystemPathManager;
    }

    public Collection<String> getFileSeparators()
    {
        return fileSeparators;
    }
    public String getStandardFileSeparator()
    {
        return standardFileSeparator;
    }
    
    public PrintStream getVerboseInput()
    {
        return verboseInput;
    }
    
    Consumer<Exception> getOnError()
    {
        return onError;
    }
    
    public HTTPServerSettings println()
    {
        println("");
        return this;
    }
    public HTTPServerSettings println(Object object)
    {
        try
        {
            if(verboseInput != null && isVerbose)
                verboseInput.println(object);
        }
        catch(Throwable t)
        { }
        return this;
    }
    
    /**
     * Generate a file manager from 'iResourceManager' specified in the 
     * HTTPServerSettings constructor.
     * 
     * @return FileManager
     */
    public FileManager getFileManager()
    {
        return resourceManager;
    }
    
    /**
     * Get the timeout in seconds.
     * 
     * @return Timeout (sec)
     */
    public int getTimeout()
    {
        return timeout;
    }
    
    /**
     * Get the server information.
     * 
     * @return String
     */
    public String getServer()
    {
        return server;
    }
    
    /**
     * Get the HTTP version.
     * 
     * @return double
     */
    public double getHTTPVersion()
    {
        return httpVersion;
    }
    
    /**
     * Get the allowed commands.
     * 
     * @return Set of HTTPCommand
     */
    public final Set<HTTPCommand> getAllowedCommands()
    {
        return allowedCommands;
    }

    /**
     * Get the maximum number of requests by TCP connection.
     * 
     * @return int
     */
    public int getMaxNbRequests()
    {
        return maxNbRequests;
    }
    
    protected HTTPAuthenticationManager defaultAuthenticationManager = null;
    /**
     * Get the authentication manager to use.
     * 
     * @return HTTPAuthenticationManager
     */
    public HTTPAuthenticationManager getAuthenticationManager()
    {
        if(authenticationManager == null)
        {
            if(defaultAuthenticationManager == null)
                defaultAuthenticationManager = new HTTPDefaultAuthentication("WebDAV Server Realm");
            authenticationManager = defaultAuthenticationManager;
        }
        return authenticationManager;
    }

    /**
     * Get if the server has to print errors.
     * 
     * @return boolean
     */
    public boolean getPrintErrors()
    {
        return printErrors;
    }

    /**
     * Get if the server has to print requests.
     * 
     * @return boolean
     */
    public boolean getPrintRequests()
    {
        return printRequests;
    }

    /**
     * Get if the server has to print requests.
     * 
     * @return boolean
     */
    public boolean getPrintResponses()
    {
        return printResponses;
    }

    /**
     * Get if the server has to use a resource buffer.
     * With certain kind of resource types, the resource buffer can be a
     * problem (example : a resource type that don't check everytime if the
     * resource exists).
     * 
     * @return boolean
     */
    public boolean getUseResourceBuffer()
    {
        return useResourceBuffer;
    }

    public int getMaxBufferSize()
    {
        return maxBufferSize;
    }

    public int getStepBufferSize()
    {
        return stepBufferSize;
    }
    // </editor-fold>
}
