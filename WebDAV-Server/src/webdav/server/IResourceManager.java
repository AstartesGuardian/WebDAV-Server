package webdav.server;

import http.server.exceptions.UserRequiredException;
import http.server.exceptions.NotFoundException;
import http.server.authentication.HTTPUser;

public interface IResourceManager
{
    public IResource getResource(String path, HTTPUser user) throws UserRequiredException, NotFoundException;
    public IResource createFile(String path, HTTPUser user) throws UserRequiredException;
    public IResource createDirectory(String path, HTTPUser user) throws UserRequiredException;
}
