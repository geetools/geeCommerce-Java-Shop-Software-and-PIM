define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product' ], function(app, ko, gc, productAPI) {

	function ProductVM(productId) {
		var self = this;
		self.id = ko.observable(productId);
		self.articleNumber = ko.observable();
	}
	
	function InventoryItemVM(productId, inventoryItemId) {
		var self = this;
		self.id = ko.observable(inventoryItemId);
		self.productId = ko.observable(productId);
		self.qty = ko.observable();
	}

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductStockController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductStockController)) {
			throw new TypeError("ProductStockController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.productVM = {};
		this.variants = ko.observableArray();
		this.inventoryItems = ko.observableArray();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'inventoryItemsFor', 'activate', 'attached');
	}

	ProductStockController.prototype = {
		constructor : ProductStockController,
		
		// Returns inventory-items for a particular product or variant-product.
		inventoryItemsFor : function(productId) {
			var _productId = ko.unwrap(productId);
			var _filteredInventoryItems = [];
			_.each(this.inventoryItems(), function(inventoryItem) {
				var ii = ko.unwrap(inventoryItem);
				if (ii.productId() == _productId) {
					_filteredInventoryItems.push(inventoryItem);
				}
			});
			return _filteredInventoryItems;
		},
		
		activate : function(productId) {
			this.productVM = new ProductVM(productId);
			this.inventoryItems = ko.observableArray();
		},
		attached : function() {
			var self = this;
			
			$(document).on('click', '#tab-prd-details-stock', function() {

				$('#header-store-pills').hide();

				// Reset array before pushing new results into it.
				self.inventoryItems([]);				
				
				// Callback for save/cancel toolbar.
				gc.app.onToolbarEvent({
					save : function() {
						var updateModel = gc.app.newUpdateModel();
						productAPI.updateStockInventoryItems(self.productVM.id(), ko.gc.unwrap(self.inventoryItems));
					}
				});

				return productAPI.getProduct(self.productVM.id()).then(function(data) {
					self.productVM.articleNumber(gc.attributes.find(data.attributes, 'article_number').value);
				}).then(function(data) {
					productAPI.getVariants(self.productVM.id()).then(function(data) {
						self.variants(data.data.products);
					});

					productAPI.getStockInventoryItems(self.productVM.id()).then(function(data) {
						_.each(data.data.inventoryItems, function(inventoryItem) {

							var inventoryItemVM = new InventoryItemVM(inventoryItem.productId, inventoryItem.id);
							inventoryItemVM.qty(inventoryItem.qty);
							
							self.inventoryItems.push(inventoryItemVM);
						});
					});
				});				
			});
		},
		detached : function() {
			$(document).off('click', '#tab-prd-details-stock');
		}
	}
	
	return ProductStockController;
});