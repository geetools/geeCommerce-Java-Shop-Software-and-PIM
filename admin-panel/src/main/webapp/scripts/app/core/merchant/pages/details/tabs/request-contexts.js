define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-merchant' ], function(app, ko, gc, merchantAPI) {


    function RequestContextVM(id) {
        var self = this;
        self.id = ko.observable(id);

        self.merchant = ko.observable();
        self.store = ko.observable();
        self.view = ko.observable();

        self.language = ko.observable();
        self.country = ko.observable();

        self.urlPrefix = ko.observable();
        self.urlType = ko.observable();

        self.isNew = ko.computed(function() {
            return self.id() == 'new';
        });

    }

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function MerchantTabRequestContextsController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof MerchantTabRequestContextsController)) {
            throw new TypeError("MerchantTabRequestContextsController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.merchantVM = {};
        this.merchantId = ko.observable();
        this.requestContexts = ko.observableArray([]);

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate', 'removeRequestContext', 'addNewRequestContext',  'initRequestContexts');
    }

    MerchantTabRequestContextsController.prototype = {
        constructor : MerchantTabRequestContextsController,
        saveData : function(view, parent, toolbar) {
            var self = this;
            var updates = [];

            if (!_.isEmpty(self.requestContexts())) {

                _.each(self.requestContexts(), function(data) {

                    var updateModel = gc.app.newUpdateModel().id(data.id() != 'new'? data.id() : null)
                    .field('merchantId', data.merchant())
                    .field('storeId', data.store())
                    .field('viewId', data.view())
                    .field('language', data.language())
                    .field('country', data.country())
                    .field('urlPrefix', data.urlPrefix())
                    .field('urlType', data.urlType());

                    updates.push(updateModel.data());
                });

                merchantAPI.updateRequestContexts(self.merchantId(), updates).then(function(data) {
                    self.initRequestContexts(data.data.requestContexts);
                    toolbar.hide();
                });
            }

        },
        initRequestContexts: function (data) {
            var self = this;
            var requestContexts = []

            _.each(data, function (requestContext) {
                var requestContextVM = new RequestContextVM(requestContext.id);
                requestContextVM.merchant(requestContext.merchantId);
                requestContextVM.store(requestContext.storeId);
                requestContextVM.view(requestContext.viewId);
                requestContextVM.language(requestContext.language);
                requestContextVM.country(requestContext.country);
                requestContextVM.urlPrefix(requestContext.urlPrefix);
                requestContextVM.urlType(requestContext.urlType);

                requestContexts.push(requestContextVM)
            })

            self.requestContexts(requestContexts)
        },
        activate : function(merchantId) {
            var self = this;

            self.merchantVM = gc.app.sessionGet('merchantVM');
            self.merchantId(merchantId);

            return merchantAPI.getRequestContexts(merchantId).then(function(data){
                self.initRequestContexts(data.data.requestContexts);
            });
        },
        addNewRequestContext : function(data, element) {
            var self = this;
            var requestContextVM = new RequestContextVM('new');
            requestContextVM.merchant(self.merchantId());
            self.requestContexts.unshift(requestContextVM)
        },
        removeRequestContext : function(data, element) {
            var self = this;
            var _data = ko.toJS(data);

            if(_data.id == 'new'){
                self.requestContexts.remove(data);
            } else {
                merchantAPI.removeRequestContext(self.merchantId(), _data.id).then(function () {
                    self.requestContexts.remove(data);
                })
            }
        },
    };

    return MerchantTabRequestContextsController;
});