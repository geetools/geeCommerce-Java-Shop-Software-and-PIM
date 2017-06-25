define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-merchant' ], function(app, ko, gc, merchantAPI) {

    function StoreVM(id) {
        var self = this;
        self.id = ko.observable(id);
        self.code = ko.observable();
        self.name = ko.observable();
        self.defaultLanguage = ko.observable();
        self.parentStore = ko.observable();

        self.editMode = ko.observable(false);

        self.edit = function () {
            self.editMode(true)
        }

        self.save = function () {
            self.editMode(false)

            if(self.id() != 'new'){

            } else {

            }
        }



    }
    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function MerchantTabStoresController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof MerchantTabStoresController)) {
            throw new TypeError("MerchantTabStoresController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.merchantVM = {};
        this.merchantId = ko.observable();
        this.stores = ko.observableArray([]);

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate', 'removeStore', 'addNewStore',  'initStores');
    }

    MerchantTabStoresController.prototype = {
        constructor : MerchantTabStoresController,
        saveData : function(view, parent, toolbar) {
            var self = this;
            var updates = [];

            if (!_.isEmpty(self.stores())) {

                _.each(self.stores(), function(data) {

                    var updateModel = gc.app.newUpdateModel().id(data.id() != 'new'? data.id() : null)
                        .field('code', data.code())
                        .field('name', data.name())
                        .field('defaultLanguage', data.defaultLanguage())
                        .field('parentStoreId', data.parentStore());

                    updates.push(updateModel.data());
                });

                merchantAPI.updateStores(self.merchantId(), updates).then(function(data) {
                    self.merchantVM.data = data;
                    self.initStores();
                    toolbar.hide();
                });
            }

        },
        initStores: function () {
            var self = this;
            var stores = []

            _.each(self.merchantVM.data.stores, function (store) {
                var storeVM = new StoreVM(store.id);
                storeVM.code(store.code);
                storeVM.name(store.name);
                storeVM.defaultLanguage(store.defaultLanguage);
                storeVM.parentStore(store.parentStoreId);

                stores.push(storeVM)
            })

            self.stores(stores)
        },
        activate : function(merchantId) {
            var self = this;

            self.merchantVM = gc.app.sessionGet('merchantVM');
            self.merchantId(merchantId);

            self.initStores();
        },
        addNewStore : function(data, element) {
            var self = this;
            var storeVM = new StoreVM('new');
            self.stores.unshift(storeVM)
        },
        removeStore : function(data, element) {
            var self = this;
            var _data = ko.toJS(data);

            if(_data.id == 'new'){
                self.stores.remove(data);
            } else {
                merchantAPI.removeStore(self.merchantId(), _data.id).then(function () {
                    self.stores.remove(data);
                })
            }
        },
    };

    return MerchantTabStoresController;
});