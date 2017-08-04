define(['knockout', 'gc/gc'], function (ko, gc) {

    return {
        getMessage: function(messageId) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.get({
                url : '/api/v1/text/' + messageId,
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
        updateMessage: function(messageId, messageUpdateData) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.put({
                url : '/api/v1/text/' + messageId,
                data: messageUpdateData.data(),
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
        createMessage: function(messageUpdateData) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.post({
                url : '/api/v1/text/',
                data: messageUpdateData.data(),
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
                url: '/api/v1/text',
                searchUrl: '/api/v1/search/',
                filter: options.filter || [],
                // Only load specific fields for better performance.
                fields: options.fields || [ 'id', 'key', 'value' ],
                // Optionally pre-sort the results.
                sort: options.sort || ['createdOn'],
                columns: options.columns,
                // Returns an array that the pager can add to the
                // ko.observableArray.
                getArray: function(data) {
                    _.each(data.contextMessages, function(mag){
                        mag.value = gc.ctxobj.val(mag.value, gc.app.currentUserLang(), 'any');
                    })
                    return data.contextMessages || [];
                }
            }
        }
    }
});