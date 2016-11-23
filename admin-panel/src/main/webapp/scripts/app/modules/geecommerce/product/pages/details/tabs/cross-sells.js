define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product' ], function( app, ko, gc, productAPI ) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductCrosssellsController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductCrosssellsController)) {
			throw new TypeError("ProductCrosssellsController constructor cannot be called as a function.");
		}

		this.gc = gc;
		this.app = gc.app;
		this.productVM = {};
		this.query = ko.observable().extend({ rateLimit: 1000 });
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'setupSearchListener', 'dropFromSource', 'removeCrossSell', 'updatePositions', 'activate');
	}

	ProductCrosssellsController.prototype = {
		constructor : ProductCrosssellsController,
        // The pager takes care of filtering, sorting and paging functionality.
        sourceCrossSellProductsPager: {},
        dropFromSource : function(data) {
        	var self = this;
        	var vm = self.productVM();
        	
        	// Only add product to cross-sells if it does not exist yet.
    		var foundProduct = _.findWhere(ko.unwrap(vm.crossSells), { id : data.id });
    		
    		if(_.isUndefined(foundProduct)) {
            	productAPI.addCrossSell(vm.productId(), data.id).then(function( response ) {
                    vm.crossSellProducts.push(data);
                	self.sourceCrossSellProductsPager.removeData(data);
            	});
    		}
        },
        removeCrossSell : function(data) {
        	var self = this;
        	var vm = self.productVM();
        	
        	productAPI.removeCrossSell(vm.productId(), data.id).then(function() {
        		// See if the product is already in the source container.
        		var foundProduct = _.findWhere(ko.unwrap(self.sourceCrossSellProductsPager.data), { id : data.id });
        		
        		// Only add to drag&drop source container if it does not exist yet.
        		if(_.isUndefined(foundProduct)) {
                	self.sourceCrossSellProductsPager.data.push(data);
        		}
        		
        		// Remove from target-container in view.
        		vm.crossSellProducts.remove(data);
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
              {'name' : '$attr.article_number', 'label' : 'Artikelnummer'},
              {'name' : '$attr.name', 'label' : 'Name'}
            ];
			
			var pagingOptions = productAPI.pagingOptions( { columns : pagerColumns, filter : [], attributes : [] } )
			pagingOptions.fields.push('attributeOptions');
			pagingOptions.fields.push('properties');
			pagingOptions.fields.push('sortOrder');
			pagingOptions.fields.push('sortOrder');
			pagingOptions.fields.push('mainImageURI');
			
	    	// Init the pager.
        	this.sourceCrossSellProductsPager = new gc.Pager(pagingOptions);
        	
			//---------------------------------------------------------------
			// Cross-sell for drag&drop target-container
			//---------------------------------------------------------------
			
        	//  Get cross-sell-products that are already connected to the main product.
			productAPI.getCrossSellProducts(vm.productId()).then(function(data) {
				var products = data.data.products;

				if(!_.isEmpty(products)) {
					// Append the attribute meta-data as we only have the attributeId at this point.
					gc.attributes.appendAttributes(products);
				
					// Populate drag&drop target container.
					vm.crossSellProducts(products);
				}
			});
		},
		setupSearchListener : function() {
			var self = this;
			
			self.query.subscribe(function(value) {
					console.log('SEARCHING FOR: ', value);
			
	        	self.sourceCrossSellProductsPager.columnValue('$attr.article_number', undefined);
	        	self.sourceCrossSellProductsPager.columnValue('$attr.name', undefined);
				
	        	self.sourceCrossSellProductsPager.columnValue('$attr.article_number', value);
				self.sourceCrossSellProductsPager.load().then(function(data) {
					console.log(data)
					if(_.isEmpty(data.data)) {
			        	self.sourceCrossSellProductsPager.columnValue('$attr.article_number', undefined);
			        	self.sourceCrossSellProductsPager.columnValue('$attr.name', value);
			        	self.sourceCrossSellProductsPager.load().then(function(data2) {
							console.log(data2)
			        	});
					}
				});
			});
		},
		attached : function(view, parent) {
			$('#tab-prd-details-cross-sells').click(function() {
				$('#header-store-pills').hide();
			});
		}
	};

	return ProductCrosssellsController;
});