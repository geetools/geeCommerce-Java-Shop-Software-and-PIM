define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-user' ], function(app, ko, gc, userAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function UserMappingController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof UserMappingController)) {
			throw new TypeError("UserMappingController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.userId = undefined;
		this.userVM = {};
		this.query = ko.observable();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'dropFromSource', 'removeRoleFromUser', 'activate', 'attached');
	}

    UserMappingController.prototype = {
		constructor : UserMappingController,
        // The pager takes care of filtering, sorting and paging functionality.
        sourceRolesPager: {},
        dropFromSource : function(data) {
        	var self = this;

        	// Only add attribute to tab if it does not exist yet.
    		var foundRole = _.findWhere(ko.unwrap(self.userVM.roles), { id : data.id });
        	
    		if(_.isUndefined(foundRole)) {
            	userAPI.addRoleToUser(self.userId, data.id).then(function( response ) {
                	self.userVM.roles.push( { id: data.id, name: data.name, code: data.code} );
                	self.sourceRolesPager.data.remove(data);
            	});
    		}
        },
        removeRoleFromUser : function(data) {
        	var self = this;

			userAPI.removeRoleFromUser(self.userId, data.id).then(function() {
        		// See if the attribute is already in the source container.
        		var foundRole = _.findWhere(ko.unwrap(self.sourceRolesPager.data), { id : data.id });
        		
        		// Only add to drag&drop source container if it does not exist yet.
        		if(_.isUndefined(foundRole)) {
                	self.sourceRolesPager.data.push( { id: data.id, name: data.name, code: data.code} );
        		}
        		
        		// Remove from target-container in view.
            	self.userVM.roles.remove(data);
        	});
        },
		activate : function(userId) {
			var self = this;
			
			self.userVM = gc.app.sessionGet('userVM');
			self.userId = userId;
			
	    	// Init the pager.
        	this.sourceRolesPager = new gc.Pager(userAPI.getRolesPagingOptions(userId, { fields : [ 'id', 'code','name' ], sort : [ 'code' ] }));

			return self.sourceRolesPager.load().then(function(data) {          	});
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

	return UserMappingController;
});