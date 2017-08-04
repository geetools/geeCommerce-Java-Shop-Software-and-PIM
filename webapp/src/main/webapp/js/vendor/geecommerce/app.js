define(['require', 'jquery', 'bootstrap', 'gc/gc', 'postal'], function (require, $, Bootstrap, gc, postal) {

    "use strict";

    function App(options) {

        if (!(this instanceof App)) {
            throw new TypeError("App constructor cannot be called as a function.");
        }

        var self = this;
        this.utils = {};

        this.setupChannels();

    }

    App.prototype = {
        constructor: App,
        pageInfo: function (key) {
            if (!_.isUndefined(key)) {
                return $('body').data(key);
            } else {
                return {
                    module: $('body').data('module'),
                    action: $('body').data('action'),
                    event: $('body').data('event'),
                    id: $('body').data('id'),
                    api: $('body').data('api')
                };
            }
        },
        setupChannels: function () {
            this.channel = postal.channel('app');

            var tap = postal.addWireTap( function( d, e ) {
                console.log( ">>>> Postal Envelope >>>> " + JSON.stringify( e ) );
            });

        },
        render: function (options, callback) {
            var self = this;

            options = options || {};

            var sliceURI = options.slice;
            var templatePath = options.template;
            var templateData = options.data;
            var target = options.target;
            var process = options.process || false;

            if (!_.isUndefined(sliceURI)) {
                var sliceOptions = {slice: sliceURI}
                if(!process){
                    sliceOptions["target"] = target;
                }

                self.slice(sliceOptions, function (data) {
                    var html = data.html;

                    if (process) {
                        self.mustache({html: html, data: templateData, target: target}, callback);
                    } else {
                        if (!_.isUndefined(target)) {
                            $(target).html(html);
                        }

                        if (!_.isUndefined(callback) && _.isFunction(callback)) {
                            callback({target: $(target), html: html});
                        }
                    }
                });
            } else if (!_.isUndefined(templatePath)) {
                self.mustache({template: templatePath, data: templateData, target: target}, callback);
            }
        },
        mustache: function (options, callback) {
            var self = this;

            var templatePath = options.template;
            var html = options.html;
            var data = options.data;
            var target = options.target;

            if (html) {
                require(['mustache'], function (Mustache) {
                    var renderedHTML = Mustache.render(html, data);

                    if (!_.isUndefined(target)) {
                        $(target).html(renderedHTML);
                    }

                    if (!_.isUndefined(callback) && _.isFunction(callback)) {
                        callback({target: $(target), html: renderedHTML});

                    }
                });
            } else if (templatePath) {
                require(['mustache', 'text!' + templatePath], function (Mustache, html) {
                    var renderedHTML = Mustache.render(html, data);

                    if (!_.isUndefined(target)) {
                        $(target).filter(':visible').html(renderedHTML);
                    }

                    if (!_.isUndefined(callback) && _.isFunction(callback)) {
                        callback({target: $(target).filter(':visible'), html: renderedHTML});
                    }
                });
            }
        },
        fragment: function (fragmentURI, target, callback) {
            var self = this;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.get({
                url: fragmentURI,
                headers: {'Accept': 'text/html'},
                success: function (data, status, xhr) {

                    if (!_.isUndefined(target)) {
                        $(target).html(data);
                    }

                    if (!_.isUndefined(callback) && _.isFunction(callback)) {
                        callback($(target));
                    }

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
        slice: function (options, callback) {
            var self = this;

            options = options || {};

            var sliceURI = options.slice;
            var target = options.target;

            var deferred = new $.Deferred();
            var promise = deferred.promise();

            promise.success = promise.done;
            promise.error = promise.fail;
            promise.complete = promise.done;

            gc.rest.get({
                url: '/web/slice/' + sliceURI,
                headers: {'Accept': 'text/html'},
                success: function (data, status, xhr) {

                    if (!_.isUndefined(target)) {
                        $(target).html(data);
                    }

                    if (!_.isUndefined(callback) && _.isFunction(callback)) {
                        callback({target: $(target), html: data});
                    }

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
        util: function (key, requirePath) {
            var self = this;

            if (!_.isUndefined(key) && !_.isUndefined(requirePath)) {
                require([requirePath], function (util) {
                    self.utils[key] = util;
                });
            } else if (!_.isUndefined(key)) {
                return self.utils[key];
            }
        }
    }

    return App;
});