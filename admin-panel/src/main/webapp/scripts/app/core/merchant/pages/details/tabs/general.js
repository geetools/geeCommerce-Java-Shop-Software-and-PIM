define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-merchant' ], function(app, ko, gc, merchantAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function MerchantTabGeneralController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof MerchantTabGeneralController)) {
			throw new TypeError("MerchantTabGeneralController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.merchantVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate');
	}

    MerchantTabGeneralController.prototype = {
		constructor : MerchantTabGeneralController,
		saveData : function(view, parent, toolbar) {
			var self = this;

			//----------------------------------------------------
			// Save new url-rewrite.
			//----------------------------------------------------
			if(self.merchantVM.id() == 'new') {
				
				var newMerchant  = {};
				newMerchant.code = self.merchantVM.code();
				newMerchant.companyName = self.merchantVM.companyName();
				newMerchant.companyAddressLine1 = self.merchantVM.companyAddressLine1();
				newMerchant.companyAddressLine2 = self.merchantVM.companyAddressLine2();
				newMerchant.companyCity = self.merchantVM.companyCity();
				newMerchant.companyState = self.merchantVM.companyState();
                newMerchant.companyCountry = self.merchantVM.companyCountry();
                newMerchant.companyPhone = self.merchantVM.companyPhone();
                newMerchant.companyFax = self.merchantVM.companyFax();
                newMerchant.companyZipCode = self.merchantVM.companyZipCode();
                newMerchant.companyWebsite = self.merchantVM.companyWebsite();

                merchantAPI.createMerchant(newMerchant).then(function(data) {
					toolbar.hide();
                    router.navigate('//merchants/details/' + data.id);
				});

			} else {
				var updateModel = gc.app.newUpdateModel();

				updateModel.field('code', self.merchantVM.code());
				updateModel.field('companyName', self.merchantVM.companyName());
				updateModel.field('companyAddressLine1', self.merchantVM.companyAddressLine1());
				updateModel.field('companyAddressLine2', self.merchantVM.companyAddressLine2());
				updateModel.field('companyCity', self.merchantVM.companyCity());
				updateModel.field('companyState', self.merchantVM.companyState());
				updateModel.field('companyCountry', self.merchantVM.companyCountry());
				updateModel.field('companyPhone', self.merchantVM.companyPhone());
                updateModel.field('companyFax', self.merchantVM.companyFax());
                updateModel.field('companyZipCode', self.merchantVM.companyZipCode());
                updateModel.field('companyWebsite', self.merchantVM.companyWebsite());
				
				merchantAPI.updateMerchant(self.merchantVM.id(), updateModel).then(function(data) {
					toolbar.hide();
				});
			}
		},
		activate : function() {
			var self = this;
			
			self.merchantVM = gc.app.sessionGet('merchantVM');
		}
	};

	return MerchantTabGeneralController;
});