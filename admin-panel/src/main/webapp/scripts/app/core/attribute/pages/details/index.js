define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-attribute', 'gc-attribute-vm' ], function(app, ko, gc, attrAPI, attributeVM) {
//
//	function AttributeVM(attributeId) {
//		var self = this;
//		
//        self.id = ko.observable(attributeId);
//        
//        //--------------------------------------------------------
//        // General tab
//        //--------------------------------------------------------
//		self.code = ko.observable();
//		self.code2 = ko.observable();
//		self.editable = ko.observable();
//		self.enabled = ko.observable();
//		self.targetObjectId = ko.observable();
//		self.targetObject = ko.observable(); // fetched in separate call.
//		self.type = ko.observable();
//		self.scopes = ko.observableArray([]);
//		self.backendLabel = ko.observableArray([]);
//		self.frontendLabel = ko.observableArray([]);
//		self.frontendFormat = ko.observableArray([]);
//		self.options = ko.observableArray([]);
//		self.searchable = ko.observable();
//		self.frontendInput = ko.observable();
//		self.frontendOutput = ko.observable();
//		self.inputType = ko.observable();
//		self.backendType = ko.observable();
//		self.includeInProductListFilter = ko.observable();
//        self.includeInProductListQuery = ko.observable();
//		self.includeInSearchFilter = ko.observable();
//		self.showInProductDetails = ko.observable();
//        self.allowMultipleValues = ko.observable();
//		self.i18n = ko.observable();
//		self.linkedAttributeIds = ko.observableArray([]);
//		self.productTypes = ko.observableArray([]);
//		self.dimensionAttribute = ko.observable();
//        self.allowNewOptionsViaImport = ko.observable();
//		
//        //--------------------------------------------------------
//        // Validation properties
//        //--------------------------------------------------------
//		
//		self.validationMin = ko.observableArray([]);
//		self.validationMax = ko.observableArray([]);
//		self.validationMinLength = ko.observableArray([]);
//		self.validationMaxLength = ko.observableArray([]);
//		self.validationFuture = ko.observableArray([]);
//		self.validationPast = ko.observableArray([]);
//		self.validationAssertTrue = ko.observableArray([]);
//		self.validationAssertFalse = ko.observableArray([]);
//		self.validationPattern = ko.observableArray([]);
//		self.validationScript = ko.observableArray([]);
//		self.validationMessage = ko.observableArray([]);
//		
//        //--------------------------------------------------------
//        // Product list filter tab
//        //--------------------------------------------------------
//		self.productListFilterType = ko.observable();
//		self.productListFilterIndexFields = ko.observableArray([]);
//		self.productListFilterKeyAlias = ko.observable();
//		self.productListFilterFormatLabel = ko.observable();
//		self.productListFilterFormatValue = ko.observable();
//		self.productListFilterParseValue = ko.observable();
//		self.productListFilterMulti = ko.observable();
//		self.productListFilterInheritFromParent = ko.observable();
//		self.productListFilterIncludeChildren = ko.observable();
//		self.productListFilterPosition = ko.observable();
//
//			
//		self.isNew = ko.computed(function() {
//			return self.id() == 'new';
//		});
//
//		self.isEditable = ko.computed(function() {
//			return self.editable() == true;
//		});
//
//        self.isCodeEditable = ko.observable(true);
//		
//		self.isVirtual = ko.computed(function() {
//			return self.type() == 'VIRTUAL';
//		});
//		
//		self.isProductAttribute = ko.computed(function() {
//			console.log('_____________ IS PRODUCT!!!?? ', (self.targetObject.code == 'product'));
//			
//			return self.targetObject.code == 'product';
//		});
//		
//		self.isOptionsAttribute = ko.computed(function() {
//			return self.frontendInput() == 'SELECT';
//		});
//		
//		self.showOptionsTab = ko.computed(function() {
//			return !self.isNew() && !self.isVirtual() && self.isOptionsAttribute();
//		});
//		
//		self.showProductListFilterTab = ko.computed(function() {
//			return !self.isNew() && self.isProductAttribute() && self.includeInProductListFilter() == true;
//		});		
//		
//		self.showInputConditionsTab = ko.computed(function() {
//			return !self.isNew() && !self.isVirtual() && self.isProductAttribute();
//		});		
//	}	

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function AttributeIndexController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof AttributeIndexController)) {
			throw new TypeError("AttributeIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.attributeVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate');
	}

	AttributeIndexController.prototype = {
		constructor : AttributeIndexController,
		pageTitle : function() {
			var self = this;
			var title = '';
			var vm = ko.unwrap(self.attributeVM);
			
			if(!_.isUndefined(vm)) {
				var blabel = ko.unwrap(vm.backendLabel);
				var code = ko.unwrap(vm.code);
				var code2 = ko.unwrap(vm.code2);

				if(!_.isEmpty(blabel)) {
					title += gc.ctxobj.val(blabel, self.app.currentUserLang(), 'any');
				}
				
				if(!_.isUndefined(code)) {
					title += ' - ' + code;
				}

				if(!_.isUndefined(code2)) {
					title += ' (' + code2 + ')';
				}
			}
			
			if(title == '') {
				title = 'Attributdetails';
			}
			
			return title;
		},
		pageDescription : 'Attributdetails ansehen und bearbeiten',
		activate : function(attributeId) {
			var self = this;

	        var AttributeVM = require('gc-attribute-vm');       
	        self.attributeVM = new AttributeVM(attributeId);
			
			gc.app.sessionPut('attributeVM', self.attributeVM);
			
			if(attributeId == 'new') {
				gc.app.pageTitle(gc.app.i18n('app:modules.attribute.newAttributeTitle'));
				gc.app.pageDescription('app:modules.attribute.newAttributeDesc');
			} else {
				gc.app.pageTitle(self.pageTitle());
				gc.app.pageDescription(self.pageDescription);
				
				return attrAPI.getAttribute(attributeId).then(function(attribute) {
					self.attributeVM.id(attribute.id);

					//--------------------------------------------------------
			        // General tab
			        //--------------------------------------------------------
					self.attributeVM.targetObjectId(attribute.targetObjectId);
					self.attributeVM.type(attribute.type);
					self.attributeVM.scopes(attribute.scopes || []);
					self.attributeVM.backendLabel(attribute.backendLabel);
					self.attributeVM.frontendLabel(attribute.frontendLabel);
					self.attributeVM.code(attribute.code);
					self.attributeVM.code2(attribute.code2);
					self.attributeVM.editable(attribute.editable);
					self.attributeVM.enabled(attribute.enabled);
					self.attributeVM.searchable(attribute.searchable);
					self.attributeVM.frontendInput(attribute.frontendInput);
					self.attributeVM.frontendOutput(attribute.frontendOutput);
					self.attributeVM.frontendFormat(attribute.frontendFormat);
					self.attributeVM.inputType(attribute.inputType);
					self.attributeVM.backendType(attribute.backendType);
					self.attributeVM.includeInProductListFilter(attribute.includeInProductListFilter);
                    self.attributeVM.includeInProductListQuery(attribute.includeInProductListQuery);
					self.attributeVM.includeInSearchFilter(attribute.includeInSearchFilter);
					self.attributeVM.showInProductDetails(attribute.showInProductDetails);
	                self.attributeVM.allowMultipleValues(attribute.allowMultipleValues);
					self.attributeVM.dimensionAttribute(attribute.dimensionAttribute);
					self.attributeVM.i18n(attribute.i18n);
					self.attributeVM.linkedAttributeIds(attribute.linkedAttributeIds || []);
					self.attributeVM.productTypes(attribute.productTypes || []);
                    self.attributeVM.allowNewOptionsViaImport(attribute.allowNewOptionsViaImport);
					self.attributeVM.validationMin(attribute.validationMin || []);
					self.attributeVM.validationMax(attribute.validationMax || []);
					self.attributeVM.validationMinLength(attribute.validationMinLength || []);
					self.attributeVM.validationMaxLength(attribute.validationMaxLength || []);
					self.attributeVM.validationFuture(attribute.validationFuture || []);
					self.attributeVM.validationPast(attribute.validationPast || []);
					self.attributeVM.validationAssertTrue(attribute.validationAssertTrue || []);
					self.attributeVM.validationAssertFalse(attribute.validationAssertFalse || []);
					self.attributeVM.validationPattern(attribute.validationPattern || []);
					self.attributeVM.validationScript(attribute.validationScript || []);
					self.attributeVM.validationMessage(attribute.validationMessage || []);
					
                    if(attribute.code && attribute.code != ''){
                        self.attributeVM.isCodeEditable(false)
                    }
                    
            		attrAPI.getAttributeTargetObjects().then(function(data) {
                		var targetObj = _.findWhere(data.data.attributeTargetObjects, { id : self.attributeVM.targetObjectId() });
                		self.attributeVM.targetObject(targetObj);
            		});

			        //--------------------------------------------------------
			        // Product list filter tab
			        //--------------------------------------------------------
					self.attributeVM.productListFilterType(attribute.productListFilterType);
					self.attributeVM.productListFilterIndexFields(attribute.productListFilterIndexFields || []);
					self.attributeVM.productListFilterKeyAlias(attribute.productListFilterKeyAlias);
					self.attributeVM.productListFilterFormatLabel(attribute.productListFilterFormatLabel);
					self.attributeVM.productListFilterFormatValue(attribute.productListFilterFormatValue);
					self.attributeVM.productListFilterParseValue(attribute.productListFilterParseValue);
					self.attributeVM.productListFilterMulti(attribute.productListFilterMulti);
					self.attributeVM.productListFilterInheritFromParent(attribute.productListFilterInheritFromParent);
					self.attributeVM.productListFilterIncludeChildren(attribute.productListFilterIncludeChildren);
					self.attributeVM.productListFilterPosition(attribute.productListFilterPosition);
					
					console.log('LOAD ATTRIBUTE::: ', attribute, self.attributeVM);
					
					gc.app.pageTitle(self.pageTitle());
				});
			}
		}
	};

	return AttributeIndexController;
});
