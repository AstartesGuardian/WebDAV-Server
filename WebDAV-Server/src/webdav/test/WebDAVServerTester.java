package webdav.test;

import com.sun.glass.ui.Application;
import console.FileNavigationConsole;
import http.FileSystemPathManager;
import http.server.HTTPCommand;
import http.server.HTTPServer;
import http.server.HTTPServerSettings;
import http.server.exceptions.NotFoundException;
import http.server.message.HTTPResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Stream;
import webdav.server.commands.light.WDL_Create;
import webdav.server.commands.light.WDL_Propfind;
import webdav.server.commands.light.WDL_Rename;
import webdav.server.crypter.AbstractCrypter;
import webdav.server.virtual.FileManager;
import webdav.server.virtual.ResourceMutationRemote;
import webdav.server.virtual.contentmutation.CryptedContentMutation;
import webdav.server.virtual.entity.ResourceSetBuilder;
import webdav.server.virtual.entity.local.RsLocal;
import webdav.server.virtual.entity.local.RsLocalDirectory;
import webdav.server.virtual.entity.remote.webdav.RsWebDavInterface;

public class WebDAVServerTester implements Runnable
{
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException
    {
        new WebDAVServerTester().run();
    }
    
    private boolean running = false;
    public boolean isRunning()
    {
        return running;
    }

    @Override
    public void run()
    {
        try
        {
            running = true;
            
            FileManager fm = FileManager.create()
                    .addProperties(RsLocal.createProperties()
                            .setPath("D:\\Documents\\FTP_TEST")
                            .setContentMutation(CryptedContentMutation.create()
                                    .setAlgorithm(AbstractCrypter.Algorithm.AES_CBC_PKCS5Padding)
                                    .setLogin("username")
                                    .setPassword("password")
                                    .setKeyAdditionSupplier((re,e) ->
                                            Stream.of(re)
                                                    .map(r -> r.getCreationTime(e).getEpochSecond())
                                                    .map(value -> new byte[]
                                                    {
                                                        (byte)(value >> (8 * 7)),
                                                        (byte)(value >> (8 * 6)),
                                                        (byte)(value >> (8 * 5)),
                                                        (byte)(value >> (8 * 4)),
                                                        (byte)(value >> (8 * 3)),
                                                        (byte)(value >> (8 * 2)),
                                                        (byte)(value >> (8 * 1)),
                                                        (byte)(value >> (8 * 0))
                                                    })
                                                    .findFirst()
                                                    .get())
                                    .build())
                            .build())
                    .addResourceMutation(new ResourceMutationRemote())
                    .build();
            
            ResourceSetBuilder.create(fm.getRoot())
                        .addFolder(new RsLocalDirectory("public"))
                            .addEmptyFolder(new RsWebDavInterface("http://localhost:1706/public", "remote"))
                            .addEmptyFolder(new RsLocalDirectory("caramel"))
                        .close()
                    .close();
            
            HTTPServer.create()
                    .setPort(1704)
                    .setSettings(HTTPServerSettings.create()
                            .setPrintErrors(true)
                            .setPrintRequests(true)
                            .setPrintResponses(true)
                            .setResourceManager(fm)
                            .setUseResourceBuffer(false)
                            .setVerbose(true)
                            //.onNotFoundException(Throwable::printStackTrace)
                            .addAllowedCommands(HTTPCommand.getStandardCommands())
                            .addAllowedCommand(new WDL_Propfind())
                            .addAllowedCommand(new WDL_Create())
                            .addAllowedCommand(new WDL_Rename())
                            .build())
                    .setPortAutoScan(true)
                    .setContinueRunningSupplier(this::isRunning)
                    .build()
                    .toThread()
                    .start();
            
            
            
            
            
            
            
            
            
            fm = FileManager.create()
                    .addProperties(RsLocal.createProperties()
                            .setPath("D:\\Documents\\FTP_TEST\\_2")
                            .setContentMutation(CryptedContentMutation.create()
                                    .setAlgorithm(AbstractCrypter.Algorithm.AES_CBC_PKCS5Padding)
                                    .setLogin("username")
                                    .setPassword("password")
                                    .setKeyAdditionSupplier((re,e) ->
                                            Stream.of(re)
                                            .map(r -> r.getCreationTime(e).getEpochSecond())
                                            .map(value -> new byte[]
                                            {
                                                (byte)(value >> (8 * 7)),
                                                (byte)(value >> (8 * 6)),
                                                (byte)(value >> (8 * 5)),
                                                (byte)(value >> (8 * 4)),
                                                (byte)(value >> (8 * 3)),
                                                (byte)(value >> (8 * 2)),
                                                (byte)(value >> (8 * 1)),
                                                (byte)(value >> (8 * 0))
                                            })
                                            .findFirst()
                                    .get())
                                    .build())
                            .build())
                    .build();
            
            ResourceSetBuilder.create(fm.getRoot())
                        .addFolder(new RsLocalDirectory("public"))
                            .addFolder(new RsLocalDirectory("caramel"))
                                .addEmptyFolder(new RsLocalDirectory("chocolat"))
                            .close()
                        .close()
                    .close();
            
            HTTPServer.create()
                    .setPort(1706)
                    .setSettings(HTTPServerSettings.create()/*
                            .setPrintErrors(true)
                            .setPrintRequests(true)
                            .setPrintResponses(true)*/
                            .setResourceManager(fm)
                            .setUseResourceBuffer(false)
                            .setVerbose(true)
                            .onNotFoundException(Throwable::printStackTrace)
                            .addAllowedCommands(HTTPCommand.getStandardCommands())
                            .addAllowedCommand(new WDL_Propfind())
                            .addAllowedCommand(new WDL_Create())
                            .addAllowedCommand(new WDL_Rename())
                            .addRequestFilter(r -> r.getPath().getName().equals("desktop.ini") ? NotFoundException.getHTTPResponse() : (HTTPResponse.Builder)null)
                            .build())
                    .setPortAutoScan(true)
                    .setContinueRunningSupplier(this::isRunning)
                    .build()
                    .toThread()
                    .start();
            
            FileSystemPathManager fspm = new FileSystemPathManager(Arrays.asList("/", "\\"), "/");
            
            new WebDavConsole(fspm, fspm.createFromString("/public"), "\\\\localhost@1704")
                    .run();
            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            running = false;
        }
    }
}
