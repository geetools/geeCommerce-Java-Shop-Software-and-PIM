define(['jquery', 'gc/gc'], function ($, gc) {

    return {
        isCustomerLoggedIn: function () {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.get({
                url: '/api/v1/web/customers/isLoggedIn',
                success: function (data, status, xhr) {
                    if (self._onload) {
                        self._onload(data, status, xhr);
                    }

                    deferred.resolve(data, status, xhr);
                },
                error: function (jqXHR, status, error) {
                    if (self._onerror) {
                        self._onerror(jqXHR, status, error);
                    }

                    deferred.reject(jqXHR, status, error);
                },
                complete: function (data, status, xhr) {
                    if (self._oncomplete) {
                        self._oncomplete(data, status, xhr);
                    }

                    deferred.resolve(data, status, xhr);
                }
            });

            return promise;
        },
        getLoggedInCustomer: function () {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.get({
                url: '/api/v1/web/customers/getLoggedInCustomer',
                success: function (data, status, xhr) {
                    if (self._onload) {
                        self._onload(data, status, xhr);
                    }

                    deferred.resolve(data, status, xhr);
                },
                error: function (jqXHR, status, error) {
                    if (self._onerror) {
                        self._onerror(jqXHR, status, error);
                    }

                    deferred.reject(jqXHR, status, error);
                },
                complete: function (data, status, xhr) {
                    if (self._oncomplete) {
                        self._oncomplete(data, status, xhr);
                    }

                    deferred.resolve(data, status, xhr);
                }
            });

            return promise;
        },
        toggleShowPassword: function (options) {
            var settings = $.extend({
                field1: "#password",
                field2: "#passwordRepeat",
                control: "#toggleShowPasswordControl"
            }, options);

            var field1 = $(settings.field1);
            var field2 = $(settings.field2);
            var control = $(settings.control);

            control.bind('click', function () {
                if (control.is(':checked')) {
                    field1.attr('type', 'text');
                    field2.attr('type', 'text');
                } else {
                    field1.attr('type', 'password');
                    field2.attr('type', 'password');
                }
            })
        }
    };
});