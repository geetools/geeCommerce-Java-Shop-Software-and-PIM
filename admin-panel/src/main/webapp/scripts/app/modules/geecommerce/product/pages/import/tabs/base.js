define([
        'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-product', 'gc-attribute', 'gc-product/util', 'gc-import'
], function(app, ko, router, gc, productAPI, attrAPI, productUtil, importAPI) {

    // -----------------------------------------------------------------
    // Controller
    // -----------------------------------------------------------------
    function ProductImportBaseController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ProductImportBaseController)) {
            throw new TypeError("ProductImportBaseController constructor cannot be called as a function.");
        }

        this.gc = gc;
        this.app = gc.app;
        this.importVM = ko.observable({});

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'attached');
    }

    ProductImportBaseController.prototype = {
        constructor : ProductImportBaseController,
        nextTab : function(viewModel, event) {
            
            $('#tab-prd-import-mapping>a').tab('show');            
        },
        activate : function(data) {
            var self = this;

            var vm = gc.app.sessionGet('importVM');
            self.importVM(vm);
            
//            var io = require('socket.io')(80);
//            
//            var news = io
//            .of('/api/v1/import/statuses/')
//            .on('connection', function (socket) {
//              socket.emit('item', { news: 'item' });
//            });           
        },
        attached : function() {
            var self = this;
            
            var vm = self.importVM();
            
            Dropzone.autoDiscover = false;
            
            $form = $('#prd-import-upload-form').get(0);
            
            var dz = new Dropzone($form, {
                url : '/api/v1/import/files/product'
            });

            dz.on("success", function(file) {
                vm.token(file.xhr.responseText.replace(/"/g,""));
            });
        }
    }

    return ProductImportBaseController;
});