define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute', 'gc-pictogram', 'gc-media-asset' ], function(app, ko, gc, productAPI, attrAPI, pictogramAPI, mediaAssetAPI) {

	function ProductAttributeValueVM(formAttributeValues, product, valueId, attributeId, backendLabel, frontendLabel, code, code2, value, optOut, editable, enabled, inputType, frontendInput, isOptionAttribute, allowMultipleValues, i18n, options, inputConditions, productTypes, scopes) {
		var self = this;
		
		self.formAttributeValues = formAttributeValues || [];
		self.product = product;
		self.valueId = valueId;
		self.attributeId = attributeId;
		self.backendLabel = backendLabel;
		self.frontendLabel = frontendLabel;
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

        self.getSuggestions = function(searchTerm, sourceArray) {
            function Suggestion(suggestion) {
                this.val = ko.observable(suggestion);
            }
            productAPI.getAttributeSuggestions(self.attributeId, gc.app.currentLang(), searchTerm).then(function(data){
                var terms = [];
                _.each(data.data.results, function(val) {
                    terms.push(new Suggestion(val));
                });
                sourceArray(terms);

            })
        };

		self.format =  function(state) {
			console.log(state);
			var originalOption = state.element;

			var $state = $(
				'<span><img src="' + state.element.value.toLowerCase() + '.png" class="img-flag" /> ' + state.text + '</span>'
			);
			return $state;
		}

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

		self.isShowField = ko.computed(function() {
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
							if(!_.isEmpty(formAttrValue.value())) {
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
							var matchingOptionIds = _.intersection(attrConditionOptions, prodAttr.optionIds);

							isValid = matchingOptionIds.length > 0;
						}
					}
				});
			}
			
			return isValid;
		}, self);
		
		self.selectOptions = ko.computed(function() {
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
							var matchingOptionIds = _.intersection(attrConditionOptions, prodAttr.optionIds);

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
	    });
	}

	function AttributePosVM(attrId) {
		var self = this;

		self.attr_id = attrId;
		self.x = ko.observable();
		self.y = ko.observable();
		self.value = ko.observable();
		self.unit = ko.observable();
		self.label = ko.observable();
		self.backendLabel = ko.observable();

		self.labelPreview =  ko.computed(function() {
			var val = self.value() ? self.value(): '123';
			var unit = self.unit() ? self.unit(): 'cm';

			return self.backendLabel() + '<br/>' + val + ' ' + unit;
		}, self);

		self.showOnImage =  ko.computed(function() {
			if(self.x() > 0 && self.y() > 0)
				return true;
			return false;
		}, self);

		self.top =  ko.computed(function() {
			return self.y() + 'px';
		}, self);

		self.left =  ko.computed(function() {
			return self.x() + 'px';
		}, self);

	}
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductPictogramController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductPictogramController)) {
			throw new TypeError("ProductPictogramController constructor cannot be called as a function.");
		}

        var self = this;
		this.app = gc.app;
		this.gc = gc;
		this.productId = undefined;
		this.product = {};
		this.attributeInputConditions = [];

		this.formAttributeValues = ko.observableArray([]);
		this.pictogramOptions = ko.observableArray([]);
		this.pictogram = ko.observable();
		this.pictogramValue = ko.observable('');
		this.pictogramImage = ko.observable();

		this.pictogramImageUrl = ko.observable();

		this.previewTemplate = ko.observable(true);

		this.pictogramUrl = ko.computed(function() {
			if(self.pictogramValue() && self.pictogramValue() != ''){
				var id = self.pictogramValue();
				var pctg = _.findWhere(self.pictogramOptions(), { id: id});
				if(pctg)
					return pctg.url;
			}
			return "";
		});

		this.pictogramTemplateUrl = ko.computed(function() {
			if(!self.previewTemplate())
				return "";

			if(self.pictogramValue() && self.pictogramValue() != ''){
				var id = self.pictogramValue();
				var pctg = _.findWhere(self.pictogramOptions(), { id: id});
				if(pctg)
					return pctg.template;
			}
			return "";
		});


		self.subscriptionPictogramValue = self.pictogramValue.subscribe(function(newValue) {
			if(self.pictogramValue() && newValue != ''){
				$toolbar = $("#productPictogramForm").closest('form').find('.toolbar-trigger').first();
				// Make sure that the save/cancel toolbar sees the change.
				$toolbar.click();
				$toolbar.trigger('change');
			};

		});

		self.selectedDimensionAttributePos =  ko.computed(function() {
			var positions = [];

			if(self.pictogramValue() && self.pictogramValue() != '') {
				var id = self.pictogramValue();
				var pctg = _.findWhere(self.pictogramOptions(), { id: id});
				if (pctg)
				{
					_.each(pctg.attributes, function(attrId) {
						var aVM = _.findWhere(pctg.pos, { attributeId: attrId});

						if(aVM) {
							var label = _.findWhere(self.formAttributeValues(), {attributeId: attrId});

						//	var value = label.value();
							var value = gc.ctxobj.val(label.value(), gc.app.currentUserLang(), 'closest')


							nVM = new AttributePosVM(attrId);
							nVM.x(aVM.x);
							nVM.y(aVM.y);

							nVM.unit(gc.ctxobj.val(pctg.unit, gc.app.currentUserLang(), 'closest'));
							nVM.value(value);

							if (label) {
								nVM.label(gc.ctxobj.val(label.frontendLabel, gc.app.currentUserLang(), 'closest'));
								nVM.backendLabel(gc.ctxobj.val(label.backendLabel, gc.app.currentUserLang(), 'closest'));
							}

							positions.push(nVM);
						}
					})
				}
			}
			return positions;
		}, self);




		this.selectedDimensionAttributeValues =  ko.computed(function() {
			if(self.pictogramValue() && self.pictogramValue() != ''){
				var id = self.pictogramValue();
				var pctg = _.findWhere(self.pictogramOptions(), { id: id});
				var result = [];
				if(pctg && pctg.attributes) {
					_.each(pctg.attributes, function(attrId) {
						var attrValue = _.findWhere(self.formAttributeValues(), { attributeId: attrId});
						result.push(attrValue);
					});
					return result;
				}
			}
			return [];
		}, self);


		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

	ProductPictogramController.prototype = {
		constructor : ProductPictogramController,
		format: function(pictogram) {

			var $pictogram= $(
				'<img src="' + pictogram.text + '" /> '
			);
			return $pictogram;
		},
		activate : function(activateData) {
			var self = this;

			self.product = gc.app.sessionKGet('productVM');
			self.productId = activateData;

			$(document).on('click', '#tab-prd-details-pictogram', function() {

				pictogramAPI.getPictograms().then(function (data) {

					var prdGroupId;
					if (self.product().productGroup())
						prdGroupId = self.product().productGroup().id;

					var pictogramOpts = [];
					pictogramOpts.push({id: '', url: '', attributes: []})
					_.each(data.data.pictograms, function (pictogram) {

						if (!pictogram.productGroupIds || pictogram.productGroupIds.indexOf(prdGroupId) >= 0) {
							if (pictogram.pictogram && pictogram.template)
								pictogramOpts.push({
									id: pictogram.id,
									url: pictogram.pictogram.webThumbnailPath,
									pos: pictogram.dimensionPositions,
									template: pictogram.template.url,
									attributes: pictogram.dimensionAttributeIds,
									unit: pictogram.unit
								})
						}

					});
					self.pictogramOptions(pictogramOpts);

					return attrAPI.getInputConditionsFor({fields: ['whenAttributeId', 'hasOptionIds', 'showAttributeId', 'showOptionsHavingTag', 'applyToProductTypes']}).
						then(function (response) {
							self.attributeInputConditions = response.data.attributeInputConditions;
						}).then(function (data) {

							return attrAPI.getAttributes({fields: ['code', 'code2', 'backendLabel', 'frontendLabel', 'editable', 'enabled', 'inputType', 'frontendInput', 'optionAttribute', 'allowMultipleValues', 'i18n', 'options', 'tags', 'label', 'productTypes', 'scopes', 'dimensionAttribute']}).then(function (response) {
								var attributes = response.data.attributes;

								// The attributes come unsorted, so we make sure that we restore the tab-mapping-order again.
								/*							var sortedAttributes = [];
								 _.each(attributeIds, function(attrId) {
								 var foundAttr = _.findWhere(attributes, { id : attrId } );
								 if(!_.isEmpty(foundAttr)) {
								 sortedAttributes.push(foundAttr);
								 }
								 });*/

								var dimAttrs = [];

								var product = ko.gc.unwrap(self.product);

								_.each(attributes/*sortedAttributes*/, function (attr) {

									var foundPrdAttr = _.findWhere(product.data.attributes, {attributeId: attr.id});
									var inputConditions = _.where(self.attributeInputConditions, {showAttributeId: attr.id});

									var attrOptions = attr.options;
									gc.ctxobj.enhance(attrOptions, [ 'label' ],'any');

									var attrVM = null;

									if (!_.isUndefined(attr) && !_.isUndefined(foundPrdAttr)) {

										attrVM = new ProductAttributeValueVM(
											self.formAttributeValues,
											self.product,
											foundPrdAttr.id,
											foundPrdAttr.attributeId,
											foundPrdAttr.attribute.backendLabel,
											foundPrdAttr.attribute.frontendLabel,
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
											attr.scopes);
									} else if (!_.isUndefined(attr)) {
										attrVM = new ProductAttributeValueVM(
											self.formAttributeValues,
											self.product,
											undefined,
											attr.id,
											attr.backendLabel,
											attr.frontendLabel,
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
											attr.scopes);
									}

									if (attr.dimensionAttribute) {
										dimAttrs.push(attrVM);
									}

									if (attrVM.code == 'pictogram') {
										self.pictogram(attrVM);
									}

									if (attrVM.code == 'pictogram_image') {

										var imageId = gc.ctxobj.val(attrVM.value(), self.app.currentUserLang(), 'any');
										self.pictogramImage(attrVM);
										if (imageId)
											mediaAssetAPI.getMediaAsset(imageId).then(function (data) {
												console.log('11111111111111111111111111');
												self.previewTemplate(false);
												self.pictogramImageUrl(data.url);
											});
									}

								});
								self.formAttributeValues(dimAttrs);
								if (self.pictogram() && gc.ctxobj.val(self.pictogram().value(), self.app.currentUserLang(), 'any')) {
									self.pictogramValue(gc.ctxobj.val(self.pictogram().value(), self.app.currentUserLang(), 'any'));
								}
							})
						});


				})
			});
		},
		attached : function() {
			var self = this;

		},
		saveData : function(view, parent, toolbar) {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();
			
			var formAttributes = ko.gc.unwrap(self.selectedDimensionAttributeValues());
			
			_.each(formAttributes, function(attrVal) {
				
				if(attrVal.isEditable ) {
					var isOptOut = gc.ctxobj.plain(attrVal.optOut);
					
					if(!_.isUndefined(isOptOut)) {
						updateModel.optOut(attrVal.code, attrVal.optOut);
					}
						
					if(attrVal.isOption) {
               			var foundPrdAttr = _.findWhere(self.product().data().attributes, { attributeId : attrVal.attributeId });
					
						// Allow resetting of value if product already has attribute and new value is empty.
						if((!_.isUndefined(attrVal.value) && !_.isEmpty(attrVal.value)) || !_.isUndefined(foundPrdAttr)) {
							updateModel.options(attrVal.code, attrVal.value);
						}
					} else {
						updateModel.attr(attrVal.code, attrVal.value);
						console.log(attrVal.value)
					}
				}
			});

			pictogramAPI.generateTemplate(self.pictogramValue(), updateModel).then(function(data){
				self.pictogramImageUrl(data.data.mediaAsset.url);

				updateModel.attr(self.pictogram().code, [{val: "" + self.pictogramValue()}]);
				updateModel.attr(self.pictogramImage().code, [{val: "" + data.data.mediaAsset.id}]);

				productAPI.updateProduct(self.productId, updateModel).then(function() {
					toolbar.hide();
					gc.app.channel.publish('product.changed', self.productId);
				});
			})


		},
		attached : function(view, parent) {

		},
		detached : function() {
			var self = this;
			console.log('!!! DETACNI');
			$(document).off('click', '#tab-prd-details-media');
		}
	}
	
	return ProductPictogramController;
});