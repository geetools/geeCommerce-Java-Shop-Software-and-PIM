define(['durandal/app', 'knockout', 'gc/gc', 'plugins/router', 'gc-content'], function (app, ko, gc, router, contentAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
	    activate: function(data) {
            gc.app.pageTitle(gc.app.i18n('app:modules.content.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.content.subtitle'));
	    	
	    	// Pager columns
			var pagerColumns = [
				{'name' : 'key', 'label' : 'app:modules.content.gridColKey'},
				{'name' : 'name', 'label' : 'app:modules.content.gridColName'},
			];
	    	
	    	// Init the pager.
        	this.pager = new gc.Pager(contentAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return this.pager.load();
	    },
		// ---------------------------------------------
		// Durandal callback.
		// ---------------------------------------------
		attached : function() {
			var self = this;


		}
    }
});