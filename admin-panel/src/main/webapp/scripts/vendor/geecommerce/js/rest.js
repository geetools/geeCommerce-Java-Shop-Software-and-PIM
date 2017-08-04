define([ 'plugins/router', 'knockout', 'gc/gc' ], function(router, ko, gc) {

    this.getData = function(options) {

        var requestData = {};
        var filter = options.filter;
        var fields = options.fields;
        var attributes = options.attributes;
        var sortFields = options.sort;
        var offset = options.offset;
        var limit = options.limit;
        var query = options.query;
        var nocache = options.nocache;

        // ---------------------------------------------------
        // Filter
        // ---------------------------------------------------
        if (_.isObject(filter) && !_.isEmpty(filter)) {
            for ( var propt in filter) {
                requestData[propt] = filter[propt];
            }
        }

        // ---------------------------------------------------
        // Fields
        // ---------------------------------------------------
        if (_.isArray(fields) && !_.isEmpty(fields)) {
            var fieldsCsv = fields.join(",");
            requestData.fields = fieldsCsv;
        }

        // ---------------------------------------------------
        // Attributes
        // ---------------------------------------------------
        if (_.isArray(attributes) && !_.isEmpty(attributes)) {
            var attributesCsv = attributes.join(",");
            requestData.attributes = attributesCsv;
        }

        // ---------------------------------------------------
        // Sort
        // ---------------------------------------------------
        if (_.isArray(sortFields) && !_.isEmpty(sortFields)) {
            var sortFieldsCsv = sortFields.join(",");
            requestData.sort = sortFieldsCsv;
        }

        // ---------------------------------------------------
        // Offset
        // ---------------------------------------------------
        if (!_.isUndefined(offset)) {
            requestData.offset = offset;
        }

        // ---------------------------------------------------
        // Limit
        // ---------------------------------------------------
        if (!_.isUndefined(limit)) {
            requestData.limit = limit;
        }

        // ---------------------------------------------------
        // NoCache
        // ---------------------------------------------------
        if (!_.isUndefined(nocache)) {
            requestData.nocache = nocache;
        }

        // ---------------------------------------------------
        // Offset
        // ---------------------------------------------------
        if (!_.isUndefined(query)) {
            requestData.query = query;
        }


        return requestData;
    }

    return {
        get : function(options) {

            options = options || {};

            // if(options.async == undefined)
            // options.async = true;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            var headers = options.headers || {};

            if (_.isEmpty(headers['Accept']))
                headers['Accept'] = 'application/json';

            $.ajax({
                url : options.url,
                data : getData(options),
                type : 'GET',
                headers : headers,
                success : function(data, status, xhr) {
                    if (options.success) {
                        options.success(data, status, xhr);
                    }

                    deferred.resolve(data, status, xhr);
                },
                error : function(jqXHR, status, error) {
                    if (jqXHR.status !== 401) {
                        if (options.error) {
                            options.error(jqXHR, status, error);
                        }

                        deferred.reject(jqXHR, status, error);
                    } else {
                        gc.app.handleSessionTimeout();
                        return;
                    }
                },
                complete : function(data, status, xhr) {
                    var sessionTimeoutAt = data.getResponseHeader('CB-Timeout-At');

                    if (sessionTimeoutAt) {
                        gc.app.sessionPut('sessionTimeoutAtMillis', sessionTimeoutAt);
                    }
                },
                beforeSend : function(xhr) {
                    // var activeStore = gc.app.activeStore();
                    //
                    // if(!_.isEmpty(activeStore) && !_.isUndefined(activeStore.id)) {
                    // xhr.setRequestHeader('X-CB-StoreContext', activeStore.id);
                    // }
                }
            });

            return promise;
        },
        post : function(options) {

            options = options || {};

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            var headers = options.headers;

            var request = {
                url : options.url,
                type : 'POST',
                success : function(data, status, xhr) {
                    if (options.success) {
                        options.success(data, status, xhr);
                    }

                    deferred.resolve(data, status, xhr);
                },
                error : function(jqXHR, status, error) {
                    if (jqXHR.status !== 401) {
                        if (options.error) {
                            options.error(jqXHR, status, error);
                        }

                        deferred.reject(jqXHR, status, error);
                    } else {
                        gc.app.handleSessionTimeout();
                        return;
                    }
                },
                complete : function(data, status, xhr) {
                    var sessionTimeoutAt = data.getResponseHeader('CB-Timeout-At');

                    if (sessionTimeoutAt) {
                        gc.app.sessionPut('sessionTimeoutAtMillis', sessionTimeoutAt);
                    }
                },
                beforeSend : function(xhr) {
                    // var activeStore = gc.app.activeStore();
                    //
                    // if(!_.isEmpty(activeStore) && !_.isUndefined(activeStore.id)) {
                    // xhr.setRequestHeader('X-CB-StoreContext', activeStore.id);
                    // }
                }
            }

            if (options.formData) {
                request.data = $.param(options.formData);
                request.headers = {
                    'Accept' : 'application/json'
                };
            } else if (options.formId) {
                request.data = $('#' + options.formId).serialize();
                request.headers = {
                    'Accept' : 'application/json'
                };
            } else {
                gc.utils.unwrap(options.data);
                request.data = JSON.stringify(options.data);
                request.contentType = "application/json";
                request.headers = {
                    'Accept' : 'application/json',
                    'Content-Type' : 'application/json'
                };
            }

            $.ajax(request);

            return promise;
        },
        put : function(options) {

            options = options || {};

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            var headers = options.headers;

            gc.utils.unwrap(options.data);

            $.ajax({
                url : options.url,
                data : JSON.stringify(options.data),
                type : 'PUT',
                contentType : "application/json",
                headers : {
                    'Accept' : 'application/json',
                    'Content-Type' : 'application/json'
                },
                success : function(data, status, xhr) {
                    if (options.success) {
                        options.success(data, status, xhr);
                    }

                    deferred.resolve(data, status, xhr);
                },
                error : function(jqXHR, status, error) {
                    if (jqXHR.status !== 401) {
                        if (options.error) {
                            options.error(jqXHR, status, error);
                        }

                        deferred.reject(jqXHR, status, error);
                    } else {
                        gc.app.handleSessionTimeout();
                        return;
                    }
                },
                complete : function(data, status, xhr) {
                    var sessionTimeoutAt = data.getResponseHeader('CB-Timeout-At');

                    if (sessionTimeoutAt) {
                        gc.app.sessionPut('sessionTimeoutAtMillis', sessionTimeoutAt);
                    }
                },
                beforeSend : function(xhr) {
                    // var activeStore = gc.app.activeStore();
                    //
                    // if(!_.isEmpty(activeStore) && !_.isUndefined(activeStore.id)) {
                    // xhr.setRequestHeader('X-CB-StoreContext', activeStore.id);
                    // }
                }
            });

            return promise;
        },
        del : function(options) {

            options = options || {};

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            var headers = options.headers;

            gc.utils.unwrap(options.data);

            $.ajax({
                url : options.url,
                data : options.data,
                type : 'DELETE',
                headers : {
                    'Accept' : 'application/json',
                    'Content-Type' : 'application/json'
                },
                success : function(data, status, xhr) {
                    if (options.success) {
                        options.success(data, status, xhr);
                    }

                    deferred.resolve(data, status, xhr);
                },
                error : function(jqXHR, status, error) {
                    if (jqXHR.status !== 401) {
                        if (options.error) {
                            options.error(jqXHR, status, error);
                        }

                        deferred.reject(jqXHR, status, error);
                    } else {
                        gc.app.handleSessionTimeout();
                        return;
                    }
                },
                complete : function(data, status, xhr) {
                    var sessionTimeoutAt = data.getResponseHeader('CB-Timeout-At');

                    if (sessionTimeoutAt) {
                        gc.app.sessionPut('sessionTimeoutAtMillis', sessionTimeoutAt);
                    }
                },
                beforeSend : function(xhr) {
                    // var activeStore = gc.app.activeStore();
                    //
                    // if(!_.isEmpty(activeStore) && !_.isUndefined(activeStore.id)) {
                    // xhr.setRequestHeader('X-CB-StoreContext', activeStore.id);
                    // }
                }
            });

            return promise;
        },
        file : function(options) {

            options = options || {};

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            var headers = options.headers || {};

            var addContextHeader = options.addContextHeader || headers;
            // var activeStore = gc.app.sessionGet('activeStore');
            //
            // if(addContextHeader && !_.isEmpty(activeStore)) {
            // headers['X-CB-store'] = activeStore;
            // }

            gc.utils.unwrap(options.data);

            $.ajax({
                url : options.url,
                data : options.data,
                type : 'POST',
                cache : false,
                processData : false,
                contentType : false,
                headers : headers,
                success : function(data, status, xhr) {
                    if (options.success) {
                        options.success(data, status, xhr);
                    }

                    deferred.resolve(data, status, xhr);
                },
                error : function(jqXHR, status, error) {
                    if (jqXHR.status !== 401) {
                        if (options.error) {
                            options.error(jqXHR, status, error);
                        }

                        deferred.reject(jqXHR, status, error);
                    } else {
                        gc.app.handleSessionTimeout();
                        return;
                    }
                },
                complete : function(data, status, xhr) {
                    var sessionTimeoutAt = data.getResponseHeader('CB-Timeout-At');

                    if (sessionTimeoutAt) {
                        gc.app.sessionPut('sessionTimeoutAtMillis', sessionTimeoutAt);
                    }
                }
            });

            return promise;
        }
    }
});
