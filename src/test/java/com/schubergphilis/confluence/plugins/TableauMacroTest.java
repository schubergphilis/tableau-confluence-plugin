/**
 * Developer: Roel Gerrits
 * Company: Schuberg Philis
 * Date: 6/4/12
 * Time: 3:44 PM
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
 */
package com.schubergphilis.confluence.plugins;

import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import com.schubergphilis.confluence.configuration.ConfigurationManager;
import com.schubergphilis.confluence.configuration.DefaultValueBehaviour;
import com.schubergphilis.confluence.exceptions.AuthenticationException;
import com.schubergphilis.confluence.exceptions.ValidationException;
import com.schubergphilis.confluence.html.TableauRenderer;
import com.schubergphilis.tableau.authentication.TrustedAuthentication;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class TableauMacroTest {

    TableauMacro _tableauMacro;

    // mock objects
    TableauRenderer _mockTableauRenderer;
    ConfigurationManager _mockConfigurationManager;
    WebResourceManager _mockResourceManager;
    TrustedAuthentication _mockTrustedAuthentication;

    Map<String,String> _defaultParameters;
    RenderContext _renderContext;

    private String _host = "http://localhost";
    private String _trustedHost = "http://localhost/trusted/123456789";
    private String _title = "view1";
    private String _workbook = "Workbook";
    private String _report = "view1";
    private String _borderStyle = "";
    private Integer _width = 500;
    private Integer _height = 500;
    private Boolean _interactive = false;
    private Boolean _toolbar = true;
    private Boolean _embed = true;
    private Boolean _tabs = false;
    private Boolean _showInteractiveButton = false;
    private Boolean _exportContext = false;
    private Boolean _refresh = false;
    private String _parameters = "";

    private String _confluenceUsername = "admin";
    private String _expectedUsername = "admin";
    private String _expectedSite = "";

    @Before
    public void setupTest()
    {
        _defaultParameters = getDefaultParameters();
        _renderContext = new RenderContext();

        // setup mocks
        _mockResourceManager = mock(WebResourceManager.class);
        _mockTableauRenderer = mock(TableauRenderer.class);
        _mockConfigurationManager = mock(ConfigurationManager.class);
        _mockTrustedAuthentication = mock(TrustedAuthentication.class);

        // arrange macro
        _tableauMacro = new TableauMacro(_mockResourceManager, null);
        _tableauMacro.setConfigurationManager(_mockConfigurationManager);
        _tableauMacro.setTrustedAuthentication(_mockTrustedAuthentication);
        _tableauMacro.setTableauRenderer(_mockTableauRenderer);
    }

    @After
    public void tearDown()
    {
        verify(_mockResourceManager).requireResource("com.schubergphilis.confluence.plugins.tableau-plugin:javascript-resources");
    }

    private void setDefaultExpects() throws ValidationException, IOException, AuthenticationException, NoSuchAlgorithmException, KeyManagementException {

        _tableauMacro.setUsername(_confluenceUsername);

        // expects for configuration manager
        when(_mockConfigurationManager.getValue("prod", DefaultValueBehaviour.firstInList)).thenReturn("http://localhost");

        // expects for trusted authentication
        when(_mockTrustedAuthentication.withTableauUrl("http://localhost")).thenReturn(_mockTrustedAuthentication);
        when(_mockTrustedAuthentication.withUsername(_expectedUsername)).thenReturn(_mockTrustedAuthentication);
        when(_mockTrustedAuthentication.withSite(_expectedSite)).thenReturn(_mockTrustedAuthentication);
        when(_mockTrustedAuthentication.authenticate()).thenReturn("http://localhost/trusted/123456789");

        // expects for renderer
        when(_mockTableauRenderer.withSize(_width, _height)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withReport(_report)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withWorkbook(_workbook)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withTitle(_title)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withInteractiveStart(_interactive)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withEmbed(_embed)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withToolbar(_toolbar)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withBorderStyle(_borderStyle)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withInteractiveButton(_showInteractiveButton)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withExportContext(_exportContext)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withTabs(_tabs)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withRefresh(_refresh)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withParameters(_parameters)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.withHost(_host, _trustedHost)).thenReturn(_mockTableauRenderer);
        when(_mockTableauRenderer.render()).thenReturn("ok");
    }

    private Map<String,String> getDefaultParameters()
    {
        Map<String,String> result = new HashMap<String,String>();
        result.put("workbook","Workbook");
        result.put("report","view1");
        result.put("title","");
        result.put("environment","prod");
        result.put("borderstyle","");
        result.put("height","500");
        result.put("width","500");
        result.put("interactive","false");
        result.put("toolbar","true");
        result.put("embed","true");
        result.put("tabs","false");
        result.put("button","false");
        result.put("noprint","false");
        result.put("parameters","");
        result.put("refresh","false");

        return result;
    }
    @Test
    public void renderMacroTest_Should_Read_from_ConfigurationManager_Should_Use_Trusted_Auth_And_Call_TableauRenderer() throws MacroException, IOException, ValidationException, AuthenticationException, NoSuchAlgorithmException, KeyManagementException {

        // arrange
        setDefaultExpects();

        // act
        String result = _tableauMacro.execute(_defaultParameters, "", _renderContext);

        // assert
        Assert.assertEquals(result, "ok");
    }

    @Test
    public void renderMacro_Should_Append_Domain_To_Username_If_Configured() throws ValidationException, IOException, AuthenticationException, NoSuchAlgorithmException, KeyManagementException, MacroException {

        // arrange
        _confluenceUsername = "admin";
        _expectedUsername = "mydomain.local\\admin";

        when(_mockConfigurationManager.getValue("domain")).thenReturn("mydomain.local");
        setDefaultExpects();

        // act
        String result = _tableauMacro.execute(_defaultParameters, "", _renderContext);

        // assert
        Assert.assertEquals(result, "ok");
    }

    @Test
    public void renderMacro_Should_Use_debugusername_When_Configured() throws IOException, ValidationException, AuthenticationException, NoSuchAlgorithmException, KeyManagementException, MacroException {
        // arrange
        _expectedUsername = "testuser";
        when(_mockConfigurationManager.getValue("debugusername")).thenReturn("testuser");
        setDefaultExpects();

        // act
        String result = _tableauMacro.execute(_defaultParameters, "", _renderContext);

        // assert
        Assert.assertEquals(result, "ok");
    }

    @Test
    public void renderMacro_Should_Provide_Site_When_Provided() throws IOException, NoSuchAlgorithmException, AuthenticationException, KeyManagementException, ValidationException, MacroException {
        // arrange
        _expectedSite = "site";
        setDefaultExpects();
        _defaultParameters.put("site", "site");

        // act
        String result = _tableauMacro.execute(_defaultParameters, "", _renderContext);

        // assert
        Assert.assertEquals(result, "ok");
        verify(_mockTrustedAuthentication).withSite("site");
    }
}