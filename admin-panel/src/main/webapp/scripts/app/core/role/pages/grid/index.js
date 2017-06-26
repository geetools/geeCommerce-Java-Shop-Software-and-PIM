define(['durandal/app', 'knockout', 'gc/gc', 'gc-role'], function (app, ko, gc, roleAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
	    activate: function(data) {

            gc.app.pageTitle(gc.app.i18n('app:modules.role.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.role.subtitle'));
	    	
	    	// Pager columns
			var pagerColumns = [
              {'name' : 'code', 'label' : 'Code'},
              {'name' : 'name', 'label' : 'Name'},
              {'name' : '', 'label' : ''}
            ];
	    	
	    	// Init the pager.
        	this.pager = new gc.Pager(roleAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return this.pager.load();
	    }
    }
});