/**
 * Developer: Roel Gerrits
 * Company: Schuberg Philis
 * Date: 6/17/11
 * Time: 2:53 PM
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

package com.schubergphilis.confluence.configuration;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.schubergphilis.confluence.exceptions.ValidationException;
import org.apache.log4j.Logger;

public class ConfigurationManager
{
    private com.atlassian.bandana.BandanaManager _bandanaManager;
    private final String PLUGIN_KEY_VALUES = "com.schubergphilis.com.tableau-plugin";
    private ConfluenceBandanaContext _context = new ConfluenceBandanaContext("tableau-plugin");
    private final Logger log = Logger.getLogger(this.getClass().getName());

    public ConfigurationManager(BandanaManager bandanaManager)
    {
        _bandanaManager = bandanaManager;
    }

    public String getData()
    {
        return (String) _bandanaManager.getValue(_context, PLUGIN_KEY_VALUES);
    }

    public void setData(String data)
    {
        _bandanaManager.setValue(_context, PLUGIN_KEY_VALUES, data);
    }

    public String getValue(String key) throws ValidationException
    {
        return getValue(key, DefaultValueBehaviour.nullValue);
    }

    public String getValue(String key, DefaultValueBehaviour behaviour) throws ValidationException
    {
        String value = null;
        String data = getData();

        if(data == null)
        {
            log.error("no key values configured in tableau-plugin");
            throw new ValidationException("Check plugin configuration (no key/values configured), or contact your confluence administrator");
        }

        if(data.length() > 0)
        {
            String[] list = data.split("#");
            if(list.length > 0)
            {
                // init with first item
                if(list[0].contains(";") && behaviour == DefaultValueBehaviour.firstInList)
                    value = list[0].split(";")[1];

                // search for the env
                for(String s : list)
                {
                    if( s.split(";")[0].equals(key))
                        value =  s.split(";")[1];
                }
            }
        }

        return value;
    }
}
