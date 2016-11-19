define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product-list' ], function(app, ko, gc, productListAPI) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function ProductListBaseController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ProductListBaseController)) {
            throw new TypeError("ProductListBaseController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.productListVM = {};
        this.filterRules = ko.observableArray([]);

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'attached');
    }

    ProductListBaseController.prototype = {
        constructor : ProductListBaseController,
        saveData : function(view, parent, toolbar) {
            var self = this;
        },
        activate : function(productId) {
            var self = this;

            productListAPI.getProductListFilterRules().then(function(data){
            
                self.filterRules.push( { id : '', text : function() {
                    return gc.app.i18n('app:common.choose', {}, gc.app.currentLang);
                }});
                
                if(data.data.productListFilterRules){
                    data.data.productListFilterRules.forEach(function(entry) {
					    gc.ctxobj.enhance(entry, [ 'label' ], 'any');
                        self.filterRules.push({id: entry.id, text: entry.label.i18n});
                    });
                }
            });

            self.productListVM = gc.app.sessionGet('productListVM');

            ko.bindingHandlers.sortable.afterMove = function(){
                $toolbar = $("#inputPrdListKey").closest('form').find('.toolbar-trigger').first();
                // Make sure that the save/cancel toolbar sees the change.
                $toolbar.click();
                $toolbar.trigger('change');
            }
        },
        attached : function() {
            var self = this;
        }
    }

    return ProductListBaseController;
});