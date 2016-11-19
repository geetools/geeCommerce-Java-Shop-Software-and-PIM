define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-search-rewrite' ], function(app, ko, gc, searchRewriteAPI) {

	return {
		app : gc.app,
		searchRewriteId : ko.observable(),
		pageTitle : function() {
			var self = this;
			var title = 'app:modules.search-rewrite.detailsTitle';

			return title;
		},
		pageDescription : 'app:modules.search-rewrite.detailsSubtitle',
		activate : function(data) {
			var self = this;
			self.searchRewriteId(data);
			
			gc.app.pageTitle(self.pageTitle());
			gc.app.pageDescription(self.pageDescription);
		}
	}
});