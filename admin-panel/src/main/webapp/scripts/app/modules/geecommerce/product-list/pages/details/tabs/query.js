define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product-list' ], function(app, ko, gc, productListAPI) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function ProductListQueryController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ProductListQueryController)) {
            throw new TypeError("ProductListQueryController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.productListVM = {};

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'attached');
    }

    ProductListQueryController.prototype = {
        constructor : ProductListQueryController,
        saveData : function(view, parent, toolbar) {
            var self = this;
        },
        activate : function(productId) {
            var self = this;

            self.productListVM = gc.app.sessionGet('productListVM');
        },
        attached : function() {
            var self = this;
        }
    }

    return ProductListQueryController;
});