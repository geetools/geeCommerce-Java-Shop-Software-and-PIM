define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-user' ], function(app, ko, gc, userAPI) {

	return {
		app : gc.app,
		userId : ko.observable(),
		pageTitle : function() {
			var self = this;
			var title = 'Kundendetails';
			var userId = ko.unwrap(self.userId);
			
			if(!_.isUndefined(userId)) {
				title += ': ' + userId;
			}
			
			return title;
		},
		pageDescription : 'Kundendetails ansehen und bearbeiten',
		activate : function(data) {
			var self = this;
			self.userId(data);
			
			gc.app.pageTitle(self.pageTitle());
			gc.app.pageDescription(self.pageDescription);
		},
		attached : function() {
		},
		compositionComplete : function() {
		},
		detached : function() {
		},
		deactivate : function() {
		}
	}
});