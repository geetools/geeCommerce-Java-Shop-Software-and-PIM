define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product-list' ], function(app, ko, gc, productListAPI) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function ContentProductQueryController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ContentProductQueryController)) {
            throw new TypeError("ContentProductQueryController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.productListVM = {};

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'attached');
    }

    ContentProductQueryController.prototype = {
        constructor : ContentProductQueryController,
        operatorChoice: ko.observableArray([{value:"AND", label:"All"}, {value:"OR", label:"Any"}]),
        nodeTypeChoice: ko.observableArray([{value:"BOOLEAN", label:"Condition Combination"}, {value:"ATTRIBUTE", label:"Attribute"}]),

        saveData : function(view, parent, toolbar) {
            var self = this;
        },
        activate : function(productId) {
            var self = this;

            self.productListVM = gc.app.sessionGet('productListVM');

            self.ofTheseConditionsAreP1 = gc.app.i18n('app:modules.product-list.ofTheseConditionsAreP1');
            self.ofTheseConditionsAreP2 = gc.app.i18n('app:modules.product-list.ofTheseConditionsAreP2');
            self.ofTheseConditionsAreP3 = gc.app.i18n('app:modules.product-list.ofTheseConditionsAreP3');

        },
        attached : function() {
            var self = this;
        }
    }

    return ContentProductQueryController;
});