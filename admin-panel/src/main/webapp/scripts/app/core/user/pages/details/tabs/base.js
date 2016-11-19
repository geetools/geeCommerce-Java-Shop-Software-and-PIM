define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-user' ], function(app, ko, gc, userAPI) {

	function UserVM(userId) {
		var self = this;
		self.id = ko.observable(userId);
		self.forename = ko.observable();
		self.surname = ko.observable();
		self.email = ko.observableArray();
        self.username = ko.observableArray();
		self.createdAt = ko.observableArray();
	}

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
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

    UserController.prototype = {
		constructor : UserController,
		activate : function(userId) {
			var self = this;
			
			self.userVM = new UserVM(userId);
			
			return userAPI.getUser(userId).then(function(user) {
				self.userVM.forename(user.forename);
				self.userVM.surname(user.surname);
				self.userVM.email(user.email);
                self.userVM.username(user.username);
			});
		},
		saveData : function() {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();
			updateModel.field('forename', self.userVM.forename());
			updateModel.field('surname', self.userVM.surname());
			updateModel.field('email', self.userVM.email());
            updateModel.field('username', self.userVM.username());

			userAPI.updateUser(self.userVM.id(), updateModel);
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