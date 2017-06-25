define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-role' ], function(app, ko, gc, roleAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function PermissionMappingController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof PermissionMappingController)) {
			throw new TypeError("PermissionMappingController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.roleId = undefined;
		this.roleVM = {};
		this.query = ko.observable();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'dropFromSource', 'removePermissionFromRole', 'activate', 'attached');
	}

    PermissionMappingController.prototype = {
		constructor : PermissionMappingController,
        // The pager takes care of filtering, sorting and paging functionality.
        sourcePermissionsPager: {},
        dropFromSource : function(data) {
        	var self = this;

        	// Only add attribute to tab if it does not exist yet.
    		var foundPermission = _.findWhere(ko.unwrap(self.roleVM.permissions), { id : data.id });
        	
    		if(_.isUndefined(foundPermission)) {
            	roleAPI.addPermissionToRole(self.roleId, data.id).then(function( response ) {
                	self.roleVM.permissions.push( { id: data.id, name: data.name, code: data.code} );
                	self.sourcePermissionsPager.data.remove(data);
            	});
    		}
        },
        removePermissionFromRole : function(data) {
        	var self = this;

            roleAPI.removePermissionFromRole(self.roleId, data.id).then(function() {
        		// See if the attribute is already in the source container.
        		var foundPermission = _.findWhere(ko.unwrap(self.sourcePermissionsPager.data), { id : data.id });
        		
        		// Only add to drag&drop source container if it does not exist yet.
        		if(_.isUndefined(foundPermission)) {
                	self.sourcePermissionsPager.data.push( { id: data.id, name: data.name, code: data.code} );
        		}
        		
        		// Remove from target-container in view.
            	self.roleVM.permissions.remove(data);
        	});
        },
		activate : function(roleId) {
			var self = this;
			
			self.roleVM = gc.app.sessionGet('roleVM');
			self.roleId = roleId;
			
	    	// Init the pager.
        	this.sourcePermissionsPager = new gc.Pager(roleAPI.getPermissionsPagingOptions(roleId, { fields : [ 'id', 'code','name' ], sort : [ 'code' ] }));

			return self.sourcePermissionsPager.load().then(function(data) {          	});
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

	return PermissionMappingController;
});