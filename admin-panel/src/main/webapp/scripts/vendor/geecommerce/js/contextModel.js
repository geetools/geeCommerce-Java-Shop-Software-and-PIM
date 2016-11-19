define([ 'knockout', 'gc/gc' ], function(ko, gc) {

    "use strict";

    /**
     * Class constructor.
     */
    function ContextModel(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ContextModel)) {
            throw new TypeError("ContextModel constructor cannot be called as a function.");
        }

        options = options || {};

        var self = this;

        this._merchantIds = ko.observableArray([]);
        this._storeIds = ko.observableArray([]);
        this._requestContextIds = ko.observableArray([]);

        var isGlobal = this._merchantIds().length == 0 && this._storeIds().length == 0 && this._requestContextIds().length == 0;
        this._global = ko.observable(isGlobal);

        this._global.subscribe(function(newVal) {
            if (newVal === true) {
                self._merchantIds([]);
                self._storeIds([]);
                self._requestContextIds([]);
            }
        });

        this._merchantIds.subscribe(function(newVal) {
            if (!_.isEmpty(newVal)) {
                self.updateGlobal();
                self._storeIds([]);
                self._requestContextIds([]);
            }
        });

        this._storeIds.subscribe(function(newVal) {
            if (!_.isEmpty(newVal)) {
                self.updateGlobal();
                self._merchantIds([]);
                self._requestContextIds([]);
            }
        });

        this._requestContextIds.subscribe(function(newVal) {
            if (!_.isEmpty(newVal)) {
                self.updateGlobal();
                self._merchantIds([]);
                self._storeIds([]);
            }
        });

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'fromData', 'addMerchantId', 'addStoreId', 'addRequestContextId', 'updateGlobal', 'toggleMerchantId', 'toggleStoreId', 'toggleRequestContextId', 'setMerchantId', 'setStoreId', 'setRequestContextId', 'setMerchantIds',
            'setStoreIds', 'setRequestContextIds', 'merchantIds', 'storeIds', 'requestContextIds');
    }

    ContextModel.prototype = {
        constructor : ContextModel,
        fromData : function(data) {
            var data = data || {};

            if (data.merchantIds) {
                this._merchantIds(data.merchantIds);
            }

            if (data.storeIds) {
                this._storeIds(data.storeIds);
            }

            if (data.requestContextIds) {
                this._requestContextIds(data.requestContextIds);
            }
        },
        addMerchantId : function(merchantId) {
            if (_.isObject(merchantId)) {
                merchantId = merchantId.id;
            }

            if (this._merchantIds.indexOf(merchantId) == -1) {
                this._merchantIds.push(merchantId);
            }
            return this;
        },
        addStoreId : function(storeId) {
            if (_.isObject(storeId)) {
                storeId = storeId.id;
            }

            if (this._storeIds.indexOf(storeId) == -1) {
                this._storeIds.push(storeId);
            }
            return this;
        },
        addRequestContextId : function(reqCtxId) {
            if (_.isObject(reqCtxId)) {
                reqCtxId = reqCtxId.id;
            }

            if (this._requestContextIds.indexOf(reqCtxId) == -1) {
                this._requestContextIds.push(reqCtxId);
            }
            return this;
        },
        updateGlobal : function() {
            var isGlobal = this._merchantIds().length == 0 && this._storeIds().length == 0 && this._requestContextIds().length == 0;
            this._global(isGlobal);

            return this;
        },
        toggleMerchantId : function(merchantId) {
            if (_.isObject(merchantId)) {
                merchantId = merchantId.id;
            }

            if (this._merchantIds.indexOf(merchantId) == -1) {
                this._merchantIds.push(merchantId);
            } else {
                this._merchantIds.remove(merchantId);
            }
            return this;
        },
        toggleStoreId : function(storeId) {
            if (_.isObject(storeId)) {
                storeId = storeId.id;
            }

            if (this._storeIds.indexOf(storeId) == -1) {
                this._storeIds.push(storeId);
            } else {
                this._storeIds.remove(storeId);
            }
            return this;
        },
        toggleRequestContextId : function(reqCtxId) {
            if (_.isObject(reqCtxId)) {
                reqCtxId = reqCtxId.id;
            }

            if (this._requestContextIds.indexOf(reqCtxId) == -1) {
                this._requestContextIds.push(reqCtxId);
            } else {
                this._requestContextIds.remove(reqCtxId);
            }
            return this;
        },
        setMerchantId : function(merchantId) {
            if (_.isObject(merchantId)) {
                merchantId = merchantId.id;
            }

            this._merchantIds([]);
            this._merchantIds.push(merchantId);
            return this;
        },
        setStoreId : function(storeId) {
            if (_.isObject(storeId)) {
                storeId = storeId.id;
            }

            this._storeIds([]);
            this._storeIds.push(storeId);
            return this;
        },
        setRequestContextId : function(reqCtxId) {
            if (_.isObject(reqCtxId)) {
                reqCtxId = reqCtxId.id;
            }

            this._requestContextIds([]);
            this._requestContextIds.push(reqCtxId);
            return this;
        },
        setMerchantIds : function(merchantIds) {
            this._merchantIds(merchantIds);
            return this;
        },
        setStoreIds : function(storeIds) {
            this._storeIds(storeIds);
            return this;
        },
        setRequestContextIds : function(reqCtxIds) {
            this._requestContextIds(reqCtxIds);
            return this;
        },
        merchantIds : function() {
            return this._merchantIds();
        },
        storeIds : function() {
            return this._storeIds();
        },
        requestContextIds : function() {
            return this._requestContextIds();
        },
        isGlobal : function() {
            return this._merchantIds().length == 0 && this._storeIds().length == 0 && this._requestContextIds().length == 0;
        },
        containsMerchantId : function(merchantId) {
            if (_.isObject(merchantId)) {
                merchantId = merchantId.id;
            }

            return this._merchantIds.indexOf(merchantId) != -1;
        },
        containsStoreId : function(storeId) {
            return this._storeIds.indexOf(storeId) != -1;
        },
        containsRequestContextId : function(reqCtxId) {
            return this._requestContextIds.indexOf(reqCtxId) != -1;
        }
    };

    return ContextModel;
});
