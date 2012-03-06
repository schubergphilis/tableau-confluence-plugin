package com.schubergphilis.confluence.configuration;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.ConfluenceActionSupport;

/**
 * Created by IntelliJ IDEA.
 * User: rgerrits
 * Date: 6/16/11
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
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
