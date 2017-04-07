define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product' ], function(app, ko, gc, productAPI) {

	return {
		app : gc.app,
		orderId : ko.observable(),
		pageTitle : function() {
			var self = this;
			var title = 'Bestellung';
			var orderId = ko.unwrap(self.orderId);
			
			if(!_.isUndefined(orderId)) {
				title += ': #' + orderId;
			}
			
			return title;
		},
		pageDescription : 'Bestellung ansehen und bearbeiten',
		activate : function(data) {
			var self = this;
			self.orderId(data);
			
			gc.app.pageTitle(self.pageTitle());
			gc.app.pageDescription(self.pageDescription);
		}
	}
});