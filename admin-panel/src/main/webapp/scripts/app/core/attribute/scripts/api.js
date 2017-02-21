define(['knockout', 'gc/gc'], function (ko, gc) {

	return {
        getAttribute: function(attributeId) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url : '/api/v1/attributes/' + attributeId,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        createAttribute: function(newAttribute) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____createAttribute_____', newAttribute);

			gc.rest.post({
				url : '/api/v1/attributes',
				data: newAttribute,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        updateAttribute: function(attributeId, updateModel) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____updateModel_____', updateModel.data());

			gc.rest.put({
				url : '/api/v1/attributes/' + attributeId,
				data: updateModel.data(),
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        getAttributes: function(arg1, arg2) {
			self = this;

            var targetObjCode;
            var options;

			if(!_.isUndefined(arg1) && _.isObject(arg1) && _.isUndefined(arg2)) {
			    options = arg1;
			} else if(!_.isUndefined(arg1) && _.isString(arg1) && _.isUndefined(arg2)) {
			    targetObjCode = arg1;
			} else if(!_.isUndefined(arg1) && !_.isUndefined(arg2)) {
                targetObjCode = arg1;
                options = arg2;
			}

            var options = options || {};
            var filter = options.filter || {};

			if(!_.isUndefined(targetObjCode) && _.isUndefined(filter.targetObjectId)) {
			    filter.targetObjectId = self.targetObjectId(targetObjCode);
			}


			console.log('?????????????=====??????? targetObjectId: ', targetObjCode, filter.targetObjectId);

			if(!options.nocache) {
				var cachedAttributes = gc.cache.get('attributes', options);

				if(!_.isEmpty(cachedAttributes ) && cachedAttributes[0]) {
					return $.when({ isFromCache : true, data : { attributes : cachedAttributes } });
				}
			}

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url : '/api/v1/attributes',
				filter : options.filter,
				fields : options.fields,
				sort : options.sort,
				nocache : options.nocache || false,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        getAttributeTargetObjects: function(options) {
            self = this;

            var options = options || {};

            if(!options.nocache) {
                var cachedAttributeTargetObjects = gc.cache.get('attributeTargetObjects', options);

                if(!_.isEmpty(cachedAttributeTargetObjects ) && cachedAttributeTargetObjects[0]) {
                    return $.when({ isFromCache : true, data : { attributeTargetObjects : cachedAttributeTargetObjects } });
                }
            }

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.get({
                url : '/api/v1/attribute-target-objects',
                filter : options.filter,
                fields : options.fields,
                sort : options.sort,
                nocache : options.nocache || false,
                success : function(data, status, xhr) {
                    if (self._onload) {
                        self._onload(data, status, xhr);
                    }

                    deferred.resolve(data, status, xhr);
                },
                error : function(jqXHR, status, error) {
                    if (self._onerror) {
                        self._onerror(jqXHR, status, error);
                    }

                    deferred.reject(jqXHR, status, error);
                },
                complete : function(data, status, xhr) {
                    if (self._oncomplete) {
                        self._oncomplete(data, status, xhr);
                    }

                    deferred.resolve(data, status, xhr);
                }
            });

            return promise;
        },
        targetObjectId: function(targetObjectCode) {
            self = this;

            var options = options || {};
            var filter = options.filter || {};

            if(!_.isEmpty(targetObjectCode)) {

                var cachedAttributeTargetObjects = gc.cache.get('attributeTargetObjects', {filter: {code: targetObjectCode}});
                if(!_.isEmpty(cachedAttributeTargetObjects) && cachedAttributeTargetObjects.length === 1) {
                    return cachedAttributeTargetObjects[0].id;
                }
            }
        },
        getOptionAttributes: function(options) {
			var self = this;

			var options = options || {};

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url : '/api/v1/attributes',
				filter : { optionAttribute: options.optionAttribute || true, group: options.group },
				fields : options.fields,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        getAttributeOptions: function(attributeId, optionIds) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			var optionIdParam = '';

			if(!_.isUndefined(optionIds) && _.isArray(optionIds) && !_.isEmpty(optionIds)) {
			    optionIdParam = '?id=' + optionIds.join();
			}

			gc.rest.get({
				url : '/api/v1/attributes/' + attributeId + '/options' + optionIdParam,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        getAttributeOption: function(attributeId, optionId) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url : '/api/v1/attributes/' + attributeId + '/options/'  + optionId,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        findAttributeOptions: function(attributeId, term, lang, limit, matchCase) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url : '/api/v1/attributes/' + attributeId + '/options/map?term=' + term + '&lang=' + lang + (limit ? '&limit=' + limit : '') + (matchCase ? '&matchCase=' + matchCase : ''),
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        getOptionGroupingTags: function(attributeId) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url : '/api/v1/attributes/' + attributeId + '/options/tags',
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        updateOptionPositions: function(attributeId, optionPositions) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____attributeId_____', attributeId);

			gc.rest.put({
				url : '/api/v1/attributes/' + attributeId + '/options/positions',
				data: optionPositions,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        createOptions: function(attributeId, newOptionsData) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____attributeId_____', attributeId);
			console.log('_____newOptionsData_____', newOptionsData);

			gc.rest.post({
				url : '/api/v1/attributes/' + attributeId + '/options',
				data: newOptionsData,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
		createOption: function(attributeId, newOptionData, ifNotExists, matchCase) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____newOptionData_____', newOptionData);

			var params = '';

			if(!_.isUndefined(ifNotExists)) {
				params += '?ifNotExists=' + ifNotExists;
			}

			if(!_.isUndefined(matchCase)) {
				params += (params == '' ? '?' : '&') + 'matchCase=' + matchCase;
			}

			gc.rest.post({
                url : '/api/v1/attributes/' + attributeId + '/option' + params,
				data: newOptionData,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
		},
        updateOptions: function(attributeId, optionsUpdateData) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____attributeId_____', attributeId);
			console.log('_____optionsUpdateData_____', optionsUpdateData);

			gc.rest.put({
				url : '/api/v1/attributes/' + attributeId + '/options',
				data: optionsUpdateData,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        removeAttribute: function(attributeId) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____removeAttribute_____', attributeId);

			gc.rest.del({
				url : '/api/v1/attributes/' + attributeId,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        removeOption: function(attributeId, optionId) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____removeOption_____', attributeId, optionId);

			gc.rest.del({
				url : '/api/v1/attributes/' + attributeId + '/options/' + optionId,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        getInputConditionsFor: function(options) {
			self = this;

			var cachedInputConditions = gc.cache.get('inputConditions', options);

			if(!_.isEmpty(cachedInputConditions)) {
				return $.when({ isFromCache : true, data : { attributeInputConditions : cachedInputConditions } });
			}

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url : '/api/v1/attributes/input-conditions',
				filter : options.filter,
				fields : options.fields,
				sort : options.sort,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        getInputConditions: function(attributeId) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url : '/api/v1/attributes/' + attributeId + '/input-conditions',
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        createInputConditions: function(attributeId, newInputConditionsData) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____attributeId_____', attributeId);
			console.log('_____newInputConditionsData_____', newInputConditionsData);

			gc.rest.post({
				url : '/api/v1/attributes/' + attributeId + '/input-conditions',
				data: newInputConditionsData,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        updateInputConditions: function(attributeId, inputConditionsUpdateData) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____attributeId_____', attributeId);
			console.log('_____inputConditionsUpdateData_____', inputConditionsUpdateData);

			gc.rest.put({
				url : '/api/v1/attributes/' + attributeId + '/input-conditions',
				data: inputConditionsUpdateData,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
        removeInputCondition: function(attributeId, inputConditionId) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____inputCondition_____', attributeId, inputConditionId);

			gc.rest.del({
				url : '/api/v1/attributes/' + attributeId + '/input-conditions/' + inputConditionId,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
        },
		getAttributeSuggestions: function(attributeId, lang, query, collection) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url : '/api/v1/attributes/' + attributeId + '/suggestions/' + lang + '/' + query + '/' + collection,
				success : function(data, status, xhr) {
					if (self._onload) {
						self._onload(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if (self._onerror) {
						self._onerror(jqXHR, status, error);
					}

					deferred.reject(jqXHR, status, error);
				},
				complete : function(data, status, xhr) {
					if (self._oncomplete) {
						self._oncomplete(data, status, xhr);
					}

					deferred.resolve(data, status, xhr);
				}
			});

			return promise;
		},
		// ----------------------------------------------------------------
		// Returns an options object that can be used by the gc-pager.
		// ----------------------------------------------------------------
        getAttributeOptionsPagingOptions: function(attributeId) {
        	return {
        		// Base URI of attributes-API.
        		url: '/api/v1/attributes/' + attributeId + '/options',
        		// Optionally pre-sort the results.
        		sort: ['position'],
        		// Returns an array that the pager can add to the
				// ko.observableArray.
        		getArray: function(data) {
        			var attributeOptions = data['attribute-options'];

        			// Hack to stop editable-plugin from causing errors.
        			_.each(attributeOptions, function(item) {
        				if(_.isUndefined(item.tags)) {
        					item.tags = [];
        				}
        			});

//        			return ko.mapping.fromJS(attributeOptions || []);
        			return attributeOptions || [];
        		}
        	}
        },
		// ----------------------------------------------------------------
		// Returns an options object that can be used by the gc-pager.
		// ----------------------------------------------------------------
        getPagingOptions: function(pagingOptions) {
        	pagingOptions = pagingOptions || {};

        	return {
        		// Base URI of attributes-API.
        		url: pagingOptions.uri  || '/api/v1/attributes',
        		filter: pagingOptions.filter || {},
        		// Only load specific fields for better performance.
        		fields: pagingOptions.fields  || ['code', 'backendLabel', 'inputType', 'frontendInput', 'group', 'enabled'],
        		// Optionally pre-sort the results.
        		sort: pagingOptions.sort || ['code'],
        		// Returns an array that the pager can add to the
				// ko.observableArray.
                columns: pagingOptions.columns,
        		getArray: function(data) {
        			return data.attributes;
        		},
        		// ----------------------------------------------------------------
        		// Mapping used by the ko.mapping plugin.
        		// ----------------------------------------------------------------
        		mapping: {
        			create: function(options) {
        				var viewModel = options.data;
					    viewModel.ctxValue = ko.computed(function () {
			                return gc.getValue(viewModel.backendLabel);
			             });

					     return viewModel;
					}
				}
        	}
        }
    }
});
