jQuery(document).ready(
    function()
    {
        initChartControl();
    }
);

function Dimension(height, width) {
    this.height = height;
    this.width = width;
}

function getDimension()
{
    var dim = new Dimension(100,100);

    if(window.innerHeight)
    {
        dim.height = window.innerHeight;
        dim.width = window.innerWidth;
    }
    else if(document.body.clientHeight)
    {
        dim.height = document.body.clientHeight;
        dim.width = document.body.clientWidth;
    }

    return dim;
}

function getPopupDimension()
{
    dim = getDimension();

    dim.width = (Math.round(dim.width * 0.97)).toString() + 'px';
    dim.height = (Math.round(dim.height * 0.97)).toString();

    return dim;
}

function getChartDimension()
{
    dim = getDimension();

    dim.width = (Math.round(dim.width * 0.95)).toString();
    dim.height = (Math.round(dim.height * 0.85)).toString();

    return dim;
}

function initChartControl()
{
    popupDim = getPopupDimension();

    // initialize dialog
    var dialog = jQuery("<div></div>")
    .dialog(
        {
            autoOpen: 	false,
            draggable: 	false,
            modal: 		true,
            resizable: 	false,
            show: 		'puff',
            hide:		'fast',
            title:		'Chart',
            width:		popupDim.width,
            height:		popupDim.height
        }
    );

    // add click event to elements with a workbook/chart attr
    jQuery('*[workbook][report]').click(
        function()
        {
            openChart(dialog, this)
        });
}

function openChart(target, source)
{
    var workbook = jQuery(source).attr( 'workbook' );
    var report = jQuery(source).attr( 'report' );
    var title = jQuery(source).attr('title') ? jQuery(source).attr('title') : report;
    var host = jQuery(source).attr('tableau_host');
    var embed = jQuery(source).attr('embed');
    var toolbar = jQuery(source).attr('toolbar');
    var tabs = jQuery(source).attr('tabs');
    var params = jQuery(source).attr('parameters');

    var url = host + "/views/" + workbook + "/" + report;
    url += "?:embed=" + embed
            + "&:toolbar=" + toolbar
            + "&:tabs=" + tabs;

    if(params && params.length> 0)
    {
        url += "&" + params;
    }

    if(embed && embed == 'no')
    {
        var newWindow = window.open(url, '_blank');
        newWindow.focus();
        return false;
    }

    var chartDim = getChartDimension();

    var iframe = document.createElement('iframe');
    iframe.setAttribute('id', 'iframe1');
    iframe.setAttribute('width', chartDim.width);
    iframe.setAttribute('height', chartDim.height);
    iframe.setAttribute('frameborder','0');
    iframe.setAttribute('src', url);
    iframe.setAttribute('border', '0');
    iframe.style.border = '0';

    var popupDimension = getPopupDimension();

    jQuery(target).dialog('option', 'title', title );
    jQuery(target).dialog('option', 'width', popupDimension.width );
    jQuery(target).dialog('option', 'height', popupDimension.height );
    jQuery(target).dialog('open');
    jQuery(target).html(iframe);

    return false;
}