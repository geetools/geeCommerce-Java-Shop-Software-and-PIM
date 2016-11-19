define(['durandal/app', 'knockout', 'gc/gc', 'gc-order'], function (app, ko, gc, orderAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
	    activate: function(data) {

	    	gc.app.pageTitle('Bestellungen Verwalten');
	    	gc.app.pageDescription('Bestellungen ansehen und bearbeiten');
	    	
	    	// Pager columns
			var pagerColumns = [
              {'name' : 'order_number', 'label' : 'CB Bestellnummer', 'type' : 'simple', cookieKey : 'on'},
              {'name' : 'id2', 'label' : 'ERP Bestellnr.', cookieKey : 'id2'},
              {'name' : 'transactionId', 'label' : 'Transaktionsnummer', cookieKey : 't'},
              {'name' : 'customer_fk', 'label' : 'CB Kunden-ID', 'type' : 'simple', cookieKey : 'c'},
              {'name' : 'name', 'label' : 'Kunde', cookieKey : 'n'},
              {'name' : 'totalAmount', 'label' : 'Umsatz', cookieKey : 'ta'},
              {'name' : 'discount_code', 'label' : 'Coupon', cookieKey : 'dc'},
              {'name' : 'order_status', 'label' : 'Status', cookieKey : 'os'},
              {'name' : 'createdOn', 'label' : 'Erstellt am', cookieKey : 'co'},
/*              {'name' : '', 'label' : ''}*/
            ];
	    	
	    	// Init the pager.
        	this.pager = new gc.Pager(orderAPI.pagingOptions({columns : pagerColumns}));

			var promise = this.pager.load();
			
			promise.then(function(data) {
			console.log(data);
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