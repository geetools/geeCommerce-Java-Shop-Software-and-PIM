define(['durandal/app', 'knockout', 'gc/gc', 'gc-merchant'], function (app, ko, gc, merchantAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
	    activate: function(data) {
	    	var self = this;
	    
	    	gc.app.pageTitle('Merchants Verwalten');
	    	gc.app.pageDescription('Merchants ansehen und bearbeiten');

	        // Pager columns
	        var pagerColumns = [
	            {'name' : 'companyName', 'label' : 'app:modules.urlrewrite.gridColCompanyName', cookieKey : 'cn'},
                {'name' : 'companyWebsite', 'label' : 'app:modules.urlrewrite.gridColCompanyWebsite', cookieKey : 'cw'}
	        ];
	    		    	
	    	// Init the pager.
        	self.pager = new gc.Pager(merchantAPI.getPagingOptions({ columns : pagerColumns }));
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return self.pager.load();
	    },
	    compositionComplete : function() {
	    	var self = this;
	    	self.pager.activateSubscribers();
	    }
    }
});