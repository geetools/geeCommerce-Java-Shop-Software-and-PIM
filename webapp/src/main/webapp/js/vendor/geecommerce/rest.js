define([ 'jquery', 'gc/gc' ], function( $, gc ) {

	this.getData = function(options) {

		var requestData = {};
		var filter = options.filter;
		var fields = options.fields;
        var attributes = options.attributes;
		var sortFields = options.sort;
		var offset = options.offset;
		var limit = options.limit;
		var nocache = options.nocache;

		// ---------------------------------------------------
		// Filter
		// ---------------------------------------------------
		if (_.isObject(filter) && !_.isEmpty(filter)) {
//console.log('#################### USING FILTER', filter);
			for ( var propt in filter) {
				requestData[propt] = filter[propt];
			}
//			console.log('#################### USING requestData', requestData);

		}

		// ---------------------------------------------------
		// Fields
		// ---------------------------------------------------
		if (_.isArray(fields) && !_.isEmpty(fields)) {
//			console.log('#################### USING FIELDS', fields);
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
//			console.log('#################### USING SORT', sortFields);
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

		return requestData;
	}

	return {
		get : function(options) {

			options = options || {};

            //if(options.async == undefined)
            //    options.async = true;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			var headers = options.headers || {};
			
			if(!headers['Accept']) {
				headers['Accept'] = 'application/json';
			}
			
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
					if(jqXHR.status !== 401) {
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
				},
				beforeSend : function(xhr){
					xhr.setRequestHeader('X-Requested-By', 'public-web-user');
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
	
			var headers = options.headers || {};
			
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
					if(jqXHR.status !== 401) {
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
				},
				beforeSend : function(xhr){
					xhr.setRequestHeader('X-Requested-By', 'public-web-user');
				}
			}

			if(!headers['Accept']) {
				headers['Accept'] = 'application/json';
			}
			
			if(options.formData) {
				request.data = $.param(options.formData);
				request.headers = headers;
			} else if(options.formId) {
				request.data = $('#' + options.formId).serialize();
				request.headers = headers;
			} else {
				if(!headers['Content-Type']) {
					headers['Content-Type'] = 'application/json';
				}
				
				request.data = JSON.stringify(options.data);
				request.contentType = headers['Content-Type'];
				request.headers = headers;
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
	
			var headers = options.headers || {};
	
			cb.utils.unwrap(options.data);
			
			if(!headers['Accept']) {
				headers['Accept'] = 'application/json';
			}

			if(!headers['Content-Type']) {
				headers['Content-Type'] = 'application/json';
			}
			
			$.ajax({
				url : options.url,
				data : JSON.stringify(options.data),
				type : 'PUT',
				contentType: headers['Content-Type'],
				headers : headers,
				success : function(data, status, xhr) {
					if (options.success) {
						options.success(data, status, xhr);
					}
	
					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if(jqXHR.status !== 401) {
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
				},
				beforeSend : function(xhr){
					xhr.setRequestHeader('X-Requested-By', 'public-web-user');
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
	
			var headers = options.headers || {};
	
			if(!headers['Accept']) {
				headers['Accept'] = 'application/json';
			}

			if(!headers['Content-Type']) {
				headers['Content-Type'] = 'application/json';
			}
			
			cb.utils.unwrap(options.data);
			
			$.ajax({
				url : options.url,
				data : options.data,
				type : 'DELETE',
				headers : headers,
				success : function(data, status, xhr) {
					if (options.success) {
						options.success(data, status, xhr);
					}
	
					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if(jqXHR.status !== 401) {
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
				},
				beforeSend : function(xhr){
					xhr.setRequestHeader('X-Requested-By', 'public-web-user');
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

			cb.utils.unwrap(options.data);
			
			$.ajax({
				url : options.url,
				data : options.data,
				type : 'POST',
				cache: false,
				processData: false,
				contentType: false,
				headers:  headers,
				success : function(data, status, xhr) {
					if (options.success) {
						options.success(data, status, xhr);
					}
	
					deferred.resolve(data, status, xhr);
				},
				error : function(jqXHR, status, error) {
					if(jqXHR.status !== 401) {
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
					var sessionTimeoutAt = data.getResponseHeader('SCS-Timeout-At');
				
					if(sessionTimeoutAt) {
					    gc.app.sessionPut('sessionTimeoutAtMillis', sessionTimeoutAt);
					}
				}
			});
	
			return promise;
		}
	}
});
