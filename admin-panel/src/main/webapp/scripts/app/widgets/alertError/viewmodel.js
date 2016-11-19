define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc' ], function(app, composition, ko, i18n, gc) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;
		
		this.options = options || {};
		
		// Title of alerts box. 
		this.title = options.title;

		// Title-args of alerts box. 
		this.titleArgs = options.titleArgs || {};

		// Body of alerts box. 
		this.body = options.body;

		// Body-args of alerts box. 
		this.bodyArgs = options.bodyArgs || {};
	};

	return ctor;
});