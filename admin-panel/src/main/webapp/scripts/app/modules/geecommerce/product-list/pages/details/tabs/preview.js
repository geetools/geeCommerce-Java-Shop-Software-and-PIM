define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product-list' ], function(app, ko, gc, productListAPI) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function ProductListPreviewController(options) {

        var self = this;
        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ProductListPreviewController)) {
            throw new TypeError("ProductListPreviewController constructor cannot be called as a function.");
        }

        self.app = gc.app;
        self.productListVM = {};
        self.products = ko.observableArray([]);
        self.metaRobotsOptions = ko.observableArray([ 'index, follow', 'noindex, follow', 'index, nofollow', 'noindex, nofollow' ]);

        // Callback for widget i18nEditor.
        self.unjsonDescriptionPanels = function(data) {
            var asJson = $.parseJSON(data);
            var asText = '';
            _.each(asJson, function(row) {
                asText += row.title + row.body;
            });
            return asText;
        };

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'attached');
    }

    ProductListPreviewController.prototype = {
        constructor : ProductListPreviewController,
        saveData : function(view, parent, toolbar) {
            var self = this;
        },
        activate : function(productId) {
            var self = this;

            self.productListVM = gc.app.sessionGet('productListVM');
            ko.computed(function() {
                $("#fake-preview-btn").click();
                var query = self.productListVM.query();
                if(!self.productListVM.isNew()) {
                    productListAPI.getProductListProducts(self.productListVM.id()).then(function(data){
                        productListAPI.getProductListImages(self.productListVM.id()).then(function(dataUrls){
                            var prds = [];
                            var index = 0;
                            var row = [];
                            _.each(data.data.products, function(product) {
                                product.imgUrl = _.findWhere(dataUrls.data.results, { id : product.id }).url;//dataUrls.data.results[product.id];
                                row.push(product);
                                if(index % 3 == 2){
                                    prds.push(row);
                                    row = [];
                                }
                                index++;
                            });
                            self.products(prds);
                            self.productListVM.$gc.resetLoader();

                        });
                    }, function(){
                        self.products([]);
                        self.productListVM.$gc.resetLoader();

                    });
                }
            }, self);

        },
        attached : function() {
            var self = this;
        }
    }

    return ProductListPreviewController;
});