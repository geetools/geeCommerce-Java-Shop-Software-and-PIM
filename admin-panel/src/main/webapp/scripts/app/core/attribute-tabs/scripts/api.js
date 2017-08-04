define(['knockout', 'gc/gc'], function (ko, gc) {
	
	return {
		createAttributeTab: function(newAttributeTab) {
			
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;
			
			gc.rest.post({
				url : '/api/v1/control-panels/' + gc.app.confGet('controlPanelId') + '/attribute-tabs',
				data: newAttributeTab,
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
        getAttributeTab: function(id) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url : '/api/v1/control-panels/' + gc.app.confGet('controlPanelId') + '/attribute-tabs/' + id,
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
        getAttributeTabs: function(type) {
			self = this;

			if(type){
				var cachedAttributeTabs = gc.cache.get('attributeTabs-' + type);
			} else {
				var cachedAttributeTabs = gc.cache.get('attributeTabs');
			}
			
			if(!_.isEmpty(cachedAttributeTabs)) {
				return $.when({ isFromCache : true, data : { attributeTabs : cachedAttributeTabs } });
			}

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			var url = type ? '/attribute-tabs/group/' + type : '/attribute-tabs'
			gc.rest.get({
				url : '/api/v1/control-panels/' + gc.app.confGet('controlPanelId') + url ,
				sort : [ 'position' ],
				success : function(data, status, xhr) {

					if(type){
						var attributeTabs = data.data.attributeTabs;
						gc.cache.put('attributeTabs-' + type, attributeTabs);
					}

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
        getAttributeTabMapping: function(attrTabId) {
			self = this;

			var cachedAttributeTabMapping = gc.cache.get('attributeTabMappings', {filter: {tabId: attrTabId}});
			
			if(!_.isEmpty(cachedAttributeTabMapping)) {
				return $.when({ isFromCache : true, data : { attributeTabMappings : cachedAttributeTabMapping } });
			}

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			var uri = attrTabId ? '/api/v1/control-panels/' + gc.app.confGet('controlPanelId') + '/attribute-tabs/' + attrTabId + '/attributes' : '/api/v1/control-panels/' + gc.app.confGet('controlPanelId') + '/attribute-tabs/attributes'

			gc.rest.get({
				url : uri,
				sort : [ 'position' ],
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
        addAttributeToTab: function(attrTabId, attributeId) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.put({
				url : '/api/v1/control-panels/' + gc.app.confGet('controlPanelId') + '/attribute-tabs/' + attrTabId + '/attributes/' + attributeId,
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
        removeAttributeFromTab: function(attrTabId, attributeId) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.del({
				url : '/api/v1/control-panels/' + gc.app.confGet('controlPanelId') + '/attribute-tabs/' + attrTabId + '/attributes/' + attributeId,
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
        updateAttributeTab: function(id, updateModel) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.put({
				url : '/api/v1/control-panels/' + gc.app.confGet('controlPanelId') + '/attribute-tabs/' + id(),
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
        updateAttributePositions: function(attrTabId, attrPositions) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;
			
			gc.rest.put({
				url : '/api/v1/control-panels/' + attrTabId + '/options/positions',
				data: attrPositions,
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
        			
        			return attributeOptions;
        		}
        	}
        },
		// ----------------------------------------------------------------
		// Returns an options object that can be used by the gc-pager.
		// ----------------------------------------------------------------
        getAttributeTabsPagingOptions: function() {
        	return {
        		// Base URI of attributes-tabs.
        		url: '/api/v1/control-panels/' + gc.app.confGet('controlPanelId') + '/attribute-tabs',
        		// Optionally pre-sort the results.
        		sort: ['position'],
        		// Returns an array that the pager can add to the
				// ko.observableArray.
        		getArray: function(data) {
        			return data.attributeTabs;
        		}
        	};
        },
        removeAttributeTab: function(attrTabId) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.del({
                url : '/api/v1/control-panels/' + attrTabId,
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
        getPagingOptions: function(attributeTabId, pagingOptions) {
            pagingOptions = pagingOptions || {};

            return {
                url: '/api/v1/control-panels/' + gc.app.confGet('controlPanelId') + '/attribute-tabs/' + attributeTabId + '/notTabAttributes',
                filter: pagingOptions.filter || {},
                // Only load specific fields for better performance.
                fields: pagingOptions.fields  || ['code', 'code2', 'backendLabel', 'group', 'enabled'],
                // Optionally pre-sort the results.
                sort: pagingOptions.sort || ['code'],
				columns: pagingOptions.columns || [],
                // Returns an array that the pager can add to the
                // ko.observableArray.
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