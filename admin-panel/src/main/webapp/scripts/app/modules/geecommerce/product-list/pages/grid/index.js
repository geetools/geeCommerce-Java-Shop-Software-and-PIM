define(['durandal/app', 'knockout', 'gc/gc', 'gc-product-list'], function (app, ko, gc, productListAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
        removeProductList: function(productList) {
            var self = this;

            productListAPI.removeProductList(productList.id).then(function() {
                self.pager.removeData(productList);
            });
        },
	    activate: function(data) {
            gc.app.pageTitle(gc.app.i18n('app:modules.product-list.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.product-list.subtitle'));
	    	
	    	// Pager columns
			var pagerColumns = [
              {'name' : 'label', 'label' : 'app:modules.product-list.gridColLabel', cookieKey : 'l'},
              {'name' : 'createdOn', 'label' : 'Erstellt am', cookieKey : 'e'},
              {'name' : 'createdBy', 'label' : 'Erstellt von', cookieKey : 'e'},
              {'name' : '', 'label' : 'Löschen'}
            ];
	    	
	    	// Init the pager.
        	this.pager = new gc.Pager(productListAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return this.pager.load();
	    },
	    compositionComplete : function() {
	    	var self = this;
	    	self.pager.activateSubscribers();
	    }
    }
});