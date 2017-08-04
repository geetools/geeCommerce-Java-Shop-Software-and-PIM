define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-coupon-promotion',  'gc-product-list' ], function(app, ko, gc, couponPromotionsAPI, productListAPI) {

	function CouponPromotionVM(id) {
		var self = this;
        self.id = id;
		self.label = ko.observableArray();
		self.description = ko.observableArray([]);
		self.descriptionProduct = ko.observableArray([]);
		self.coupon = ko.observable();
		self.enabled = ko.observableArray([]);
		self.productLists = ko.observableArray([]);
		self.conditionMediaAsset = ko.observable();


/*
		self.position = ko.observable();
		self.showInVariantMaster = ko.observable();
		self.showInProgramme = ko.observable();
		self.showInProduct = ko.observable();
		self.enabled = ko.observable();
		self.attributes = ko.observableArray();*/
	}	

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function CouponPromotionIndexController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof CouponPromotionIndexController)) {
			throw new TypeError("CouponPromotionIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.couponPromotionId = undefined;
		this.couponPromotionVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

	CouponPromotionIndexController.prototype = {
		constructor : CouponPromotionIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.coupon-promotion.detailsTitle';
            return title;
        },
		saveData : function() {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();
		},
		activate : function(couponPromotionId) {
			var self = this;
			
/*			gc.app.pageTitle(self.pageTitle());
			gc.app.pageDescription(self.pageDescription);*/


			if(couponPromotionId != 'new'){
				return couponPromotionsAPI.getCouponPromotion(couponPromotionId).then(function(couponPromotion) {

					self.couponPromotionId = couponPromotion.id;

					self.couponPromotionVM = new CouponPromotionVM(couponPromotion.id);
					self.couponPromotionVM.label(couponPromotion.label);
					self.couponPromotionVM.description(couponPromotion.description);
					self.couponPromotionVM.descriptionProduct(couponPromotion.descriptionProduct);
					self.couponPromotionVM.coupon(couponPromotion.couponId);
					self.couponPromotionVM.enabled(couponPromotion.enabled);
					self.couponPromotionVM.conditionMediaAsset(couponPromotion.conditionMediaAssetId);

					gc.app.sessionPut('couponPromotionVM', self.couponPromotionVM);

					return productListAPI.getProductLists().then(function (data) {
                    	var productLists =  data.data.productLists;

                        _.each(couponPromotion.productListIds, function (id) {
							var productList = _.findWhere( productLists, { id : id } );
                            self.couponPromotionVM.productLists.push({id: productList.id, key: productList.key, label: productList.label});
                        });


                    });

/*					if(couponPromotion.productLists){
						_.each(couponPromotion.productLists, function (productList) {
							self.couponPromotionVM.productLists.push({id: productList.id, key: productList.key, label: productList.label});
						})
					}*/


					//gc.app.pageTitle(self.pageTitle());
				});
			} else {
				 self.couponPromotionVM = new CouponPromotionVM('new');
				 gc.app.sessionPut('couponPromotionVM', self.couponPromotionVM);
			}

		},
		attached : function() {
			var self = this;
		}
	};

	return CouponPromotionIndexController;
});
