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
		_.bindAll(this, 'activate', 'removeProductPromotion', 'statusEnabled');
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
              {'name' : 'key', 'label' : 'app:modules.product-promotion.gridColKey'},
              {'name' : 'label', 'label' : 'app:modules.product-promotion.gridColLabel'},
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
	return ProductPromotionGridIndexController;
});