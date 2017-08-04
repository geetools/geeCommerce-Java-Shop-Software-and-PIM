define(['durandal/app', 'knockout', 'gc/gc', 'gc-navigation'], function (app, ko, gc, navigationAPI) {
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function NavigationGridIndexController(options) {

		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof NavigationGridIndexController)) {
			throw new TypeError("NavigationGridIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.pager = {};

		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'removeNavigation');
	}

	NavigationGridIndexController.prototype = {
			
    	app: gc.app,
	    activate: function(data) {
            gc.app.pageTitle(gc.app.i18n('app:modules.navigation.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.navigation.subtitle'));
	    	
	    	// Pager columns
			var pagerColumns = [
              {'name' : 'key', 'label' : 'app:modules.navigation.gridColKey'},
              {'name' : 'label', 'label' : 'app:modules.navigation.gridColLabel'},
              {'name' : '', 'label' : ''}
            ];
	    	
	    	// Init the pager.
        	this.pager = new gc.Pager(navigationAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return this.pager.load();
	    },
	    removeNavigation: function(navigation) {
	    	var self = this;
			var yes = gc.app.i18n('app:common.yes');
			var no = gc.app.i18n('app:common.no');

			app.showMessage(gc.app.i18n('app:modules.navigation.confirmDelete'), gc.ctxobj.val(navigation.Label, gc.app.currentLang()), [yes, no]).then(function (confirm) {
				if (confirm == yes) {
					navigationAPI.removeNavigationItem(navigation.id).then(function () {
						self.pager.removeData(navigation);
					});
				}
			});
	    }
    }
	
	return NavigationGridIndexController;
});