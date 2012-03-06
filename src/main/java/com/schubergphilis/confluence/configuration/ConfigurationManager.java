package com.schubergphilis.confluence.configuration;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.schubergphilis.confluence.exceptions.ValidationException;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: rgerrits
 * Date: 6/17/11
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
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
