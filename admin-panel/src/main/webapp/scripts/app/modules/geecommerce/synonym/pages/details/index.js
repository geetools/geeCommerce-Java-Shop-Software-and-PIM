define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-synonym' ], function(app, ko, gc, synonymAPI) {

	return {
		app : gc.app,
		synonymId : ko.observable(),
		pageTitle : function() {
			var self = this;
			var title = 'app:modules.synonym.detailsTitle';

			return title;
		},
		pageDescription : 'app:modules.synonym.detailsSubtitle',
		activate : function(data) {
			var self = this;
			self.synonymId(data);
			
			gc.app.pageTitle(self.pageTitle());
			gc.app.pageDescription(self.pageDescription);
		}
	}
});