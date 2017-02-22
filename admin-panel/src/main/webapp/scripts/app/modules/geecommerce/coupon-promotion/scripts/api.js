define(['knockout', 'gc/gc'], function (ko, gc) {
	
	return {
        getCouponPromotion: function(id) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url: '/api/v1/coupon-promotions/' + id,
				fields: [ '-attribute', '-productLists' ],
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
		cloneCouponPromotion: function(id) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url: '/api/v1/coupon-promotions/clone/' + id,
				fields: [ '-attribute' ],
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
        getCouponPromotions: function() {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url: '/api/v1/coupon-promotions/',
				fields: [ '-attribute' ],
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
		getCoupons: function() {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.get({
				url: '/api/v1/coupon-promotions/coupons',
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
		createCouponPromotion: function(couponPromotion) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.post({
				url : '/api/v1/coupon-promotions',
				data: couponPromotion,
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
        updateCouponPromotion: function(id, updateModel) {
			self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.put({
				url: '/api/v1/coupon-promotions/' + id,
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
		removeCouponPromotion: function(id) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			console.log('_____removeAttribute_____', id);

			gc.rest.del({
				url : '/api/v1/coupon-promotions/' + id,
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
		addProductListToPromotion: function(promotionId, productListId) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.put({
				url : '/api/v1/coupon-promotions/' + promotionId + '/product-list/' + productListId,
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
		removeProductListFromPromotion: function(promotionId, productListId) {
			var self = this;

			var deferred = new $.Deferred();
			var promise = deferred.promise();

			promise.success = promise.done;
			promise.error = promise.fail;
			promise.complete = promise.done;

			gc.rest.del({
				url : '/api/v1/coupon-promotions/' + promotionId + '/product-list/' + productListId,
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
		getProductListPagingOptions: function(couponPromotionId, pagingOptions) {
			pagingOptions = pagingOptions || {};

			return {
				url: '/api/v1/coupon-promotions/' + couponPromotionId + '/notPromotionProductLists',
				filter: pagingOptions.filter || {},
				// Only load specific fields for better performance.
				fields: pagingOptions.fields  || ['key', 'label'],
				// Optionally pre-sort the results.
				sort: pagingOptions.sort || ['key'],
                columns: pagingOptions.columns,
				// Returns an array that the pager can add to the
				// ko.observableArray.
				getArray: function(data) {
					return data.productLists;
				}
			}
		},
        getPagingOptions: function(pagingOptions) {
            pagingOptions = pagingOptions || {};

            return {
				url: '/api/v1/coupon-promotions/',
                filter: pagingOptions.filter || {},
                // Only load specific fields for better performance.
                fields: pagingOptions.fields  || ['id', 'label', 'enabled'],
                // Optionally pre-sort the results.
                sort: pagingOptions.sort || ['id'],

                columns: pagingOptions.columns,
                // Returns an array that the pager can add to the
                // ko.observableArray.
                getArray: function(data) {
					return data.couponPromotions;
                }
                // ----------------------------------------------------------------
                // Mapping used by the ko.mapping plugin.
                // ----------------------------------------------------------------
            }
        }
    }
});