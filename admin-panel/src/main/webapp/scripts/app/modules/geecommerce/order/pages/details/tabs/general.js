define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-order' ], function(app, ko, gc, orderAPI) {

	/*function OrderVM(orderId) {
		var self = this;
		self.id = ko.observable(orderId);
		self.customerId = ko.observable();
		self.totalAmount = ko.observable();
		self.createdAt = ko.observable();
	}*/

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function OrderController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof OrderController)) {
			throw new TypeError("OrderController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.orderVM = ko.observable({});
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

	OrderController.prototype = {
		constructor : OrderController,
		saveData : function() {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();
//			updateModel.field('forename', self.customerVM.forename());
//			updateModel.field('surname', self.customerVM.surname());
//			updateModel.field('email', self.customerVM.email());

//			customerAPI.updateCustomer(self.customerVM.id(), updateModel);
		},
		activate : function(orderId) {
			var self = this;
			
			//self.orderVM = //new OrderVM(orderId);
			
			return orderAPI.getOrder(orderId).then(function(order) {
			//	self.orderVM.customerId(order.customerId);
			//	self.orderVM.totalAmount(order.totalAmount);
			//	self.orderVM.createdAt(order.createdAt);
                self.orderVM(order);
                console.log(self.orderVM());
			});
		},
		attached : function() {
			var self = this;
			
			// For the first tab, we register the save event immediately and not
			// just on click.
			gc.app.onToolbarEvent({
				save : self.saveData
			});

			// Subscribe again if someone leaves the tab and comes back again.
			$(document).on('click', '#tab-order-details-general', function() {
				// Callback for save/cancel toolbar.
				gc.app.onToolbarEvent({
					save : self.saveData
				});
			});
		}
	};
	
	return OrderController;
});