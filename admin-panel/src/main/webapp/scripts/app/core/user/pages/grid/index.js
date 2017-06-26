define(['durandal/app', 'knockout', 'gc/gc', 'gc-user'], function (app, ko, gc, userAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
	    activate: function(data) {

            gc.app.pageTitle(gc.app.i18n('app:modules.user.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.user.subtitle'));
	    	
	    	// Pager columns
			var pagerColumns = [
              {'name' : 'username', 'label' : 'Username'},
              {'name' : 'forename', 'label' : 'Vorname'},
              {'name' : 'surname', 'label' : 'Nachname'},
              {'name' : 'email', 'label' : 'E-Mail'},
              {'name' : 'createdOn', 'label' : 'Erstellt am'},
              {'name' : '', 'label' : ''}
            ];
	    	
	    	// Init the pager.
        	this.pager = new gc.Pager(userAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return this.pager.load();
	    }
    }
});