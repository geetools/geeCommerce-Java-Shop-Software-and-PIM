define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-user' ], function(app, ko, router, gc, userAPI) {


	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function UserController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof UserController)) {
			throw new TypeError("UserController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.userVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'attached');
	}

    UserController.prototype = {
		constructor : UserController,
		activate : function(userId) {
			var self = this;

            self.userVM = gc.app.sessionGet('userVM');
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
	
	return UserController;
});