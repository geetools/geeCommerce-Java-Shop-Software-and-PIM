define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute', 'gc-attribute-tabs' ], function(app, ko, gc, productAPI, attrAPI, attrTabAPI) {

	function ProductAttributeValueVM(formAttributeValues, product, valueId, attributeId, backendLabel, code, code2, value, optOut, editable, enabled, inputType, frontendInput, isOptionAttribute, allowMultipleValues, i18n, options, inputConditions, productTypes, scopes) {
		var self = this;
		
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
		self.inputConditions = inputConditions;
		self.productTypes = productTypes;
        self.suggestions = ko.observableArray([]);
		self.srcOptions = ko.observableArray([]);

/*		
		self.comboboxValue = ko.observableArray([]);
		self.comboboxValueId = ko.observable();

        self.getSuggestions = function(searchTerm, sourceArray) {
            function Suggestion(suggestion) {
                this.val = ko.observable(suggestion);
            }
			attrAPI.getAttributeSuggestions(self.attributeId, gc.app.currentLang(), searchTerm, "products").then(function(data){
				var terms = [];
				_.each(data.data.results, function(val) {
					terms.push(new Suggestion(val));
				});
				sourceArray(terms);
			});
        };

		self.removeOption = function(data) {
			self.value.remove(data.id);
			var toolbar = $('div.info-toolbar-outer');
			$(toolbar).attr('data-init', '1');
			$(toolbar).find('div.loader').hide();
			$(toolbar).find('div.buttons').show();
			$(toolbar).fadeIn(600);
		}

		//Combobox Options Part
		self.addOptionKey13 = function(d, e) {
			if (e.keyCode === 13) {
				e.currentTarget.blur();
				self.addOption();
			}
			return true;
		}

		self.addOption = function() {
			if (self.comboboxValueId()) {
				var option = _.findWhere(self.options, {id: self.comboboxValueId()});
				
				if(!_.isUndefined(option)) {
					self.value.push(self.comboboxValueId());
					self.comboboxValue([]);
				} else {
					attrAPI.getAttributeOption(self.attributeId, self.comboboxValueId()).then(function(data) {
						gc.ctxobj.enhance(data, [ 'label' ], 'any');
						self.options.push(data);

						self.value.push(self.comboboxValueId());
						self.comboboxValue([]);
					});
				}
				
				return;
			}
			
			var newAttributeOptions = {
				attributeId: self.attributeId,
				label: self.comboboxValue(),
				tags: [],
				position: 0
			};

			self.comboboxValue([]);

			if(!_.isEmpty(newAttributeOptions)) {
				attrAPI.createOption(self.attributeId, newAttributeOptions).then(function(data) {
					gc.ctxobj.enhance(data, [ 'label' ], 'any');
					if(!self.options)
						self.options = []
					self.options.push(data);
					self.value.push(data.id);
				});
			}
		};

		self.getOptions = function(searchTerm, sourceArray) {
			function Option(id, label) {
				this.id = ko.observable(id);
				this.label = ko.observable(label);
			}
			
			var terms = [];
			attrAPI.findAttributeOptions(self.attributeId, searchTerm, gc.app.currentLang(), 20).then(function(data) {

				if(!_.isEmpty(data.data.options)) {
					_.each(data.data.options, function(option) {
						gc.ctxobj.enhance(option, [ 'label' ], 'any');
						
						if (option.id && option.label && option.label.i18n) {
							terms.push(new Option(option.id, option.label.i18n));
						}
					});
				
					sourceArray(terms);
				}
			});
		};

		self.comboboxValues = ko.observableArray();

		self.computeComboboxValues = ko.computed(function() {
			var val = self.value();
			var _options = [];

			if(_.isEmpty(self.options))
				return _options;

			if(!_.isArray(val)) {
				val = [val];
			}

//console.log('____________ COMBO-option__________ ',  self.code, val, self.options);

			_.each(val, function (optionId) {
				var option = _.findWhere(self.options, {id: optionId});
				
				if(!_.isUndefined(option) && !_.isUndefined(option.label)) {
					_options.push({ id: option.id, text: option.label.i18n });
				} else {
					attrAPI.getAttributeOption(self.attributeId, optionId).then(function(data) {
						gc.ctxobj.enhance(data, [ 'label' ], 'any');
						self.options.push(data);
//console.log('____________ COMBO-DATAAAAA __________ ',  self.code, data);
						if(!_.isEmpty(data) && data.label) {
							_options.push({ id: data.id, text: data.label.i18n });
							self.comboboxValues(_options);
						}
					});
				}
			});

//console.log('____________ COMBO-option__________ ',  self.code, _options);

			self.comboboxValues(_options);

//			return _options;

		});
*/
		
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

		self.isOptOut = ko.computed(function() {
			var optOut = self.optOut();
			var ctxVal = gc.ctxobj.closest(optOut);
			
			return _.isUndefined(ctxVal) ? false : ctxVal;
		}, self);

		self.isShowField = ko.pureComputed(function() {
			var product = self.product().data();
			
			// Product can only be empty during initialization, so we just return false.
			// Also, editing dyn-attributes only makes sense if there is a product-type.
			if(_.isUndefined(product) || _.isEmpty(product.type)) {
				return false;
			}
			
			if(!_.isEmpty(self.productTypes) && self.productTypes.indexOf(product.type) == -1) {
				return false;
			}
		
			var inputConditions = _.where( self.inputConditions, { showAttributeId : self.attributeId } );

			var isValid = true;

			// Some input conditions only apply to certain product-types, so we filter the others out
			// first if needed.
			var filteredInputConditions = [];
			if(!_.isEmpty(inputConditions)) {
				_.each(inputConditions, function(inputCondition) {
					if(_.isEmpty(inputCondition.applyToProductTypes) || inputCondition.applyToProductTypes.indexOf(product.type) != -1) {
						filteredInputConditions.push(inputCondition);
					} else {
						// Product types have been assigned, but this product does not have the right one.
						isValid = false;
					}
				});
			}

			// Don't bother continuing if this attribute has been marked as invalid during filtering.
			if(!isValid) {
				return false;
			// No input conditions exist, so we use them as they are.
			} else if(_.isEmpty(filteredInputConditions)) {
				return true;
			} else {
				// Get a unique list of attributes to check.
				var whenAttributeIds = _.uniq(_.pluck(filteredInputConditions, 'whenAttributeId'));

				// Check all conditions belonging to this attribute.
				_.each(whenAttributeIds, function(whenAttributeId) {

					// If one of the attributes fail, no need to check anymore.
					if(isValid) {
						var formAttrValue = _.findWhere( self.formAttributeValues(), { attributeId : whenAttributeId } );
						var prodAttr = undefined; // _.findWhere( product.attributes, { attributeId : whenAttributeId } );
						
						if(formAttrValue && formAttrValue.attributeId) {
							if(!_.isUndefined(formAttrValue.value())) {
								prodAttr = {
									attributeId : formAttrValue.attributeId,
									optionIds : formAttrValue.value()
								}
							} else {
								if(self.isOption) {
									self.value([]);
								} else {
									self.value('');
								}
							}
						} else {
							prodAttr = _.findWhere( product.attributes, { attributeId : whenAttributeId } );
						}

						// If product does not have the attribute, then fail. Field is not valid for this product.
						if(_.isUndefined(prodAttr)) {
							isValid = false;
						}

						// Get conditions set up for this attribute (whenAttributeId).
						var attrConditions = _.where( filteredInputConditions, { whenAttributeId : whenAttributeId } );
						// Also grab hold of the options when we have them. 
						// (Idea is this: whenAttributeId -> hasOptionIds -> then show this attribute in panel).
						var attrConditionOptions = _.pluck( attrConditions, 'hasOptionIds' );
						
						// Get unique list of optionIds where te product must fulfil at least one.
						if(!_.isEmpty(attrConditionOptions)) {
							attrConditionOptions = _.uniq(_.flatten(attrConditionOptions));
						}

						// If optionIds exist in condition (whenAttribute), then check those too.
						if(isValid && !_.isEmpty(attrConditionOptions)) {
						
							var optionIds = _.isArray(prodAttr.optionIds) ? prodAttr.optionIds : [prodAttr.optionIds];
						
							var matchingOptionIds = _.intersection(attrConditionOptions, optionIds);

							isValid = matchingOptionIds.length > 0;
						}
					}
				});
			}
			
			return isValid;
		}, self).extend({ rateLimit: 0 });

		self.selectOptions = ko.pureComputed(function() {
			var product = self.product().data();
			
			// Product can only be empty during initialization, so we just return false.
			// Also, editing dyn-attributes only makes sense if there is a product-type.
			if(_.isUndefined(product) || _.isEmpty(product.type)) {
				return [];
			}
		
			var inputConditions = _.where( self.inputConditions, { showAttributeId : self.attributeId } );
			
			// Some input conditions only apply to certain product-types, so we filter the others out
			// first if needed.
			var filteredInputConditions = [];
			if(!_.isEmpty(inputConditions)) {
				_.each(inputConditions, function(inputCondition) {
					if(_.isEmpty(inputCondition.applyToProductTypes) || inputCondition.applyToProductTypes.indexOf(product.type) != -1) {
						filteredInputConditions.push(inputCondition);
					}
				});
			}

			// No input conditions exist, so we use them as they are.
			if(_.isEmpty(filteredInputConditions)) {
				var _options = [];
				
				if(_.isEmpty(self.options))
					return _options;

                _options.push( { id : '', text : function() {
                    return gc.app.i18n('app:common.choose', {}, gc.app.currentLang);
                }});

				_.each(self.options, function(option) {
					if(!_.isUndefined(option.label)) {
						_options.push({ id: option.id, text: option.label.i18n });
					}
				});
			} else {
				var isValid = true;
				
				// Get a unique list of attributes to check.
				var whenAttributeIds = _.uniq(_.pluck(filteredInputConditions, 'whenAttributeId'));
				var optionTags = [];
				
				// Check all conditions belonging to this attribute.
				_.each(whenAttributeIds, function(whenAttributeId) {

					// If one of the attributes fail, no need to check anymore.
					if(isValid) {
						// First we check if the attributeId exists in the product.
						var formAttrValue = _.findWhere( self.formAttributeValues(), { attributeId : whenAttributeId } );
						var prodAttr = undefined; // _.findWhere( product.attributes, { attributeId : whenAttributeId } );
						
						if(formAttrValue && formAttrValue.attributeId) {
							if(!_.isEmpty(formAttrValue.value())) {
								prodAttr = {
									attributeId : formAttrValue.attributeId,
									optionIds : formAttrValue.value()
								}
							}
						} else {
							prodAttr = _.findWhere( product.attributes, { attributeId : whenAttributeId } );
						}

						// If product does not have the attribute, then fail. Dropdown is not valid for this product.
						if(_.isUndefined(prodAttr)) {
							isValid = false;
						}

						// Get conditions set up for this attribute (whenAttributeId).
						var attrConditions = _.where( filteredInputConditions, { whenAttributeId : whenAttributeId } );
						// Also grab hold of the options when we have them. 
						// (Idea is this: whenAttributeId -> hasOptionIds -> then show this attribute in panel).
						var attrConditionOptions = _.pluck( attrConditions, 'hasOptionIds' );
						
						// Get unique list of optionIds where te product must fulfil at least one.
						if(!_.isEmpty(attrConditionOptions)) {
							attrConditionOptions = _.uniq(_.flatten(attrConditionOptions));
						}

						// If optionIds exist in condition (whenAttribute), then check those too.
						if(isValid && !_.isEmpty(attrConditionOptions)) {
							var optionIds = _.isArray(prodAttr.optionIds) ? prodAttr.optionIds : [ prodAttr.optionIds ];
						
							var matchingOptionIds = _.intersection(attrConditionOptions, optionIds);

							isValid = matchingOptionIds.length > 0;
							
							var matchingAttrConditions = _.filter(attrConditions, function(attrCondition) {
								var match = _.intersection(attrCondition.hasOptionIds, matchingOptionIds);
								return  match.length > 0;
							});

							// If we have matching conditions, then extract the option-tags should they exist.
							if(!_.isEmpty(matchingAttrConditions)) {
								optionTags = _.pluck(matchingAttrConditions, 'showOptionsHavingTag');
							}
						}
					}
				});
				
				if(!_.isEmpty(optionTags) && optionTags.length == 1 && _.isEmpty(optionTags[0])) {
					optionTags = [];
				}
				
				//  If product has dependent attribute (and option exists), 
				// then we can start building the options list for the dropdown.
				if(isValid) {
					var _options = [];
                    _options.push( { id : '', text : function() {
                        return gc.app.i18n('app:common.choose', {}, gc.app.currentLang);
                    }});
					
					_.each(self.options, function(option) {
						// If we only want to show options with a particular tag, 
						// then we need to find out if this option has that tag.
						var matchingTags = _.intersection(optionTags, option.tags);
						
						if((!_.isEmpty(optionTags) && matchingTags.length > 0) || _.isEmpty(optionTags) || _.isEmpty(optionTags)) {
							if(_.isUndefined(option.label)) {
								_options.push({id: option.id, text: '--- ??? ---'});
							} else {
								_options.push({id: option.id, text: option.label.i18n});
							}
						}
					})
				}
			}
			
	        return _options || [];
	    }).extend({ rateLimit: 0 });
	}
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductDynAttributesController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductDynAttributesController)) {
			throw new TypeError("ProductDynAttributesController constructor cannot be called as a function.");
		}

        var self = this;
		this.app = gc.app;
		this.gc = gc;
		this.productId = undefined;
		this.product = {};
		this.attributeTab = {};
		this.attributeInputConditions = [];
		this.tabAttributes = [];
		this.formAttributeValues = ko.observableArray([]);

		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate');
	}

	ProductDynAttributesController.prototype = {
		constructor : ProductDynAttributesController,
		activate : function(activateData) {
			var self = this;

			self.product = gc.app.sessionKGet('productVM');
			self.productId = activateData.productId;
			self.attributeTab = ko.unwrap(activateData.attributeTab);

			return attrTabAPI.getAttributeTabMapping(self.attributeTab.id()).then(function(data) {
				
				var attributeTabMappings = data.data.attributeTabMappings;
				
            	var attributeIds = _.pluck(attributeTabMappings, 'attributeId');
            	
            	if(!_.isEmpty(attributeIds)) {
                	return attrAPI.getInputConditionsFor( { fields : [ 'whenAttributeId', 'hasOptionIds', 'showAttributeId', 'showOptionsHavingTag', 'applyToProductTypes' ], filter: { showAttributeId : attributeIds.join() } } ).then(function( response ) {
                		self.attributeInputConditions = response.data.attributeInputConditions;
                	}).then(function(data) {
                		
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
	                			var inputConditions = _.where(self.attributeInputConditions, { showAttributeId : attr.id });
	                			
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
	                						inputConditions,
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
	                						inputConditions,
	                						attr.productTypes,
	                						attr.scopes));
	                			}
	                		});

							self.formAttributeValues(formAttributeValuesArray);

							// Also pass the form fields to the product for the central save function.
                            product.formAttributeValues.push(formAttributeValuesArray);
	                	})
                	});
            	}
			});
		},
		attached : function(view, parent) {
		    var self = this;
		    
			var tabbedPaneContent = $(parent);
			var tabbedPaneContentId = tabbedPaneContent.attr('id');
			var tabId = tabbedPaneContentId.replace('content', 'link');
			
			$('#' + tabbedPaneContentId).addClass('save-button-listen-area');
		}
	}
	
	return ProductDynAttributesController;
});