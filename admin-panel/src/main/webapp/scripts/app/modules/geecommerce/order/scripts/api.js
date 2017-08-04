define(['knockout', 'gc/gc'], function (ko, gc) {
	
	return {
        getOrder: function(orderId) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url : '/api/v1/orders/' + orderId,
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
        updateOrder: function(orderId, updateModel) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;
			
			gc.rest.put({
				url : '/api/v1/orders/' + orderId,
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
		// ----------------------------------------------------------------
		// Returns an options object that can be used by the gc-pager.
		// ----------------------------------------------------------------
        pagingOptions: function(options) {
        	
        	options = options || {};
        	
        	return {
        		// Base URI of order-API.
        		url: '/api/v1/orders',
        		filter: options.filter || [],
        		// Only load specific fields for better performance.
        		fields: options.fields || [ 'id', 'id2', 'orderNumber', 'customerId', 'totalAmount', 'discountCode', 'orderStatus', 'createdOn', 'invoiceOrderAddress', 'firstName', 'lastName', 'orderPayment', 'transactionId' ],
        		// Optionally pre-sort the results.
        		sort: options.sort || ['-createdOn'],
        		columns: options.columns,
        		// Returns an array that the pager can add to the
				// ko.observableArray.
        		getArray: function(data) {
                    _.each( data.orders, function(order) {
                        if(!order.customerId){
                            order.customerId = ""
                        }

                    });
        			return data.orders;
        		}
        	}
        }
    }
});