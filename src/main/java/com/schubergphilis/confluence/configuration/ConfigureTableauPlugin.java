/**
 * Developer: Roel Gerrits
 * Company: Schuberg Philis
 * Date: 6/16/11
 * Time: 4:58 PM
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
import com.atlassian.confluence.core.ConfluenceActionSupport;

public class ConfigureTableauPlugin extends ConfluenceActionSupport
{
    private ConfigurationManager _configManager;
    protected String _data = "";

    public void setBandanaManager(BandanaManager bandanaManager)
    {
        _configManager = new ConfigurationManager(bandanaManager);
    }

    public void setData(String newData)
    {
        _data = newData;
    }

    public String getData()
    {
        return _data;
    }

    public String load()
    {
        _data = _configManager.getData();
        return SUCCESS;
    }

    public String save()
    {
        _configManager.setData(_data);
        return SUCCESS;
    }
}
