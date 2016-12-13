define([ 'durandal/app', 'postal', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute-tabs', 'gc-attribute' ], function(app, postal, ko, gc, productAPI, attrTabsAPI, attrAPI) {

	function ProductVM(data) {
		var self = this;

		self.productId = ko.observable(data);
		self.data = ko.observable();
		
		//-----------------------------------------------------
		// Add attribute meta data (from cache).
		//-----------------------------------------------------
		self.data.subscribe(function(newData) {
			gc.attributes.appendAttributes(newData);
		});

		self.statusVM = new StatusVM(self);
		self.tabs = ko.observableArray([]);
        self.bundleProducts = ko.observableArray();
        self.programmeProducts = ko.observableArray();
        self.upsellProducts = ko.observableArray();
        self.crossSellProducts = ko.observableArray();

		self.productExists = ko.computed(function() {
			var prd = ko.unwrap(self.data);
			return !_.isUndefined(prd);
		});

		self.articleNumber = ko.computed(function() {
			if(!self.productExists()) {
				return undefined;
			}
			
			var prd = ko.unwrap(self.data);
			return gc.attributes.find(prd.attributes, 'article_number').value;
		});

		self.name = ko.computed(function() {
			if(!self.productExists()) {
				return undefined;
			}
			
			var prd = ko.unwrap(self.data);
			return gc.attributes.find(prd.attributes, 'name').value;
		});

		self.productGroup = ko.computed(function() {
			if(!self.productExists()) {
				return undefined;
			}
			
			var prd = ko.unwrap(self.data);
			var productGroup = gc.attributes.find(prd.attributes, 'product_group');
			
			if(!_.isEmpty(productGroup.attributeOptions)) {
				return productGroup.attributeOptions[0];
			} else if(!_.isEmpty(productGroup.optionIds)) {
				return productGroup.optionIds[0];
			}
			
			return undefined;
		});

		self.programme = ko.computed(function() {
			if(!self.productExists()) {
				return undefined;
			}
			
			var prd = ko.unwrap(self.data);
			var programme = gc.attributes.find(prd.attributes, 'programme');
			
			if(!_.isEmpty(programme.attributeOptions)) {
				return programme.attributeOptions[0];
			} else if(!_.isEmpty(programme.optionIds)) {
				return programme.optionIds[0];
			}
			
			return undefined;
		});
		
		self.isCategorized = ko.computed(function() {
            if (!self.productExists()) {
                return false;
            }

            var prd = ko.unwrap(self.data);

            var productGroup = gc.attributes.find(prd.attributes, 'product_group').optionIds;
            var programme = gc.attributes.find(prd.attributes, 'programme').optionIds;
            var bundleGroup = gc.attributes.find(prd.attributes, 'bundle_group').optionIds;

            if (self.isProgramme() && _.isEmpty(programme)) {
                return false;
            }

            if (self.isBundle() && _.isEmpty(bundleGroup)) {
                return false;
            }

            if (!self.isProgramme() && !self.isBundle() && _.isEmpty(productGroup)) {
                return false;
            }

            if (_.isEmpty(programme) && _.isEmpty(productGroup) && _.isEmpty(bundleGroup)) {
                return false;
            }

            return true;
		});
		
		self.isDeleted = ko.computed(function() {
			if(!self.productExists()) {
				return false;
			}
			
			var prd = ko.unwrap(self.data);
			
			if(!_.isUndefined(prd.deleted)) {
				return prd.deleted;
			}
			
			return false;
		});
		
		self.isProduct = ko.computed(function() {
			var prd = ko.unwrap(self.data);
			var type = '';

			if(!_.isUndefined(prd)) {
				type = prd.type;
			}

			return type == 'PHYSICAL';
		});

		self.isProgramme = ko.computed(function() {
			var prd = ko.unwrap(self.data);
			var type = '';

			if(!_.isUndefined(prd)) {
				type = prd.type;
			}

			return type == 'PROGRAMME';
		});

        self.isBundle = ko.computed(function() {
            var prd = ko.unwrap(self.data);
            var type = '';

            if(!_.isUndefined(prd)) {
                type = prd.type;
            }

            return type == 'BUNDLE';
        });

		self.isVariantMaster = ko.computed(function() {
			var prd = ko.unwrap(self.data);
			var type = '';

			if(!_.isUndefined(prd)) {
				type = prd.type;
			}

			return type == 'VARIANT_MASTER';
		});
		
		self.isVariant = ko.computed(function() {

			var prd = ko.unwrap(self.data);
			var type = '';

			if(!_.isUndefined(prd)) {
				type = prd.type;
			}

			return type == 'PHYSICAL' && !_.isUndefined(prd) && !_.isUndefined(prd.parentId);
		});
		
		self.initTabs = function(data) {
			_.each(data, function(tab) {
				self.tabs.push(new TabVM(self, tab));
			});
		};
		
		self.pageTitle = function() {
			var pageTitle = '';
			
			var prd = ko.unwrap(self.data);
			
			if(!_.isUndefined(prd)) {
				var artNo = gc.attributes.find(prd.attributes, 'article_number').value;
				var name = gc.attributes.find(prd.attributes, 'name').value;
				var name2 = gc.attributes.find(prd.attributes, 'name2').value;
				var productGroup = gc.attributes.find(prd.attributes, 'product_group').optionIds;
				var programme = gc.attributes.find(prd.attributes, 'programme').optionIds;
				
				if(!_.isEmpty(name)) {
					pageTitle += gc.ctxobj.closest(name);
				}

				if(!_.isEmpty(name2)) {
					pageTitle += ' ' + gc.ctxobj.closest(name2);
				}
				
				if(!_.isEmpty(artNo)) {
					artNo = gc.ctxobj.closest(artNo);
					
					if(!_.isEmpty(artNo)) {
						if(!_.isEmpty(name) || !_.isEmpty(name2)) {
							pageTitle += ' - ';
						}
						
						pageTitle += artNo;
					}
				}
			}

			if(_.isEmpty(pageTitle)) {
				pageTitle = 'app:modules.product.detailsTitle';
			}
			
			return pageTitle;
		};
		
		self.pageDescription = function() {
			
			var pageSubTitle = '';
			
			var prd = ko.unwrap(self.data);
			
			if(!_.isUndefined(prd)) {
				
				// TODO: Make ERP fields come dynamically from DB as this is project specific.
				var erpSuppModelNo = gc.attributes.find(prd.attributes, 'erpSuppModelNo').value;
				var erpName1 = gc.attributes.find(prd.attributes, 'erpName1').value;
				var erpName2 = gc.attributes.find(prd.attributes, 'erpName2').value;
				
				if(!_.isEmpty(erpName1)) {
					pageSubTitle += gc.ctxobj.any(erpName1, gc.app.currentLang(), gc.app.sessionGet('activeStore').id);
				}

				if(!_.isEmpty(erpName2)) {
					pageSubTitle += ', ' + gc.ctxobj.any(erpName2, gc.app.currentLang(), gc.app.sessionGet('activeStore').id);
				}
				
				if(!_.isEmpty(erpSuppModelNo)) {
					erpSuppModelNo = gc.ctxobj.any(erpSuppModelNo, gc.app.currentLang(), gc.app.sessionGet('activeStore').id);
					
					if(!_.isEmpty(erpSuppModelNo)) {
						if(!_.isEmpty(erpName1) || !_.isEmpty(erpName2)) {
							pageSubTitle += ' - ';
						}
						
						pageSubTitle += erpSuppModelNo;
					}
				}
			}

			if(_.isEmpty(pageSubTitle)) {
				pageSubTitle = 'app:modules.product.detailsSubtitle';
			}
			
			return pageSubTitle;
		};
		
		self.isAdminOrPM = function() {
			return gc.security.isInRole('product-manager') || gc.security.isInRole('admin');
		};
		
		//------------------------------------------------------------------------
		// Tab functions for deciding on which tab to display and which ones not.
		//------------------------------------------------------------------------
		
		self.showDynamicAttributeTabs = ko.computed(function() {
			return self.productExists() && self.isCategorized();
		});
		
		self.showMediaTab = ko.computed(function() {
			return false; // self.productExists() && self.isCategorized();
		});

		self.showDocumentsTab = ko.computed(function() {
			return false; //  self.productExists() && self.isCategorized() && self.isAdminOrPM();
		});

		self.showUpsellsTab = ko.computed(function() {
			return false; //  self.productExists() && self.isCategorized() && self.isAdminOrPM();
		});

		self.showCrossSellsTab = ko.computed(function() {
			return false; //  self.productExists() && self.isCategorized() && self.isAdminOrPM();
		});

		self.showStockTab = ko.computed(function() {
			return false; //  self.productExists() && !self.isProgramme() && self.isCategorized() && self.isAdminOrPM();
		});

		self.showPricesTab = ko.computed(function() {
			return false; //  self.productExists() && (self.isProgramme() || self.isProduct()) && self.isCategorized() && self.isAdminOrPM();
		});

		self.showVariantsTab = ko.computed(function() {
			return false; //  self.productExists() && self.isCategorized() && self.isVariantMaster() && self.isAdminOrPM();
		});

		self.showProgrammeTab = ko.computed(function() {
			return false; //  self.productExists() && self.isCategorized() && self.isProgramme() && self.isAdminOrPM();
		});

        self.showBundleTab = ko.computed(function() {
            return false; //  self.productExists() && self.isCategorized() && self.isBundle() && self.isAdminOrPM();
        });


        self.showPictogramTab = ko.computed(function() {
			return false; //  self.productExists() && self.isCategorized() && self.isAdminOrPM();
		});
		
		self.unwrap = function() {
			var unwrappedObject = {};
			var keys = Object.keys(self);

			for(var i=0; i<keys.length; i++) {
				unwrappedObject[keys[i]] = ko.unwrap(self[keys[i]]);
			}

			return unwrappedObject;
		}
	}
	
	function StatusVM(productVM) {
		var self = this;
		
		self.productVM = productVM;
		
		self.descStatus = ko.computed(function() {
			var prd = ko.unwrap(self.productVM.data);
			var status = [];

			if(!_.isUndefined(prd) && !_.isUndefined(prd.attributes)) {
				status = gc.attributes.find(prd.attributes, 'status_description');
			}
		});
		
		self.imageStatus = ko.computed(function() {
			var prd = ko.unwrap(self.productVM.data);
			var status = [];

			if(!_.isUndefined(prd) && !_.isUndefined(prd.attributes)) {
				status = gc.attributes.find(prd.attributes, 'status_image');
			}
		});
		
		self.articleStatus = ko.computed(function() {
			var prd = ko.unwrap(self.productVM.data);
			var status = [];

			if(!_.isUndefined(prd) && !_.isUndefined(prd.attributes)) {
				status = gc.attributes.find(prd.attributes, 'status_article');
			}
		});
	}

	function TabVM(productVM, data) {
		var self = this;

		self.productVM = productVM;
		self.data = ko.observable(data);
		self.label = ko.observable();

		ko.computed(function() {
			var tab = ko.unwrap(self.data);
			gc.ctxobj.enhance(tab, [ 'label' ]);
			self.label(tab.label);
		});

		self.id = function() {
			var tab = ko.unwrap(self.data);
			return tab.id;
		};
		
		self.isEnabled = function() {
			var tab = ko.unwrap(self.data);
			return tab.enabled;
		}
		
		self.isShowInProduct = function() {
			var tab = ko.unwrap(self.data);
			return tab.showInProduct;
		}
		
		self.isShowInProgramme = function() {
			var tab = ko.unwrap(self.data);
			return tab.showInProgramme;
		}
		
		self.isShowInVariantMaster = function() {
			var tab = ko.unwrap(self.data);
			return tab.showInVariantMaster;
		}
		
		self.show = ko.computed(function() {
			var tab = ko.unwrap(self.data);

			var showForProductType = false
			
			if(self.productVM.isProgramme()) {
				showForProductType = self.isShowInProgramme();
			} else if(self.productVM.isVariantMaster()) {
				showForProductType = self.isShowInVariantMaster();
			} else {
				showForProductType = self.isShowInProduct();
			}
			
			return self.productVM.productExists() && self.productVM.isCategorized() && showForProductType;
		});

	}
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductHistoryIndexController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductHistoryIndexController)) {
			throw new TypeError("ProductHistoryIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.subscriptions = [];
		this.productVM = ko.observable({});
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate');
	}

	ProductHistoryIndexController.prototype = {
		constructor : ProductHistoryIndexController,
		activate : function(data) {
			var self = this;

			console.log('VERSION !!!!!!!!!!!!!! ', data);

			var vm = new ProductVM(data.id);
			
			console.log('VERSION !!!!!!!!!!!!!!vm ', vm);
			
			
			self.productVM(vm);

			console.log('VERSION !!!!!!!!!!!!!!self.productVM() ', self.productVM());
			
			gc.app.sessionPut('historyProductVM', self.productVM);

			gc.app.pageTitle(vm.pageTitle());
			
			ko.computed(function() {
				var activeStore = gc.app.sessionGet('activeStore');
				gc.app.pageDescription(vm.pageDescription());
			});
			
			var promise = $.when({});
			
				promise = productAPI.getSnapshot(data.id, data.version).then(function(response) {
					console.log('Loaded snapshot: ', response.data.products[0]);
					
					vm.data(response.data.products[0]);
					gc.app.pageTitle(vm.pageTitle());
					gc.app.pageDescription(vm.pageDescription());
				});
			
			return promise.then(function(data) {
				return attrTabsAPI.getAttributeTabs().then(function(response) {
					vm.initTabs(response.data.attributeTabs);
				});
			});
		},
		detached : function() {
			var self = this;
			gc.app.unsubscribeAll(self.subscriptions);
		}
	}
	
	return ProductHistoryIndexController;
});