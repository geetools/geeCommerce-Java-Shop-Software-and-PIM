define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-product-promotion', 'knockout-validation' ], function(app, ko, router, gc, productPromotionAPI, validation) {
    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function ProductPromotionDetailsIndexController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ProductPromotionDetailsIndexController)) {
            throw new TypeError("ProductPromotionDetailsIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.productPromotionId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate');
    }

    ProductPromotionDetailsIndexController.prototype = {
        constructor : ProductPromotionDetailsIndexController,
        pageTitle : function() {
            return "";
        },
        pageDescription : 'app:modules.product-promotion.detailsSubtitle',
        saveData : function(view, parent, toolbar) {
            var self = this;
        },
        activate : function(data) {
            var self = this;
            this.productPromotionId(data);
        },
        attached : function() {
            var self = this;
            /*gc.app.onToolbarEvent({
             save : self.saveData
             });*/
        },
        compositionComplete : function() {
        },
        detached : function() {
        },
        deactivate : function() {
        }
    }

    return ProductPromotionDetailsIndexController;
});