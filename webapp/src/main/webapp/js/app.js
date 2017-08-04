require.config({
	waitSeconds: 0,
    baseUrl: '/js/v2/lib',
    paths: {
        jquery: 'jquery/jquery-1.11.3.min'

    }
});

require(['jquery'], function($) {
	    var pageMain = $('#gc-main-script').data('page-main')
		require([pageMain]);
});