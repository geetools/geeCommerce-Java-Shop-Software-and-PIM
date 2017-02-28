define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-coupon-promotion' ], function(app, ko, router, gc, couponPromotionsAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function CouponPromotionBaseController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof CouponPromotionBaseController)) {
			throw new TypeError("CouponPromotionBaseController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.couponPromotionVM = {};
		this.coupons = ko.observableArray([]);
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate');
	}

	CouponPromotionBaseController.prototype = {
		constructor : CouponPromotionBaseController,
		saveData : function(view, parent, toolbar) {
			var self = this;

			if(self.couponPromotionVM.id == 'new'){
				var couponPromotion = {};
				couponPromotion.label = self.couponPromotionVM.label();
				couponPromotion.couponId = self.couponPromotionVM.coupon();
				couponPromotion.description = self.couponPromotionVM.description();
				couponPromotion.descriptionProduct = self.couponPromotionVM.descriptionProduct();
				couponPromotion.enabled = self.couponPromotionVM.enabled();
				couponPromotion.conditionMediaAssetId = self.couponPromotionVM.conditionMediaAsset();

				couponPromotionsAPI.createCouponPromotion(couponPromotion).then(function(data) {
					toolbar.hide();
					router.navigate('//coupon-promotions/details/' + data.id);
				});
			} else {
				var updateModel = gc.app.newUpdateModel();
				updateModel.field('label', self.couponPromotionVM.label(), true);
				updateModel.field('description', self.couponPromotionVM.description(), true);
				updateModel.field('descriptionProduct', self.couponPromotionVM.descriptionProduct(), true);
				updateModel.field('enabled', self.couponPromotionVM.enabled(), true);
				updateModel.field('couponId', self.couponPromotionVM.coupon());
				updateModel.field('conditionMediaAssetId', self.couponPromotionVM.conditionMediaAsset());
				couponPromotionsAPI.updateCouponPromotion(self.couponPromotionVM.id, updateModel).then(function(data) {
					toolbar.hide();
				});
			}

		},
		activate : function(couponPromotionId) {
			var self = this;
			
			self.couponPromotionVM = gc.app.sessionGet('couponPromotionVM');

			return couponPromotionsAPI.getCoupons().then(function(data){
				self.coupons.push( { id : '', text : function() {
					return gc.app.i18n('app:common.choose', {}, gc.app.currentLang);
				}});

				if(data.data.coupons){
					data.data.coupons.forEach(function(entry) {
						gc.ctxobj.enhance(entry, [ 'name' ], 'any');
						self.coupons.push({id: entry.id, text: entry.name.i18n});
					});
				}
			});
		}
	};

	return CouponPromotionBaseController;
});