define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-product-promotion', 'gc-product-list' ], function(app, ko, router, gc, productPromotionAPI, productListAPI) {

    function ProductPromotionVM(productPromotionId) {
        var self = this;
        self.id = ko.observable(productPromotionId);
        self.label = ko.observableArray([]);
        self.key = ko.observable();
        self.query = ko.observable();
        self.limit = ko.observable();
        self.slidesToShow = ko.observable();
        self.teaserId = ko.observable();

        self.displayLabel = ko.observable("");
        self.targetId = ko.observable();
        self.targetType = ko.observable();
        self.useTargetLabel = ko.observable(false);

        self.enabled = ko.observableArray([]);

        self.isNew = ko.observable(false);

        if(productPromotionId == 'new'){
            self.isNew(true);
        }



    }

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function ProductPromotionBaseController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ProductPromotionBaseController)) {
            throw new TypeError("ProductPromotionBaseController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.productPromotionVM = {};
        this.productPromotionId = ko.observable();
        this.productLists = ko.observableArray([]);

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'attached', 'saveData');
    }

    ProductPromotionBaseController.prototype = {
        constructor : ProductPromotionBaseController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.product-promotion.detailsTitle';
            var vm = ko.gc.unwrap(self.productPromotionVM);

            if(!_.isUndefined(vm)) {
                var name = vm.label;

                if(!_.isEmpty(name)) {
                    title += ': ' + gc.ctxobj.val(vm.name, self.app.currentLang(), 'any');
                }
            }

            return title;
        },
        saveData : function(view, parent, toolbar) {
            var self = this;
            var updateModel = gc.app.newUpdateModel();

            updateModel.field('label', self.productPromotionVM.label(), true);
            updateModel.field('key', self.productPromotionVM.key());
            updateModel.field('limit', self.productPromotionVM.limit());
            updateModel.field('slidesToShow', self.productPromotionVM.slidesToShow());
            updateModel.field('enabled', self.productPromotionVM.enabled(), true);
            updateModel.field('useTargetObjectLabel', self.productPromotionVM.useTargetLabel());
            updateModel.field('teaserImageId', self.productPromotionVM.teaserId());

            if(self.productPromotionVM.targetId() && self.productPromotionVM.targetId() != '' ){
                updateModel.field('targetObjectType', 'PRODUCT_LIST');
                updateModel.field('targetObjectId', self.productPromotionVM.targetId())
            }


            if(self.productPromotionVM.isNew()) {
                productPromotionAPI.createProductPromotion(updateModel).then(function(data) {
                    router.navigate('//product-promotions/details/' + data.id);
                    self.productPromotionId(data.id);
                    toolbar.hide();
                })
            } else {
                productPromotionAPI.updateProductPromotion(self.productPromotionId(), updateModel).then(function(data) {
                    self.productPromotionVM.query(data.query)
                    toolbar.hide();
                })
            }
        },
        activate : function(data) {
            var self = this;
            self.productPromotionId(data);
            var vm = new ProductPromotionVM(data);
            self.productPromotionVM = vm;

            var prdoductListArray = [];

            productListAPI.getProductLists().then(function(data){
                prdoductListArray.push( { id : '', text : function() {
                    return gc.app.i18n('app:common.choose', {}, gc.app.currentLang);
                }});

                gc.ctxobj.enhance(data.data.productLists, [ 'label' ],  'any');
                _.each(data.data.productLists, function(option) {
                    if(!_.isUndefined(option.label)){
                        prdoductListArray.push({id: option.id, text: option.label.i18n});
                    }
                });
                self.productLists(prdoductListArray);

            });

            if(!vm.isNew()){
                return productPromotionAPI.getProductPromotion(self.productPromotionId()).then(function(data) {

                    vm.key(data.key);
                    vm.query(data.query);
                    vm.label(data.label);
                    vm.limit(data.limit);
                    vm.slidesToShow(data.slidesToShow);
                    vm.enabled(data.enabled);

                    vm.displayLabel(data.displayLabel);
                    if(data.useTargetObjectLabel)
                        vm.useTargetLabel(data.useTargetObjectLabel);
                    vm.targetId(data.targetObjectId);
                    vm.targetType(data.targetObjectType);
                    vm.teaserId(data.teaserImageId)

                });
            }
        },
        attached : function() {
            var self = this;
        }
    }

    return ProductPromotionBaseController;
});