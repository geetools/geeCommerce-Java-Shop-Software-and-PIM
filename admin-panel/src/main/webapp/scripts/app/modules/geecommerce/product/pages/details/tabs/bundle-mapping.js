define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product' ], function( app, ko, gc, productAPI ) {

	function GroupBundleVM(vm) {
		var self = this;

		self.vm = vm;

		self.label = ko.observable();
        self.optional = ko.observable(false);
        self.showInProductDetails = ko.observable(true);
        self.type = ko.observable("SELECT");

        self.bundleItems = ko.observableArray([]);

        self.newProducts = ko.observableArray([]);

        self.newProducts.subscribe(function (newData) {
			console.log(newData)
			
			_.each(newData, function (productId) {
                var prd = _.findWhere(self.bundleItems(), { id : productId });

				if(!prd) {
                    productAPI.getProduct(productId).then(function (data) {

                        //we don't add programme and bundles to bundles
                        if(!(data.type == "PROGRAMME" || data.type == "BUNDLE")) {
                            var productBundleVM = new ProductBundleVM(vm, data, 1);

                            self.bundleItems.push(productBundleVM);
                        }
                    });
                }

            })
        })

        self.allowDrop  = function (parent) {
            console.log(this);
            console.log(parent());
        };

        self.isMultiselect = ko.computed(function() {
            return self.type() == "MULTISELECT" || self.type() == "CHECKBOX";
        });

        self.selected = ko.observable('');

        self.selected.subscribe(function (newValue) {
            _.each(self.bundleItems(), function (bundleItem) {
                if(bundleItem.id == newValue){
                    bundleItem.selected(true);
                } else {
                    bundleItem.selected(false);
                }
            })
        })

        self.type.subscribe(function (newValue) {
            _.each(self.bundleItems(), function (bundleItem) {
                bundleItem.selected(false)
            })
        })

        self.dropCallback = function (arg) {
            arg.item.selected(false);
        }
        
        self.removeItem = function (data) {
            self.bundleItems.remove(data);
        }

    }

	function ProductBundleVM(vm, product, quantity) {
        var self = this;
        self.vm = vm;
        self.id = product.id;
        self.product = product;
        self.quantity = ko.observable(quantity);

        self.selected = ko.observable(false);
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
		_.bindAll(this, 'saveData', 'activate', 'addBundleGroup', 'removeBundleGroup');
	}

    ProductBundlesController.prototype = {
		constructor : ProductBundlesController,
        // The pager takes care of filtering, sorting and paging functionality.
        sourceBundleProductsPager: {},
        removeBundleGroup: function (group) {
            this.bundleGroups.remove(group);
        },
        allowDrop : function (parent) {
            console.log(this);
            console.log(parent);
        },
		addBundleGroup : function () {
            var vm = this.productVM();
			var bundleGroup = new GroupBundleVM(vm);
			this.bundleGroups.push(bundleGroup);
        },
		saveData: function () {
            var self = this;
            var vm = self.productVM();
			
            var bundleGroups = [];

            _.each(self.bundleGroups(), function (bundleGroupVM) {
				var bundleGroup = {};
                bundleGroup.label = bundleGroupVM.label();
				bundleGroup.optional = bundleGroupVM.optional();
				bundleGroup.show_in_prd_details = bundleGroupVM.showInProductDetails();
                bundleGroup.type = bundleGroupVM.type();
				bundleGroup.bundle_items = [];
                
				_.each(bundleGroupVM.bundleItems(), function (bundleItemVM) {
					var bundleItem = {};

                    bundleItem.prd_id = bundleItemVM.id;
                    bundleItem.qty = bundleItemVM.quantity();
					bundleItem.def_prd_id = bundleItemVM.defaultVariant();
                    bundleItem.selected = bundleItemVM.selected();

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

            productAPI.getBundleGroups(vm.productId()).then(function (data) {

                if (data.data.bundleGroupItems) {
                    _.each(data.data.bundleGroupItems, function (bundleGroupItem) {
                        var bundleGroupItemVM = new GroupBundleVM(vm);
                        bundleGroupItemVM.label(bundleGroupItem.label);
                        bundleGroupItemVM.type(bundleGroupItem.type);
                        bundleGroupItemVM.optional(bundleGroupItem.optional);
                        bundleGroupItemVM.showInProductDetails(bundleGroupItem.showInProductDetails);

                        if (bundleGroupItem.bundleItems) {
                            _.each(bundleGroupItem.bundleItems, function (bundleItem) {
                                var productBundleVM = new ProductBundleVM(vm, bundleItem.product, bundleItem.quantity);
                                productBundleVM.selected(bundleItem.selected);
                                bundleGroupItemVM.bundleItems.push(productBundleVM);
                            });
                        }

                        if(!bundleGroupItemVM.isMultiselect()){
                            _.each(bundleGroupItemVM.bundleItems(), function (bundleItem) {
                                if(bundleItem.selected()){
                                    bundleGroupItemVM.selected(bundleItem.id);
                                }
                            });
                        }

                        self.bundleGroups.push(bundleGroupItemVM);
                    })
                }

            });
        },
		attached : function(view, parent) {
			$('#tab-prd-details-bundle-mapping').click(function() {
				$('#header-store-pills').hide();
			});
		}
	};

	return ProductBundlesController;
});