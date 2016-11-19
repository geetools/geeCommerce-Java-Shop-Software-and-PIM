define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc' ], function(app, composition, ko, i18n, gc) {
    var ctor = function() {
    };

    ctor.prototype.activate = function(options) {
        var self = this;

        self.title = options.title;

        self.isBoolean = options.type == 'boolean';

        self.ctxValues = ko.observableArray([]);

        ko.computed(function() {

            // if (options.value()) {
            var ctxObject = options.value() || [];
            var data = ko.unwrap(options.data);
            var ctxValues = [];
            var ctxValuesTable = '';

            var availableContexts = gc.app.confGet('availableContexts');
            var ctxMap = gc.app.confGet('contextMap');

            for (m = 0; m < availableContexts.length; m++) {
                var merchant = availableContexts[m];
                var globalVal;
                var merchantVal;
                var storeVal;
                var reqCtxVal;
                var inheritedVal = false;

                if (merchant.scope == 'global') {
                    // globalVal = gc.ctxobj.global(ctxObject);
                    globalVal = self.findValue(ctxObject, merchant, data);

                    var val = '-';
                    if (!_.isUndefined(globalVal)) {
                        if (self.isBoolean) {
                            val = gc.app.i18n('app:common.' + globalVal);
                        } else {
                            if (!_.isUndefined(globalVal.text) && _.isFunction(globalVal.text)) {
                                val = globalVal.text();
                            } else {
                                val = globalVal;
                            }
                        }
                    }

                    ctxValues.push({
                        name : merchant.scope.capitalize(),
                        value : val,
                        scope : merchant.scope
                    });

                    continue;
                }

                var merchantName = ctxMap[merchant.id].name;
                merchantVal = self.findValue(ctxObject, merchant, data);

                if (_.isUndefined(merchantVal)) {
                    merchantVal = globalVal;
                    inheritedVal = true;
                }

                if (!_.isUndefined(merchantVal)) {
                    ctxValues.push({
                        name : merchantName,
                        value : self.isBoolean ? gc.app.i18n('app:common.' + merchantVal) : merchantVal,
                        scope : merchant.scopeLabel,
                        inherited : inheritedVal
                    });
                } else {
                    ctxValues.push({
                        name : merchantName,
                        value : '-',
                        scope : merchant.scopeLabel,
                        inherited : false
                    });
                }

                if (merchant.stores) {
                    for (s = 0; s < merchant.stores.length; s++) {
                        var store = merchant.stores[s];
                        var storeName = ctxMap[store.id].name;
                        storeVal = self.findValue(ctxObject, store, data);
                        inheritedVal = false;

                        if (_.isUndefined(storeVal)) {
                            storeVal = merchantVal;
                            inheritedVal = true;
                        }

                        if (!_.isUndefined(storeVal)) {
                            ctxValues.push({
                                name : storeName,
                                value : self.isBoolean ? gc.app.i18n('app:common.' + storeVal) : storeVal,
                                scope : store.scopeLabel,
                                inherited : inheritedVal
                            });
                        } else {
                            ctxValues.push({
                                name : storeName,
                                value : '-',
                                scope : store.scopeLabel,
                                inherited : false
                            });
                        }

                        if (store.requestContexts) {
                            for (r = 0; r < store.requestContexts.length; r++) {
                                var reqCtx = store.requestContexts[r];
                                var reqCtxName = ctxMap[reqCtx.id].name;
                                reqCtxVal = self.findValue(ctxObject, reqCtx, data);
                                inheritedVal = false;

                                if (_.isUndefined(reqCtxVal)) {
                                    reqCtxVal = storeVal;
                                    inheritedVal = true;
                                }

                                if (!_.isUndefined(reqCtxVal)) {
                                    ctxValues.push({
                                        name : reqCtxName,
                                        value : self.isBoolean ? gc.app.i18n('app:common.' + reqCtxVal) : reqCtxVal,
                                        scope : reqCtx.scopeLabel,
                                        inherited : inheritedVal
                                    });
                                } else {
                                    ctxValues.push({
                                        name : reqCtxName,
                                        value : '-',
                                        scope : reqCtx.scopeLabel,
                                        inherited : false
                                    });
                                }
                            }
                        }
                    }
                }
            }

            self.ctxValues(ctxValues);
        });
    };

    ctor.prototype.findValue = function(ctxObject, context, data) {
        var ctxVal = gc.ctxobj.val(ctxObject, undefined, undefined, context);

        if (!_.isUndefined(ctxVal)) {
            if (!_.isUndefined(data)) {
                if (_.isArray(ctxVal)) {
                    ctxVal = _.findWhere(ko.unwrap(data), {
                        id : ctxVal[0]
                    });
                } else {
                    ctxVal = _.findWhere(ko.unwrap(data), {
                        id : ctxVal
                    });
                }
            }

            if (!_.isUndefined(ctxVal) && !_.isUndefined(ctxVal.text) && _.isFunction(ctxVal.text)) {
                ctxVal = ctxVal.text();
            }
        }

        return ctxVal;
    };

    return ctor;
});