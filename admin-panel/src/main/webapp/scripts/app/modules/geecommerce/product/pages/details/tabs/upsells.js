define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product' ], function( app, ko, gc, productAPI ) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductUpsellsController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductUpsellsController)) {
			throw new TypeError("ProductUpsellsController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.productVM = {};
		this.query = ko.observable().extend({ rateLimit: 1000 });
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'setupSearchListener', 'dropFromSource', 'removeUpsell', 'updatePositions', 'activate');
	}

	ProductUpsellsController.prototype = {
		constructor : ProductUpsellsController,
        // The pager takes care of filtering, sorting and paging functionality.
        sourceUpsellProductsPager: {},
        dropFromSource : function(data) {
        	var self = this;
        	var vm = self.productVM();
        	
        	// Only add product to upsells if it does not exist yet.
    		var foundProduct = _.findWhere(ko.unwrap(vm.upsells), { id : data.id });
    		
    		if(_.isUndefined(foundProduct)) {
            	productAPI.addUpsell(vm.productId(), data.id).then(function( response ) {
                    vm.upsellProducts.push(data);
                	self.sourceUpsellProductsPager.removeData(data);
            	});
    		}
        },
        removeUpsell : function(data) {
        	var self = this;
        	var vm = self.productVM();
        	
        	productAPI.removeUpsell(vm.productId(), data.id).then(function() {
        		// See if the product is already in the source container.
        		var foundProduct = _.findWhere(ko.unwrap(self.sourceUpsellProductsPager.data), { id : data.id });
        		
        		// Only add to drag&drop source container if it does not exist yet.
        		if(_.isUndefined(foundProduct)) {
                	self.sourceUpsellProductsPager.data.push(data);
        		}
        		
        		// Remove from target-container in view.
        		vm.upsellProducts.remove(data);
        	});
        },
		updatePositions : function(domTableRows) {
			var self = this;
			
			var optionPositions = {};

            domTableRows.each(function(index, elem) {
                var row = $(elem),
                    pos = row.index()+1,
                    optionId = $(row).attr('data-id');

                optionPositions[optionId] = pos;
            });

//            attrTabsAPI.updateOptionPositions(self.attributeTabId, optionPositions).then(function(data) {
////                self.pager.refresh();
//            });
		},
		activate : function(productId) {
			var self = this;

			self.productVM = gc.app.sessionKGet('productVM');			
			
			var vm = self.productVM();

			
			self.setupSearchListener();
			
			
	    	// Pager columns
			var pagerColumns = [
              {'name' : '!attr.article_number', 'label' : 'Artikelnummer'},
              {'name' : '!attr.name', 'label' : 'Name'}
            ];
			
			var pagingOptions = productAPI.pagingOptions( { columns : pagerColumns, filter : [], attributes : [] } )
			pagingOptions.fields.push('attributeOptions');
			pagingOptions.fields.push('properties');
			pagingOptions.fields.push('sortOrder');
			
	    	// Init the pager.
        	this.sourceUpsellProductsPager = new gc.Pager(pagingOptions);
        	
			//---------------------------------------------------------------
			// Upsell for drag&drop target-container
			//---------------------------------------------------------------
			
        	//  Get upsell-products that are already connected to the main product.
			productAPI.getUpsellProducts(vm.productId()).then(function(data) {
				var products = data.data.products;

				if(!_.isEmpty(products)) {
					// Append the attribute meta-data as we only have the attributeId at this point.
					gc.attributes.appendAttributes(products);
				
					// Populate drag&drop target container.
					vm.upsellProducts(products);
				}
			});
		},
		setupSearchListener : function() {
			var self = this;
			
			self.query.subscribe(function(value) {
					console.log('SEARCHING FOR: ', value);
			
	        	self.sourceUpsellProductsPager.columnValue('!attr.article_number', undefined);
	        	self.sourceUpsellProductsPager.columnValue('!attr.name', undefined);
				
	        	self.sourceUpsellProductsPager.columnValue('!attr.article_number', value);
				self.sourceUpsellProductsPager.load().then(function(data) {
					if(_.isEmpty(data.data)) {
			        	self.sourceUpsellProductsPager.columnValue('!attr.article_number', undefined);
			        	self.sourceUpsellProductsPager.columnValue('!attr.name', value);
			        	self.sourceUpsellProductsPager.load().then(function(data2) {
			        	});
					}
				});
			});
		},
		attached : function(view, parent) {
			$('#tab-prd-details-upsells').click(function() {
				$('#header-store-pills').hide();
			});
		}
	};

	return ProductUpsellsController;
});