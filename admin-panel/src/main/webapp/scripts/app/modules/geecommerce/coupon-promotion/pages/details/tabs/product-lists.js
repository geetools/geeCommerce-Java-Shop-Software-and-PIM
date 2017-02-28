define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-coupon-promotion' ], function(app, ko, gc, couponPromotionsAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function CouponPromotionMappingController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof CouponPromotionMappingController)) {
			throw new TypeError("CouponPromotionMappingController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.couponPromotionId = undefined;
		this.couponPromotionVM = {};
		this.query = ko.observable();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'dropFromSource', 'removeProductListFromPromotion', 'activate', 'attached');
	}

	CouponPromotionMappingController.prototype = {
		constructor : CouponPromotionMappingController,
        // The pager takes care of filtering, sorting and paging functionality.
        sourceProductListsPager: {},
        dropFromSource : function(data) {
        	var self = this;

        	// Only add attribute to tab if it does not exist yet.
    		var foundProductList = _.findWhere(ko.unwrap(self.couponPromotionVM.productLists), { id : data.id });
        	
    		if(_.isUndefined(foundProductList)) {
            	couponPromotionsAPI.addProductListToPromotion(self.couponPromotionId, data.id).then(function( response ) {
                	self.couponPromotionVM.productLists.push( { id: data.id, label: data.label, key: data.key} );
                	self.sourceProductListsPager.data.remove(data);
            	});
    		}
        },
		removeProductListFromPromotion : function(data) {
        	var self = this;

			couponPromotionsAPI.removeProductListFromPromotion(self.couponPromotionId, data.id).then(function() {
        		// See if the attribute is already in the source container.
        		var foundProductList = _.findWhere(ko.unwrap(self.sourceProductListsPager.data), { id : data.id });
        		
        		// Only add to drag&drop source container if it does not exist yet.
        		if(_.isUndefined(foundProductList)) {
                	self.sourceProductListsPager.data.push( { id: data.id, label: data.label, key: data.key } );
        		}
        		
        		// Remove from target-container in view.
            	self.couponPromotionVM.productLists.remove(data);
        	});
        },
		activate : function(couponPromotionId) {
			var self = this;
			
			self.couponPromotionVM = gc.app.sessionGet('couponPromotionVM');
			self.couponPromotionId = couponPromotionId;
			
	    	// Init the pager.
        	this.sourceProductListsPager = new gc.Pager(couponPromotionsAPI.getProductListPagingOptions(couponPromotionId, { fields : [ 'key', 'label' ], sort : [ 'key' ] }));

        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.

			return self.sourceProductListsPager.load().then(function(data) {          	});
		},
		attached : function() {
			var self = this;

			
			// For the first tab, we register the save event immediately and not
			// just on click.
			gc.app.onToolbarEvent({
				save : self.saveData
			});
		}
	};

	return CouponPromotionMappingController;
});