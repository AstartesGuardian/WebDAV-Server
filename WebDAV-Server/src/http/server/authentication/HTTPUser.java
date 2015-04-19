package http.server.authentication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import http.server.exceptions.UserRequiredException;
import java.util.Arrays;

public class HTTPUser implements Serializable
{
    private static final long serialVersionUID = -1L;
    
    public HTTPUser(String username, String password, String... rights)
    {
        this.username = username;
        this.password = password;
        this.rights = new ArrayList<>(Arrays.asList(rights));
    }
    public HTTPUser(String username, String password)
    {
        this(username, password, new String[]
        { // rights : read only
            "webname::get",
            "visible::get",
            "time::creation::get",
            "time::lastmodified::get",
            "path::get",
            "type::get",
            "lock::check",
            "lock::get",
            "property::get",
            "children::get",
            "content::get",
            "size::get",
            "mimetype::get"
        });
    }
    
    private final String username;
    public String getUserName()
    {
        return username;
    }
    
    private final String password;
    public String getPassword()
    {
        return password;
    }
    
    private boolean allRights = false;
    private final List<String> rights;
    private String formatRightName(String rightName)
    {
        while(rightName.contains("  "))
            rightName = rightName.replace("  ", " ");
        return rightName.replace(" ::", "::").replace(":: ", "::").replace(" ", "::").toLowerCase();
    }
    public boolean hasRight(String rightName)
    {
        return this.allRights || rights.contains(formatRightName(rightName));
    }
    public void checkRight(String rightName) throws UserRequiredException
    {
        if(!hasRight(rightName))
            throw new UserRequiredException();
    }
    public void addRight(String rightName)
    {
        rights.add(formatRightName(rightName));
    }
    public void removeRight(String rightName)
    {
        rights.remove(formatRightName(rightName));
    }
    void giveAllRights(boolean allRights)
    {
        this.allRights = allRights;
    }
    
    
    private transient HTTPAuthenticationManager manager = null;
    public HTTPAuthenticationManager getManager()
    {
        return manager;
    }
    public void setManager(HTTPAuthenticationManager manager)
    {
        this.manager = manager;
    }
}
