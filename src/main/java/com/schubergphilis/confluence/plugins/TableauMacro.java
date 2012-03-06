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
        super.webResourceManager = webResourceManager;
        super.bandanaManager = bandanaManager;
    }

    public boolean hasBody()
    {
        return false;
    }

    public RenderMode getBodyRenderMode()
    {
        return RenderMode.NO_RENDER;
    }

    public String RenderPlugin(Map params, String body, RenderContext renderContext)
            throws ValidationException, AuthenticationException, IOException, NoSuchAlgorithmException, KeyManagementException
    {
        String workbook = getStrParameter(params, "workbook");
        String report = getStrParameter(params, "report");
        String title = getStrParameter(params, "title", report);
        String environment = getStrParameter(params, "environment");
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

        configManager = new ConfigurationManager(bandanaManager);
        String host = configManager.getValue(environment, DefaultValueBehaviour.firstInList);

        Boolean isExport = isPdfOrWordOutput(renderContext);

        // skip for exporting to pdf/word or preview mode
        if(isExport && noPrint)
            return "";

        if(RenderContext.PREVIEW.equals(renderContext.getOutputType()) && ( workbook.length() == 0 || report.length() == 0))
            return "Please enter a workbook and a report and hit the refresh button.";

        TableauRenderer renderer = new TableauRenderer()
                .WithHeight(height)
                .WithWidth(width)
                .WithReport(report)
                .WithWorkbook(workbook)
                .WithTitle(title)
                .WithInteractiveStart(interactive)
                .WithEmbed(embed)
                .WithToolbar(toolbar)
                .WithBorderStyle(borderStyle)
                .WithInteractiveButton(button)
                .WithExportContext(isExport)
                .WithTabs(tabs)
                .WithParameters(parameters);

        DetermineHost(host, renderer);

        return renderer.Render();
    }

    private void DetermineHost(String host, TableauRenderer renderer) throws IOException, AuthenticationException, NoSuchAlgorithmException, KeyManagementException, ValidationException
    {
        String username = AuthenticatedUserThreadLocal.getUsername();

        // for debugging purposes, override the confluence username
        String debugusername = configManager.getValue("debugusername");
        if(debugusername != null && debugusername.length() > 0)
            username = debugusername;

        String trustedHost = new TrustedAuthentication(new HttpRequest())
                .WithTableauUrl(host)
                .WithUsername(username)
                .Authenticate();

        renderer.WithHost(host, trustedHost);
    }

    private boolean isPdfOrWordOutput(RenderContext context)
    {
        return RenderContext.PDF.equals(context.getOutputType()) || RenderContext.WORD.equals(context.getOutputType());
    }

    void includeResources()
    {
        String resource = "com.schubergphilis.confluence.plugins.tableau-plugin:javascript-resources";
        webResourceManager.requireResource(resource);
    }
}