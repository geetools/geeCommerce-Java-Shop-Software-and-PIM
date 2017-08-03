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

	    	gc.app.pageTitle('app:modules.synonym.title');
	    	gc.app.pageDescription('app:modules.synonym.subtitle');
	    	
	    	// Pager columns
			var pagerColumns = [
              {'name' : 'word', 'label' : 'app:modules.synonym.gridColWord', cookieKey : 'wd'},
              {'name' : 'custom', 'label' : 'app:modules.synonym.gridColCustom', cookieKey : 'c'},
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