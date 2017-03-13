define(['durandal/app', 'knockout', 'gc/gc', 'gc-discount-promotion'], function (app, ko, gc, discountPromotionAPI) {
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function DiscountPromotionGridIndexController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof DiscountPromotionGridIndexController)) {
			throw new TypeError("DiscountPromotionGridIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.pager = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'removeDiscountPromotion');
	}
	
	DiscountPromotionGridIndexController.prototype = {
		constructor : DiscountPromotionGridIndexController,
    	app: gc.app,
	    activate: function(data) {
	    	var self = this;
            gc.app.pageTitle(gc.app.i18n('app:modules.discount-promotion.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.discount-promotion.subtitle'));
	    	
	    	// Pager columns
			var pagerColumns = [
			  {'name' : '#'},
              {'name' : 'key', 'label' : 'app:modules.discount-promotion.gridColKey'},
              {'name' : 'label', 'label' : 'app:modules.discount-promotion.gridColLabel'},
			  {'name' : 'enabled', 'label' : 'app:modules.discount-promotion.gridColEnabled'},
              {'name' : '', 'label' : ''}
            ];
	    	
	    	// Init the pager.
        	self.pager = new gc.Pager(discountPromotionAPI.pagingOptions({columns : pagerColumns}));
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return self.pager.load();
	    },
	    removeDiscountPromotion: function(discountPromotion) {
    		var self = this;
    		var yes = gc.app.i18n('app:common.yes');
    		var no = gc.app.i18n('app:common.no');
    		
			app.showMessage(gc.app.i18n('app:modules.discount-promotion.confirmDelete'), gc.ctxobj.val(discountPromotion.backendLabel, gc.app.currentLang()), [yes, no]).then(function(confirm) {
				if(confirm == yes) {
					discountPromotionAPI.removeDiscountPromotion(discountPromotion.id).then(function() {
		    			self.pager.removeData(discountPromotion);
		    		});
				}
			});
    	}
    }
	
	return DiscountPromotionGridIndexController;
});