define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc' ], function(app, composition, ko, i18n, gc) {
	var ctor = function() {
		this.gc = gc;
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		if(options.product){
			self.product = options.product;
		}

        if(options.products){
            self.products = options.products;
        }

		console.log("PRD")
		console.log(self.product)
	};


    ctor.prototype.remove = function() {
    	var self = this;
    	console.log("-----REMOVE-----");
    	console.log(self.products());
		self.products.remove(self.product.id)
    }

	return ctor;
});