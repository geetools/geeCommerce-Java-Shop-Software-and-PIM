define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-discount-promotion', 'knockout-validation', 'gc-coupon' ], function(app, ko, router, gc, discountPromotionAPI, validation, couponAPI) {

    function DiscountPromotionVM(discountPromotionId) {
        var self = this;
        self.id = ko.observable(discountPromotionId);
        self.label = ko.observableArray([]);
        self.description = ko.observableArray([]);
        self.descriptionEmail = ko.observableArray([]);
        self.titlePromoPage = ko.observableArray([]);
        self.descriptionPromoPage = ko.observableArray([]);
        self.key = ko.observable();
        self.showTimes = ko.observable(1);
        self.rerunAfter = ko.observable();
        self.showFrom = ko.observable(null);
        self.showTo = ko.observable(null);
        self.couponId = ko.observable();
        self.couponDuration = ko.observable();
        self.showForAll = ko.observable();
        self.enabled = ko.observableArray([]);

        self.coupons = ko.observableArray([]);

        self.couponOptions = ko.computed(function() {
            var _options = [ { id: '', text: 'auswählen' } ];
            if(self.coupons()) {
                _.each(self.coupons(), function(coupon) {
                    if(coupon.couponCodeGeneration && coupon.couponCodeGeneration.auto){
                        _options.push({id: coupon.id, text: gc.ctxobj.val(coupon.name, gc.app.currentLang(), 'any')});
                    }
                });
            }
            return _options;
        }, self);

        self.exportOptions = ko.computed(function() {
            var _options = [ { id: 'order', text: 'Ordered' },  { id: 'expired', text: 'Expired' },  { id: 'all', text: 'All' },  { id: 'gifts', text: 'Gifts' }];
            return _options;
        }, self);

        self.export = ko.observable('order');
        self.exportUrl = ko.computed(function() {
            if(self.export() == 'order'){
                return "/api/v1/discount-promotions/" + self.id() + "/export/ordered"
            } else if(self.export() == 'expired'){
                return "/api/v1/discount-promotions/" + self.id() +"/export/expired"
            } else if(self.export() == 'all'){
                return "/api/v1/discount-promotions/" + self.id() +"/export/all"
            } else if(self.export() == 'gifts'){
                return "/api/v1/discount-promotions/" + self.id() +"/export/gifts"
            }
        }, self);

        self.isNew = ko.observable(false);
        if(discountPromotionId == 'new'){
            self.isNew(true);
        }

    }

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function DiscountPromotionBaseController(options) {
        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof DiscountPromotionBaseController)) {
            throw new TypeError("DiscountPromotionBaseController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.discountPromotionVM = ko.observable({});
        this.discountPromotionId = ko.observable();
        this.coupons = ko.observableArray();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate', 'attached');
    }

    DiscountPromotionBaseController.prototype = {
        constructor : DiscountPromotionBaseController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.discount-promotion.detailsTitle';
            var vm = ko.unwrap(self.discountPromotionVM);

            if(!_.isUndefined(vm)) {
                var name = ko.unwrap(vm.label);

                if(!_.isEmpty(name)) {
                    title += ': ' + gc.ctxobj.val(name, self.app.currentLang(), 'any');
                }
            }

            return title;
        },
        saveData : function(view, parent, toolbar) {
            var self = this;
            var updateModel = gc.app.newUpdateModel();
            console.log(self.discountPromotionVM) ;

            updateModel.field('label', self.discountPromotionVM.label(), true);
            updateModel.field('key', self.discountPromotionVM.key());
            updateModel.field('description', self.discountPromotionVM.description(), true);
            updateModel.field('descriptionEmail', self.discountPromotionVM.descriptionEmail(), true);
            updateModel.field('titlePromoPage', self.discountPromotionVM.titlePromoPage(), true);
            updateModel.field('descriptionPromoPage', self.discountPromotionVM.descriptionPromoPage(), true);

            updateModel.field('showTimes', self.discountPromotionVM.showTimes());
            updateModel.field('rerunAfter', self.discountPromotionVM.rerunAfter());
            updateModel.field('showFrom', self.discountPromotionVM.showFrom());
            updateModel.field('showTo', self.discountPromotionVM.showTo());
            updateModel.field('enabled', self.discountPromotionVM.enabled(), true);

            if(self.discountPromotionVM.couponId() == ''){
                updateModel.field('couponId', null);
            } else {
                updateModel.field('couponId', self.discountPromotionVM.couponId());
            }

            updateModel.field('couponDuration', self.discountPromotionVM.couponDuration());
            updateModel.field('showForAll', self.discountPromotionVM.showForAll());
            console.log(updateModel);

            if(self.discountPromotionVM.isNew()) {
                discountPromotionAPI.createDiscountPromotion(updateModel).then(function(data) {
                    router.navigate('//discount-promotions/details/' + data.id);
                    toolbar.hide();
                })
            } else {
                discountPromotionAPI.updateDiscountPromotion(self.discountPromotionId(), updateModel).then(function(data) {
                    toolbar.hide();
                })
            }
        },
        activate : function(data) {
            var self = this;

            self.discountPromotionId(data);
            var vm = new DiscountPromotionVM(data);
            self.discountPromotionVM = vm;
            couponAPI.getCoupons().then(function(data){
                vm.coupons(data.data.coupons);

                if(!vm.isNew()){
                    discountPromotionAPI.getDiscountPromotion(self.discountPromotionId()).then(function(data) {

                        vm.key(data.key);
                        vm.label(data.label);
                        vm.description(data.description || []);
                        vm.descriptionEmail(data.descriptionEmail || []);
                        vm.titlePromoPage(data.titlePromoPage);
                        vm.descriptionPromoPage(data.descriptionPromoPage || []);
                        vm.showTimes(data.showTimes);
                        vm.rerunAfter(data.rerunAfter);
                        vm.showFrom(data.showFrom);
                        vm.showTo(data.showTo);
                        vm.couponId(data.couponId);
                        vm.couponDuration(data.couponDuration);
                        vm.showForAll(data.showForAll);
                        vm.enabled(data.enabled);
                    });


                }
            });
            console.log("//////////////////////")
            console.log(self.discountPromotionVM)
        },
        attached : function() {
            var self = this;
        }
    }

    return DiscountPromotionBaseController;
});