    <meta charset="utf-8">
    <!-- Set the viewport so this responsive site displays correctly on mobile devices -->
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>geeCommerce Demo Store</title>

    <#if args?has_content>
        <@seo pageModel=args["pageModel"] title=args["title"] uri=args["uri"] metaDescription=args["metaDescription"] metaRobots=args["metaRobots"] metaKeywords=args["metaKeywords"] />
    <#else>
        <@seo />
    </#if>

    <link href='https://fonts.googleapis.com/css?family=Simonetta' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Lato' rel='stylesheet' type='text/css'>

    <!-- Include bootstrap CSS -->
    
    <link href="/js/vendor/jquery/plugins/magnific-popup/magnific-popup.css" rel="stylesheet">
    <link href="/js/vendor/jquery/plugins/slick/slick.css" rel="stylesheet">
    <link href="/js/vendor/jquery/plugins/slick/slick-theme.css" rel="stylesheet">
    <link href="/js/vendor/jquery/plugins/rateit/rateit.css" rel="stylesheet">

    <link href="/js/vendor/bootstrap/bootstrap.min.css" rel="stylesheet">
    <link href="/js/vendor/jquery-ui/jquery-ui.css" rel="stylesheet">

    <link href="<@skin path="css/gc.css"/>" rel="stylesheet">
    <link href="<@skin path="css/gc_icons.css"/>" rel="stylesheet">
    <link href="<@skin path="css/gc.css"/>" rel="stylesheet">
    <link href="<@skin path="css/gc_phone.css"/>" rel="stylesheet">
    <link href="<@skin path="css/gc_tablet.css"/>" rel="stylesheet">
    <link href="<@skin path="css/gc_desktop.css"/>" rel="stylesheet">

	<#if moduleCode?has_content>
	    <@skin type="module" fetch="styles" var="moduleCss" />
	    
		<#if moduleCss?has_content>
		    <link href="${moduleCss}" rel="stylesheet">
		</#if>
	</#if>

    <!--[if lt IE 9]>
		<script src="https://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->

	<script src="/js/vendor/sugarjs/sugar.min.js"></script>
	<script src="/js/vendor/underscorejs/underscore-min.js"></script>
    <script src="/js/vendor/postaljs/postal.min.js"></script>

	<script src="/js/vendor/geecommerce/settings.js?t=${.now?long}"></script>

	<@js type="page" var="pageJs"/>
	<script id="gc-main-script" data-page-main="${pageJs!""}" data-module="${moduleCode}" src="/js/vendor/requirejs/require.js"></script>
<!--
		<script src="/web/js-app?v=${v?long}"></script>
-->

	<script type="text/javascript">
// -----------------------------------------------------------------------
// External js libraries and settings.
// -----------------------------------------------------------------------

require.config({
	enforceDefine: true,
	waitSeconds: 0,
    baseUrl: '/js/vendor',
    paths: {
        'jquery': 'jquery/jquery-1.12.4.min',
        'jquery-magnific-popup': 'jquery/plugins/magnific-popup/jquery.magnific-popup',
        'jquery-slick': 'jquery/plugins/slick/slick',
        'jquery-swipe': 'jquery/plugins/jquery.touchSwipe.min',
        'jquery-rateit': 'jquery/plugins/rateit/jquery.rateit.min',
        'jquery-dropzone': 'jquery/plugins/dropzone/dropzone-amd-module',
        'jquery-tmpl': 'jquery/plugins/jquery-tmpl/plugins/jquery.tmpl.min',
        'jquery-ui': 'jquery-ui/jquery-ui',
        'bootstrap': 'bootstrap/bootstrap.min',
        'bootstrap-jasny': 'bootstrap/plugins/jasny-bootstrap/js/jasny-bootstrap.min',
        'bootstrap-select': 'bootstrap/plugins/select/bootstrap-select.min',
        'bootstrap-slider': 'bootstrap/plugins/slider/bootstrap-slider.min',
        'tinycolor': 'tinycolor-min',
        'pick-a-color': 'bootstrap/plugins/pick-a-color/pick-a-color-1.2.3.min',
        'mustache': 'mustache/mustache.min',
		'postal': 'postaljs/postal.min',
        'underscore': 'underscorejs/underscore-min',
        'text': 'requirejs/text',
		'gc' : 'geecommerce'
    },
	shim: {
        'bootstrap': {
            deps: [ 'jquery' ],
            exports: "jQuery.fn.popover"
        },
        'bootstrap-jasny': {
            deps: [ 'jquery' ]
        },
        'pick-a-color': {
            deps: [ 'jquery', 'tinycolor' ]
        },
		'jquery-tmpl': {
			deps: [ 'jquery', 'jquery-ui' ],
			exports : 'jQuery'
		},
		'jquery-rateit': {
			deps: [ 'jquery' ],
			exports : 'jQuery'
		},
		'jquery-magnific-popup': ['jquery'],
		'jquery-slick': ['jquery']
    }
});

