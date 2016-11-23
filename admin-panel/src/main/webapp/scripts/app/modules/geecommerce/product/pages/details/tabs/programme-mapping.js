define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product' ], function( app, ko, gc, productAPI ) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductProgrammesController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductProgrammesController)) {
			throw new TypeError("ProductProgrammesController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.productVM = {};
		this.query = ko.observable().extend({ rateLimit: 1000 });
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'setupSearchListener', 'dropFromSource', 'removeProductFromProgramme', 'updatePositions', 'activate');
	}

	ProductProgrammesController.prototype = {
		constructor : ProductProgrammesController,
        // The pager takes care of filtering, sorting and paging functionality.
        sourceProgrammeProductsPager: {},
        dropFromSource : function(data) {
        	var self = this;
        	var vm = self.productVM();

        	// Only add product to programme if it does not exist yet.
    		var foundProduct = _.findWhere(ko.unwrap(vm.programmes), { id : data.id });
        	
    		if(_.isUndefined(foundProduct)) {
            	productAPI.addProductToProgramme(vm.productId(), data.id).then(function( response ) {
                    vm.programmeProducts.push(data);
                	self.sourceProgrammeProductsPager.removeData(data);
            	});
    		}
        },
        removeProductFromProgramme : function(data) {
        	var self = this;
        	var vm = self.productVM();
        	
        	productAPI.removeProductFromProgramme(vm.productId(), data.id).then(function() {
        		// See if the product is already in the source container.
        		var foundProduct = _.findWhere(ko.unwrap(self.sourceProgrammeProductsPager.data), { id : data.id });
        		
        		// Only add to drag&drop source container if it does not exist yet.
        		if(_.isUndefined(foundProduct)) {
                	self.sourceProgrammeProductsPager.data.push(data);
        		}
        		
        		// Remove from target-container in view.
        		vm.programmeProducts.remove(data);
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
			
	    	// Init the pager.
        	this.sourceProgrammeProductsPager = new gc.Pager(pagingOptions);
        	
			//---------------------------------------------------------------
			// Programmes for drag&drop target-container
			//---------------------------------------------------------------
			
        	//  Get programme-products that are already connected to the main product.
			productAPI.getProgrammeProducts(vm.productId()).then(function(data) {
				if(!_.isEmpty(data.data.products)) {
					// Populate drag&drop target container.
					vm.programmeProducts(data.data.products);
				}
			});
		},
		setupSearchListener : function() {
			var self = this;
			
			self.query.subscribe(function(value) {
	        	self.sourceProgrammeProductsPager.columnValue('$attr.article_number', undefined);
	        	self.sourceProgrammeProductsPager.columnValue('$attr.name', undefined);
				
	        	self.sourceProgrammeProductsPager.columnValue('$attr.article_number', value);
				self.sourceProgrammeProductsPager.load().then(function(data) {
					if(_.isEmpty(data.data)) {
			        	self.sourceProgrammeProductsPager.columnValue('$attr.article_number', undefined);
			        	self.sourceProgrammeProductsPager.columnValue('$attr.name', value);
			        	self.sourceProgrammeProductsPager.load().then(function(data2) {
			        	});
					}
				});
			});
		},
		attached : function(view, parent) {
			$('#tab-prd-details-programme-mapping').click(function() {
				$('#header-store-pills').hide();
			});
		}
	};

	return ProductProgrammesController;
});