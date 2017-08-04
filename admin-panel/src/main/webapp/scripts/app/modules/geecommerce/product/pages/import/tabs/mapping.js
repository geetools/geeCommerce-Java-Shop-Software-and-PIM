define([
        'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-product', 'gc-attribute', 'gc-product/util', 'gc-import'
], function(app, ko, router, gc, productAPI, attrAPI, productUtil, importAPI) {

    // -----------------------------------------------------------------
    // Controller
    // -----------------------------------------------------------------
    function ProductImportMappingController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ProductImportMappingController)) {
            throw new TypeError("ProductImportMappingController constructor cannot be called as a function.");
        }

        this.gc = gc;
        this.app = gc.app;
        this.importVM = ko.observable({});
        this.attributes = ko.observableArray([]); // TODO!!!

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'attached');
    }

    ProductImportMappingController.prototype = {
        constructor : ProductImportMappingController,
        doImport : function(viewModel, event) {
            var self = this;
            productAPI.startImport(self.importVM().profile().token);
        },
        activate : function(data) {
            var self = this;

            var vm = gc.app.sessionGet('importVM');
            self.importVM(vm);
            
            // New token from file-upload in base.js.
            vm.token.subscribe(function(newToken) {
                
                importAPI.getProfileByToken(newToken).then(function(profile) {
                    vm.profile(profile);
                    
                    attrAPI.getAttributes('product').then(function(data) {
                        self.attributes(data.data.attributes);
                    });
                });
            });
        },
        attached : function() {
        }
    }

    return ProductImportMappingController;
});