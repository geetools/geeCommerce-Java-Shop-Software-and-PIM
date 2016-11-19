define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-customer' ], function(app, ko, gc, customerAPI) {

	function CustomerVM(customerId) {
		var self = this;
		self.id = ko.observable(customerId);
		self.id2 = ko.observable();
		self.forename = ko.observable();
		self.surname = ko.observable();
		self.email = ko.observableArray();
		self.createdAt = ko.observableArray();
		self.customerGroups = ko.observableArray();
	}

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function CustomerController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof CustomerController)) {
			throw new TypeError("CustomerController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.customerVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

	CustomerController.prototype = {
		constructor : CustomerController,
		activate : function(customerId) {
			var self = this;
			
			self.customerVM = new CustomerVM(customerId);
			
			return customerAPI.getCustomer(customerId).then(function(customer) {
				self.customerVM.id2(customer.id2);
				self.customerVM.forename(customer.forename);
				self.customerVM.surname(customer.surname);
				self.customerVM.email(customer.email);
				self.customerVM.createdAt(customer.createdAt);
			});
		},
		saveData : function() {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();
			updateModel.field('forename', self.customerVM.forename());
			updateModel.field('surname', self.customerVM.surname());
			updateModel.field('email', self.customerVM.email());

			customerAPI.updateCustomer(self.customerVM.id(), updateModel);
		},
		attached : function() {
			var self = this;
			
			// For the first tab, we register the save event immediately and not
			// just on click.
			gc.app.onToolbarEvent({
				save : self.saveData
			});

			// Subscribe again if someone leaves the tab and comes back again.
			$(document).on('click', '#tab-cust-details-general', function() {
				// Callback for save/cancel toolbar.
				gc.app.onToolbarEvent({
					save : self.saveData
				});
			});
		}
	};
	
	return CustomerController;
});