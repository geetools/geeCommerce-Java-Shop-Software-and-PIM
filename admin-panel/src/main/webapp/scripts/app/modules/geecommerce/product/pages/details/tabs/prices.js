define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product', 'gc-price', 'gc-customer', 'numeral' ], function(app, ko, gc, productAPI, priceAPI, customerAPI, numeral) {

	function ProductVM(productId) {
		var self = this;
		self.id = ko.observable(productId);
		self.articleNumber = ko.observable();
		self.articleNumberLabel = ko.computed(function() {
			var artNo = self.articleNumber();
			return _.isUndefined(artNo) || _.isEmpty(artNo) ? self.id() : gc.ctxobj.plain(artNo);
		});
	}
	
	function PriceVM(productId, priceId) {
		var self = this;
		self.id = ko.observable(priceId);
		self.productId = ko.observable(productId);
		self.typeId = ko.observable();
		self.country = ko.observable();
		self.currency = ko.observable();
		self.customerId = ko.observable();
		self.customerGroupId = ko.observable();
		self.price = ko.observable();
		self.qtyFrom = ko.observable();
		self.qtyTo = ko.observable();
		self.validFrom = ko.observable();
		self.validTo = ko.observable();
		
		// Query builder for price rule-set.
		self.withProductIds = ko.observableArray([]);
		self.withProducts = ko.observableArray([]);
		self.showProductfinderModal = ko.observable(false);

        self.withProductIds.subscribe(function(newValue) {
            console.log('!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! @@whenProductIdsInCart ', newValue);
        });
	}

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductPriceController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductPriceController)) {
			throw new TypeError("ProductPriceController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.id = ko.observable();
		this.productVM = {};
		this.prices = ko.observableArray();
		this.newPrices = ko.observableArray();
		this.variants = ko.observableArray();
		this.customerGroups = ko.observableArray();
		this.priceTypes = ko.observableArray();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'pricesFor', 'customerGroup', 'priceType', 'addPrice', 'saveNewPrice', 'cancelNewPrice', 'removePrice', 'activate', 'attached');
	}

	ProductPriceController.prototype = {
		constructor : ProductPriceController,
		
		// Returns prices for a particular product or variant-product.
		pricesFor : function(productId) {
			var _productId = ko.unwrap(productId);
			var _filteredPrices = [];
			_.each(this.prices(), function(price) {
				var p = ko.unwrap(price);
				if (p.productId() == _productId) {
					_filteredPrices.push(price);
				}
			});
			return _filteredPrices;
		},
		customerGroup : function(customerGroupId) {
			var cg = _.findWhere(ko.unwrap(this.customerGroups), {id: ko.unwrap(customerGroupId)});
			return _.isUndefined(cg) ? {id: 0, label: 'Alle'} : cg;
		},
		priceType : function(typeId) {
			var pt = _.findWhere(ko.unwrap(this.priceTypes), {id: ko.unwrap(typeId)});
			return _.isUndefined(pt) ? {id: 0, label: 'Alle'} : pt;
		},
		//----------------------------------------------------
		// Adds an empty price object to the new-prices array.
		//----------------------------------------------------
		addPrice : function(data) {
			this.newPrices.push(new PriceVM(ko.unwrap(data.id)));
		},
		//----------------------------------------------------
		// Saves new price.
		//----------------------------------------------------
		saveNewPrice : function(price) {
			var self = this;
			var _price = ko.gc.unwrap(price);
			
            // Convert nicely formatted price back to raw number.
			_price.price = numeral(_price.price).value();
			
			if(!_.isUndefined(price.validFrom())) {
				_price.validFrom = gc.utils.toServerTime(gc.utils.startOfTheDay(price.validFrom()));
			}
				
			if(!_.isUndefined(price.validTo())) {
				_price.validTo = gc.utils.toServerTime(gc.utils.startOfTheDay(price.validTo()));	
			}
			
            delete _price.withProducts;
			
			productAPI.saveNewPrice(_price).then(function(newId) {
				// Move from new list to normal update list.
				self.newPrices.remove(price);
				price.id(newId);
				self.prices.push(price);
				
				_price.$gc.resetLoader();
			});
		},
		//----------------------------------------------------
		// Cancels adding new price.
		//----------------------------------------------------
		cancelNewPrice : function(price) {
			this.newPrices.remove(price);
		},
		//----------------------------------------------------
		// Removes price for a product or variant-product.
		//----------------------------------------------------
		removePrice : function(price) {
			var self = this;
			var _price = ko.gc.unwrap(price);
			
			// Remove from DB
			productAPI.removePrice(_price.productId, _price.id).then(function(data) {
				// Remove from view.
				self.prices.remove(price);
				
				_price.$gc.resetLoader();
			});
		},
		//----------------------------------------------------
		// Updates price for a product or variant-product.
		//----------------------------------------------------
		updatePrice : function(price) {
			var self = this;
			var _price = ko.gc.unwrap(price);
			
		    // Convert nicely formatted price back to raw number.
            _price.price = numeral(_price.price).value();

            delete _price.withProducts;
            
			productAPI.updatePrice(_price).then(function(data) {
				_price.$gc.resetLoader();
			});
		},
		activate : function(id) {
			var self = this;
			
			this.id(id);
			this.productVM = new ProductVM(id);
			this.prices = ko.observableArray();
			
			// Load customer groups for select-options.
			customerAPI.getCustomerGroups().then(function(data) {
				var selectMap = gc.ctxobj.reduce(data.data.customerGroups, 'id', 'label', gc.app.currentUserLang());
				selectMap.unshift({id:0, label:'Alle'});
				self.customerGroups(selectMap);
			});

			// Load price types for select-options.
			priceAPI.getPriceTypes().then(function(data) {
				var selectMap = gc.ctxobj.reduce(data.data.priceTypes, 'id', 'label', gc.app.currentUserLang());
				self.priceTypes(selectMap);
			});
		},
		attached : function() {
			var self = this;
			
			$(document).on('click', '#tab-prd-details-prices', function() {
				// Reset array before pushing new results into it.
				self.prices([]);
				
				return productAPI.getProduct(self.productVM.id()).then(function(data) {
					self.productVM.articleNumber(gc.attributes.find(data.attributes, 'article_number').value);
				}).then(function(data) {
					productAPI.getVariants(self.productVM.id()).then(function(data) {
						self.variants(data.data.products);
					});

					productAPI.getPrices(self.productVM.id()).then(function(data) {
						_.each(data.data.prices, function(price) {

							var priceVM = new PriceVM(price.productId, price.id);
							priceVM.typeId(price.typeId);
							priceVM.country(price.country);
							priceVM.currency(price.currency);
							priceVM.customerId(price.customerId);
							priceVM.customerGroupId(price.customerGroupId);
							priceVM.price(price.price);
							priceVM.qtyFrom(price.qtyFrom);
							priceVM.qtyTo(price.qtyTo);
							priceVM.validFrom(gc.utils.fromServerTime(price.validFrom));
							priceVM.validTo(gc.utils.fromServerTime(price.validTo));
							priceVM.withProductIds(price.withProductIds);
							
							self.prices.push(priceVM);
						});
					});
				});
			});
		},
		detached : function() {
			$(document).off('click', '#tab-prd-details-prices');
		}
	}
	
	return ProductPriceController;
});