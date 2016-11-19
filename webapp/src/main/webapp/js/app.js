require.config({
	waitSeconds: 0,
    baseUrl: '/js/v2/lib',
    paths: {
        jquery: 'jquery/jquery-1.11.3.min'

    }
});


console.log('::::: MODULES :::: ', modules, require.config);


console.log('IN APPPPPPPPPP!!');


require(['jquery'], function($) {
	    var pageMain = $('#gc-main-script').data('page-main')
	    
	    console.log('~~~ pageMain ~~~ ', pageMain);
	
		require([pageMain]);
});