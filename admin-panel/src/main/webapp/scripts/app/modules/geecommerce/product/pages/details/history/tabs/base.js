define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute' ], function(app, ko, gc, productAPI, attrAPI) {

	function EditVM($root) {
		var self = this;
		self.$root = $root;

		self.isDirty = ko.observable();
		
		self.id = ko.observable();
		self.id2 = ko.observable();
		self.articleStatus = ko.observableArray([]);

		self.articleStatusIcon = ko.computed(function() {
			var activeStore = gc.app.sessionGet('activeStore');
			var availableStores = gc.app.confGet('availableStores');
			var articleStatus = self.articleStatus();

			var storeCount = 0;
			var articleStatusOnlineCount = 0;
			
			for(var i=0; i<availableStores.length; i++) {
				var store = availableStores[i];
				if((!_.isEmpty(activeStore) && !_.isEmpty(activeStore.id) && activeStore.id != store.id) || _.isEmpty(store.id)) {
					continue;
				}

				storeCount++;

				var articleStatusId = gc.ctxobj.val(articleStatus, undefined, undefined, store.id);

				if(!_.isEmpty(articleStatusId)) {
					var statusObj = _.findWhere(self.$root.articleStatuses(), {id: articleStatusId[0]});
					
					if(!_.isUndefined(statusObj)) {
						var statusLabel = statusObj.text();
		
						if(!_.isEmpty(statusLabel) && statusLabel.substring(0, 1) === '1') {
							articleStatusOnlineCount++;
						}
					}
				}
			}
		
			return storeCount === articleStatusOnlineCount ? "product-status-tick fa fa-check-square" : "product-status-cross fa fa-square-o";
		});

		self.descriptionStatus = ko.observableArray();
		self.descriptionStatusLabel = ko.computed(function() {
			var lang = gc.app.currentLang();
			
			var activeStore = gc.app.sessionGet('activeStore');
			var availableStores = gc.app.confGet('availableStores');
			var descriptionStatus = self.descriptionStatus();


			var summaryText = '';
			var x = 0;
			_.each(availableStores, function(store) {
				var ctxVal = gc.ctxobj.val(descriptionStatus, undefined, undefined, store.id);
				
console.log('ctxVal::::: ----> ', ctxVal);
				
				if(!_.isEmpty(ctxVal)) {
					if(x > 0)
						summaryText += ', <br/>';
				
					var foundValue = _.findWhere(self.$root.descriptionStatuses(), {id: ctxVal[0]});

console.log('foundValue::::: ----> ', foundValue, self.$root.descriptionStatuses());

					
					if(ko.isObservable(foundValue.text) || _.isFunction(foundValue.text)) {
						summaryText += '<span class="descStatus_' + foundValue.text().substring(0, 1) + '">' + foundValue.text() + '</span>';
					} else {
						summaryText += '<span class="descStatus_' + foundValue.text.substring(0, 1) + '>' + foundValue.text + '</span>';
					}
					
					x++;
				}
			});

			return summaryText;
		});
		
		self.descriptionStatusIcon = ko.computed(function() {
			var activeStore = gc.app.sessionGet('activeStore');
			var availableStores = gc.app.confGet('availableStores');
			var descriptionStatus = self.descriptionStatus();

			var storeCount = 0;
			var descStatusCompletedCount = 0;
			for(var i=0; i<availableStores.length; i++) {
				var store = availableStores[i];
				if((!_.isEmpty(activeStore) && !_.isEmpty(activeStore.id) && activeStore.id != store.id) || _.isEmpty(store.id)) {
					continue;
				}

				storeCount++;

				var descStatusId = gc.ctxobj.val(descriptionStatus, undefined, undefined, store.id);

				if(!_.isEmpty(descStatusId)) {
					var statusObj = _.findWhere(self.$root.descriptionStatuses(), {id: descStatusId[0]});
					
					if(!_.isUndefined(statusObj)) {
						var statusLabel = statusObj.text();
		
						if(!_.isEmpty(statusLabel) && statusLabel.substring(0, 1) === '2') {
							descStatusCompletedCount++;
						}
					}
				}
			}

			return storeCount === descStatusCompletedCount ? "product-status-tick fa fa-check-square" : ( descStatusCompletedCount > 0 ? "product-status-half-tick fa fa-check" : "product-status-cross fa fa-square-o" );
		});
		
		self.imageStatus = ko.observableArray();
		self.mainImageURL = ko.observable();

        self.isVariant = ko.observable(false);
        self.isVariantMaster = ko.observable(false);
        self.isProgramme = ko.observable(false);
        self.isBundle = ko.observable(false);

        self.showLinkToMaster = ko.computed(function() {
            return self.isVariant() && !self.isVariantMaster();
        });
        
        self.descriptionText = ko.computed(function() {
        	if(self.isProgramme()) {
        		return gc.app.i18n('app:modules.product.baseTabDescProgramme', {}, gc.app.currentUserLang);
        	} else if(self.isVariantMaster()) {
        		return gc.app.i18n('app:modules.product.baseTabDescVariantMaster', {}, gc.app.currentUserLang);
        	} else if(self.isVariant()) {
				return gc.app.i18n('app:modules.product.baseTabDescVariant', {}, gc.app.currentUserLang) + ' <a href="#/products/details/' + self.parentId() + '" target="_blank">' + self.parentName() + '</a>.';
        	} else {
        		return gc.app.i18n('app:modules.product.baseTabDescStandardArticle', {}, gc.app.currentUserLang);
        	}
        });
        
        self.parentId = ko.observable();
        self.parentName = ko.observable(gc.app.i18n('app:modules.product.article', {}, gc.app.currentUserLang));

		self.imageStatusLabel = ko.computed(function() {
			var attributeOptions = self.imageStatus();
			if(!_.isEmpty(attributeOptions)) {
				return gc.ctxobj.closest(attributeOptions[0].label, gc.app.currentUserLang());
			}
		});
		
		self.imageStatusIcon = ko.computed(function() {
			var statusLabel = self.imageStatusLabel();
			
			if(!_.isEmpty(statusLabel) && statusLabel.substring(0, 1) === '2') {
				return "product-status-tick fa fa-check-square";
			}
		
			return "product-status-cross fa fa-square-o";
		});
		
		self.visible = ko.observableArray([]);
		
		self.visibleStatusIcon = ko.computed(function() {
			if(!_.isEmpty(self.visible())) {
				var activeStore = gc.app.sessionGet('activeStore');
				var globalVal = gc.ctxobj.global(self.visible());
			
				if(!_.isEmpty(activeStore) && !_.isEmpty(activeStore.id)) {
					var ctxVal = gc.ctxobj.val(self.visible(), undefined, undefined, activeStore.id);
					
					if((!_.isUndefined(ctxVal) && ctxVal) || (!_.isUndefined(globalVal) && globalVal)) {
						return "product-status-tick fa fa-check-square";
					}
				} else {
					if(!_.isUndefined(globalVal) && globalVal) {
						return "product-status-tick fa fa-check-square";
					}
				}
			}
		
			return "product-status-cross fa fa-square-o";
		});
		
		self.visibleInProductList = ko.observableArray([]);
        self.saleable = ko.observableArray([]);
        self.includeInFeeds = ko.observableArray([]);
        self.showCartButton = ko.observableArray([]);
        self.special = ko.observableArray([]);
        self.sale = ko.observableArray([]);
		self.status = ko.observableArray([]);
		self.type = ko.observable();
	    self.enabled = ko.computed({
	        read: function() {
	        	var _status = ko.unwrap(self.status);
		        return !_.isUndefined(_status) && self.status == 'ENABLED' ? true : false;
		    },
	        write: function (value) {
	            if (value)
	                self.status('ENABLED');
	            else
	                self.status('DISABLED');
	        }
	    });		
	    
	    self.articleNumber = ko.observableArray([]);
		self.name = ko.observableArray([]);
		self.name2 = ko.observableArray();
		self.productGroup = ko.observableArray([]);
		self.programme = ko.observableArray([]);
        self.bundleGroup = ko.observableArray([]);

		self.ean = ko.observableArray();
		self.brand = ko.observableArray();
		self.supplier = ko.observableArray();
		self.manufacturer = ko.observableArray();
	}

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductHistoryBaseController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductHistoryBaseController)) {
			throw new TypeError("ProductHistoryBaseController constructor cannot be called as a function.");
		}

		this.gc = gc;
		this.app = gc.app;
		this.subscriptions = [];
		this.productVM = {};
		this.editVM = new EditVM(this);

		this.productGroups = ko.observableArray([]);
		this.programmes = ko.observableArray([]);
		this.articleStatuses = ko.observableArray([]);
		this.descriptionStatuses = ko.observableArray([]);
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'initOptions');
	}

	ProductHistoryBaseController.prototype = {
		constructor : ProductHistoryBaseController,
		initOptions : function() {
			var self = this;

			var attrStatusArticle;
			var attrStatusDescription;
			var attrProductGroup;
			var attrProgramme;

			attrAPI.getAttributes('product', { filter: { code: 'status_article' }}).then(function(data) {
//				console.log('status_article---------------------> ', data);
				attrStatusArticle = data.data.attributes[0];
//				console.log('status_article---------------------> ', attrStatusArticle);
			});
			
			attrAPI.getAttributes('product', { filter: { code: 'status_description' }}).then(function(data) {
				attrStatusDescription = data.data.attributes[0];
//				console.log('status_description---------------------> ', attrStatusDescription);
			});
			
			attrAPI.getAttributes('product', { filter: { code: 'product_group' }}).then(function(data) {
				attrProductGroup = data.data.attributes[0];
//				console.log('product_group---------------------> ', attrProductGroup);
			});
			
			attrAPI.getAttributes('product', { filter: { code: 'programme' }}).then(function(data) {
				attrProgramme = data.data.attributes[0];
//				console.log('programme---------------------> ', attrProgramme);
			});
			
			if(!_.isEmpty(attrStatusArticle) && !_.isEmpty(attrStatusArticle.options)) {
				_.forEach(attrStatusArticle.options, function(option) {
					self.articleStatuses.push( { id : option.id, text : option.label.i18n } );
				});
			}
			
			if(!_.isEmpty(attrStatusDescription) && !_.isEmpty(attrStatusDescription.options)) {
				_.forEach(attrStatusDescription.options, function(option) {
					self.descriptionStatuses.push( { id : option.id, text : option.label.i18n } );
				});
			}
			
			if(!_.isEmpty(attrProductGroup) && !_.isEmpty(attrProductGroup.options)) {
				self.productGroups.push( { id : '', text : function() {
						return gc.app.i18n('app:modules.product.newProductGroupSelectTitle', {}, gc.app.currentLang);
					}
				});
				_.forEach(attrProductGroup.options, function(option) {
					if(option && option.id && option.label) {
						self.productGroups.push( { id : option.id, text : option.label.i18n } );
					}
				});
			}
				
			if(!_.isEmpty(attrProgramme) && !_.isEmpty(attrProgramme.options)) {
				self.programmes.push( { id : '', text : function() {
						return gc.app.i18n('app:modules.product.newProgrammeSelectTitle', {}, gc.app.currentLang);
					}
				});
				_.forEach(attrProgramme.options, function(option) {
					if(option && option.id && option.label) {
						self.programmes.push( { id : option.id, text : option.label.i18n } );
					}
				});
			}
		},
		activate : function(data) {
			var self = this;
			
			self.initOptions();
						
			self.productVM = gc.app.sessionKGet('historyProductVM');
			
			var vm = self.productVM();
			
				var productId = data;
				var prdData = ko.unwrap(vm.data());
				
//				console.log("----------------prdData ------", prdData);
				
				self.editVM.id(productId);
				self.editVM.type(prdData.type);
				self.editVM.status(prdData.status);
				self.editVM.visible(prdData.visible);
				self.editVM.visibleInProductList(prdData.visibleInProductList);
                self.editVM.saleable(prdData.saleable);
                self.editVM.includeInFeeds(prdData.includeInFeeds);
                self.editVM.showCartButton(prdData.showCartButton);
                self.editVM.special(prdData.special);
                self.editVM.sale(prdData.sale);

                self.editVM.isVariant(vm.isVariant());
                self.editVM.isVariantMaster(vm.isVariantMaster());
                self.editVM.isProgramme(vm.isProgramme());
                self.editVM.parentId(prdData.parentId);
                
				self.editVM.id2(data.id2);
				self.editVM.articleNumber(gc.attributes.find(prdData.attributes, 'article_number').value);
				self.editVM.name(gc.attributes.find(prdData.attributes, 'name').value);
				self.editVM.name2(gc.attributes.find(prdData.attributes, 'name2').value);
				self.editVM.productGroup(gc.attributes.find(prdData.attributes, 'product_group').optionIds);
				self.editVM.programme(gc.attributes.find(prdData.attributes, 'programme').optionIds);
				self.editVM.articleStatus(gc.attributes.find(prdData.attributes, 'status_article').xOptionIds);
				self.editVM.descriptionStatus(gc.attributes.find(prdData.attributes, 'status_description').xOptionIds);
				self.editVM.imageStatus(gc.attributes.find(prdData.attributes, 'status_image').attributeOptions);

				self.editVM.ean(gc.attributes.find(prdData.attributes, 'ean').value);
				self.editVM.brand(gc.attributes.find(prdData.attributes, 'brand').value);
				self.editVM.supplier(gc.attributes.find(prdData.attributes, 'supplier').value);
				self.editVM.manufacturer(gc.attributes.find(prdData.attributes, 'manufacturer').value);
		},
		attached : function(view, parent) {
			$('#tab-prd-details-base').click(function() {
				$('#header-store-pills').show();
			});
		},
		detached : function() {
			var self = this;
			console.log('!!! DETACHED BASE');

			self.editVM.articleStatusIcon.dispose();
			self.editVM.descriptionStatusLabel.dispose();
			self.editVM.descriptionStatusIcon.dispose();
			self.editVM.showLinkToMaster.dispose();
			self.editVM.descriptionText.dispose();
			self.editVM.imageStatusLabel.dispose();
			self.editVM.imageStatusIcon.dispose();
			self.editVM.visibleStatusIcon.dispose();
			self.editVM.enabled.dispose();
		}
	}
	
	return ProductHistoryBaseController;
});