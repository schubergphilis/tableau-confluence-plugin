package com.schubergphilis.tableau.authentication;

import com.schubergphilis.confluence.exceptions.AuthenticationException;
import com.schubergphilis.util.http.HttpRequest;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: rgerrits
 * Date: 7/14/11
 * Time: 10:20 AM
 */
public class TrustedAuthentication
{
    private String _username;
    private final HttpRequest _httpRequest;
    private String _tableauUrl;

    public TrustedAuthentication(HttpRequest httpRequest)
    {
        _httpRequest = httpRequest;
    }

    public TrustedAuthentication WithUsername(String username)
    {
        _username = username;
        return this;
    }

    public TrustedAuthentication WithTableauUrl(String url)
    {
        _tableauUrl = url;
        return this;
    }

    public String Authenticate() throws AuthenticationException, IOException, NoSuchAlgorithmException, KeyManagementException
    {
        // do post to tableau server, with username
        String unique_id = _httpRequest
                                    .WithUrl(_tableauUrl)
                                    .WithPostParam("username", _username)
                                    .Post();

        if(unique_id != null && unique_id.equals("-1"))
        {
            throw new AuthenticationException("authentication failed for user: " + _username);
        }

        // construct url with unique_id > http://server/trusted/unique_id
        String url = _tableauUrl + "/trusted/" + unique_id;

        // return url
        return url;
    }
}
