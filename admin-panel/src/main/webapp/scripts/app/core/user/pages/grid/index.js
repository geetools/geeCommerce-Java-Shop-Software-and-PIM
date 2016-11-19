define(['durandal/app', 'knockout', 'gc/gc', 'gc-user'], function (app, ko, gc, userAPI) {
	
    return {
    	app: gc.app,
    	pageTitle: 'Kunden verwalten',
    	pageDescription: 'Kunden ansehen und bearbeiten',
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
	    activate: function(data) {

	    	gc.app.pageTitle('Kunden Verwalten');
	    	gc.app.pageDescription('Kunden ansehen und bearbeiten');
	    	
	    	// Pager columns
			var pagerColumns = [
              {'name' : 'id', 'label' : 'Id'},
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