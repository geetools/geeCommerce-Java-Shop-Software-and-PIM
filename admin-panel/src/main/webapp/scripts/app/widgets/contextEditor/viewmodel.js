define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc' ], function(app, composition, ko, i18n, gc) {
    var ctor = function() {
    };

    ctor.prototype.activate = function(options) {
        var self = this;

        self.showModal = ko.observable(false);

        self.value = options.value;
        self.title = options.title;
        self.type = options.type;
        self.multiple = options.multiple;

        self.isBoolean = options.type == 'boolean';
        self.isSelect = options.type == 'select';
        self.isText = options.type == 'text';

        self.ctxValues = ko.observableArray([]);
        self.selectData = options.selectData;

        // The context objectc must always be an array.
        if (_.isUndefined(self.value())) {
            self.value([]);
        }
    };

    ctor.prototype.initData = function() {
        var self = this;

        var ctxObject = self.value() || [];

        var ctxValues = [];

        var availableContexts = gc.app.confGet('availableContexts');
        var ctxMap = gc.app.confGet('contextMap');

        for (m = 0; m < availableContexts.length; m++) {
            var merchant = availableContexts[m];

            if (merchant.scope == 'global') {
                var globalVal = self.observable(gc.ctxobj.val(ctxObject, undefined, undefined, merchant), self.type, self.multiple).extend({
                    manageCtxObj : {
                        ctxObj : self.value,
                        context : {
                            id : '',
                            scope : 'global'
                        }
                    }
                });

                ctxValues.push({
                    name : merchant.scope.capitalize(),
                    value : globalVal,
                    scope : merchant.scope,
                    inherited : ko.observable(_.isUndefined(globalVal()))
                });

                continue;
            }

            var merchantName = ctxMap[merchant.id].name;
            var mVal = gc.ctxobj.val(ctxObject, undefined, undefined, merchant);
            var inheritedVal = false;

            if (_.isUndefined(mVal)) {
                mVal = globalVal();
                inheritedVal = true;
            }

            ctxValues.push({
                name : merchantName,
                value : self.observable(mVal, self.type, self.multiple).extend({
                    manageCtxObj : {
                        ctxObj : self.value,
                        context : merchant
                    }
                }),
                scope : merchant.scopeLabel,
                inherited : ko.observable(inheritedVal)
            });

            if (merchant.stores) {
                for (s = 0; s < merchant.stores.length; s++) {
                    var store = merchant.stores[s];
                    var storeName = ctxMap[store.id].name;
                    var sVal = gc.ctxobj.val(ctxObject, undefined, undefined, store);
                    inheritedVal = false;

                    if (_.isUndefined(sVal)) {
                        sVal = mVal;
                        inheritedVal = true;
                    }

                    ctxValues.push({
                        name : storeName,
                        value : self.observable(sVal, self.type, self.multiple).extend({
                            manageCtxObj : {
                                ctxObj : self.value,
                                context : store
                            }
                        }),
                        scope : store.scopeLabel,
                        inherited : ko.observable(inheritedVal)
                    });

                    if (store.requestContexts) {
                        for (r = 0; r < store.requestContexts.length; r++) {
                            var reqCtx = store.requestContexts[r];
                            var reqCtxName = ctxMap[reqCtx.id].name;
                            var rcVal = gc.ctxobj.val(ctxObject, undefined, undefined, reqCtx);
                            inheritedVal = false;

                            if (_.isUndefined(rcVal)) {
                                rcVal = sVal;
                                inheritedVal = true;
                            }

                            ctxValues.push({
                                name : reqCtxName,
                                value : self.observable(rcVal, self.type, self.multiple).extend({
                                    manageCtxObj : {
                                        ctxObj : self.value,
                                        context : reqCtx
                                    }
                                }),
                                scope : reqCtx.scopeLabel,
                                inherited : ko.observable(inheritedVal)
                            });
                        }
                    }
                }
            }
        }

        self.ctxValues(ctxValues);
    }

    ctor.prototype.observable = function(value, type, isMultiple) {
        if (type == 'select') {
            var returnVal;

            if (!_.isUndefined(value) && _.isArray(value)) {
                returnVal = value.slice();
            } else if (!_.isUndefined(value) && !_.isArray(value) && isMultiple) {
                returnVal = [];
                returnVal.push(value);
            } else if (!_.isUndefined(value) && !_.isArray(value) && !isMultiple) {
                returnVal = value;
            } else if (_.isUndefined(value) && isMultiple) {
                returnVal = [];
            }

            return !_.isUndefined(returnVal) && _.isArray(returnVal) ? ko.observableArray(returnVal) : ko.observable(returnVal);
        } else {
            return _.isArray(value) ? ko.observableArray(value) : ko.observable(value);
        }
    };

    ctor.prototype.attached = function(view, parent) {
        var self = this;

        $(view).on('show.bs.modal', '.modal', function(e) {
            self.initData();
        });
        
        $('.ctx-value-editor-widget .modal').draggable({
            handle: ".modal-header"
        });        
    };

    ctor.prototype.detached = function() {
    };

    return ctor;
});