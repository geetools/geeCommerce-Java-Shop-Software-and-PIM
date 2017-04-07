define(['durandal/app', 'knockout', 'gc/gc', 'gc-customer'], function (app, ko, gc, customerAPI) {
	
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
              {'name' : 'id', 'label' : 'CB ID', cookieKey : 'id'},
              {'name' : 'customerNumber', 'label' : 'CB Kundennummer', 'type' : 'wildcard', cookieKey : 'cn'},
              {'name' : 'id2', 'label' : 'ERP Kundennummer', 'type' : 'wildcard', cookieKey : 'id2'},
              {'name' : 'forename', 'label' : 'Vorname', cookieKey : 'fn'},
              {'name' : 'surname', 'label' : 'Nachname', cookieKey : 'sn'},
              {'name' : 'email', 'label' : 'E-Mail', cookieKey : 'e'},
              {'name' : 'createdOn', 'label' : 'Erstellt am', cookieKey : 'co'}
              /* {'name' : '', 'label' : ''} */
            ];
	    	
	    	// Init the pager.
        	this.pager = new gc.Pager(customerAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
//        	return this.pager.load();
        	
        	
            var promise = this.pager.load();
            
            promise.then(function(data) {
            console.log('*** CUSTOMERS: ', data.data.customers);
            });
            
            // We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
            return promise;
        	
	    },
	    compositionComplete : function() {
	    	var self = this;
	    	self.pager.activateSubscribers();
	    }
    }
});