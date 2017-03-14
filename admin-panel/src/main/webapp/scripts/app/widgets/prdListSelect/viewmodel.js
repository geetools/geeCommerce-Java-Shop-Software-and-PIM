define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-product-list' ], function(app, composition, ko, i18n, gc, productListAPI) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		self.selectOptions = options.selectOptions;
		
		self.value = options.value;
		
        self.valueKey = options.valueKey || 'id';
        
        self.labelKey = options.labelKey || 'label';
		
        self.ctxMode = options.ctxMode || 'closest';

        self.apiOptions = options.apiOptions;
		
        self.options = [];

        return productListAPI.getProductLists().then(function (data) {
            var productLists = data.data.productLists;

            _.forEach(productLists, function(productList) {
                self.options.push({
                    id : productList[self.valueKey],
                    text : gc.ctxobj.val(productList[self.labelKey], gc.app.currentUserLang(), self.ctxMode) || ""
                });
            });

            console.log(self.options)
        })

	};
	
    ctor.prototype.attached = function(view) {
        var self = this;
    };

	return ctor;
});