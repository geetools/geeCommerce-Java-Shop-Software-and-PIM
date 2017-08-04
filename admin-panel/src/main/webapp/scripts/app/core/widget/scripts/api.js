define(['knockout', 'gc/gc'], function (ko, gc) {
	
	return {
        getWidgets: function() {
            self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.get({
                url : '/api/v1/widgets/',
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
        getWidgetGroups: function() {
            self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.get({
                url : '/api/v1/widgets/groups/',
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
        getWidget: function(widgetId) {
            self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.get({
                url : '/api/v1/widgets/' + widgetId,
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
        getParameterOptions: function(widgetCode, parameterId) {
            self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.get({
                url : '/api/v1/widgets/' + widgetCode + '/options/' + parameterId,
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
        		url: '/api/v1/widgets',
        		filter: options.filter || [],
        		// Only load specific fields for better performance.
        		fields: options.fields || [ 'id', 'forename', 'surname', 'email', 'username', 'createdOn' ],
        		// Optionally pre-sort the results.
        		sort: options.sort || ['-createdOn'],
        		columns: options.columns,
        		// Returns an array that the pager can add to the
				// ko.observableArray.
        		getArray: function(data) {
        			return data;
        		}
        	}
        }
    }
});