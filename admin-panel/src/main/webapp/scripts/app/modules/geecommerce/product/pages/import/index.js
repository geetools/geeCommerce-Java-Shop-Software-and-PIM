define([
        'durandal/app', 'postal', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute-tabs', 'gc-attribute'
], function(app, postal, ko, gc, productAPI, attrTabsAPI, attrAPI) {

    function ImportVM() {
        var self = this;

        self.profileId = ko.observable();
        self.whenExists = ko.observable();
        self.onError = ko.observable();
        self.options = ko.observable();
        self.token = ko.observable();

        self.profile = ko.observable({});
        self.fieldMapping = ko.observableArray([]);
        
        self.profile.subscribe(function(newProfile) {
            var _fieldMapping = Object.keys(newProfile.fieldMapping).map(function(key) {
                return newProfile.fieldMapping[key];
            });
            
            self.fieldMapping(_fieldMapping);
            
            console.log('NEW FIELD MAPPING :::::: ', _fieldMapping);
        });

    }

    // -----------------------------------------------------------------
    // Controller
    // -----------------------------------------------------------------
    function ProductImportIndexController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ProductImportIndexController)) {
            throw new TypeError("ProductImportIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.importVM = ko.observable({});

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate');
    }

    ProductImportIndexController.prototype = {
        constructor : ProductImportIndexController,
        activate : function(data) {
            var self = this;

            var vm = new ImportVM();
            self.importVM(vm);

            gc.app.sessionPut('importVM', self.importVM);

            console.log('ProductImportIndexController!!!!!!!!!!!!!!!!! ');
        }
    }

    return ProductImportIndexController;
});