console.log('after initializing config');


// -----------------------------------------------------------------------
// Init CommerceBoard libraries.
// -----------------------------------------------------------------------

	// Init module js path.
	require.config({
		paths:  {
			'catalog' : '/m/catalog/js/',
			'cart' : '/m/cart/js/',
            'price' : '/m/price/js/',
			'customer' : '/m/customer/js/',
			'checkout' : '/m/checkout/js/',
			'whitelabel' : '/m/whitelabel/js/',
			'customer-review' : '/m/customer-review/js/'
		}
	});

	// Init current page.
	var mainScript = document.getElementById('gc-main-script');
    var pageModule = mainScript.getAttribute('data-module');
    var pageMain = mainScript.getAttribute('data-page-main');
    
    var pos = pageMain.lastIndexOf('/');
    var pageDir = pageMain.substring(0, pos);
    var templateDir = '/m/' + pageModule + '/js/templates/';
    
    console.log('~~~ pageMain ~~~ ', pageMain, pageDir, templateDir, pageModule);

	require.config({
		paths:  {
			'page' : pageDir,
			'templates' : templateDir
		}
	});

require(['jquery']);


define('gc-deps', ['jquery', 'bootstrap', 'gc/gc', 'gc/app', 'gc/rest'], function($, Bootstrap, gc, App, gcRest) {
	console.log('--------------------> REQUIRING DEPS!!!');
});


console.log('initialized paths!! ', require);

require(['jquery', 'jquery-ui', 'bootstrap', 'gc/gc', 'gc/app', 'gc/rest'], function($, jqui,  Bootstrap, gc, App, gcRest) {
console.log('GCCCCCCCCCCCCCCCCCCCCCCC: ', gc);
	gc.app = new App();
	gc.rest = gcRest;

console.log('GCCCCCCCCCCCCCCCCCCCCCCC: ', gc);

	// Get current page info.
	var pageInfo = gc.app.pageInfo();

	console.log('Initializing app with page-info: ', pageInfo);



	if(pageMain) {
		require([pageMain]);
	}
	
	console.log('after initializing page');

    $(document).ready(function(){

        function highlightName(productName, queryString) {

            var queryStringParts = queryString.split(' ');

            for (var i=0; i<queryStringParts.length; i++) {
                if(queryStringParts[i].trim() == '')
                    continue;
                var regex = new RegExp('(' + queryStringParts[i].trim() + ')','gi');
                productName = productName.replace(regex, '<b>$1</b>');
            }

            return productName;
        }

        $("#searchFormKeyword").autocomplete({
            source: function(request, response) {
                $.ajax({
                    url: "/catalog/search/autocomplete",
                    data: request,
                    dataType: "json",
                    method: "post",
                    success: response
                })
            },
            delay: 250,
            minLength: 3
        })
        $("#searchFormKeyword").data( "ui-autocomplete" )._renderItem = function( ul, item ) {
            return $( "<li>" )
                    .data( "ui-autocomplete-item", item )
                    .append( "<a href='" + item.uri + "' style = 'width: 194px; text-align: left; color: #000000;'>" + highlightName(item.label,$('#searchFormKeyword').val()) + "</a>" )
                    .appendTo(ul);
        };
    });

});


	
	</script>	