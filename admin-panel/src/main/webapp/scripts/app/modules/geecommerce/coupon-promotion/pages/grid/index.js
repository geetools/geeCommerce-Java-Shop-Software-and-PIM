define(['durandal/app', 'knockout', 'gc/gc', 'plugins/router', 'gc-coupon-promotion'], function (app, ko, gc, router, couponPromotionsAPI) {

	function CouponPromotionGridIndexController(options) {

		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof CouponPromotionGridIndexController)) {
			throw new TypeError("CouponPromotionGridIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.pager = {};

		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'removeCouponPromotion', 'cloneCouponPromotion');
	}

	CouponPromotionGridIndexController.prototype = {


		constructor: CouponPromotionGridIndexController,
		removeCouponPromotion: function(couponPromotion) {
			var self = this;
			var yes = gc.app.i18n('app:common.yes');
			var no = gc.app.i18n('app:common.no');

			app.showMessage(gc.app.i18n('app:modules.coupon-promotion.confirmDelete'), gc.ctxobj.val(couponPromotion.label, gc.app.currentLang()), [yes, no]).then(function(confirm) {
				if(confirm == yes) {
					couponPromotionsAPI.removeCouponPromotion(couponPromotion.id).then(function() {
						self.pager.removeData(couponPromotion);
					});
				}
			});
		},
		cloneCouponPromotion: function(couponPromotion) {
			couponPromotionsAPI.cloneCouponPromotion(couponPromotion.id).then(function(data) {
				router.navigate('//coupon-promotions/details/' + data.id);
			});
		},
		activate: function(data) {
            gc.app.pageTitle(gc.app.i18n('app:modules.coupon-promotion.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.coupon-promotion.subtitle'));


            var pagerColumns = [
                {'name' : '#'},
                {'name' : 'label', 'label' : 'app:modules.coupon-promotion.gridColName', cookieKey : 'n'},
                {'name' : 'enabled', 'label' : 'app:modules.coupon-promotion.gridColEnabled', cookieKey : 'e'},
                {'name' : ''}
            ];

            // Init the pager.
            this.pager = new gc.Pager(couponPromotionsAPI.getPagingOptions({columns : pagerColumns}));

			return this.pager.load();
		}
	}

    return CouponPromotionGridIndexController;
});