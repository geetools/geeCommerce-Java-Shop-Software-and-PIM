define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-discount-promotion', 'knockout-validation', 'gc-coupon' ], function(app, ko, router, gc, discountPromotionAPI, validation, couponAPI) {


    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function DiscountPromotionDetailsIndexController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof DiscountPromotionDetailsIndexController)) {
            throw new TypeError("DiscountPromotionDetailsIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.discountPromotionId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate');
    }

    DiscountPromotionDetailsIndexController.prototype = {
        constructor : DiscountPromotionDetailsIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.discount-promotion.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.discount-promotion.detailsSubtitle',
        saveData : function(view, parent, toolbar) {


        },
        activate : function(data) {
            var self = this;
            self.discountPromotionId(data);
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

    return DiscountPromotionDetailsIndexController;
});