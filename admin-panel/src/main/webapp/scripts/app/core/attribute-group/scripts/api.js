define(['knockout', 'gc/gc'], function (ko, gc) {

    return {
        getAttributeGroup: function(attributeGroupId) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.get({
                url : '/api/v1/attribute-groups/' + attributeGroupId,
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
        getAttributeGroups: function() {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.get({
                url : '/api/v1/attribute-groups/',
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
        updateAttributeGroup: function(attributeGroupId, attributeGroupUpdateData) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.put({
                url : '/api/v1/attribute-groups/' + attributeGroupId,
                data: attributeGroupUpdateData.data(),
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
        createAttributeGroup: function(attributeGroupUpdateData) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

		console.log('______ CREATING MESSAGE ____ ', attributeGroupUpdateData.data());

            gc.rest.post({
                url : '/api/v1/attribute-groups/',
                data: attributeGroupUpdateData.data(),
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
        removeAttributeGroup: function(attributeGroupId) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            console.log('_____removeAttributeGroup_____', attributeGroupId);

            gc.rest.del({
                url : '/api/v1/attribute-groups/' + attributeGroupId,
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
        addAttributeToGroup: function(attrGroupId, attributeId) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.put({
                url : '/api/v1/attribute-groups/' + attrGroupId + '/attributes/' + attributeId,
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
        addGroupToGroup: function(attrGroupId, groupId) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.put({
                url : '/api/v1/attribute-groups/' + attrGroupId + '/attribute-groups/' + groupId,
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
        removeItemFromGroup: function(attrGroupId, itemId) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.del({
                url : '/api/v1/attribute-groups/' + attrGroupId + '/items/' + itemId,
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
        updateItemPositions: function(attrGroupId, itemPositions) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.put({
                url : '/api/v1/attribute-groups/' + attrGroupId + '/positions',
                data: itemPositions,
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
        pagingOptions: function(options) {

            options = options || {};

            return {
                // Base URI of product-API.
                url: '/api/v1/attribute-groups',
                searchUrl: '/api/v1/search/',
                filter: options.filter || [],
                // Only load specific fields for better performance.
                fields: options.fields || [ 'id', 'code', 'label', 'position' /* , 'column'  */],
                // Optionally pre-sort the results.
                sort: options.sort || ['createdOn'],
                columns: options.columns,
                // Returns an array that the pager can add to the
                // ko.observableArray.
                getArray: function(data) {
                    _.each(data.attributeGroups, function(ag){
                        ag.label = gc.ctxobj.val(ag.label, gc.app.currentUserLang(), 'any');
                    })
                    return data.attributeGroups || [];
                }
            }
        },
        getAttrPagingOptions: function(attributeGroupId, pagingOptions) {
            pagingOptions = pagingOptions || {};

            return {
                url: '/api/v1/attribute-groups/' + attributeGroupId + '/notGroupAttributes',
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
        },
        getAttrGroupPagingOptions: function(attributeGroupId, pagingOptions) {
            pagingOptions = pagingOptions || {};

            return {
                url: '/api/v1/attribute-groups/' + attributeGroupId + '/notGroupAttributeGroups',
                filter: pagingOptions.filter || {},
                // Only load specific fields for better performance.
                fields: pagingOptions.fields  || ['code', 'label'],
                // Optionally pre-sort the results.
                sort: pagingOptions.sort || ['code'],
                columns: pagingOptions.columns || [],
                // Returns an array that the pager can add to the
                // ko.observableArray.
                getArray: function(data) {
                    _.each(data.attributeGroups, function (group) {
                        group.code2 = "GROUP";
                    });

                    return data.attributeGroups;
                },
                // ----------------------------------------------------------------
                // Mapping used by the ko.mapping plugin.
                // ----------------------------------------------------------------
                mapping: {
                    create: function(options) {
                        var viewModel = options.data;
                        viewModel.ctxValue = ko.computed(function () {
                            return gc.getValue(viewModel.label);
                        });

                        return viewModel;
                    }
                }
            }
        }
    }
});