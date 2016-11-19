define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product' ], function(app, ko, gc, productAPI) {

	function ProductImageVM() {

		if (!(this instanceof ProductImageVM)) {
			throw new TypeError("ProductImageVM constructor cannot be called as a function.");
		}

		var self = this;
	}

	return {
		_subs : [],
		app : gc.app,
		productImageVM : {},
		activate : function(productId) {
			var self = this;

//			return productAPI.getProduct(productId).then(function(data) {
//				self.productImageVM = new ProductImageVM();
//				
//			}).then(function(data) {
//				var sub = gc.app.channel.subscribe('save.event', function() {
//					console.log('!!! USER CLICKED SAVE !!!');
//					console.log(ko.toJSON(self.productImageVM));
//				});
//
//				self._subs.push(sub);
//			});
		},
		deactivate : function() {
			var self = this;
			gc.utils.unsubscribe(self._subs);
		}
	}
});