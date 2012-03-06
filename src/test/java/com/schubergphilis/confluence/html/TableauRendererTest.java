package com.schubergphilis.confluence.html;

import com.schubergphilis.confluence.exceptions.ValidationException;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: rgerrits
 * Date: 6/21/11
 * Time: 2:01 PM
 * To change this template use File | Settings | File Templates.
 */

public class TableauRendererTest {

    TableauRenderer _testRenderer;

    @Before
    public void SetupTest()
    {
        _testRenderer = new TableauRenderer();
    }

    public void RenderAndExpectException(String message, Class type)
    {
        try {
            _testRenderer.Render();
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), message);
            assertEquals(e.getClass().getName(), type.getName());
        }
    }

    @Test
    public void Render_Should_Throw_ValidationException_When_No_Workbook_Given()
    {
        // arrange, act and assert
        RenderAndExpectException("parameter workbook is missing", ValidationException.class);
    }

    @Test
    public void Render_Should_Throw_ValidationException_When_Empty_Workbook_Given()
    {
        // arrange
        _testRenderer.WithWorkbook("").WithReport("Report");

        // act and assert
        RenderAndExpectException("parameter workbook is missing", ValidationException.class);
    }

    @Test
    public void Render_Should_Throw_ValidationException_When_Empty_Report_Given()
    {
        // arrange
        _testRenderer.WithWorkbook("TableauWorkbook").WithReport("");

        // act and assert
        RenderAndExpectException("parameter report is missing", ValidationException.class);
    }

    @Test
    public void Render_Should_Throw_ValidationException_When_No_TableauHost_Given()
    {
        // arrange
        _testRenderer.WithWorkbook("TableauWorkbook").WithReport("TableauReport");

        // act and assert
        RenderAndExpectException("parameter tableau host is missing", ValidationException.class);
    }

    @Test
    public void Render_Should_Throw_ValidationException_When_Empty_TableauHost_Given()
    {
        // arrange
        _testRenderer.WithWorkbook("TableauWorkbook").WithReport("TableauReport").WithHost("","");

        // act and assert
        RenderAndExpectException("parameter tableau host is missing", ValidationException.class);
    }

    @Test
    public void Render_Should_Render_Extra_Tableau_Parameters_If_Provided() throws ValidationException, UnsupportedEncodingException {
        // arrange
        _testRenderer.WithWorkbook("TableauWorkbook")
                     .WithReport("TableauReport")
                     .WithHost("http://localhost", "http://localhost/trusted/123456789")
                     .WithInteractiveButton(true)
                     .WithParameters("param1=false&param2=true");

        // act
        String result = _testRenderer.Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png?param1=false&param2=true\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"param1=false&param2=true\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void Render_Should_Render_Ampersand_When_Size_Is_Known() throws ValidationException, UnsupportedEncodingException {
        // arrange
        _testRenderer.WithWorkbook("TableauWorkbook")
                     .WithHeight(500)
                     .WithWidth(500)
                     .WithReport("TableauReport")
                     .WithHost("http://localhost", "http://localhost/trusted/123456789")
                     .WithInteractiveButton(true)
                     .WithParameters("param1=false&param2=true");

        // act
        String result = _testRenderer.Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png?:size=500,500&param1=false&param2=true\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"param1=false&param2=true\"  value=\"Open interactive view\" /></div><br/>");


    }

    @Test
    public void Render_Should_Render_Workbook_Report_And_Host() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("TableauReport")
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithInteractiveButton(true)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithWorkbook_Should_Remove_Spaces() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("Tableau Workbook")
                .WithReport("TableauReport")
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithInteractiveButton(true)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithReport_Should_Remove_Spaces() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("Tableau Report")
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithInteractiveButton(true)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithTabs_Should_Render_Tabs_Yes() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("TableauReport")
                .WithInteractiveButton(true)
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithTabs(true)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"yes\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithTabs_Should_Render_Yes_With_True() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("TableauReport")
                .WithInteractiveButton(true)
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithTabs(true)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"yes\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithTitle_Should_Render_Title_Attribute() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("TableauReport")
                .WithInteractiveButton(true)
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithTitle("The title")
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"The title\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithWidth_Should_Not_Render_Width_When_Height_Is_Unknown() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("TableauReport")
                .WithInteractiveButton(true)
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithWidth(100)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithHeight_Should_Not_Render_Height_When_Width_Is_Unknown() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("TableauReport")
                .WithInteractiveButton(true)
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithHeight(100)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithHeight_And_WithWidth_Should_Render_Height_And_Width() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("TableauReport")
                .WithInteractiveButton(true)
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithHeight(150)
                .WithWidth(200)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png?:size=200,150\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithEmbed_False_Should_Render_Embed_Attribute_No() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("TableauReport")
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithInteractiveButton(true)
                .WithEmbed(false)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"no\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithEmbed_True_Should_Render_Embed_Attribute_Yes() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("TableauReport")
                .WithInteractiveButton(true)
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithEmbed(true)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithToolbar_False_Should_Render_Toolbar_Attribute_No() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("TableauReport")
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithInteractiveButton(true)
                .WithToolbar(false)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"no\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithToolbar_True_Should_Render_Toolbar_Attribute_Yes() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("TableauReport")
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithInteractiveButton(true)
                .WithToolbar(true)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithBorderStyle_Should_Add_Extra_Style() throws ValidationException, UnsupportedEncodingException {
        // arrange and act
        String result = _testRenderer
                .WithWorkbook("TableauWorkbook")
                .WithReport("TableauReport")
                .WithHost("http://localhost", "http://localhost/trusted/123456789")
                .WithBorderStyle("border: 1px solid red;")
                .WithInteractiveButton(true)
                .Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;border: 1px solid red;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img><br/><input type=\"button\" workbook=\"TableauWorkbook\" report=\"TableauReport\" tableau_host=\"http://localhost\" title=\"\" embed=\"yes\" toolbar=\"yes\" tabs=\"no\" parameters=\"\"  value=\"Open interactive view\" /></div><br/>");
    }

    @Test
    public void WithInteractiveStart_Should_Render_Tableau_Iframe() throws ValidationException, UnsupportedEncodingException {
        // arrange
        _testRenderer.WithWorkbook("Tableau Workbook")
                     .WithReport("Tableau Report")
                     .WithHost("http://localhost", "http://localhost/trusted/123456789")
                     .WithInteractiveButton(true)
                     .WithInteractiveStart(true);

        // act
        String result = _testRenderer.Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><iframe frameborder=\"0\" src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport?:embed=yes&:toolbar=yes&:tabs=no\" border=\"0\" style=\"border: 0pt none;\" ></iframe></div><br/>");
    }

    @Test
    public void WithExportContext_And_Interactive_Mode_Should_Render_Png_Format() throws ValidationException, UnsupportedEncodingException {
        // arrange
        _testRenderer.WithWorkbook("SomeWorkBook")
                     .WithReport("Tableau_Report")
                     .WithHost("https://localhost", "https://localhost/trusted/123456789")
                     .WithParameters("p=1")
                     .WithInteractiveStart(true)
                     .WithExportContext(true);

        // act
        String result = _testRenderer.Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"https://localhost/trusted/123456789/views/SomeWorkBook/Tableau_Report.png?p=1\"></img></div><br/>");
    }

    @Test
    public void WithExportContext_Should_Not_Render_Align_Right_Style() throws ValidationException, UnsupportedEncodingException {

        // arrange
        _testRenderer.WithWorkbook("Tableau Workbook")
                     .WithReport("Tableau Report")
                     .WithHost("http://localhost", "http://localhost/trusted/123456789")
                     .WithExportContext(true);

        // act
        String result = _testRenderer.Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><img src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport.png\"></img></div><br/>");
    }

    @Test
    public void WithParameters_Should_Render_Ampersant_Correctly()  throws ValidationException, UnsupportedEncodingException
    {
        // arrange
        _testRenderer.WithWorkbook("Tableau Workbook")
                     .WithReport("Tableau Report")
                     .WithHost("http://localhost", "http://localhost/trusted/123456789")
                     .WithInteractiveStart(true)
                     .WithParameters("test=bla&foo=bar")
                     .WithHeight(1200)
                     .WithWidth(1200);

        // act
        String result = _testRenderer.Render();

        // assert
        assertEquals(result, "<div style=\"display:inline-block;\"><iframe width=\"1200\" height=\"1200\" frameborder=\"0\" src=\"http://localhost/trusted/123456789/views/TableauWorkbook/TableauReport?:embed=yes&:toolbar=yes&:tabs=no&test=bla&foo=bar\" border=\"0\" style=\"border: 0pt none;\" ></iframe></div><br/>");
    }
}
