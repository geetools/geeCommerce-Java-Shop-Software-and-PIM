// -----------------------------------------------------------------------
// External js libraries and settings.
// -----------------------------------------------------------------------

requirejs.config({
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
        'jquery-ui': 'jquery/plugins/jquery-ui/js/jquery-ui',
        'bootstrap': 'bootstrap4/bootstrap.min',
        'bootstrap-jasny': 'bootstrap/plugins/jasny-bootstrap/js/jasny-bootstrap.min',
        'bootstrap-select': 'bootstrap/plugins/select/bootstrap-select.min',
        'bootstrap-slider': 'bootstrap/plugins/slider/bootstrap-slider.min',
        'tinycolor': 'tinycolor-min',
        'pick-a-color': 'bootstrap/plugins/pick-a-color/pick-a-color-1.2.3.min',
		'focusable': 'jquery/plugins/focusable/focus-element-overlay',
        'mustache': 'mustache/mustache.min',
		'postal': 'postaljs/postal.min',
        'underscore': 'underscorejs/underscore-min',
        'text': 'requirejs/text',
		'gc' : 'geecommerce'
    },
	shim: {
        'bootstrap': {
            deps: [ 'jquery' ]
        },
        'bootstrap-jasny': {
            deps: [ 'jquery' ]
        },
        'pick-a-color': {
            deps: [ 'jquery', 'tinycolor' ]
        },
        'jquery-ui': {
			deps: [ 'jquery' ],
			exports : 'jQuery'
		},
		'jquery-tmpl': {
			deps: [ 'jquery', 'jquery-ui' ],
			exports : 'jQuery'
		},
		'jquery-magnific-popup': ['jquery'],
		'jquery-slick': ['jquery'],
		'focusable': {
			deps: [ 'jquery' ],
			exports : 'jQuery'
		},
    }
});

console.log('after initializing config');


// -----------------------------------------------------------------------
// Init CommerceBoard libraries.
// -----------------------------------------------------------------------

	// Init module js path.
	requirejs.config({
		paths:  {
			'catalog' : '/m/catalog/js/',
			'cart' : '/m/cart/js/',
            'price' : '/m/price/js/',
			'customer' : '/m/customer/js/',
			'checkout' : '/m/checkout/js/',
			'whitelabel' : '/m/whitelabel/js/',
			'customer-review' : '/m/customer-review/js/',
			'gui-widgets' : '/m/gui-widgets/js/'
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

	requirejs.config({
		paths:  {
			'page' : pageDir,
			'templates' : templateDir
		}
	});


console.log('initialized paths!!');

require(['jquery', 'focusable'], function($, focusable){

	$(document).ready(function () {

		function messageListener(event){
<#--		var scripts = [];

		if(data.data.html && data.data.html.match(/<script>(.*?)<\/script>/g))
		{
			scripts = data.data.html.match(/<script>(.*?)<\/script>/g).map(function (val) {
				return val.replace(/<\/?script>/g, '');
			});
		}
-->

<#--		var cleanHtml = $.parseHTML(data.data.html)
		$.when($(data.data.selector).html(cleanHtml)).then(function() {
			_.each(scripts, function (script) {
				console.log($("body").html());
				$.globalEval(script);
			});
		});-->



			var data = JSON.parse(event.data);
			if(data.message == "set-html"){
				var scripts = [];
				if(data.scripts){
					scripts = data.scripts;
				}

				_.each(scripts, function (script) {
					$('<script>').attr('type', 'text/javascript').text(script).appendTo('head');
					//$.globalEval(script);
				});
			} else if(data.message == "focus") {
    			$(".preview-container").addClass('dashed-border');
    			Focusable.setFocus($(".preview-container"), {});
			} else if(data.message == "unfocus") {
    			Focusable.hide();
    			$(".preview-container").removeClass('dashed-border');
			}


};

		console.log("EXECUTED ONCE");
		if (window.addEventListener){
			addEventListener("message", messageListener, false)
		} else {
			attachEvent("onmessage", messageListener)
		}
	});

});

require(['jquery', 'bootstrap', 'gc/gc', 'gc/app', 'gc/rest'], function($, Bootstrap, gc, App, gcRest) {
	gc.app = new App();
	gc.rest = gcRest;

	// Get current page info.
	var pageInfo = gc.app.pageInfo();

	console.log('Initializing app with page-info: ', pageInfo);



	if(pageMain) {
		require([pageMain]);
	}

	console.log('after initializing page');



});


