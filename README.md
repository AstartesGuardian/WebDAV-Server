# WebDAV-Server
WebDAV over HTTP - Server - With modules

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

<br><hr><h2><b>Fast code information</b></h2>

<h4>Fast use of a standard WebDAV server :</h4>
```java
HTTPServerSettings settings = new HTTPServerSettings(
  "WebDAV Server",
  HTTPCommand.getStandardCommands(),
  StandardResourceManager.class,
  "<my folder>"
);

HTTPServer s = new HTTPServer(1700, settings);
s.run(); // Run the server
```
<br>
<h4>Fast use of a crypted WebDAV server :</h4>
```java
LocalCryptedResourceManager.loadCipherCrypter(ICrypter.Algorithm.AES_ECB_PKCS5Padding);
LocalCryptedResourceManager.setKey("<my password>");

HTTPServerSettings settings = new HTTPServerSettings(
  "WebDAV Server",
  HTTPCommand.getStandardCommands(),
  LocalCryptedResourceManager.class,
  "<my folder>"
);

HTTPServer s = new HTTPServer(1700, settings);
s.run(); // Run the server
```
It will crypt the file when received before writing it in its file.<br>
It will decrypt the file before sending it to the requester.<br>
This way, the user can synchronize the server with Windows and have a crypted folder which can be used as if it was not secured.<br>
<br>
HTTPServer class implements Runnable, so it can be used this way :
```java
// ...
HTTPServer s = new HTTPServer(1700, settings);
new Thread(s).start(); // Create and run a thread containing the server
// ...
```

<br><hr><h2><b>More information</b></h2>

The modular aspect of the project allow the one using this library to use a personal resource manager.<br>
To do so, you just have to create two classes :<br>
<table>
  <tr>
    <td><b>Interface name</b></td>
    <td><b>Description</b></td>
  </tr>
  <tr>
    <td>IResourceManager</td>
    <td>The class which will be used to create resources.</td>
  </tr>
  <tr>
    <td>IResource</td>
    <td>Manage a specific resource.</td>
  </tr>
</table>

<br><hr><h2><b>Future</b></h2>

* Virtual resources from XML file or a database
* Remote resources (on a remote machine, with a secured transfer)
* Wrap a FTP server to allow FTP servers to be used on Windows as a local folder (FTP servers can't be used as WebDAV servers can be on Windows)
* Complet the lock system

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
