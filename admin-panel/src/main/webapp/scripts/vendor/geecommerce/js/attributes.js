define([ 'knockout', 'gc/gc', 'gc-attribute' ], function(ko, gc, attrAPI) {

	return {
		find : function() {
			var self = this;
			var _attr = null;
			var _set = false;

			if (arguments.length == 3)
				_set = true;

			var attributes = arguments[0];
			var code = arguments[1];

			if (!_.isEmpty(attributes)) {
				_.some(attributes, function(element, index) {
					element.attribute = element.attribute || {};
					if (element.code == code || element.attribute.code == code) {
						_attr = element;
						return true;
					}
				});
			}

			// If attribute does not exist, create an empty object, so that
			// the knockout observables have something to work with.
			_attr = _attr || {
				code : code,
				value : [],
				optionIds: []
			};

			if (_set) {
				var value = arguments[2];

				if (!_.isUndefined(ctxValue) && !_.isNull(ctxValue)) {
					// TODO
				}
			} else {
				// TODO
			}

			return _attr;
		},
        optionLabel : function(attributes, code, mode, lang) {
            var self = this;
            var attr = self.find(attributes, code);
            var label = '';
            lang = lang || gc.app.currentUserLang();
            
            if(!_.isEmpty(attr) && !_.isEmpty(attr.attributeOptions)) {
                label = gc.ctxobj.val(attr.attributeOptions[0].label, lang, mode);
            }
            
            return label;
        },  
		havingProperty : function(name, attributes) {
			var foundAttributes = [];

			if (!_.isEmpty(attributes)) {
				_.each(attributes, function(_attr, index) {
					if (!_.isUndefined(_attr.properties) && _.has(_attr.properties, name)) {
						foundAttributes.push(_attr);
					}
				});
			}

			return foundAttributes;
		},
		addProperty : function(attribute, newProperty) {
			var self = this;
			
			if(_.isUndefined(attribute) || _.isUndefined(newProperty))
				return;
			
			if(_.isUndefined(attribute.properties))
				attribute.properties = {};

			var _keys = _.keys(newProperty);
			
			attribute.properties[_keys[0]] = newProperty[_keys[0]];
		},
		removeProperty : function(attribute, name) {
			var self = this;
			
			if(_.isUndefined(attribute) || _.isUndefined(name))
				return;
			
			if(_.isUndefined(attribute.properties))
				return;

			delete attribute.properties[name];
		},
		flattenedValues : function(attributes, options) {
			var _options = options || {};
			// Only return attribute options (no free-text values).
			var _onlyAttributeOptions = _options.onlyAttributeOptions || false;
			// Only return free-text values (no attribute options).
			var _skipAttributeOptions = _options.skipAttributeOptions || false;
			// Exclude these values in the result. {attributeId, id
			// /*optionId*/} or {attributeId, value} must be specified.
			var _exclude = _options.exclude || [];
			var _ctxMode = _options.ctxMode || 'strict';

			var _values = [];

			if (!_.isEmpty(attributes)) {
				_.each(attributes, function(_attr, index) {
					if (!_.isEmpty(_attr.attributeOptions) && !_skipAttributeOptions) {
						_.each(_attr.attributeOptions, function(_attrOption, index) {
							var _entry = _.findWhere(_exclude, {
								attributeId : _attrOption.attributeId,
								id : _attrOption.id
							});

							if (_.isUndefined(_entry)) {
								_values.push({
									id : _attrOption.id,
									attributeId : _attrOption.attributeId,
									attributeCode : _attr.attribute.code,
									attributeLabel : _attr.attribute.backendLabel,
									value : _attrOption.label,
									longValue : ko.computed(function() {
										return '<span class="attrLabel">' + gc.ctxobj.val(_attr.attribute.backendLabel, gc.app.currentLang(), _ctxMode) + ':</span> <span class="attrValue">' + gc.ctxobj.val(_attrOption.label, gc.app.currentLang(), _ctxMode) + '</span>';
									}),
									pos : _attrOption.position,
									isOption : true
								});
							}
						});
					} else if (!_.isEmpty(_attr.value) && !_onlyAttributeOptions) {
						var _entry = _.findWhere(_exclude, {
							attributeId : _attr.attributeId,
							value : _attr.value
						});

						if (_.isUndefined(_entry)) {
							_values.push({
								id : _attr.attribute.id,
								attributeId : _attr.attributeId,
								attributeCode : _attr.attribute.code,
								attributeLabel : _attr.attribute.backendLabel,
								value : _attr.value,
								longValue : ko.computed(function() {
									return gc.ctxobj.val(_attr.attribute.backendLabel, gc.app.currentLang()) + ': ' + gc.ctxobj.val(_attr.value, gc.app.currentLang());
								}),
								pos : 0,
								isOption : false
							});
						}
					}
				});
			}

			return _values;
		},
		values : function(name, attributes) {
			var values = [];

			if (!_.isEmpty(attributes)) {
				_.each(attributes, function(_attr, index) {
					var value = [];
					if (!_.isEmpty(_attr.attributeOptions)) {
						_.each(_attr.attributeOptions, function(_attrOption, index) {
							value.push(_attrOption.label);
						});
					} else if (!_.isEmpty(_attr.value)) {
						value.push(_attr.value);
					}

					values.push(value);
				});
			}

			return values;
		},
		appendAttributes : function(toObjects) {
			var self = this;
			
			console.time('appendAttributes-TIME');
			
			if(_.isEmpty(toObjects))
				return;
			
			var _toObjects = [];
			
			if(_.isArray(toObjects)) {
				_toObjects = toObjects;
			} else if (_.isObject(toObjects)) {
				_toObjects.push(toObjects);
			} else {
				return;
			}

			if(_toObjects.length > 0) {
				for(var i=0; i<_toObjects.length; i++) {
					var obj = _toObjects[i];
					
					var attributes = obj.attributes;
					var attributeIds = _.pluck(attributes, 'attributeId');
		
		           	attrAPI.getAttributes( { filter: { id : attributeIds.join() } } ).then(function( response ) {
		           		var attributeObjects = response.data.attributes;
		        		_.each(attributeObjects, function(attr) {
		        			if(attr && attr.id) {
			        			var foundAttr = _.findWhere(attributes, { attributeId : attr.id } );
			        			foundAttr.attribute = attr;
		        			} else {
		        				console.log('WARN: attribute not found: ', attr);
		        			}
						});
		           	});
				}
			}
			
			console.timeEnd('appendAttributes-TIME');
		}
	};
});
