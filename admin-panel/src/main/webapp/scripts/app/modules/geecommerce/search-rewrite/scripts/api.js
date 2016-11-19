define(['knockout', 'gc/gc'], function (ko, gc) {
	
	return {
        getSearchRewrite: function(searchRewriteId) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url : '/api/v1/search-rewrites/' + searchRewriteId,
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
		createSearchRewrite: function(updateModel) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.post({
				url : '/api/v1/search-rewrites/',
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
        updateSearchRewrite: function(searchRewriteId, updateModel) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____updateModel_____', updateModel.data());
			
			gc.rest.put({
				url : '/api/v1/search-rewrites/' + searchRewriteId,
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
		removeSearchRewrite: function(searchRewriteId) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.del({
				url : '/api/v1/search-rewrites/' + searchRewriteId,
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
        		url: '/api/v1/search-rewrites',
        		filter: options.filter || [],
        		// Only load specific fields for better performance.
        		fields: options.fields || [ 'id', 'keywords', 'targetUri'],
        		// Optionally pre-sort the results.
        		sort: options.sort || ['-createdOn'],
        		columns: options.columns,
        		// Returns an array that the pager can add to the
				// ko.observableArray.
        		getArray: function(data) {
					_.forEach(data.searchRewrites, function(searchRewrite){
						if(searchRewrite.keywords)
							searchRewrite.keywords = searchRewrite.keywords.join();
						else
							searchRewrite.keywords = '';
					})

        			return data.searchRewrites;
        		}
        	}
        }
    }
});