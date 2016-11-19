define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-attribute' ], function(app, ko, gc, attrAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function AttributeBaseController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof AttributeBaseController)) {
			throw new TypeError("AttributeBaseController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.attributeVM = {};
		this.productAttributes = ko.observableArray();
		this.attributeTargetObjects = ko.observableArray();
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate');
	}

	AttributeBaseController.prototype = {
		constructor : AttributeBaseController,
		saveData : function(context) {
			var self = this;
			
			//----------------------------------------------------
			// Save new attribute
			//----------------------------------------------------
			if(self.attributeVM.id() == 'new') {
				
				var newAttribute  = {};
				newAttribute.targetObjectId = self.attributeVM.targetObjectId();
				newAttribute.type = self.attributeVM.type();
				newAttribute.scopes = self.attributeVM.scopes();
				newAttribute.backendLabel = self.attributeVM.backendLabel();
				newAttribute.frontendLabel = self.attributeVM.frontendLabel();
				newAttribute.code = self.attributeVM.code();
				newAttribute.code2 = self.attributeVM.code2();
				newAttribute.enabled = self.attributeVM.enabled();
				newAttribute.searchable = self.attributeVM.searchable();
				newAttribute.includeInProductListFilter = self.attributeVM.includeInProductListFilter();
                newAttribute.includeInProductListQuery = self.attributeVM.includeInProductListQuery();
				newAttribute.includeInSearchFilter = self.attributeVM.includeInSearchFilter();
				newAttribute.showInProductDetails = self.attributeVM.showInProductDetails();
				newAttribute.productTypes = self.attributeVM.productTypes();
/*				newAttribute.dimensionAttribute = self.attributeVM.dimensionAttribute();*/
				
				if(self.attributeVM.isVirtual()) {
					newAttribute.editable = false;
					newAttribute.inputType = 'READ_ONLY';
					newAttribute.linkedAttributeIds = self.attributeVM.linkedAttributeIds();
				} else {
					newAttribute.editable = self.attributeVM.editable();
					newAttribute.frontendInput = self.attributeVM.frontendInput();
					newAttribute.frontendOutput = self.attributeVM.frontendOutput();
					newAttribute.inputType = self.attributeVM.inputType();
					newAttribute.backendType = self.attributeVM.backendType();
	                newAttribute.allowMultipleValues = self.attributeVM.allowMultipleValues();
					newAttribute.i18n = self.attributeVM.i18n();
					//newAttribute.frontendFormat = self.attributeVM.frontendFormat();
				}
				
				attrAPI.createAttribute(newAttribute).then(function(savedAttribute) {
					self.attributeVM.id(savedAttribute.id);

					//--------------------------------------------------------
			        // General tab
			        //--------------------------------------------------------
					self.attributeVM.targetObjectId(savedAttribute.targetObjectId);
					self.attributeVM.type(savedAttribute.type);
					self.attributeVM.scopes(savedAttribute.scopes);
					self.attributeVM.backendLabel(savedAttribute.backendLabel);
					self.attributeVM.frontendLabel(savedAttribute.frontendLabel);
					self.attributeVM.code(savedAttribute.code);
					self.attributeVM.code2(savedAttribute.code2);
					self.attributeVM.editable(savedAttribute.editable);
					self.attributeVM.enabled(savedAttribute.enabled);
					self.attributeVM.searchable(savedAttribute.searchable);
					self.attributeVM.frontendInput(savedAttribute.frontendInput);
					self.attributeVM.frontendOutput(savedAttribute.frontendOutput);
					self.attributeVM.inputType(savedAttribute.inputType);
					self.attributeVM.backendType(savedAttribute.backendType);
					self.attributeVM.includeInProductListFilter(savedAttribute.includeInProductListFilter);
                    self.attributeVM.includeInProductListQuery(savedAttribute.includeInProductListQuery);
					self.attributeVM.includeInSearchFilter(savedAttribute.includeInSearchFilter);
					self.attributeVM.showInProductDetails(savedAttribute.showInProductDetails);
	                self.attributeVM.allowMultipleValues(savedAttribute.allowMultipleValues);
					self.attributeVM.i18n(savedAttribute.i18n);
					self.attributeVM.linkedAttributeIds(savedAttribute.linkedAttributeIds);
					self.attributeVM.productTypes(savedAttribute.productTypes);
				/*	self.attributeVM.dimensionAttribute(savedAttribute.dimensionAttribute);*/
					self.attributeVM.frontendFormat(savedAttribute.frontendFormat);
                    self.attributeVM.validationMin(savedAttribute.validationMin);
                    self.attributeVM.validationMax(savedAttribute.validationMax);
                    self.attributeVM.validationMinLength(savedAttribute.validationMinLength);
                    self.attributeVM.validationMaxLength(savedAttribute.validationMaxLength);
                    self.attributeVM.validationFuture(savedAttribute.validationFuture);
                    self.attributeVM.validationPast(savedAttribute.validationPast);
                    self.attributeVM.validationAssertTrue(savedAttribute.validationAssertTrue);
                    self.attributeVM.validationAssertFalse(savedAttribute.validationAssertFalse);
                    self.attributeVM.validationPattern(savedAttribute.validationPattern);
                    self.attributeVM.validationScript(savedAttribute.validationScript);
                    self.attributeVM.validationMessage(savedAttribute.validationMessage);

                    if(savedAttribute.code && savedAttribute.code != ''){
                        self.attributeVM.isCodeEditable(false)
                    }
					
					gc.app.channel.publish('attribute.created', savedAttribute);
					context.saved();
				});

			//----------------------------------------------------
			// Update existing attribute
			//----------------------------------------------------
			} else {
				var updateModel = gc.app.newUpdateModel();
				updateModel.field('targetObjectId', self.attributeVM.targetObjectId());
				updateModel.field('type', self.attributeVM.type());
				updateModel.field('scopes', self.attributeVM.scopes());
				updateModel.field('backendLabel', self.attributeVM.backendLabel(), true);
				updateModel.field('frontendLabel', self.attributeVM.frontendLabel(), true);
				updateModel.field('code', self.attributeVM.code());
				updateModel.field('code2', self.attributeVM.code2());
				updateModel.field('enabled', self.attributeVM.enabled());
				updateModel.field('searchable', self.attributeVM.searchable());
				updateModel.field('includeInProductListFilter', self.attributeVM.includeInProductListFilter());
                updateModel.field('includeInProductListQuery', self.attributeVM.includeInProductListQuery());
				updateModel.field('includeInSearchFilter', self.attributeVM.includeInSearchFilter());
				updateModel.field('showInProductDetails', self.attributeVM.showInProductDetails());
/*				updateModel.field('dimensionAttribute', self.attributeVM.dimensionAttribute());*/
				updateModel.field('productTypes', self.attributeVM.productTypes());
				
				if(self.attributeVM.isVirtual()) {
					updateModel.field('editable', false);
					updateModel.field('inputType', 'READ_ONLY');
					updateModel.field('linkedAttributeIds', self.attributeVM.linkedAttributeIds());
				} else {
					updateModel.field('editable', self.attributeVM.editable());
					updateModel.field('frontendInput', self.attributeVM.frontendInput());
					updateModel.field('frontendOutput', self.attributeVM.frontendOutput());
					updateModel.field('inputType', self.attributeVM.inputType());
					updateModel.field('backendType', self.attributeVM.backendType());
					updateModel.field('allowMultipleValues', self.attributeVM.allowMultipleValues());
					updateModel.field('i18n', self.attributeVM.i18n());
					updateModel.field('frontendFormat', self.attributeVM.frontendFormat(), true);
                    updateModel.field('validationMin', self.attributeVM.validationMin(), true);
                    updateModel.field('validationMax', self.attributeVM.validationMax(), true);
                    updateModel.field('validationMinLength', self.attributeVM.validationMinLength(), true);
                    updateModel.field('validationMaxLength', self.attributeVM.validationMaxLength(), true);
                    updateModel.field('validationFuture', self.attributeVM.validationFuture(), true);
                    updateModel.field('validationPast', self.attributeVM.validationPast(), true);
                    updateModel.field('validationAssertTrue', self.attributeVM.validationAssertTrue(), true);
                    updateModel.field('validationAssertFalse', self.attributeVM.validationAssertFalse(), true);
                    updateModel.field('validationPattern', self.attributeVM.validationPattern(), true);
                    updateModel.field('validationScript', self.attributeVM.validationScript(), true);
                    updateModel.field('validationMessage', self.attributeVM.validationMessage(), true);
                    
                    console.log('UPDATE ATTRIBUTE!!!!! ', updateModel);
				}
				
				attrAPI.updateAttribute(self.attributeVM.id(), updateModel).then(function(data) {
                    self.attributeVM.code(data.data.attribute.code);
                    if(data.data.attribute.code && data.data.attribute.code != ''){
                        self.attributeVM.isCodeEditable(false)
                    }
                    context.saved();
				});
			}
		},
		activate : function(attributeId) {
			var self = this;
			
			self.attributeVM = gc.app.sessionGet('attributeVM');
			
			console.log('~~~~~~~~~~~~~~~~~ attributeVM: ', self.attributeVM);
			
			var reduced = gc.ctxobj.reduce(gc.app.dataGet('productAttributes'), 'id', 'backendLabel', gc.app.currentLang(), true, { backendLabel: 'text' });
			self.productAttributes(_.sortBy(reduced, "text"));
			
			return attrAPI.getAttributeTargetObjects().then(function(data) {
				var reduced = gc.ctxobj.reduce(data.data.attributeTargetObjects, 'id', 'name', gc.app.currentLang(), true, { name: 'text' });
				self.attributeTargetObjects(_.sortBy(reduced, "text"));
    		});
		},
        attached : function(view, parent) {
            var self = this;
            
            $('#attributeBaseForm').addClass('save-button-listen-area');
            
            gc.app.onSaveEvent(function(context) {
                var id = $('.tab-content>.active').attr('id');
               
                if(id == 'attr_base') {
                    self.saveData(context);
                }
            });
        },
        detached : function() {
            var self = this;
            gc.app.clearSaveEvent();
        }
	};

	return AttributeBaseController;
});