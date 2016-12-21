define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc' ], function(app, composition, ko, i18n, gc) {
	var ctor = function() {
		this.gc = gc;
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		if(options.product){
			self.product = options.product;
		}

		console.log("PRD")
		console.log(self.product)
	};

	return ctor;
});