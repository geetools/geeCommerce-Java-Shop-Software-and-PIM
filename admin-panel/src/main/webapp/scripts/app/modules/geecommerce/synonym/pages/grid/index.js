define(['durandal/app', 'knockout', 'gc/gc', 'gc-synonym'], function (app, ko, gc, synonymAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
		removeSynonym: function(synonym) {
			var self = this;

			synonymAPI.removeSynonym(synonym.id).then(function() {
				self.pager.removeData(synonym);
			});
		},
	    activate: function(data) {

	    	gc.app.pageTitle('Kunden Verwalten');
	    	gc.app.pageDescription('Kunden ansehen und bearbeiten');
	    	
	    	// Pager columns
			var pagerColumns = [
              {'name' : 'id', 'label' : 'CB ID', cookieKey : 'id'},
              {'name' : 'word', 'label' : 'app:modules.search-rewrite.gridColWord', cookieKey : 'wd'},
              {'name' : 'custom', 'label' : 'app:modules.search-rewrite.gridColCustom', cookieKey : 'c'},
              {'name' : '', 'label' : ''}
            ];
	    	
	    	// Init the pager.
        	this.pager = new gc.Pager(synonymAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return this.pager.load();
	    },
	    compositionComplete : function() {
	    	var self = this;
	    	self.pager.activateSubscribers();
	    }
    }
});