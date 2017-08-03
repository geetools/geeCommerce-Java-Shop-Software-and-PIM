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
	<script src="/web/js-app?v=${v?long}"></script>
	