define(['durandal/app', 'knockout', 'gc/gc', 'gc-coupon'], function (app, ko, gc, couponAPI) {


	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function CouponGridIndexController(options) {

		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof CouponGridIndexController)) {
			throw new TypeError("CouponGridIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.pager = {};

		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'removeCoupon', 'statusEnabled', 'statusEnabledTitle');
	}

	CouponGridIndexController.prototype = {
		constructor : CouponGridIndexController,
		removeCoupon: function(coupon) {
			var self = this;
			var yes = gc.app.i18n('app:common.yes');
			var no = gc.app.i18n('app:common.no');

			app.showMessage(gc.app.i18n('app:modules.coupon.confirmDelete'), gc.ctxobj.val(coupon.name, gc.app.currentLang()), [yes, no]).then(function(confirm) {
				if(confirm == yes) {
					couponAPI.removeCoupon(coupon.id).then(function() {
						self.pager.removeData(coupon);
					});
				}
			});
		},
		activate: function(data) {
			gc.app.pageTitle('Gutscheine Verwalten');
			gc.app.pageDescription('Gutscheine ansehen und bearbeiten');

			// Pager columns
			var pagerColumns = [
				{'name' : 'id', 'label' : 'app:modules.coupon.gridColId', cookieKey : 'id'},
				{'name' : 'name', 'label' : 'app:modules.coupon.gridColName', cookieKey : 'n'},
				{'name' : 'createdOn', 'label' : 'app:modules.coupon.gridColCreatedAt', cookieKey : 'co'},
				{'name' : 'enabled', 'label' : 'app:modules.coupon.gridColEnabled', cookieKey : 'e'},
				{'name' : 'deleted', 'label' : 'app:modules.product.gridColDeleted', cookieKey : 'd', 'selectOptions' :
					[
						{ label: gc.app.i18n('app:common.no'), value: false },
						{ label: gc.app.i18n('app:common.yes'), value: true },
						{ label: gc.app.i18n('app:common.all'), value: '' },
					]
				},
				{'name' : '', 'label' : ''}
			];

			// Init the pager.
			this.pager = new gc.Pager(couponAPI.pagingOptions({columns : pagerColumns}));

			// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
			return this.pager.load();
		},
		statusEnabledTitle : function() {
			var availableStores = gc.app.confGet('availableStores');
    		if (availableStores.length > 2) { 
    			return gc.app.i18n('app:modules.coupon.gridColEnabledMulti');
			} else {
				return gc.app.i18n('app:modules.coupon.gridColEnabled');
			}
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

						console.log("VALUE",value);
						if(!_.isUndefined(value)) {
							console.log("CTXOBJ",gc.ctxobj);
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
					statusDescTxt = '<span class="gridStoreStatusImg product-status-cross fa fa-times"></span>';
				}

			}

			return statusDescTxt;
		},
		compositionComplete : function() {
			var self = this;
			self.pager.activateSubscribers();
		}
	}

	return CouponGridIndexController;
});