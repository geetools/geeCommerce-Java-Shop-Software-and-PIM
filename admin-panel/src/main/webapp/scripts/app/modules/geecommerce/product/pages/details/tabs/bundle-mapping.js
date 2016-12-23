define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product' ], function( app, ko, gc, productAPI ) {

	function GroupBundleVM(vm) {
		var self = this;

		self.vm = vm;

		self.label = ko.observable();
        self.optional = ko.observable(false);
        self.type = ko.observable("SELECT");

        self.bundleItems = ko.observableArray([]);

        self.newProducts = ko.observableArray([]);

        self.newProducts.subscribe(function (newData) {
			console.log(newData)
			
			_.each(newData, function (productId) {
				productAPI.getProduct(productId).then(function (data) {
                    var productBundleVM = new ProductBundleVM(vm, data, 1);
					self.bundleItems.push(productBundleVM);
                });


            })
        })
    }

	function ProductBundleVM(vm, product, quantity) {
        var self = this;
        self.vm = vm;
        self.id = product.id;
        self.product = product;
        self.quantity = ko.observable(quantity);

        self.optional = ko.observable(false);
        self.defaultVariant = ko.observable();
        self.isVariantMaster = ko.observable(false);
        self.variants = ko.observableArray([]);
        self.variantOptions = ko.observableArray([])


        if(product.type == "VARIANT_MASTER"){
        	self.isVariantMaster(true);

            productAPI.getVariants(self.id).then(function(data) {

                if(!_.isEmpty(data.data.products)) {

                    // Add all products to the variants array.
                    var variants =  data.data.products;

                    // Add the attribute meta data.
                    gc.attributes.appendAttributes(variants);

                    // Populate drag&drop target container.
                    self.variants(variants)

                    _.forEach(variants, function(variant) {
                        var name = gc.attributes.find(variant.attributes, "name");
                        var number = gc.attributes.find(variant.attributes, "article_number");

                        var nameVal = gc.ctxobj.val(name.value, gc.app.currentUserLang(), "closest");
                        var numberVal = gc.ctxobj.val(number.value, gc.app.currentUserLang(), "closest");

                        self.variantOptions.push({
                            id : variant.id,
                            text : numberVal + " - " + nameVal || ""
                        });
                    });
                } else {
                	self.variants([]);
                    self.variantOptions([]);
				}
            });
		}


/*        self.quantity.subscribe(function (val) {
        	if(val > 0){
                productAPI.addProductToBundle(self.vm.productId(), self.id, val);
			}
    	})*/
    }
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductBundlesController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductBundlesController)) {
			throw new TypeError("ProductBundlesController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.gc = gc;
		this.productVM = {};
		this.query = ko.observable().extend({ rateLimit: 1000 });
		this.bundleGroups = ko.observableArray([]);
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'addBundleGroup');
	}

    ProductBundlesController.prototype = {
		constructor : ProductBundlesController,
        // The pager takes care of filtering, sorting and paging functionality.
        sourceBundleProductsPager: {},
		addBundleGroup : function () {
            var vm = this.productVM();
			var bundleGroup = new GroupBundleVM(vm);
			this.bundleGroups.push(bundleGroup);
        },
/*        dropFromSource : function(data) {
        	var self = this;
        	var vm = self.productVM();

        	// Only add product to programme if it does not exist yet.
    		//var foundProduct = _.findWhere(ko.unwrap(vm.programmes), { id : data.id });
        	
    		//if(_.isUndefined(foundProduct)) {
            	productAPI.addProductToBundle(vm.productId(), data.id, 1).then(function( response ) {
                    vm.bundleProducts.push(new ProductBundleVM(vm, data, 1));
                	self.sourceBundleProductsPager.removeData(data);
            	});
    		//}
        },
        removeProductFromBundle : function(data) {
        	var self = this;
        	var vm = self.productVM();
        	
        	productAPI.removeProductFromBundle(vm.productId(), data.id).then(function() {
        		// See if the product is already in the source container.
        		var foundProduct = _.findWhere(ko.unwrap(self.sourceBundleProductsPager.data), { id : data.id });
        		
        		// Only add to drag&drop source container if it does not exist yet.
        		if(_.isUndefined(foundProduct)) {
                	self.sourceBundleProductsPager.data.push(data.product);
        		}
        		
        		// Remove from target-container in view.
        		vm.bundleProducts.remove(data);
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
		},*/
		saveData: function () {
            var self = this;
            var vm = self.productVM();
			
            var bundleGroups = [];

            _.each(self.bundleGroups(), function (bundleGroupVM) {
				var bundleGroup = {};
                bundleGroup.label = bundleGroupVM.label();
				bundleGroup.optional = bundleGroupVM.optional();
                bundleGroup.type = bundleGroupVM.type();
				bundleGroup.bundle_items = [];
                
				_.each(bundleGroupVM.bundleItems(), function (bundleItemVM) {
					var bundleItem = {};

                    bundleItem.prd_id = bundleItemVM.id;
                    bundleItem.qty = bundleItemVM.quantity();
					bundleItem.def_prd_id = bundleItemVM.defaultVariant();

                    bundleGroup.bundle_items.push(bundleItem)
                });

				bundleGroups.push(bundleGroup);
            })


			productAPI.saveBundleGroups(vm.productId(), bundleGroups);
        },
		activate : function(productId) {
			var self = this;

			self.productVM = gc.app.sessionKGet('productVM');			
			
			var vm = self.productVM();

		/*
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
            pagingOptions.fields.push('mainImageURI');
			
	    	// Init the pager.
        	this.sourceBundleProductsPager = new gc.Pager(pagingOptions);
        	
			//---------------------------------------------------------------
			// Programmes for drag&drop target-container
			//---------------------------------------------------------------
			
        	//  Get programme-products that are already connected to the main product.
			productAPI.getBundleProducts(vm.productId()).then(function(data) {

				if(!_.isEmpty(data.data.bundleProductItems)) {
					var productItems = [];
					_.each(data.data.bundleProductItems, function (productItem) {
                        gc.attributes.appendAttributes(productItem.product);
                        productItems.push(new ProductBundleVM(vm, productItem.product, productItem.quantity))
                    });
					vm.bundleProducts(productItems);


					// Populate drag&drop target container.
					//vm.bundleProducts(data.data.products);
				}
			});*/
		},
		/*setupSearchListener : function() {
			var self = this;
			
			self.query.subscribe(function(value) {
	        	self.sourceBundleProductsPager.columnValue('$attr.article_number', undefined);
	        	self.sourceBundleProductsPager.columnValue('$attr.name', undefined);
				
	        	self.sourceBundleProductsPager.columnValue('$attr.article_number', value);
				self.sourceBundleProductsPager.load().then(function(data) {
					if(_.isEmpty(data.data)) {
			        	self.sourceBundleProductsPager.columnValue('$attr.article_number', undefined);
			        	self.sourceBundleProductsPager.columnValue('$attr.name', value);
			        	self.sourceBundleProductsPager.load().then(function(data2) {
			        	});
					}
				});
			});
		},*/
		attached : function(view, parent) {
			$('#tab-prd-details-bundle-mapping').click(function() {
				$('#header-store-pills').hide();
			});
		}
	};

	return ProductBundlesController;
});