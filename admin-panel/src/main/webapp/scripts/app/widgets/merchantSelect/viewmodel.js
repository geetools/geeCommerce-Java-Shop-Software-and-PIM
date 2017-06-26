define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-merchant' ], function(app, composition, ko, i18n, gc, merchantAPI) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		self.selectOptions = options.selectOptions;
		
		self.value = options.value;
		
        self.valueKey = options.valueKey || 'id';
        
        self.labelKey = options.labelKey || 'label';
		
        self.mode = options.ctxMode || 'any';

        self.apiOptions = options.apiOptions;
		
        self.options = [];
		
        function label(merchant) {
            return merchant.companyName + " (" + merchant.companyWebsite + ")"
        }
        
        return merchantAPI.getMerchants().then(function (data) {
            var merchants = data.data.merchants;

            self.options.push({
                id : "",
                text : " "
            });

            _.forEach(merchants, function(merchant) {
                self.options.push({
                    id: merchant[self.valueKey],
                    text: label(merchant)
                });
            });
        })

	};
	
    ctor.prototype.attached = function(view) {
        var self = this;
    };

	return ctor;
});