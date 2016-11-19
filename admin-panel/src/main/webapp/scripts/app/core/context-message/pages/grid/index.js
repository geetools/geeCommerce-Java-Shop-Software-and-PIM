define(['durandal/app', 'knockout', 'gc/gc', 'gc-context-message'], function (app, ko, gc, messageAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
	    activate: function(data) {
            gc.app.pageTitle(gc.app.i18n('app:modules.context-message.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.context-message.subtitle'));
	    	
	    	// Pager columns
			var pagerColumns = [
              {'name' : 'key', 'label' : 'app:modules.context-message.gridColKey'},
              {'name' : 'value', 'label' : 'app:modules.context-message.gridColValue'},
              {'name' : '', 'label' : ''}
            ];
	    	
	    	// Init the pager.
        	this.pager = new gc.Pager(messageAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return this.pager.load();
	    }
    }
});