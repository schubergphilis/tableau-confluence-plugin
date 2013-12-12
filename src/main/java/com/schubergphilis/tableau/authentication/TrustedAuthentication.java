/**
 * Developer: Roel Gerrits
 * Company: Schuberg Philis
 * Date: 7/14/11
 * Time: 10:20 AM
 *
 *      Licensed to the Apache Software Foundation (ASF) under one
 *      or more contributor license agreements.  See the NOTICE file
 *      distributed with this work for additional information
 *      regarding copyright ownership.  The ASF licenses this file
 *      to you under the Apache License, Version 2.0 (the
 *      "License"); you may not use this file except in compliance
 *      with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing,
 *      software distributed under the License is distributed on an
 *      "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *      KIND, either express or implied.  See the License for the
 *      specific language governing permissions and limitations
 *      under the License.
 **/

package com.schubergphilis.tableau.authentication;

import com.schubergphilis.confluence.exceptions.AuthenticationException;
import com.schubergphilis.util.http.HttpRequest;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class TrustedAuthentication
{
    private String _username;
    private final HttpRequest _httpRequest;
    private String _tableauUrl;
    private String _site;

    public TrustedAuthentication(HttpRequest httpRequest)
    {
        _httpRequest = httpRequest;
    }

    public TrustedAuthentication withUsername(String username)
    {
        _username = username;
        return this;
    }

    public TrustedAuthentication withTableauUrl(String url)
    {
        _tableauUrl = url;
        return this;
    }
    
    public TrustedAuthentication withSite(String site) {
    	_site = site;
    	return this;
    }

    public String authenticate() throws AuthenticationException, IOException, NoSuchAlgorithmException, KeyManagementException
    {
        // do post to tableau server, with username
        _httpRequest.withUrl(_tableauUrl + "/trusted")
                    .withPostParam("username", _username);

        // optional post site to use
        if(_site != null && _site.length() > 0)
        	_httpRequest.withPostParam("target_site", _site);
        
        String unique_id = _httpRequest.post();

        if(unique_id != null && unique_id.equals("-1"))
        {
            throw new AuthenticationException("authentication failed for user: " + _username);
        }

        return unique_id;
    }
}