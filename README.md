# WebDAV-Server
WebDAV over HTTP - Server - Modular

<b>Information :</b>
<table>
  <tr>
    <td><b>Language</b></td>
    <td>Java [8]</td>
  </tr>
  <tr>
    <td><b>IDE</b></td>
    <td>NetBeans [8]</td>
  </tr>
  <tr>
    <td><b>Standard</b></td>
    <td>WebDAV</td>
  </tr>
  <tr>
    <td><b>Modular</b></td>
    <td>Yes</td>
  </tr>
  <tr>
    <td><b>SSL/TLS</b></td>
    <td>Without certificate only</td>
  </tr>
  <tr>
    <td><b>Windows compatibility</b></td>
    <td>Yes</td>
  </tr>
  <tr>
    <td><b>Linux compatibility</b></td>
    <td>Unknown</td>
  </tr>
  <tr>
    <td><b>Mac OS compatibility</b></td>
    <td>Unknown</td>
  </tr>
</table>
<br>
Wiki : https://github.com/AstartesGuardian/WebDAV-Server/wiki

<br><hr><h2><b>Fast code information</b></h2>

<h4>Fast use of a standard WebDAV server :</h4>
```java
VirtualManager vm;
try
{
	vm = VirtualManager.load(new File("data.vm"));
}
catch (Exception ex)
{
	vm = new VirtualManager();
	VDirectory dir = new VDirectory(vm.getRoot(), "public");
}

vm.setRootDirectory(new File("D:\\Documents\\FTP_TEST"));
vm.addContentManager("direct", new StandardContentManager());

HTTPServerSettings settings = new HTTPServerSettings();
settings.setAllowedCommands(HTTPCommand.getStandardCommands());
settings.setAuthenticationManager(null);
settings.setHTTPVersion(1.1);
settings.setMaxNbRequests(100);
settings.setPrintErrors(true);
settings.setPrintRequests(true);
settings.setPrintResponses(true);
settings.setResourceManager(new VirtualResourceManager(vm));
settings.setRoot("");
settings.setServer("WebDAV Server");
settings.setMaxBufferSize(1048576);
settings.setStepBufferSize(5000);
settings.setTimeout(5);
settings.setUseResourceBuffer(false);


HTTPServer s = new HTTPServer(1700, settings, false, true);
s.run();
```
<br>
<h4>Fast use of a crypted WebDAV server :</h4>
```java
VirtualManager vm;
try
{
    vm = VirtualManager.load(new File("data.vm"));
}
catch (Exception ex)
{
    vm = new VirtualManager();
    VDirectory dir = new VDirectory(vm.getRoot(), "public");
}

vm.setRootDirectory(new File("D:\\Documents\\FTP_TEST"));
// ****** Changed line ******
vm.addContentManager("direct", new CryptedContentManager(ICrypter.Algorithm.AES_CBC_PKCS5Padding, "username", "password"));
// **************************

HTTPServerSettings settings = new HTTPServerSettings();
settings.setAllowedCommands(HTTPCommand.getStandardCommands());
settings.setAuthenticationManager(null);
settings.setHTTPVersion(1.1);
settings.setMaxNbRequests(100);
settings.setPrintErrors(true);
settings.setPrintRequests(true);
settings.setPrintResponses(true);
settings.setResourceManager(new VirtualResourceManager(vm));
settings.setRoot("");
settings.setServer("WebDAV Server");
settings.setMaxBufferSize(1048576);
settings.setStepBufferSize(5000);
settings.setTimeout(5);
settings.setUseResourceBuffer(false);


HTTPServer s = new HTTPServer(1704, settings, false, true);
s.run();
```
It will crypt the content before writing them on the hard drive.<br>
It will decrypt the content of the requested file before sending it to the requester.<br>
This way, the user can synchronize the server with Windows and have a crypted folder which can be used as if it was not secured.<br>
<br>
HTTPServer class implements Runnable, so it can be used this way :
```java
// [...]
HTTPServer s = new HTTPServer(1700, settings);
new Thread(s).start(); // Create and run a thread containing the server
// [...]
```

<br><hr><h2><b>More information</b></h2>

The modular aspect of the project allows the developer to change several things in the behavior of the server with the minimum of code to make.<br>
Related classes of the modular aspect :<br>
<table>
  <tr>
    <td><b>Interface name</b></td>
    <td><b>Description</b></td>
  </tr>
  <tr>
    <td>IResourceManager <br> VirtualResourceManager</td>
    <td>Manage the search and the creation of resources (`getResource`, `createFile`, `createDirectory`).</td>
  </tr>
  <tr>
    <td>VirtualManager</td>
    <td>Input of the local database of resources (list of `IContentManager`, load/save functions, etc).</td>
  </tr>
  <tr>
    <td>IContentManager <br> StandardContentManager <br> CryptedContentManager</td>
    <td>Define how to read/write content relative to resources (`getContent`, `setContent`, `appendContent`).</td>
  </tr>
  <tr>
    <td>IResource <br> VEntry <br> VFile <br> VDirectory <br> VLink</td>
    <td>Provide the information relative to a resource (size, creation date, last modified date, etc).</td>
  </tr>
</table>

<br><hr><h2><b>Future</b></h2>

* Remote resources (on a remote machine, with a secured transfer)
* Wrap a FTP server to allow FTP servers to be used on Windows as a local folder (FTP servers can't be used as WebDAV servers can be on Windows)

<br><hr><h2><b>References</b></h2>

<b>Based on</b> : http://www.ietf.org/rfc/rfc2518.txt
<br><br>
<b>Related sources : </b><br>
[1] http://www.ietf.org/rfc/rfc2518.txt<br>
[2] https://msdn.microsoft.com/en-us/library/ms876446(v=exchg.65).aspx<br>
[3] https://tools.ietf.org/html/rfc4316<br>
[4] http://tools.ietf.org/html/rfc4122<br>
[5] http://tools.ietf.org/html/rfc3339<br>
[6] http://www.webdav.org/specs/rfc4918.html<br>
[7] https://tools.ietf.org/html/rfc2068<br>
[8] http://tools.ietf.org/html/rfc2616<br>
