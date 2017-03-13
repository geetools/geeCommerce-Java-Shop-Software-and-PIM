define(['durandal/app', 'knockout', 'gc/gc', 'gc-product-promotion'], function (app, ko, gc, productPromotionAPI) {
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductPromotionGridIndexController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductPromotionGridIndexController)) {
			throw new TypeError("ProductPromotionGridIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.pager = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'removeProductPromotion');
	}
	
	ProductPromotionGridIndexController.prototype = {
		constructor : ProductPromotionGridIndexController,
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
	    activate: function(data) {
	    	var self = this;
            gc.app.pageTitle(gc.app.i18n('app:modules.product-promotion.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.product-promotion.subtitle'));
	    	
	    	// Pager columns
			var pagerColumns = [
				{'name' : '#'},
              	{'name' : 'key', 'label' : 'app:modules.product-promotion.gridColKey'},
              	{'name' : 'contextDisplayLabel', 'label' : 'app:modules.product-promotion.gridColLabel'},
			  	{'name' : 'enabled', 'label' : 'app:modules.product-promotion.gridColEnabled'},
              	{'name' : '', 'label' : ''}
            ];
	    	
	    	// Init the pager.
        	self.pager = new gc.Pager(productPromotionAPI.pagingOptions({columns : pagerColumns}));
        	
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return self.pager.load();
	    },
	    removeProductPromotion: function(productPromotion) {
    		var self = this;
    		var yes = gc.app.i18n('app:common.yes');
    		var no = gc.app.i18n('app:common.no');
    		
			app.showMessage(gc.app.i18n('app:modules.product-promotion.confirmDelete'), gc.ctxobj.val(productPromotion.backendLabel, gc.app.currentLang()), [yes, no]).then(function(confirm) {
				if(confirm == yes) {
					productPromotionAPI.removeProductPromotion(productPromotion.id).then(function() {
		    			self.pager.removeData(productPromotion);
		    		});
				}
			});
    	}
    }
	return ProductPromotionGridIndexController;
});