define([ 'knockout', 'gc/gc' , 'gc-attribute'], function(ko, gc, attrAPI) {

	function AttributeValueVM(formAttributeValues, vm, valueId, attributeId, backendLabel, code, code2, value, optOut,
							  editable, enabled, inputType, frontendInput, isOptionAttribute, allowMultipleValues, i18n,
							  options, inputConditions, productTypes, scopes, attributeTabId, collection) {
		var self = this;

		self.formAttributeValues = formAttributeValues || [];
		self.vm = vm;
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
		self.attributeTabId = attributeTabId;
		self.collection = collection;

		self.comboboxValue = ko.observableArray([]);
		self.comboboxValueId = ko.observable();

		self.getSuggestions = function(searchTerm, sourceArray) {
			function Suggestion(suggestion) {
				this.val = ko.observable(suggestion);
			}
			attrAPI.getAttributeSuggestions(self.attributeId, gc.app.currentLang(), searchTerm, self.collection).then(function(data){
				var terms = [];
				_.each(data.data.results, function(val) {
					terms.push(new Suggestion(val));
				});
				sourceArray(terms);
			})
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

//					console.log('OPTION FOUND !!!!! ', option);
				} else {
					attrAPI.getAttributeOption(self.attributeId, self.comboboxValueId()).then(function(data) {
//						console.log('OOOOOOOOOPTIONSS!! ', data);

						gc.ctxobj.enhance(data, [ 'label' ], 'any');
						self.options.push(data);

						self.value.push(self.comboboxValueId());
						self.comboboxValue([]);
					});

//					console.log('OPTION -NOT- FOUND !!!!! ', self.comboboxValueId());
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
				attrAPI.createOption(newAttributeOptions).then(function(data) {
//					console.log('+++++ CREATED OPTION! ', newAttributeOptions, data);

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
			/*
			 if(self.selectOptions) {
			 _.each(self.value(), function (optionId) {
			 selected.push(_.findWhere(self.selectOptions(), {id: optionId}));
			 });
			 }
			 return selected;*/
		});

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

			//TODO: Generalize logic
			return true;



			var product = self.product.data;

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
			var vm = self.vm.data();

			// Product can only be empty during initialization, so we just return false.
			// Also, editing dyn-attributes only makes sense if there is a product-type.
			if(_.isUndefined(vm) /*|| _.isEmpty(product.type)*/) {
				return [];
			}

			var inputConditions = _.where( self.inputConditions, { showAttributeId : self.attributeId } );

			// Some input conditions only apply to certain product-types, so we filter the others out
			// first if needed.

			var filteredInputConditions = [];
 /*			if(!_.isEmpty(inputConditions)) {
				_.each(inputConditions, function(inputCondition) {
					if(_.isEmpty(inputCondition.applyToProductTypes) || inputCondition.applyToProductTypes.indexOf(product.type) != -1) {
						filteredInputConditions.push(inputCondition);
					}
				});
			}
*/

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
						var vmAttr = undefined; // _.findWhere( product.attributes, { attributeId : whenAttributeId } );

						if(formAttrValue && formAttrValue.attributeId) {
							if(!_.isEmpty(formAttrValue.value())) {
								vmAttr = {
									attributeId : formAttrValue.attributeId,
									optionIds : formAttrValue.value()
								}
							}
						} else {
							vmAttr = _.findWhere( vm.attributes, { attributeId : whenAttributeId } );
						}

						// If vm does not have the attribute, then fail. Dropdown is not valid for this vm.
						if(_.isUndefined(vmAttr)) {
							isValid = false;
						}

						// Get conditions set up for this attribute (whenAttributeId).
						var attrConditions = _.where( filteredInputConditions, { whenAttributeId : whenAttributeId } );
						// Also grab hold of the options when we have them.
						// (Idea is this: whenAttributeId -> hasOptionIds -> then show this attribute in panel).
						var attrConditionOptions = _.pluck( attrConditions, 'hasOptionIds' );

						// Get unique list of optionIds where te vm must fulfil at least one.
						if(!_.isEmpty(attrConditionOptions)) {
							attrConditionOptions = _.uniq(_.flatten(attrConditionOptions));
						}

						// If optionIds exist in condition (whenAttribute), then check those too.
						if(isValid && !_.isEmpty(attrConditionOptions)) {
							var optionIds = _.isArray(vmAttr.optionIds) ? vmAttr.optionIds : [ vmAttr.optionIds ];

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

				//  If vm has dependent attribute (and option exists),
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

	return {
        toUpdateModel : function(formAttributes, vmAttributes, updateModel) {

            if(!updateModel)
                updateModel = gc.app.newUpdateModel();

            _.each(formAttributes, function(attrVal) {

                if(attrVal.isEditable /*&& attrVal.hasChanged*/) {
                    var isOptOut = gc.ctxobj.plain(attrVal.optOut);

                    if(!_.isUndefined(isOptOut)) {
                        updateModel.optOut(attrVal.code, attrVal.optOut);
                    }

                    if(attrVal.isOption) {
                        var foundPrdAttr = _.findWhere(vmAttributes, { attributeId : attrVal.attributeId });

                        // Allow resetting of value if vm already has attribute and new value is empty.
                        if((!_.isUndefined(attrVal.value) && !_.isEmpty(attrVal.value)) || !_.isUndefined(foundPrdAttr)) {
                            updateModel.options(attrVal.code, attrVal.value);
                        }
                    } else {
                        updateModel.attr(attrVal.code, attrVal.value);
                    }
                }
            });

            return updateModel;
        },
        toNewUpdateModel : function(attributeValues, origAttributeValues) {
           var updateModel = gc.app.newUpdateModel();

            _.each(ko.unwrap(attributeValues), function(attrVal) {
							console.log('ÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄHHHHHHHHHHHHHHH: attrVal: ', ko.toJS(attrVal));

                if(attrVal.isEditable) {
                    var isOptOut = gc.ctxobj.plain(attrVal.optOut);

                    if(!_.isUndefined(isOptOut)) {
                        updateModel.optOut(attrVal.code, attrVal.optOut);
                    }

										if(attrVal.isOption()) {
												var foundExistingAttr = undefined;

												if(!_.isEmpty(origAttributeValues)) {
													foundExistingAttr = _.findWhere(origAttributeValues, { attributeId : attrVal.attributeId });
												}

												// Allow resetting of value if product already has attribute and new value is empty.
												if((!_.isUndefined(attrVal.value) && !_.isEmpty(attrVal.value)) || !_.isUndefined(foundExistingAttr)) {
														updateModel.options(attrVal.code, ko.unwrap(attrVal.value));
												}
										} else {
												updateModel.attr(attrVal.code, ko.unwrap(attrVal.value));
										}
                }
            });

            return updateModel;
        },
		getAttribute : function (attribute, attributeTabId, vm, vmAttributes, formAttributeValues, attributeInputConditions, collection) {

			var foundPrdAttr = _.findWhere(vmAttributes, { attributeId : attribute.id });
			var inputConditions = _.where(attributeInputConditions, { showAttributeId : attribute.id });

			var attrOptions = attribute.options;
			gc.ctxobj.enhance(attrOptions, [ 'label' ], 'any');

			if(!_.isUndefined(attribute) && !_.isUndefined(foundPrdAttr)) {
				return new AttributeValueVM(
					formAttributeValues,
					vm,
					foundPrdAttr.id,
					foundPrdAttr.attributeId,
					foundPrdAttr.attribute.backendLabel,
					foundPrdAttr.attribute.code,
					foundPrdAttr.attribute.code2,
					attribute.optionAttribute ? foundPrdAttr.optionIds : foundPrdAttr.value,
					foundPrdAttr.optOut,
					attribute.editable,
					attribute.enabled,
					attribute.inputType,
					attribute.frontendInput,
					attribute.optionAttribute,
					attribute.allowMultipleValues,
					attribute.i18n,
					attrOptions,
					inputConditions,
					attribute.productTypes,
					attribute.scopes,
				    attributeTabId,
					collection);
			} else if(!_.isUndefined(attribute)) {
				return new AttributeValueVM(
					formAttributeValues,
					vm,
					undefined,
					attribute.id,
					attribute.backendLabel,
					attribute.code,
					attribute.code2,
					[],
					[],
					attribute.editable,
					attribute.enabled,
					attribute.inputType,
					attribute.frontendInput,
					attribute.optionAttribute,
					attribute.allowMultipleValues,
					attribute.i18n,
					attrOptions,
					inputConditions,
					attribute.productTypes,
					attribute.scopes,
					attributeTabId,
					collection);
			}


		}
	}
});
