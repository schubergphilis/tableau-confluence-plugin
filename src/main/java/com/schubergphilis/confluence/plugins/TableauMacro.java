/**
 * Developer: Roel Gerrits
 * Company: Schuberg Philis
 * Date: 3/30/11
 * Time: 10:49 AM
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
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.schubergphilis.confluence.configuration.DefaultValueBehaviour;
import com.schubergphilis.confluence.configuration.ConfigurationManager;
import com.schubergphilis.confluence.exceptions.AuthenticationException;
import com.schubergphilis.confluence.exceptions.ValidationException;
import com.schubergphilis.confluence.html.TableauRenderer;
import com.schubergphilis.tableau.authentication.TrustedAuthentication;
import com.schubergphilis.util.http.HttpRequest;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class TableauMacro extends SbpBaseMacro
{
    public TableauMacro(WebResourceManager webResourceManager, BandanaManager bandanaManager)
    {
        super._webResourceManager = webResourceManager;
        super._bandanaManager = bandanaManager;
    }

    public boolean hasBody()
    {
        return false;
    }

    public RenderMode getBodyRenderMode()
    {
        return RenderMode.NO_RENDER;
    }

    public String renderPlugin(Map params, String outputType)
            throws ValidationException, AuthenticationException, IOException, NoSuchAlgorithmException, KeyManagementException
    {
        String workbook = getStrParameter(params, "workbook");
        String report = getStrParameter(params, "report");
        String view = getStrParameter(params, "view", report);
        String title = getStrParameter(params, "title", view);
        String environment = getStrParameter(params, "environment", "prod");
        String borderStyle = getStrParameter(params, "borderstyle", "");
        Integer height = getIntParameter(params, "height", 550);
        Integer width = getIntParameter(params, "width", 1280);
        Boolean interactive = getBoolParameter(params, "interactive", false);
        Boolean toolbar = getBoolParameter(params, "toolbar", true);
        Boolean embed = getBoolParameter(params, "embed", true);
        Boolean tabs = getBoolParameter(params, "tabs", false);
        Boolean button = getBoolParameter(params, "button", false);
        Boolean noPrint = getBoolParameter(params, "noprint", false);
        String parameters = getStrParameter(params, "parameters", "");
        Boolean refresh = getBoolParameter(params, "refresh", false);
        String site = getStrParameter(params, "site","");

        String externalHost = getConfigurationManager().getValue(environment, DefaultValueBehaviour.firstInList);
        String internalHost = getConfigurationManager().getValue(environment + "-internal");
        String disableClientTrustedAuthValue = getConfigurationManager().getValue("disableclienttrustedauth", "false");
        boolean disableClientTrustedAuth = disableClientTrustedAuthValue.toLowerCase().startsWith("t");

        Boolean isExportContext = isPdfOrWordOutput(outputType);

        // skip for exporting to pdf/word or preview mode
        if(isExportContext && noPrint)
            return "";

        if(RenderContext.PREVIEW.equals(outputType) && ( workbook.length() == 0 || view.length() == 0))
            return "Please provide a workbook and a report and hit the refresh button.";

        TableauRenderer renderer = getTableauRenderer()
            .withSize(width, height)
            .withView(view)
            .withWorkbook(workbook)
            .withTitle(title)
            .withInteractiveStart(interactive)
            .withEmbed(embed)
            .withToolbar(toolbar)
            .withBorderStyle(borderStyle)
            .withInteractiveButton(button)
            .withExportContext(isExportContext)
            .withTabs(tabs)
            .withRefresh(refresh)
            .withSite(site)
            .withParameters(parameters);

        determineHost(externalHost, internalHost, isExportContext, disableClientTrustedAuth, site, renderer);

        return renderer.render();
    }

    private String getHostForBackendCommunication(String internalHost, String externalHost) {
        if(internalHost != null && internalHost.length() > 0){
             return internalHost;
        }

        return externalHost;
    }

    private String getHostForRenderer(String internalHost, String externalHost, boolean isExportContext) {
        // export should use internal when configured
        if(isExportContext) {
            return getHostForBackendCommunication(internalHost, externalHost);
        }
        return externalHost;
    }

    private void determineHost(String externalHost, String internalHost, boolean isExportContext, boolean disableClientTrustedAuth, String site, TableauRenderer renderer)
            throws IOException, AuthenticationException, NoSuchAlgorithmException, KeyManagementException, ValidationException
    {
        String domain = getConfigurationManager().getValue("domain");
        String username = getUsername();

        if(domain != null && domain.length() > 0)
        {
            username = String.format("%s\\%s", domain, username);
        }

        // for debugging purposes, override the confluence username
        String debugUsername = getConfigurationManager().getValue("debugusername");
        if(debugUsername != null && debugUsername.length() > 0)
            username = debugUsername;

        // only use external host for authentication when no internal is set
        String hostForTrustedAuthentication = getHostForBackendCommunication(internalHost, externalHost);

        String ticket = null;

        if(!disableClientTrustedAuth || isExportContext) {
            ticket = getTrustedAuthentication()
                    .withTableauUrl(hostForTrustedAuthentication)
                    .withUsername(username)
                    .withSite(site)
                    .authenticate();
        }

        String hostForRenderer = getHostForRenderer(internalHost, externalHost, isExportContext);
        renderer.withHost(hostForRenderer, ticket);
    }

    private boolean isPdfOrWordOutput(String outputType)
    {
        return RenderContext.PDF.equals(outputType) || RenderContext.WORD.equals(outputType);
    }

    @Override
    void includeResources()
    {
        String resource = "tableau-plugin:javascript-resources";
        _webResourceManager.requireResource(resource);
    }

    private TableauRenderer _tableauRenderer;
    private TableauRenderer getTableauRenderer()
    {
        if(_tableauRenderer == null)
            return new TableauRenderer();

        return _tableauRenderer;
    }

    public void setTableauRenderer(TableauRenderer tableauRenderer)
    {
        _tableauRenderer = tableauRenderer;
    }

    private TrustedAuthentication _trustedAuthentication;
    private TrustedAuthentication getTrustedAuthentication()
    {
        if(_trustedAuthentication == null)
            return new TrustedAuthentication(new HttpRequest());

        return _trustedAuthentication;
    }

    public void setTrustedAuthentication(TrustedAuthentication authentication)
    {
        _trustedAuthentication = authentication;
    }

    private ConfigurationManager _configurationManager;
    private ConfigurationManager getConfigurationManager()
    {
        if(_configurationManager == null)
            return new ConfigurationManager(_bandanaManager);

        return _configurationManager;
    }

    public void setConfigurationManager(ConfigurationManager configurationManager)
    {
        _configurationManager = configurationManager;
    }

    private String _username;
    private String getUsername()
    {
        if(_username == null)
            return AuthenticatedUserThreadLocal.getUsername();

        return _username;
    }

    public void setUsername(String username)
    {
        _username = username;
    }
}