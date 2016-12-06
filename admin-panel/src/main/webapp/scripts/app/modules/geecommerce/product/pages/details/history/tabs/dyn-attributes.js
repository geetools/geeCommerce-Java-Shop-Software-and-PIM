define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute', 'gc-attribute-tabs' ], function(app, ko, gc, productAPI, attrAPI, attrTabAPI) {

	function ProductAttributeValueVM(formAttributeValues, product, valueId, attributeId, backendLabel, code, code2, value, optOut, editable, enabled, inputType, frontendInput, isOptionAttribute, allowMultipleValues, i18n, options, productTypes, scopes) {
		var self = this;
		
		self.hasValue = !_.isEmpty(value);
		self.formAttributeValues = formAttributeValues || [];
		self.product = product;
		self.valueId = valueId;
		self.attributeId = attributeId;
		self.backendLabel = backendLabel;
		self.code = code;
		self.code2 = code2;
		self.value = ko.observableArray(value);
		self.optOut = ko.observableArray(optOut);
		self.scopes = ko.observableArray(scopes);
		self.isEditable = editable; 
		self.isEnabled = enabled;
		self.inputType = inputType;
		self.frontendInput = frontendInput;
		self.isOption = isOptionAttribute;
		self.isMultiple = allowMultipleValues;
		self.isI18n = i18n;
		self.options = options;
		self.hasChanged = false;
		self.productTypes = productTypes;
        self.suggestions = ko.observableArray([]);
		self.srcOptions = ko.observableArray([]);

		// Callback for widget i18nEditor.
		self.unjsonDescriptionPanels = function(data) {
			var asJson = null;
			var asText = '';
			
			try
			{
				asJson = JSON.parse(data);
			}
			catch(e)
			{
			    // exeption
			}
			 
			if(asJson === null) {
				asText = data;
			} else {
				_.each(asJson, function(row) {
					asText += row.title + row.body;
				});
			}
			
			return asText;
		};
	}
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductHistoryDynAttributesController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductHistoryDynAttributesController)) {
			throw new TypeError("ProductHistoryDynAttributesController constructor cannot be called as a function.");
		}

        var self = this;
		this.app = gc.app;
		this.gc = gc;
		this.productId = undefined;
		this.product = {};
		this.attributeTab = {};
		this.tabAttributes = [];
		this.formAttributeValues = ko.observableArray([]);

		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate');
	}

	ProductHistoryDynAttributesController.prototype = {
		constructor : ProductHistoryDynAttributesController,
		activate : function(activateData) {
			var self = this;

			self.product = gc.app.sessionKGet('historyProductVM');
			self.productId = activateData.productId;
			self.attributeTab = ko.unwrap(activateData.attributeTab);

console.log('????????????????? ', ko.toJS(self.product), self.productId, self.attributeTab);


			return attrTabAPI.getAttributeTabMapping(self.attributeTab.id()).then(function(data) {
				
				var attributeTabMappings = data.data.attributeTabMappings;
				
            	var attributeIds = _.pluck(attributeTabMappings, 'attributeId');
            	
            	if(!_.isEmpty(attributeIds)) {
	                	return attrAPI.getAttributes('product', { fields : [ 'code', 'code2', 'backendLabel', 'editable', 'enabled', 'inputType', 'frontendInput', 'optionAttribute', 'allowMultipleValues', 'i18n', 'options', 'tags', 'label', 'productTypes', 'scopes' ], filter: { id : attributeIds.join() } } ).then(function( response ) {
	                		var attributes = response.data.attributes;

	                		// The attributes come unsorted, so we make sure that we restore the tab-mapping-order again.
	                		var sortedAttributes = [];
	                		_.each(attributeIds, function(attrId) {
	                			var foundAttr = _.findWhere(attributes, { id : attrId } );
	                			if(!_.isEmpty(foundAttr)) {
		                			sortedAttributes.push(foundAttr);
	                			}
	                		});
	                		
	                		var product = self.product().unwrap();

							var formAttributeValuesArray = [];
	                		
	                		_.each(sortedAttributes, function(attr) {
	                			
	                			var foundPrdAttr = _.findWhere(product.data.attributes, { attributeId : attr.id });
	                			
	                			var attrOptions = attr.options;
	                			gc.ctxobj.enhance(attrOptions, [ 'label' ], 'any');
	                			
	                			if(!_.isUndefined(attr) && !_.isUndefined(foundPrdAttr)) {
									formAttributeValuesArray.push(new ProductAttributeValueVM(
			                				self.formAttributeValues,
	                						self.product,
	                						foundPrdAttr.id, 
	                						foundPrdAttr.attributeId, 
	                						foundPrdAttr.attribute.backendLabel, 
	                						foundPrdAttr.attribute.code, 
	                						foundPrdAttr.attribute.code2,
	                						attr.optionAttribute ? foundPrdAttr.optionIds : foundPrdAttr.value,
	                						foundPrdAttr.optOut,
	                						attr.editable,
	                						attr.enabled,
	                						attr.inputType,
	                						attr.frontendInput,
	                						attr.optionAttribute,
	                						attr.allowMultipleValues,
	                						attr.i18n,
	                						attrOptions,
	                						attr.productTypes,
	                						attr.scopes));
	                			} else if(!_.isUndefined(attr)) {
									formAttributeValuesArray.push(new ProductAttributeValueVM(
			                				self.formAttributeValues,
	                						self.product,
	                						undefined, 
	                						attr.id, 
	                						attr.backendLabel, 
	                						attr.code, 
	                						attr.code2, 
	                						[],
	                						[],
	                						attr.editable,
	                						attr.enabled,
	                						attr.inputType,
	                						attr.frontendInput,
	                						attr.optionAttribute,
	                						attr.allowMultipleValues,
	                						attr.i18n,
	                						attrOptions,
	                						attr.productTypes,
	                						attr.scopes));
	                			}
	                		});

							console.log('___formAttributeValuesArray___ ', formAttributeValuesArray);

							self.formAttributeValues(formAttributeValuesArray);
	                	})
            	}
			});
			
			
		},
		attached : function(view, parent) {
			var tabbedPaneContent = $(parent);
			var tabbedPaneContentId = tabbedPaneContent.attr('id');
			var tabId = tabbedPaneContentId.replace('content', 'link');
			
			$('#' + tabId).click(function() {
				if(tabId == 'tab-link-4336136636210190' 
					|| tabId == 'tab-link-4341127984210100'
					|| tabId == 'tab-link-4336136636210155'
					|| tabId == 'tab-link-4336136636210189'
					) {
					$('#header-store-pills').hide();
				} else {
					$('#header-store-pills').show();
				}
			});
		}
	}
	
	return ProductHistoryDynAttributesController;
});