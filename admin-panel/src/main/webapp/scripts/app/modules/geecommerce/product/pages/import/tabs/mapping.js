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
        nextTab : function(viewModel, event) {
        },
        activate : function(data) {
            var self = this;

            var vm = gc.app.sessionGet('importVM');
            self.importVM(vm);

            console.log('IN IMPORT MAPPING!!!');
            
            // New token from file-upload in base.js.
            vm.token.subscribe(function(newToken) {
                console.log('NEW TOKEN!!!! ', newToken);
                
                importAPI.getProfileByToken(newToken).then(function(profile) {
                    console.log('DATA !!!  --------------------->', profile);
                    
                    vm.profile(profile);
                    
                    attrAPI.getAttributes('product').then(function(data) {
                        self.attributes(data.data.attributes);
                        
                        console.log('****** DATA ATTRIBUTES !!!  ---------------------> ', data);
                    });
                });
            });
        },
        attached : function() {
            
        }
    }

    return ProductImportMappingController;
});