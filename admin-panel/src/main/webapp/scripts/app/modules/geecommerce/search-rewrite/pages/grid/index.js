define(['durandal/app', 'knockout', 'gc/gc', 'gc-search-rewrite'], function (app, ko, gc, searchRewriteAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
		removeSearchRewrite: function(searchRewrite) {
			var self = this;
			searchRewriteAPI.removeSearchRewrite(searchRewrite.id).then(function() {
				self.pager.removeData(searchRewrite);
			});
		},
	    activate: function(data) {

	    	gc.app.pageTitle('app:modules.search-rewrite.title');
	    	gc.app.pageDescription('app:modules.search-rewrite.subtitle');
	    	
	    	// Pager columns
			var pagerColumns = [
              {'name' : 'keywords', 'label' : 'app:modules.search-rewrite.gridColKeywords', cookieKey : 'kwds'},
              {'name' : 'targetUri', 'label' : 'app:modules.search-rewrite.gridColTargetUri', cookieKey : 'tu'},
              {'name' : '', 'label' : 'app:common.action'}
            ];
	    	
	    	// Init the pager.
        	this.pager = new gc.Pager(searchRewriteAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return this.pager.load();
	    },
	    compositionComplete : function() {
	    	var self = this;
	    	self.pager.activateSubscribers();
	    }
    }
});