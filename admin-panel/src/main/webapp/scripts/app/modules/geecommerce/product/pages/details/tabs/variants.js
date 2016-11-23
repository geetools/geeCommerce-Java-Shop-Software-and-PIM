define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product' ], function(app, ko, gc, productAPI) {

	//-----------------------------------------------------------------
	// ProductVM for holding main products data
	//-----------------------------------------------------------------
	
	function ProductVM(productId) {
		var self = this;
		self.id = ko.observable(productId);
		self.articleNumber = ko.observable();
		self.name = ko.observable();
		self.productGroup = ko.observable();
		self.isVariant = ko.observable();
		self.isVariantMaster = ko.observable();
	}

	//-----------------------------------------------------------------
	// VariantsVM for holding drag&drop target and source data.
	//-----------------------------------------------------------------
	
	function VariantsVM(mainProductId, $root) {
		var self = this;
		
		this.$root = $root;
		this.parentProductId = mainProductId;
        this.sourceContainer = ko.observableArray();
        this.targetContainer = ko.observableArray();
        this.showPlusButton = ko.observable(true);
        this.showOptionsSelection = ko.observable(false);
        this.toggleOptionsSelection = function() {
        	self.showPlusButton(false);
        	self.showOptionsSelection(true);
        };
        
        this.unflaggedVariantOptions = [];
        
        this.dragEnter = function (event, data, model) {
        	
            var availableOptions = data.availableOptions();
            var variantOptions = data.variantOptions();
            var productGroup = data.productGroup;
        	
			var productVM = gc.app.sessionKGet('productVM');			
			var parentVM = productVM();
			var parentProductGroup = gc.ctxobj.val(parentVM.productGroup().label, gc.app.currentLang(), 'closest');
			
            if((_.isEmpty(availableOptions) && _.isEmpty(variantOptions)) || productGroup != parentProductGroup) {
            	
            	var message = '';
            	if(productGroup != parentProductGroup) {
            		message = gc.app.i18n('app:modules.product.alertNotSameProductGroup');
            	} else {
            		message = gc.app.i18n('app:modules.product.alertNoVariantOptions');
            	}
            	
                $( event.target ).popover({
                    placement: 'right',
                    content: '<div style="width:250px"><i style="margin-right: 5px" class="fa fa-ban fa-2x variant-options-none"></i>' + message + '</div>',
                    html: true,
                    trigger: 'manual'
                });        	

                $( event.target ).popover('show');
                
                $( event.target ).mouseleave(function() {
                	$(event.target).popover('destroy')
                });            
                
                _.delay(function() {
                	$(event.target).popover('destroy')
                }, 6000);
                
                return false;
            } else {
            	return true;
            }
        };
        
        // Called when user drops variant into target container.
        this.dropFromSource = function (data, model) {
        	var existingVariant = _.findWhere(ko.unwrap(self.targetContainer), {id: data.id});
        	var sourceVariant = _.findWhere(ko.unwrap(self.sourceContainer), {id: data.id});

        	var hasOptionAttributes =  false;
        	
        	if(!_.isUndefined(sourceVariant)) {
            	if(!_.isEmpty(sourceVariant.variantOptions) || !_.isEmpty(sourceVariant.availableOptions)) {
            		hasOptionAttributes = true;
            	}
        	}
        	
        	if(hasOptionAttributes) {
            	// Add variant link to DB and then add to target container.
            	productAPI.addVariant(self.parentProductId, data.id).then(function() {
//                	self.sourceContainer.remove(data);
                	
//                	if(_.isUndefined(existingVariant)) {
//                    	self.targetContainer.push(data);
//                	}
                	
                	self.reloadVariants();                	
            	});
        	}
        };
        
        this.reloadVariants = function() {
			var self = this;
			
			//  Get variants that are already connected to the main product.
			productAPI.getVariants(self.parentProductId).then(function(data) {
				if(!_.isEmpty(data.data.products)) {
					
					// Add all products to the variants array.
					$root.variants = data.data.products;
					
					// Add the attribute meta data.
					gc.attributes.appendAttributes($root.variants);					
					
					// Populate drag&drop target container.
					self.refreshTargetContainer($root.variants);
				}
			});
		};
        
        // Refresh contents of source container.
        this.refreshSourceContainer = function(products) {
        	var emptied = false;
        	
        	_.each(products, function(product) {
        		
				var isVariant = productAPI.isVariant(product);
				var isVariantMaster = productAPI.isVariantMaster(product);

				// Variant holders cannot be variants at the same time.
        		if(!isVariantMaster) {
        			// Only add to drag&drop source container if it does not exist in the target container yet.
        			var existingVariant = _.findWhere(ko.unwrap(self.targetContainer), {id: product.id});

    				// Empty container before adding new results.
    				if(!emptied) {
    					emptied = true;
    					self.sourceContainer([]);
    				}
        			
        			if(_.isUndefined(existingVariant)) {
        				var prd = {
    							id: product.id,
    							articleNumber: gc.attributes.find(product.attributes, 'article_number').value,
    							name: gc.attributes.find(product.attributes, 'name').value,
    							productGroup: gc.attributes.optionLabel(product.attributes, 'product_group'),
    	        				variantOptions: ko.observableArray(self.getLinkedVariantOptions(product)),
    	        				availableOptions: ko.observableArray(self.getAvailableVariantOptions(product)),
    	        				image: product.mainImageURI,
    	        				thumbnail: gc.images.thumbnail(product.mainImageURI),
    							hasChanged: ko.observable(false)
    						};
        				
						self.sourceContainer.push(prd);
        			}
        		}
        	});
        };
        // Called when user drops variant into source container.
        this.dropFromTarget = function (data, model) {
        	var existingVariant = _.findWhere(ko.unwrap(self.sourceContainer), {id: data.id});
        	// Remove variant link from DB and then add to source container.
        	productAPI.removeVariant(self.parentProductId, data.id).then(function() {
            	self.targetContainer.remove(data);
            	
            	if(_.isUndefined(existingVariant)) {
                	self.sourceContainer.push(data);
            	}
        	});
        };
        // Refresh contents of target container.
        this.refreshTargetContainer = function(products) {
    		// Add lightweight product objects to the drag&drop target container.
    		// These are the variants that the main product already has.
        	_.each(products, function(product) {
            	var existingProduct = _.findWhere(ko.unwrap(self.targetContainer), {id: product.id});
        		
            	if(_.isUndefined(existingProduct)) {
            		
            		var prd = {
        				id: product.id,
        				articleNumber: gc.attributes.find(product.attributes, 'article_number').value,
        				name: gc.attributes.find(product.attributes, 'name').value,
        				productGroup: gc.attributes.optionLabel(product.attributes, 'product_group'),
        				variantOptions: ko.observableArray(self.getLinkedVariantOptions(product)),
        				availableOptions: ko.observableArray(self.getAvailableVariantOptions(product)),
        				image: product.mainImageURI,
        				thumbnail: gc.images.thumbnail(product.mainImageURI),
        				hasChanged: ko.observable(false)
        			};
            		
        			self.targetContainer.push(prd);
            	}
        	});        	
        };
        // Refresh variant options of product.
        this.refreshVariantOptions = function(product) {
        	var existingProduct = _.findWhere(ko.unwrap(self.targetContainer), {id: product.id});
    		
        	if(!_.isUndefined(existingProduct)) {
        		existingProduct.variantOptions(self.getLinkedVariantOptions(product));
				existingProduct.availableOptions(self.getAvailableVariantOptions(product));
				existingProduct.hasChanged(true);
        	}
        };
		this.getLinkedVariantOptions = function(variantProduct) {
			var variantAttributes = productAPI.getVariantAttributes(variantProduct.attributes);
			return gc.attributes.flattenedValues(variantAttributes, { ctxMode : 'closest' });
		};
		this.getAvailableVariantOptions = function(variantProduct) {
			var variantAttributes = productAPI.getVariantAttributes(variantProduct.attributes);
			var flattenedVariantValues = gc.attributes.flattenedValues(variantAttributes);
			var availableVariantOptions = gc.attributes.flattenedValues(variantProduct.attributes, { onlyAttributeOptions : true, exclude : flattenedVariantValues, ctxMode : 'closest' });
			
			return availableVariantOptions;
		};
		this.rememberUnflaggedVariantOption =  function(unflaggedVariantOption) {
			var existingUnflaggedVariantOption = _.findWhere(self.unflaggedVariantOptions, unflaggedVariantOption);
			
			if(_.isUndefined(existingUnflaggedVariantOption)) {
				self.unflaggedVariantOptions.push(unflaggedVariantOption);
			}
		};
		this.removeUnflaggedVariantOption =  function(unflaggedVariantOption) {
			var existingUnflaggedVariantOption = _.findWhere(self.unflaggedVariantOptions, unflaggedVariantOption);
			
			if(!_.isUndefined(existingUnflaggedVariantOption)) {
				self.unflaggedVariantOptions.remove(existingUnflaggedVariantOption);
			}
		};
    }
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductVariantsController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductVariantsController)) {
			throw new TypeError("ProductVariantsController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.productVM = {};
		this.variantsVM = {};
		// Delay setting of new value to void making REST-search calls for each letter typed into the field.
		this.searchVM = {articleNumber : ko.observable().extend({ rateLimit: { method: "notifyWhenChangesStop", timeout: 1000 } })};
		this.variants = [];
		this.variantsPager = {};
//		this.isAttached = false;
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'getLinkedVariantOptions', 'getAvailableVariantOptions', 'flagOptionAsVariant', 'unflagVariantOption', 'saveVariantOptions', 'activate', 'attached', 'detached');
	}

	ProductVariantsController.prototype = {
		constructor : ProductVariantsController,
		getLinkedVariantOptions : function(variantProduct) {
			var completeProduct = _.findWhere(this.variants, {id: variantProduct.id});
			var variantAttributes = productAPI.getVariantAttributes(completeProduct.attributes);
			return gc.attributes.flattenedValues(variantAttributes, { ctxMode : 'closest' });
		},
		getAvailableVariantOptions : function(variantProduct) {
			var completeProduct = _.findWhere(this.variants, {id: variantProduct.id});
			var variantAttributes = productAPI.getVariantAttributes(completeProduct.attributes);
			var flattenedVariantValues = gc.attributes.flattenedValues(variantAttributes);

			var availableVariantOptions = gc.attributes.flattenedValues(completeProduct.attributes, { onlyAttributeOptions : true, exclude : flattenedVariantValues, ctxMode : 'closest' })			
			
			return availableVariantOptions;
		},
		flagOptionAsVariant : function(variantOption, event) {
			var productId = $(event.currentTarget).attr('data-productId');
			var completeProduct = _.findWhere(this.variants, {id: productId});
			var foundAttribute = _.findWhere(completeProduct.attributes, {attributeId: variantOption.attributeId});

			// Turn attribute into a selectable variant option.
			gc.attributes.addProperty(foundAttribute, {'variant' : true});
			
			// If we unflagged the variant option before, we need to undo that again.
			this.variantsVM.removeUnflaggedVariantOption({productId: productId, attributeId: variantOption.attributeId});
			
			// Re-render the options list of variant, so that the option now shows up as a pill.
			this.variantsVM.refreshVariantOptions(completeProduct);
			
			this.variantsVM.showPlusButton(true);
			this.variantsVM.showOptionsSelection(false);
		},
		unflagVariantOption : function(variantOption, event) {
			var productId = $(event.currentTarget).attr('data-productId');
			var completeProduct = _.findWhere(this.variants, {id: productId});
			var foundAttribute = _.findWhere(completeProduct.attributes, {attributeId: variantOption.attributeId});

			// Remove the variant property from the attribute.
			gc.attributes.removeProperty(foundAttribute, 'variant');

			// Remember the unflagged variant option for saving later.
			this.variantsVM.rememberUnflaggedVariantOption({productId: productId, attributeId: variantOption.attributeId});
			
			// Re-render the options list of variant.
			this.variantsVM.refreshVariantOptions(completeProduct);
		},
		saveVariantOptions :  function(variantProduct) {
			var self = this;
			
			// Find  the product we originally loaded from DB.
			var completeProduct = _.findWhere(this.variants, {id: variantProduct.id});
			
			// Get any attributes where we may have unflagged the variant-property.
			var attributesToSave = _.where(this.variantsVM.unflaggedVariantOptions, {productId: variantProduct.id});

			// Add attributes that we may have flagged as variant.
			_.each(variantProduct.variantOptions(), function(variantOption) {
				attributesToSave.push({productId: variantProduct.id, attributeId: variantOption.attributeId});
			});
			
			// We do not want to save any product twice.
			attributesToSave = _.uniq(attributesToSave);
			
			// TODO: Dirty delay hack for now. This should be replaced by some intelligent queue that honours promises.
			var delay = 0;
			_.each(attributesToSave, function(attrToSave) {
				
				var foundAttribute = _.findWhere(completeProduct.attributes, {attributeId: attrToSave.attributeId});

				// Shallow copy.
				var attrCopy = _.clone(foundAttribute);
				
				// TODO: There shouldn't be a need to remove them, but genson 
				// fails to ignore them, even although the ignore-annotation is set.
				delete attrCopy['attributeOptions'];

				// Update attribute value of variant product.
				_.delay(productAPI.updateAttribute, delay, variantProduct.id, attrCopy);
				
				delay += 1000;
			});
			
			if(!_.isEmpty(variantProduct.variantOptions())) {
				// Add variant productId to main product. It is not problem if it already exists 
				// as the backend ensures that a variant option does not exist twice.
				_.delay(productAPI.addVariant, delay, self.productVM().productId(), variantProduct.id);
			} else {
				// If there are no more attributes flagged as variants, we might as well
				// just remove the complete variant from the main product.
				_.delay(productAPI.removeVariant, delay, self.productVM().productId(), variantProduct.id);
			}
			
			var savedVariant = _.findWhere(ko.unwrap(self.variantsVM.targetContainer), {id: variantProduct.id});
			if(!_.isUndefined(savedVariant)) {
				savedVariant.hasChanged(false);
			}
		},
		activate : function(productId) {
			var self = this;
			
			self.productVM = gc.app.sessionKGet('productVM');			
			
			var vm = self.productVM();
			
			this.variantsVM = new VariantsVM(productId, this);
			
	    	// Pager columns
			var pagerColumns = [
              {'name' : '$attr.article_number', 'label' : 'Artikelnummer'},
              {'name' : '$attr.name', 'label' : 'Name'}
            ];
			
			var pagingOptions = productAPI.variantProductPagingOptions( { columns : pagerColumns, filter : [ { name: 'type', value: 1 /* PHYSICAL */ }, { name: '$opt.product_group', value: vm.productGroup().id } ], attributes : [] } )
			
	    	// Init the pager.
        	this.variantsPager = new gc.Pager(pagingOptions);
		},
		attached : function() {
			var self = this;
			
			var vm = self.productVM();
			
			$(document).on('click', '#tab-prd-details-variants', function() {

				$('#header-store-pills').hide();

/*
				// If view has already been loaded
				if(self.isAttached) {
					return;
				} else {
				}
				self.isAttached = true;
*/				

				//---------------------------------------------------------------
				// Subscribe to the variant's search field and automatically
				// attempt to load data and refresh the drag&drop source container.
				//---------------------------------------------------------------
				
				self.searchVM.articleNumber.subscribe(function(articleNumber) {
					var emptySourceContainer = true;
					
					if(articleNumber.length > 2) {
						self.variantsPager.setDefaultFilter([ { name: 'type', value: 1 /* PHYSICAL */ } ]);
			        	self.variantsPager.columnValue('$attr.article_number', articleNumber);
						self.variantsPager.load().then(function(data) {
							self.variantsVM.sourceContainer([]);							
							
							console.log('$$$$$$$$$$$$$$4 ', data);
							
							// Remember products that we have fetched as we will need them after the 
							// light copy has been dropped into the target container.
							_.each(data.data.products, function(product) {
								var existingProduct = _.findWhere(self.variants, {id: product.id});
								if(_.isUndefined(existingProduct)) {
									self.variants.push(product);
								}
							});
							
							// Re-populate drag&drop source container.
			        		self.variantsVM.refreshSourceContainer(data.data.products);
						});
					}
				});
				
				// First attempt to get the main product. If it exists, we can start loading the other stuff.
//				return productAPI.getProduct(self.productVM.id()).then(function(product) {
//					self.productVM.articleNumber(gc.attributes.find(product.attributes, 'article_number').value);
//					self.productVM.name(gc.attributes.find(product.attributes, 'name').value);
//					self.productVM.productGroup(gc.attributes.find(product.attributes, 'product_group').value);
//					self.productVM.isVariant(productAPI.isVariant(product));
//					self.productVM.isVariantMaster(productAPI.isVariantMaster(product));
//				}).then(function(data) {

					//---------------------------------------------------------------
					// Variants for drag&drop target-container
					//---------------------------------------------------------------
					
		        	//  Get variants that are already connected to the main product.
					productAPI.getVariants(vm.productId()).then(function(data) {
						if(!_.isEmpty(data.data.products)) {
							
							// Add all products to the variants array.
							self.variants = data.data.products;
							
							// Append the attribute meta-data as we only have the attributeId at this point.
							gc.attributes.appendAttributes(self.variants);
							
							// Populate drag&drop target container.
							self.variantsVM.refreshTargetContainer(self.variants);
						}
					});
					
					//---------------------------------------------------------------
					// Variants for drag&drop source-container
					//---------------------------------------------------------------
					
					// Pre-fetch possible variants according to the first part of the article-number.
					if(!_.isEmpty(vm.articleNumber())) {
			        	self.variantsPager.columnValue('$attr.article_number', gc.ctxobj.plain(vm.articleNumber()).substring(0,5));
			        	self.variantsPager.load().then(function(data) {
			        		
							// Populate drag&drop source container.
			        		self.variantsVM.refreshSourceContainer(data.data.products);
			        	});
					}
//				});
			});
		},
		detached : function(productId) {
//			this.isAttached = false;
			$(document).off('click', '#tab-prd-details-variants');
		}		
	};
	
	return ProductVariantsController;
});