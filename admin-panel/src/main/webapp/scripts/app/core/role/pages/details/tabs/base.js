define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-role' ], function(app, ko, router, gc, roleAPI) {


	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function RoleController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof RoleController)) {
			throw new TypeError("RoleController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.roleVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

    RoleController.prototype = {
		constructor : RoleController,
		activate : function(roleId) {
			var self = this;

            self.roleVM = gc.app.sessionGet('roleVM');
		},
		saveData : function(view, parent, toolbar) {
			var self = this;

			if(self.roleVM.id() == 'new'){
                var updateModel = gc.app.newUpdateModel();
                updateModel.field('code', self.roleVM.code());
                updateModel.field('name', self.roleVM.name(), true);

                roleAPI.createRole(updateModel).then(function (data) {
                    toolbar.hide();
                    router.navigate('//roles/details/' + data.id);
                });
			} else {
                var updateModel = gc.app.newUpdateModel();
                updateModel.field('code', self.roleVM.code());
                updateModel.field('name', self.roleVM.name(), true);

                roleAPI.updateRole(self.roleVM.id(), updateModel).then(function () {
                    toolbar.hide();
                });
			}

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
	
	return RoleController;
});