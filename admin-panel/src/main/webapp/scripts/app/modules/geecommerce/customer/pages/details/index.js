define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product' ], function(app, ko, gc, productAPI) {

	return {
		app : gc.app,
		customerId : ko.observable(),
		pageTitle : function() {
			var self = this;
			var title = 'Kundendetails';
			var customerId = ko.unwrap(self.customerId);
			
			if(!_.isUndefined(customerId)) {
				title += ': ' + customerId;
			}

			return title;
		},
		pageDescription : 'Kundendetails ansehen und bearbeiten',
		activate : function(data) {
			var self = this;
			self.customerId(data);
			
			gc.app.pageTitle(self.pageTitle());
			gc.app.pageDescription(self.pageDescription);
		}
	}
});