define([ 'knockout', 'gc/gc' ], function(ko, gc) {

	return {
		isInRole : function(role) {
			var self = this;
			var roles = self.roles();
			
			return roles && roles.indexOf(role) != -1;
		},
		roleHasPermission : function(role) {
			var self = this;
			var roles = self.roles();
			
			return roles && (roles.indexOf(role) != -1 || roles.indexOf('admin') != -1);
		},
		roles : function() {
			var self = this;
		
			var session = gc.app.currentSession();
			return session ? session.roles || [] : [];
		}
	};
});