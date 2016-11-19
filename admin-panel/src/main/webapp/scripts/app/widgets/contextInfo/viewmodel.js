define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc' ], function(app, composition, ko, i18n, gc) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		self.scopeLabel = ko.observable();

        self.scopeName = ko.observable();
		
		app.on('context:change').then(function(activeContext){
		    var ctxMap = gc.app.confGet('contextMap');
            self.scopeLabel(activeContext.scopeLabel);
            self.scopeName(activeContext.name);
		});
	};

	return ctor;
});