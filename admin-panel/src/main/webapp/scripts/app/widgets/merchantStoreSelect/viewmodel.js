define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-merchant' ], function(app, composition, ko, i18n, gc, merchantAPI) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		self.selectOptions = options.selectOptions;
		
		self.value = options.value;

		self.merchant = options.merchant;

		self.currentMerchant = options.merchant();

        self.valueKey = options.valueKey || 'id';
        
        self.labelKey = options.labelKey || 'label';
		
        self.mode = options.ctxMode || 'any';

        self.apiOptions = options.apiOptions;
		
        self.options = ko.observableArray([]);

        self.merchant.subscribe(function (value) {
            if(value && value != self.currentMerchant){
                self.value('');
                self.currentMerchant = value;
                self.initStores(value);
            }
        });

        self.initStores = function (merchantId) {

            function label(store) {
                return store.name;
            }

            if(merchantId){
                return  merchantAPI.getMerchant(merchantId).then(function (merchant) {
                    var options = []

                    options.push({
                        id : "",
                        text : " "
                    });

                    _.forEach(merchant.stores, function(store) {
                        options.push({
                            id: store[self.valueKey],
                            text: label(store)
                        });
                    });

                    self.options(options);
                })
            } else {
                self.options([]);
            }

        }

        return self.initStores(self.merchant());
	};
	
    ctor.prototype.attached = function(view) {
        var self = this;
    };

	return ctor;
});