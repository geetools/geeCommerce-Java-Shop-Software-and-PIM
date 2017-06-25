define(['durandal/app', 'knockout', 'gc/gc', 'gc-request-context'], function (app, ko, gc, requestContextAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
	    activate: function(data) {
	    	var self = this;
	    
	    	gc.app.pageTitle('URL-Rewrites Verwalten');
	    	gc.app.pageDescription('URL-Rewrites ansehen und bearbeiten');
	    		    	
	        // Pager columns
	        var pagerColumns = [
	            {'name' : 'urlPrefix', 'label' : 'app:modules.request-merchant.gridColUrlPrefix', cookieKey : 'up'},
                {'name' : 'language', 'label' : 'app:modules.request-merchant.gridColLanguage', cookieKey : 'lang'},
                {'name' : 'country', 'label' : 'app:modules.request-merchant.gridColCountry', cookieKey : 'cntr'},
	        ];
	    		    	
	    	// Init the pager.
        	self.pager = new gc.Pager(requestContextAPI.getPagingOptions({ columns : pagerColumns }));
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return self.pager.load();
	    },
	    compositionComplete : function() {
	    	var self = this;
	    	self.pager.activateSubscribers();
	    }
    }
});