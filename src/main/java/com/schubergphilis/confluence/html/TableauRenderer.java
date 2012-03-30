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

    public TableauRenderer()
    {

    }

    public TableauRenderer WithWorkbook(String workbook)
    {
        _workbook = workbook.replace(" ", "");
        return this;
    }

    public TableauRenderer WithTitle(String title)
    {
        _title = title;
        return this;
    }

    public TableauRenderer WithHost(String host, String trustedHost)
    {
        _host = host;
        _trustedHost =trustedHost;
        return this;
    }

    public TableauRenderer WithReport(String report)
    {
        _report = report.replace(" ", "");
        return this;
    }

    public TableauRenderer WithWidth(Integer width)
    {
        _width = width;
        return this;
    }

    public TableauRenderer WithHeight(Integer height)
    {
        _height = height;
        return this;
    }

    public TableauRenderer WithInteractiveStart(Boolean interactive)
    {
        _interactive = interactive;
        return this;
    }

    public TableauRenderer WithEmbed(Boolean embed)
    {
        _embed = embed;
        return this;
    }

    public TableauRenderer WithToolbar(Boolean toolbar)
    {
        _toolbar = toolbar;
        return this;
    }

    public TableauRenderer WithBorderStyle(String borderStyle)
    {
        _borderStyle = borderStyle;
        return this;
    }

    public TableauRenderer WithTabs(Boolean tabs)
    {
        _tabs = tabs;
        return this;
    }

    public TableauRenderer WithInteractiveButton(Boolean showButton)
    {
        _showInteractiveButton = showButton;
        return this;
    }

    public TableauRenderer WithExportContext(Boolean isExport)
    {
        _isExportContext = isExport;
        return this;
    }

    public TableauRenderer WithParameters(String parameters)
    {
        _parameters = parameters;
        _renderParameters = true;
        return this;
    }

    private String HtmlAttribute(String name, String value)
    {
        return name + "=\"" + value + "\" ";
    }

    private String TableauAttributes() {
        return new StringBuffer()
            .append(HtmlAttribute("workbook", _workbook))
            .append(HtmlAttribute("report", _report))
            .append(HtmlAttribute("tableau_host", _host))
            .append(HtmlAttribute("title", _title == null ? "" : _title))
            .append(HtmlAttribute("embed", (_embed ? "yes" : "no")))
            .append(HtmlAttribute("toolbar", (_toolbar ? "yes" : "no")))
            .append(HtmlAttribute("tabs", (_tabs ? "yes" : "no")))
            .append(HtmlAttribute("parameters", _parameters))
            .toString();
    }

    private String TableauUrl()  {
        return _trustedHost.concat("/views/")
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

    protected String Validate()
    {
        if(_workbook == null || _workbook.length() == 0)
            return "parameter workbook is missing";

        if(_report == null || _report.length() == 0)
            return "parameter report is missing";

        if(_trustedHost == null || _report.length() == 0)
            return "parameter tableau host is missing";

        return null;
    }

    private String getSize()
    {
        if(_width == null || _height == null)
            return "";

        return "?:size=" + _width + "," + _height;
    }

    protected String RenderHtml() {
        if(!_interactive || _isExportContext) {
            BuildNonInteractiveHtml();
        }
        else {
            BuildInteractiveHtml();
        }

        return _result.toString();
    }

    private void BuildNonInteractiveHtml() {
        _result.append("<div ")
                .append(getBorderStyle())
                .append(">")
                .append("<img ")
                .append("src=\"")
                .append(TableauUrl())
                .append(".png")
                .append(getSize())
                .append(_renderParameters ? (getSize().length() == 0 ? "?" : "&") : "")
                .append(_parameters)
                .append("\"></img>");

        if(_showInteractiveButton && !_isExportContext)
        {
            _result.append("<br/><input type=\"button\" " + TableauAttributes() + " value=\"Open interactive view\" />");
        }

        _result.append("</div><br/>");
    }

    private void BuildInteractiveHtml() {
        _result.append("<div " + getBorderStyle() + "><iframe ");

        if(_width != null && _height != null)
        {
            _result.append(HtmlAttribute("width", _width.toString()))
                   .append(HtmlAttribute("height", _height.toString()));
        }

        _result.append(HtmlAttribute("frameborder", "0"))
               .append(HtmlAttribute("src", TableauUrl()
                       .concat("?:embed=")
                       .concat(_embed ? "yes" : "no")
                       .concat("&:toolbar=")
                       .concat(_toolbar ? "yes" : "no")
                       .concat("&:tabs=")
                       .concat(_tabs ? "yes" : "no")
                       .concat(_renderParameters ? "&" : "")
                       .concat(_parameters)))
               .append(HtmlAttribute("border", "0"))
               .append(HtmlAttribute("style", "border: 0pt none;"))
               .append("></iframe></div><br/>");
    }
}