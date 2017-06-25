define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-user' ], function(app, ko, gc, userAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function UserProductsController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof UserProductsController)) {
			throw new TypeError("UserProductsController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.userId = undefined;
		this.userVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'attached');
	}

    UserProductsController.prototype = {
		constructor : UserProductsController,
		activate : function(userId) {
			var self = this;
			
			self.userVM = gc.app.sessionGet('userVM');
			self.userId = userId;

		},
		attached : function() {
			var self = this;

			
			// For the first tab, we register the save event immediately and not
			// just on click.
			gc.app.onToolbarEvent({
				save : self.saveData
			});
		}
	};

	return UserProductsController;
});