/**
 * Developer: Roel Gerrits
 * Company: Schuberg Philis
 * Date: 6/16/11
 * Time: 11:49 AM
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

package com.schubergphilis.confluence.html;

import java.util.HashMap;
import java.util.Map;

public class TableauRenderer extends BaseHtmlRenderer
{
    private StringBuffer _result = new StringBuffer();
    private String _host;
    private String _trustedHost;
    private String _title;
    private String _workbook;
    private String _report;
    private String _borderStyle;
    private Integer _width;
    private Integer _height;
    private Boolean _interactive = false;
    private Boolean _toolbar = true;
    private Boolean _embed = true;
    private Boolean _tabs = false;
    private Boolean _showInteractiveButton = false;
    private Boolean _isExportContext = false;
    private String _parameters = "";
    private Boolean _renderParameters = false;
    private String _site;

    private Map<String, String> _urlParameters = new HashMap<String, String>();

    public TableauRenderer()
    {

    }

    public TableauRenderer withWorkbook(String workbook)
    {
        _workbook = workbook.replace(" ", "");
        return this;
    }

    public TableauRenderer withTitle(String title)
    {
        _title = title;
        return this;
    }

    public TableauRenderer withHost(String host, String ticket)
    {
        _host = host;

        if(ticket != null) {
            _trustedHost = _host + "/trusted/" + ticket;
        }
        else {
            _trustedHost = _host;
        }

        return this;
    }

    public TableauRenderer withReport(String report)
    {
        _report = report.replace(" ", "");
        return this;
    }

    public TableauRenderer withSize(Integer width, Integer height)
    {
        if(width != null && height != null)
        {
            _urlParameters.put(":size", String.format("%d,%d", width, height));
            _width = width;
            _height = height;
        }

        return this;
    }

    public TableauRenderer withInteractiveStart(Boolean interactive)
    {
        _interactive = interactive;
        return this;
    }

    public TableauRenderer withEmbed(Boolean embed)
    {
        _embed = embed;
        return this;
    }

    public TableauRenderer withToolbar(Boolean toolbar)
    {
        _toolbar = toolbar;
        return this;
    }

    public TableauRenderer withBorderStyle(String borderStyle)
    {
        _borderStyle = borderStyle;
        return this;
    }

    public TableauRenderer withTabs(Boolean tabs)
    {
        _tabs = tabs;
        return this;
    }

    public TableauRenderer withInteractiveButton(Boolean showButton)
    {
        _showInteractiveButton = showButton;
        return this;
    }

    public TableauRenderer withExportContext(Boolean isExport)
    {
        _isExportContext = isExport;
        return this;
    }

    public TableauRenderer withParameters(String parameters)
    {
        if(parameters != null && parameters.length() > 0)
        {
            _urlParameters.put("parameters", parameters);
            _parameters = parameters;
            _renderParameters = true;
        }

        return this;
    }

    public TableauRenderer withRefresh(Boolean refresh)
    {
        if(refresh)
            _urlParameters.put(":refresh","yes");

        return this;
    }

    public TableauRenderer withSite(String site) {
        _site = site;

        return this;
    }

    private String htmlAttribute(String name, String value)
    {
        return name + "=\"" + value + "\" ";
    }

    private String tableauAttributes() {
        return new StringBuffer()
            .append(htmlAttribute("workbook", _workbook))
            .append(htmlAttribute("report", _report))
            .append(htmlAttribute("tableau_host", _host))
            .append(htmlAttribute("title", _title == null ? "" : _title))
            .append(htmlAttribute("embed", (_embed ? "yes" : "no")))
            .append(htmlAttribute("toolbar", (_toolbar ? "yes" : "no")))
            .append(htmlAttribute("tabs", (_tabs ? "yes" : "no")))
            .append(htmlAttribute("parameters", _parameters))
            .toString();
    }

    private String multiSite() {
        return (_site !=null && _site.length() > 0 ? String.format("/t/%s", _site) : "");
    }
    private String tableauUrl() {
        return _trustedHost.concat(multiSite())
                    .concat("/views/")
                    .concat(_workbook)
                    .concat("/")
                    .concat(_report);
    }

    private String getBorderStyle()
    {
        return "style=\""
                + "display:inline-block;"
                + (_borderStyle == null ? "" : _borderStyle)
                + "\"";
    }

    protected String validate()
    {
        if(_workbook == null || _workbook.length() == 0)
            return "parameter workbook is missing";

        if(_report == null || _report.length() == 0)
            return "parameter report is missing";

        if(_trustedHost == null || _report.length() == 0)
            return "parameter tableau host is missing";

        return null;
    }

    private String getPngUrlParameters()
    {
        StringBuffer result = new StringBuffer();

        if(_urlParameters.size() == 0)
            return "";

        result.append("?");

        for(String key : _urlParameters.keySet())
        {
            if(result.length()>2)
                result.append("&");

            if(key.equals("parameters"))
                result.append(_urlParameters.get(key));
            else
                result.append(key+"="+_urlParameters.get(key));
        }

        return result.toString();
    }

    protected String renderHtml() {
        if(!_interactive || _isExportContext) {
            buildNonInteractiveHtml();
        }
        else {
            buildInteractiveHtml();
        }

        return _result.toString();
    }

    private void buildNonInteractiveHtml() {
        _result.append("<div ")
                .append(getBorderStyle())
                .append(">")
                .append("<img ")
                .append("src=\"")
                .append(tableauUrl())
                .append(".png")
                .append(getPngUrlParameters())
                .append("\"></img>");

        if(_showInteractiveButton && !_isExportContext)
        {
            _result.append("<br/><input type=\"button\" " + tableauAttributes() + " value=\"Open interactive view\" />");
        }

        _result.append("</div><br/>");
    }

    private void buildInteractiveHtml() {
        _result.append("<div " + getBorderStyle() + "><iframe ");

        if(_width != null && _height != null)
        {
            _result.append(htmlAttribute("width", _width.toString()))
                   .append(htmlAttribute("height", _height.toString()));
        }

        _result.append(htmlAttribute("frameborder", "0"))
               .append(htmlAttribute("src", tableauUrl()
                       .concat("?:embed=")
                       .concat(_embed ? "yes" : "no")
                       .concat("&:toolbar=")
                       .concat(_toolbar ? "yes" : "no")
                       .concat("&:tabs=")
                       .concat(_tabs ? "yes" : "no")
                       .concat(_renderParameters ? "&" : "")
                       .concat(_parameters)))
               .append(htmlAttribute("border", "0"))
               .append(htmlAttribute("style", "border: 0pt none;"))
               .append("></iframe></div><br/>");
    }
}