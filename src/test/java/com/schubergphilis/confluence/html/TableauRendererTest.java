package com.schubergphilis.confluence.html;

import com.schubergphilis.confluence.exceptions.ValidationException;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

/**
 * Developer: Roel Gerrits
 * Company: Schuberg Philis
 * Date: 6/21/11
 * Time: 2:01 PM
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

public class TableauRendererTest {

    TableauRenderer _testRenderer;

    @Before
    public void SetupTest()
    {
        _testRenderer = new TableauRenderer();
    }

    public void renderAndExpectException(String message, Class type)
    {
        try {
            _testRenderer.render();
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), message);
            assertEquals(e.getClass().getName(), type.getName());
        }
    }

    @Test
    public void render_Should_Throw_ValidationException_When_No_Workbook_Given()
    {
        // arrange, act and assert
        renderAndExpectException("parameter workbook is missing", ValidationException.class);
    }

    @Test
    public void render_Should_Throw_ValidationException_When_Empty_Workbook_Given()
    {
        // arrange
        _testRenderer.withWorkbook("").withReport("Report");

        // act and assert
        renderAndExpectException("parameter workbook is missing", ValidationException.class);
    }

    @Test
    public void render_Should_Throw_ValidationException_When_Empty_Report_Given()
    {
        // arrange
        _testRenderer.withWorkbook("TableauWorkbook").withReport("");

        // act and assert
        renderAndExpectException("parameter report is missing", ValidationException.class);
    }

    @Test
    public void render_Should_Throw_ValidationException_When_No_TableauHost_Given()
    {
        // arrange
        _testRenderer.withWorkbook("TableauWorkbook").withReport("TableauReport");

        // act and assert
        renderAndExpectException("parameter tableau host is missing", ValidationException.class);
    }

    @Test
    public void render_Should_Throw_ValidationException_When_Empty_TableauHost_Given()
    {
        // arrange
        _testRenderer.withWorkbook("TableauWorkbook").withReport("TableauReport").withHost("", "");

        // act and assert
        renderAndExpectException("parameter tableau host is missing", ValidationException.class);
    }

    @Test
    public void render_Should_Render_Extra_Tableau_Parameters_If_Provided() throws ValidationException, UnsupportedEncodingException {
        // arrange
        _testRenderer.withWorkbook("TableauWorkbook")
                     .withReport("TableauReport")
                     .withHost("http://localhost", "123456789")
                     .withInteractiveButton(true)
                     .withParameters("param1=false&param2=true");

        // act
        String result = _testRenderer.render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png?param1=false&param2=true\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"param1=false&param2=true\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void render_Should_Render_Ampersand_When_Size_Is_Known() throws ValidationException, UnsupportedEncodingException {
        // arrange
        _testRenderer.withWorkbook("TableauWorkbook")
                     .withSize(500, 500)
                     .withReport("TableauReport")
                     .withHost("http://localhost", "123456789")
                     .withInteractiveButton(true)
                     .withParameters("param1=false&param2=true");

        // act
        String result = _testRenderer.render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png?:size=500,500&param1=false&param2=true\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"param1=false&param2=true\"  value=\"Open interactive view\" /></div><br/>");


    }

    @Test
    public void render_Should_Render_Workbook_Report_And_Host() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .withWorkbook("TableauWorkbook")
                .withReport("TableauReport")
                .withHost("http://localhost", "123456789")
                .withInteractiveButton(true)
                .render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void withWorkbook_Should_Remove_Spaces() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .withWorkbook("Tableau Workbook")
                .withReport("TableauReport")
                .withHost("http://localhost", "123456789")
                .withInteractiveButton(true)
                .render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void withReport_Should_Remove_Spaces() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .withWorkbook("TableauWorkbook")
                .withReport("Tableau Report")
                .withHost("http://localhost", "123456789")
                .withInteractiveButton(true)
                .render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void withTabs_Should_Render_Tabs_Yes() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .withWorkbook("TableauWorkbook")
                .withReport("TableauReport")
                .withInteractiveButton(true)
                .withHost("http://localhost", "123456789")
                .withTabs(true)
                .render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"yes\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void withTabs_Should_Render_Yes_With_True() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .withWorkbook("TableauWorkbook")
                .withReport("TableauReport")
                .withInteractiveButton(true)
                .withHost("http://localhost", "123456789")
                .withTabs(true)
                .render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"yes\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void withTitle_Should_Render_Title_Attribute() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .withWorkbook("TableauWorkbook")
                .withReport("TableauReport")
                .withInteractiveButton(true)
                .withHost("http://localhost", "123456789")
                .withTitle("The title")
                .render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"The title\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void withHeight_And_WithWidth_Should_Render_Height_And_Width() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .withWorkbook("TableauWorkbook")
                .withReport("TableauReport")
                .withInteractiveButton(true)
                .withHost("http://localhost", "123456789")
                .withSize(200, 150)
                .render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png?:size=200,150\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void withEmbed_False_Should_Render_Embed_Attribute_No() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .withWorkbook("TableauWorkbook")
                .withReport("TableauReport")
                .withHost("http://localhost", "123456789")
                .withInteractiveButton(true)
                .withEmbed(false)
                .render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"no\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void withEmbed_True_Should_Render_Embed_Attribute_Yes() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .withWorkbook("TableauWorkbook")
                .withReport("TableauReport")
                .withInteractiveButton(true)
                .withHost("http://localhost", "123456789")
                .withEmbed(true)
                .render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void withToolbar_False_Should_Render_Toolbar_Attribute_No() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .withWorkbook("TableauWorkbook")
                .withReport("TableauReport")
                .withHost("http://localhost", "123456789")
                .withInteractiveButton(true)
                .withToolbar(false)
                .render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"no\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void withToolbar_True_Should_Render_Toolbar_Attribute_Yes() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .withWorkbook("TableauWorkbook")
                .withReport("TableauReport")
                .withHost("http://localhost", "123456789")
                .withInteractiveButton(true)
                .withToolbar(true)
                .render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void withBorderStyle_Should_Add_Extra_Style() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .withWorkbook("TableauWorkbook")
                .withReport("TableauReport")
                .withHost("http://localhost", "123456789")
                .withBorderStyle("border: 1px solid red;")
                .withInteractiveButton(true)
                .render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;border: 1px solid red;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void withInteractiveStart_Should_Render_Tableau_Iframe() throws ValidationException, UnsupportedEncodingException {
        // arrange
        _testRenderer.withWorkbook("Tableau Workbook")
                     .withReport("Tableau Report")
                     .withHost("http://localhost", "123456789")
                     .withInteractiveButton(true)
                     .withInteractiveStart(true);

        // act
        String result = _testRenderer.render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><iframe frameborder=\"0\" src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport?:embed=yes&:toolbar=yes&:tabs=no\" border=\"0\" style=\"border: 0pt none;\" ></iframe></div><br/>");
    }

    @Test
    public void withExportContext_And_Interactive_Mode_Should_Render_Png_Format() throws ValidationException, UnsupportedEncodingException {
        // arrange
        _testRenderer.withWorkbook("SomeWorkBook")
                     .withReport("Tableau_Report")
                     .withHost("https://localhost", "123456789")
                     .withParameters("p=1")
                     .withInteractiveStart(true)
                     .withExportContext(true);

        // act
        String result = _testRenderer.render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"https://localhost/trusted/123456789/views/SomeWorkBook/Tableau_Report.png?p=1\"></img></div><br/>");
    }

    @Test
    public void withExportContext_Should_Not_Render_Align_Right_Style() throws ValidationException, UnsupportedEncodingException {

        // arrange
        _testRenderer.withWorkbook("Tableau Workbook")
                     .withReport("Tableau Report")
                     .withHost("http://localhost", "123456789")
                     .withExportContext(true);

        // act
        String result = _testRenderer.render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img></div><br/>");
    }

    @Test
    public void withParameters_Should_Render_Ampersant_Correctly()  throws ValidationException, UnsupportedEncodingException
    {
        // arrange
        _testRenderer.withWorkbook("Tableau Workbook")
                     .withReport("Tableau Report")
                     .withHost("http://localhost", "123456789")
                     .withInteractiveStart(true)
                     .withParameters("test=bla&foo=bar")
                     .withSize(1200, 1200);

        // act
        String result = _testRenderer.render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><iframe width=\"1200\" height=\"1200\" frameborder=\"0\" src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport?:embed=yes&:toolbar=yes&:tabs=no&test=bla&foo=bar\" border=\"0\" style=\"border: 0pt none;\" ></iframe></div><br/>");
    }

    @Test
    public void withRefresh_Should_Add_Refresh_option_to_url() throws ValidationException
    {
        // arrange
        _testRenderer.withWorkbook("ExampleWorkbook")
                .withReport("view1")
                .withSize(500, 500)
                .withParameters("a=1&b=2")
                .withHost("http://localhost", "123456789")
                .withRefresh(true);

        // act
        String result = _testRenderer.render();

        // assert
        String expect = "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/ExampleWorkbook/view1.png?:size=500,500&a=1&b=2&:refresh=yes\"></img></div><br/>";
        assertEquals(result, expect);
    }

    @Test
    public void with_Site_Should_Use_Site_In_Url() throws ValidationException {
        // arrange
        _testRenderer.withWorkbook("ExampleWorkbook")
                .withReport("view1")
                .withSize(500, 500)
                .withSite("site")
                .withHost("http://localhost", "123456789");

        // act
        String result = _testRenderer.render();

        // assert
        String expect = "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/t/site/views/ExampleWorkbook/view1.png?:size=500,500\"></img></div><br/>";
        assertEquals(result, expect);
    }

    @Test
    public void when_no_ticket_provided_dont_use_trusted_url() throws ValidationException {
        // arrange
        _testRenderer.withWorkbook("ExampleWorkbook")
                .withReport("view1")
                .withSize(500, 500)
                .withSite("site")
                .withHost("http://localhost", null);

        // act
        String result = _testRenderer.render ();

        // assert
        String expect = "<div style=\"display:inline-block;\"><img src=\"http://localhost/t/site/views/ExampleWorkbook/view1.png?:size=500,500\"></img></div><br/>";

        assertEquals(result, expect);
    }
}