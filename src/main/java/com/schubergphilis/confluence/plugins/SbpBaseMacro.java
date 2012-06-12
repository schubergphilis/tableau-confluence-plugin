/**
 * Developer: Roel Gerrits
 * Company: Schuberg Philis
 * Date: 3/30/11
 * Time: 10:49 AM
 * Description: This class includes some helper methods that can be used in all sbp macro's
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

package com.schubergphilis.confluence.plugins;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import com.schubergphilis.confluence.exceptions.AuthenticationException;
import com.schubergphilis.confluence.exceptions.ValidationException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public abstract class SbpBaseMacro extends com.atlassian.renderer.v2.macro.BaseMacro
{
    protected WebResourceManager _webResourceManager;
    protected BandanaManager _bandanaManager;

    private final Logger log = Logger.getLogger(this.getClass().getName());

    public SbpBaseMacro()
    {

    }

    private String getParameter(Map params, String key)
    {
        return params.containsKey(key) ? (String) params.get(key) : "";
    }

    protected String getStrParameter(Map params, String key)
    {
        return getStrParameter(params, key, "");
    }

    protected String getStrParameter(Map params, String key, String defaultValue)
    {
        String value = getParameter(params, key);
        if(value==null || value.length()==0)
        {
            return defaultValue;
        }

        return value;
    }

    protected Boolean getBoolParameter(Map params, String key, Boolean defaultValue)
    {
        Boolean value = params.containsKey(key) ? params.get(key).equals("true") : defaultValue;
        return value;
    }

    protected Integer getIntParameter(Map params, String key, Integer defaultValue)
    {
        Integer returnVal = params.containsKey(key) ? Integer.parseInt(getParameter(params,key)) : defaultValue;
        return returnVal;
    }

    public String execute(Map params, String body, RenderContext renderContext) throws MacroException {
        includeResources();
        try
        {
            return RenderPlugin(params, renderContext);
        }
        catch (ValidationException e)
        {
            log.info("validation error: ".concat(e.toString()));
            throw new MacroException("validation error: ".concat(e.getMessage().toString()));
        }
        catch (AuthenticationException e)
        {
            log.error("authentication error: ".concat(e.getMessage().toString()).concat(" please contact your confluence or tableau server administrator"));
            throw new MacroException("authentication error: there is an authentication problem while retrieving the report, please contact your confluence or tableau server administrator");
        }
        catch (Exception e)
        {
            log.error("unexpected error: please contact your confluence or tableau server administrator, ".concat(e.toString()));
            throw new MacroException("unexpected error: please contact your confluence or tableau server administrator: ".concat(e.toString()));
        }
    }

    abstract void includeResources();
    abstract String RenderPlugin(Map params,RenderContext renderContext) throws ValidationException, AuthenticationException, IOException, NoSuchAlgorithmException, KeyManagementException;
}
