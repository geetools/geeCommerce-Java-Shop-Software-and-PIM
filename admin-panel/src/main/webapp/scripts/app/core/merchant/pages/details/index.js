define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-merchant' ], function(app, ko, gc, merchantAPI) {
	
	function MerchantVM(id) {
		var self = this;
        self.id = ko.observable(id);
		self.code = ko.observable();
        self.companyName = ko.observable();
		self.companyAddressLine1 = ko.observable();
		self.companyAddressLine2 = ko.observable();
		self.companyCity = ko.observable();
        self.companyState = ko.observable();
		self.companyCountry = ko.observable();
        self.companyPhone = ko.observable();
		self.companyFax = ko.observable();
		self.companyZipCode = ko.observable();
        self.companyWebsite = ko.observable();

        self.data = null;
		
		self.isNew = ko.computed(function() {
			return self.id() == 'new';
		});

	}	

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function MerchantIndexController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof MerchantIndexController)) {
			throw new TypeError("MerchantIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.merchantVM = {};
        this.merchantId = ko.observable();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

    MerchantIndexController.prototype = {
		constructor : MerchantIndexController,
		pageTitle : function() {
			var self = this;
			var title = 'Merchant';
			
			return title;
		},
		pageDescription : 'Merchants ansehen und bearbeiten',
		saveData : function() {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();
		},
		activate : function(merchantId) {
			var self = this;

			self.merchantVM = new MerchantVM(merchantId);
			self.merchantId(merchantId);
			gc.app.sessionPut('merchantVM', self.merchantVM);
			
			if(merchantId == 'new') {
				gc.app.pageTitle('New Merchant');
				gc.app.pageDescription('Create a new Merchant');
			} else {
				gc.app.pageTitle(self.pageTitle());
				gc.app.pageDescription(self.pageDescription);
				
				return merchantAPI.getMerchant(merchantId).then(function(merchant) {
				
					self.merchantVM.id(merchant.id);
					self.merchantVM.code(merchant.code);
					self.merchantVM.companyName(merchant.companyName);
					self.merchantVM.companyAddressLine1(merchant.companyAddressLine1);
					self.merchantVM.companyAddressLine2(merchant.companyAddressLine2);
					self.merchantVM.companyCity(merchant.companyCity);
					self.merchantVM.companyState(merchant.companyState);
					self.merchantVM.companyCountry(merchant.companyCountry);
					self.merchantVM.companyPhone(merchant.companyPhone);
                    self.merchantVM.companyFax(merchant.companyFax);
                    self.merchantVM.companyZipCode(merchant.companyZipCode);
                    self.merchantVM.companyWebsite(merchant.companyWebsite);

                    self.merchantVM.data = merchant;

					gc.app.pageTitle(self.pageTitle());
				});
			}
		},
		attached : function() {
			var self = this;
		}
	};

	return MerchantIndexController;
});
