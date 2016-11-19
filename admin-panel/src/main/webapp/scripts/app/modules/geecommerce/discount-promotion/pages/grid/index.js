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
		_.bindAll(this, 'activate', 'removeDiscountPromotion', 'statusEnabled');
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
    	},
		statusEnabled : function(data) {
			var statusDescTxt = '';

			var value = data.enabled;
			var activeStore = gc.app.sessionGet('activeStore');
			var availableStores = gc.app.confGet('availableStores');

			// If no store is currently selected, just show the current values as text.
			if(_.isEmpty(activeStore) || _.isEmpty(activeStore.id)) {
				var summaryText = '';
				_.each(availableStores, function(store) {
					if(store.id && store.id != '') {

						var txt = false;

						if(!_.isUndefined(value)) {
							txt = gc.ctxobj.val(value, undefined, 'closest', store.id);
						}

						if(!_.isUndefined(txt)) {
							if (availableStores.length > 2) { 
								if(txt) {
									summaryText += '<img class="gridStoreImg" src="' + store.iconPathXS + '" title="' + store.name + '"/><br/>';
								} else {
									summaryText += '';
								}
							} else {
								if(txt) {
									summaryText += '<span class="gridStoreStatusImg product-status-tick fa fa-check"></span>';
								} else {
									summaryText += '<span class="gridStoreStatusImg product-status-tick fa fa-times"></span>';
								}
							}
						}
					}
				});

				statusDescTxt = summaryText;
			} else {
				var txt = false;
				if(!_.isUndefined(value)) {
					txt = gc.ctxobj.val(value, undefined, 'closest', activeStore.id);

				}
				if(txt) {
					statusDescTxt = '<span class="gridStoreStatusImg product-status-tick fa fa-check"></span>';
				} else {
					statusDescTxt = '<span class="gridStoreStatusImg product-status-cross fa fa-circle-o"></span>';
				}

			}

			return statusDescTxt;
		}
    }
	
	return DiscountPromotionGridIndexController;
});