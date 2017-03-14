define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-coupon', 'knockout-validation', 'gc-customer', 'gc-price','gc-product-list' ], function(app, ko, router, gc, couponAPI, validation, customerAPI, priceAPI, productListAPI) {

    function CouponVM(couponId) {
        var self = this;
        self.id = ko.observable(couponId);
        self.name = ko.observable().extend({ require_i18n: 'any' });
        self.description = ko.observable().extend({ require_i18n: 'any' });
        self.fromDate = ko.observable(new Date().toISOString()).extend({ required: true, date: true });

        var d = new Date();
        d.setMonth( d.getMonth( ) + 1 );

        self.toDate = ko.observable(d).extend({ required: true, date: true});
        self.usesPerCoupon = ko.observable(1000).extend({ required: true, number: true, min: 0});
        self.usesPerCustomer = ko.observable(1).extend({ required: true, number: true, min: 0});
        self.auto = ko.observable();
        self.erpCode = ko.observable();
        self.enabled = ko.observableArray([]);

        self.isNew = ko.observable(false);

        self.isClone = ko.observable(false);


        self.validAfterFirstUse = ko.observable();

        self.generation = new CouponCodeGenerationVM(self);

        self.possibleGenerateAdditionalCodes = ko.observable(false);

        if(couponId == 'new'){
            self.isNew(true);
        }

        if(couponId.indexOf("clone") == 0){
            self.isNew(true);
            self.isClone(true);
        }

        self.couponAction = new CouponActionVM();

        self.couponCondition = ko.observable();
        self.customerGroupIds = ko.observableArray([]);
        self.customerGroups = ko.observableArray();
        self.priceTypeIds = ko.observableArray([]);
        self.priceTypes = ko.observableArray();

        self.rawProductAttributesOptions = ko.observable();
        self.optionsForProductAttributes = ko.observable();
        self.couponActionTypesOptions = ko.observableArray();
        self.productSelectionTypeOptions = ko.observableArray();
        self.productAttributesOptions = ko.observableArray();
        self.productListOptions = ko.observableArray();
        self.cartAttributesOptions = ko.observableArray();
        self.cartItemAttributesOptions = ko.observableArray();
        self.couponFilterNodeTypes = ko.observableArray();
        self.couponFilterOperators = ko.observableArray();

        self.customerGroupOptions = ko.computed(function() {
            var _options = [ { id: '', text: 'auswählen' } ];
            if(self.customerGroups()){
                _.each(self.customerGroups(), function(option) {
                    _options.push({id: option.id, text: gc.ctxobj.val(option.label, gc.app.currentLang(), 'any')});
                })
            }
            return _options;
        }, self);

        self.priceTypeOptions = ko.computed(function() {
            var _options = [ { id: '', text: 'auswählen' } ];
            if(self.priceTypes()){
                _.each(self.priceTypes(), function(option) {
                    _options.push({id: option.id, text: gc.ctxobj.val(option.label, gc.app.currentLang(), 'any')});
                })
            }
            return _options;
        }, self);

        self.enableOpt = ko.observable(false);


        /*self.isValid = function(){
         return self.couponAction.isValid();
         }*/
    }

    function CouponCodeGenerationVM(parent) {
        var self = this;

        self.parent = ko.observable(parent);
        self.auto = ko.observable(false);
        self.code = ko.observable().extend({
            required: {
                onlyIf: function () {
                    console.log(self.parent().isNew() );
                    console.log(self.auto() );
                    console.log(self.parent().auto() );
                    return self.parent().isNew() && !self.auto() && !self.parent().auto(); }
            },
            validation: {
                async: true,
                validator: function (val, param, callback) {
                    var res = couponAPI.getIsCodeUnique(val).then(function(result) {
                        callback(result.data.results);
                    });
                },
                message: 'Coupon code should be unique',
                onlyIf: function () { return self.parent().isNew() && !self.auto() && !self.parent().auto(); }
            }
        });
        self.quantity = ko.observable().extend({
            required: {
                onlyIf: function () { return self.parent().isNew() && self.auto(); }
            }
        });
        self.length = ko.observable().extend({
            required: {
                onlyIf: function () { return self.parent().isNew() && self.auto() && !self.isPattern(); }
            }
        });
        self.pattern = ko.observable().extend({
            required: {
                onlyIf: function () { return self.parent().isNew() && self.auto(); }
            }
        });
        self.prefix = ko.observable();
        self.postfix = ko.observable();

        self.patterns = {};

        self.isPattern = ko.computed(function() {
            var p = self.patterns[self.pattern()];
            if(p){
                return p.isPattern;
            }
            return false;
        }, self);
    }

    function CouponRangeDiscountVM(parent){
        var self = this;
        self.parent = ko.observable(parent);
        self.discountAmount = ko.observable(0);
        self.fromAmount = ko.observable(0);
        self.toAmount = ko.observable(0);

        self.deleteNode = function(){
            self.parent().rangeDiscountAmount.remove(self);
        };

    }

    function CouponListDiscountVM(parent){
        var self = this;
        self.parent = ko.observable(parent);
        self.discountAmount = ko.observable(0);

        self.deleteNode = function(){
            self.parent().discountAmounts.remove(self);
        };

    }

    function CouponActionVM() {
        var self = this;
        self.type = ko.observable('PERCENT_PRODUCT').extend({ required: true });

        self.productIds = ko.observableArray([]);
        self.products = ko.observableArray([]);
        self.productListIds = ko.observableArray([]);
        self.productSelectionType = ko.observable('LIST');
        self.productSortOrder = ko.observable('ASC');

        self.freeShipping = ko.observable();
        self.discountAmount = ko.observable().extend({ //required: true,
            max:{
                onlyIf:function() { return (self.type() === 'PERCENT_PRODUCT' || self.type() === 'PERCENT_CART')},
                params:100
            },
            min: 0,
            number: true});
        self.discountAmounts = ko.observableArray([]);
        self.rangeDiscountAmount = ko.observableArray([]);
        self.discountQtyStep = ko.observable().extend({
            required: {
                onlyIf: function () { return (self.type() === 'BUY_X_GET_Y_FREE_SAME' || self.type() === 'BUY_X_GET_Y_FREE' || self.type() === 'SPEND_X_GET_Y_FREE'); }
            },
            min: 0,
            number: true
        });
        self.maximumQtyApplyTo = ko.observable().extend({
            min: 0,
            number: true
        });
        self.filter = ko.observable();
        self.priceTypeId = ko.observable();

        self.addRangeDiscountAmount = function(target, reason){
            //if( !(reason === 'nochange' || reason === 'save')) return;
            var item = new CouponRangeDiscountVM(self);
            self.rangeDiscountAmount.push(item);
        }

        self.addDiscountAmount = function(target, reason){
            var item = new CouponListDiscountVM(self);
            self.discountAmounts.push(item);
        }

        /* self.isValid = function(){
         return self.discountAmount.isValid() && self.discountQtyStep.isValid();
         }*/
    }

    function CouponFilterNodeVM(parent, couponVM){
        var self = this;

        self.couponVM = couponVM;
        self.type = ko.observable();
        self.operator = ko.observable();
        self.operation = ko.observable();
        self.operationValue = ko.observable();
        self.attributeType = ko.observable();
        self.attributeCode = ko.observable();
        self.value = ko.observable();
        self.nodes = ko.observableArray([]);
        self.parent = ko.observable(parent);

        self.newNodeType = ko.observable();

        self.couponFilterOperators = ko.computed(function () {
            if(self.type() == 'FIlTER_ATTRIBUTE_OPERATION' && self.attributeType() == 'PRODUCT'){
                if(self.inputType() == 'SELECT' || self.inputType() == 'BOOLEAN' ){
                    var result = [];
                    console.log(self.couponVM.couponFilterOperators())
                    result.push(_.where(self.couponVM.couponFilterOperators(), {value: "=="})[0])
                    result.push(_.where(self.couponVM.couponFilterOperators(), {value: "!="})[0])
                    console.log(result);
                    return result;
                } else {
                    return self.couponVM.couponFilterOperators();
                }
            } else {
                return self.couponVM.couponFilterOperators();
            }
        });


        self.options = ko.computed(function() {
            var _options = [ { id: '', text: 'auswählen' } ];
            if(self.couponVM.enableOpt()){
                var attr = self.couponVM.rawProductAttributesOptions()[self.attributeCode()];
                if(attr){
                    _.each(self.couponVM.optionsForProductAttributes()[attr.id], function(option) {
                        _options.push({id: option.id, text: gc.ctxobj.val(option.label, gc.app.currentLang(), 'any')});
                    })
                }
            }
            return _options;
        }, self);
        self.inputType = ko.computed(function() {
            if(!self.couponVM.enableOpt())
                return 'TEXT';
            if(self.type() != 'FIlTER_ATTRIBUTE_OPERATION' && self.attributeType() != 'PRODUCT')
                return 'TEXT';
            if(!self.couponVM.rawProductAttributesOptions() || !self.couponVM.rawProductAttributesOptions()[self.attributeCode()])
                return 'TEXT';
            return self.couponVM.rawProductAttributesOptions()[self.attributeCode()].frontendInput;
        }, self);

        self.deleteNode = function(){
            self.parent().nodes.remove(self);
        };

        self.addNode = function(target, reason){
            if( !(reason === 'nochange' || reason === 'save')) return;
            var nodeVM = new CouponFilterNodeVM(self, self.couponVM);
            if(self.newNodeType() === '1'){
                nodeVM.type('BOOLEAN_OPERATION');
                nodeVM.operation('AND');
                nodeVM.operationValue(true);
            } else if (self.newNodeType() === '2' || self.newNodeType() === '3' || self.newNodeType() === '11'){
                nodeVM.type('FIlTER_ATTRIBUTE_OPERATION');
                nodeVM.operator("==");
                if(self.newNodeType() === '2'){
                    nodeVM.attributeType('CART_ITEM');
                } else if(self.newNodeType() === '3'){
                    nodeVM.attributeType('PRODUCT');
                } else if(self.newNodeType() === '11'){
                    nodeVM.attributeType('CART');
                }
            } else if (self.newNodeType() === '12'){
                nodeVM.type('FOUND');
                nodeVM.operation('AND');
            }
            self.nodes.push(nodeVM);
        }
    }

    return {
        app : gc.app,
        couponVM : {},

        couponId : ko.observable(),
        pageTitle : function() {
            var self = this;
            var title = 'Gutschein';
            var vm = ko.unwrap(self.couponVM);

            if(!_.isUndefined(vm)) {
                var name = ko.unwrap(vm.name);

                if(!_.isEmpty(name)) {
                    title += ': ' + gc.ctxobj.val(name, self.app.currentLang(), 'any');
                }
            }

            return title;
        },
        pageDescription : 'Gutschein ansehen und bearbeiten',
        saveData : function(view, parent, toolbar) {
            var self = this;
            self.couponVM = gc.app.sessionGet('couponVM');

            /*if(!self.couponVM.isValid())
             return;*/

            var result = ko.validation.group([self.couponVM.fromDate,
                self.couponVM.toDate,
                self.couponVM.name,
                // self.couponVM.description//,
                self.couponVM.usesPerCoupon,
                self.couponVM.usesPerCustomer,
                self.couponVM.generation.code,
                self.couponVM.generation.quantity,
                self.couponVM.generation.length,
                self.couponVM.generation.pattern
            ], {deep: false});

            if (result().length > 0)
            {
                result.showAllMessages(true);
                $('#tab-coupon-details-base').tab('show');
                toolbar.hide();
                
                var ok = gc.app.i18n('app:common.ok');
        		
    			app.showMessage(gc.app.i18n('app:modules.coupon.savingUnsuccessful'), null, [ok]).then(function(confirm) {

    			});
                
                return false;
            }


            result = ko.validation.group([
                self.couponVM.couponAction.type,
                self.couponVM.couponAction.discountAmount,
                self.couponVM.couponAction.discountQtyStep
            ], {deep: false});

            if (result().length > 0)
            {
                result.showAllMessages(true);
                $('#tab-coupon-details-action').tab('show');
                toolbar.hide();
                return false;
            }


            var updates = [];

            var couponUpdateModel = gc.app.newUpdateModel();
            couponUpdateModel.field('name', self.couponVM.name(), true)
                .field('description', self.couponVM.description(), true)
                .field('fromDate', gc.utils.toServerTime(gc.utils.startOfTheDay(self.couponVM.fromDate())))
                .field('toDate', gc.utils.toServerTime(gc.utils.endOfTheDay(self.couponVM.toDate())))
                .field('usesPerCoupon', self.couponVM.usesPerCoupon())
                .field('usesPerCustomer', self.couponVM.usesPerCustomer())
                .field('validAfterFirstUse', self.couponVM.validAfterFirstUse())
                .field('auto', self.couponVM.auto())
                .field('erpCode', self.couponVM.erpCode())
                .field('enabled', self.couponVM.enabled(), true)
                .field('condition', ko.toJSON(self.couponVM.couponCondition(),["type", "operator", "operation",
                    "operationValue", "attributeType", "attributeCode", "value", "nodes"]));
            if(self.couponVM.priceTypeIds() && self.couponVM.priceTypeIds().length > 0)
                couponUpdateModel.field('priceTypeIds', self.couponVM.priceTypeIds());
            if(self.couponVM.customerGroupIds() && self.couponVM.customerGroupIds().length > 0)
                couponUpdateModel.field('customerGroupIds', self.couponVM.customerGroupIds());


            console.log(self.couponVM.couponAction.discountAmounts());



            var couponActionUpdateModel = gc.app.newUpdateModel();
            couponActionUpdateModel.field('type', self.couponVM.couponAction.type())
                .field('freeShipping', self.couponVM.couponAction.freeShipping())
                .field('discountAmount', self.couponVM.couponAction.discountAmount())
                .field('rangeDiscountAmount', ko.toJSON(self.couponVM.couponAction.rangeDiscountAmount(),["discountAmount", "fromAmount", "toAmount"]))
                .field('discountQtyStep', self.couponVM.couponAction.discountQtyStep())
                .field('maximumQtyApplyTo', self.couponVM.couponAction.maximumQtyApplyTo())
                .field('priceTypeId', self.couponVM.couponAction.priceTypeId())
                .field('productIds', self.couponVM.couponAction.productIds())
                .field('productListIds', self.couponVM.couponAction.productListIds())
                .field('productSelectionType', self.couponVM.couponAction.productSelectionType())
                .field('discountOrder', self.couponVM.couponAction.productSortOrder())
                .field('filter', ko.toJSON(self.couponVM.couponAction.filter(),["type", "operator", "operation",
                    "operationValue", "attributeType", "attributeCode", "value", "nodes"]));

            if(self.couponVM.couponAction.discountAmounts() && self.couponVM.couponAction.discountAmounts().length > 0) {
                var discountAmounts = [];
                self.couponVM.couponAction.discountAmounts().each(function(elem, index) {
                    discountAmounts.push(elem.discountAmount());
                });
                couponActionUpdateModel.field('discountAmounts', discountAmounts);
            }

            updates.push(couponUpdateModel.data());
            updates.push(couponActionUpdateModel.data());
            if(self.couponVM.isNew()){
                var couponCodeGeneratorUpdateModel = gc.app.newUpdateModel();
                couponCodeGeneratorUpdateModel.field('auto', self.couponVM.generation.auto())
                    .field('code', self.couponVM.generation.code())
                    .field('pattern', self.couponVM.generation.pattern())
                    .field('quantity', self.couponVM.generation.quantity())
                    .field('length', self.couponVM.generation.length())
                    .field('prefix', self.couponVM.generation.prefix())
                    .field('postfix', self.couponVM.generation.postfix());

                updates.push(couponCodeGeneratorUpdateModel.data());
                couponAPI.createCoupon(updates).then(function(data) {
                    router.navigate('//coupons/details/' + data.id);
                    toolbar.hide();
                });
            } else {
                couponAPI.updateCoupon(self.couponVM.id(), updates).then(function(data){
                    toolbar.hide();
                });
            }

        },
        activate : function(data) {
            var self = this;
            self.couponId(data);

            self.couponVM = new CouponVM(data);

            gc.app.pageTitle(self.pageTitle());
            gc.app.pageDescription(self.pageDescription);


            self.couponVM.productSelectionTypeOptions.push( { id : 'PRODUCT', text : function() {
                return gc.app.i18n('app:modules.coupon.productSelectionTypeProduct', {}, gc.app.currentLang);
            }});

            self.couponVM.productSelectionTypeOptions.push( { id : 'LIST', text : function() {
                return gc.app.i18n('app:modules.coupon.productSelectionTypeList', {}, gc.app.currentLang);
            }});

            self.couponVM.productSelectionTypeOptions.push( { id : 'QUERY', text : function() {
                return gc.app.i18n('app:modules.coupon.productSelectionTypeQuery', {}, gc.app.currentLang);
            }});

            self.couponVM.couponActionTypesOptions.push( { id : 'PERCENT_PRODUCT', text : function() {
                return gc.app.i18n('app:modules.coupon.typeActionPercentProduct', {}, gc.app.currentLang);
            }});
            self.couponVM.couponActionTypesOptions.push( { id : 'FIXED_PRODUCT', text : function() {
                return gc.app.i18n('app:modules.coupon.typeActionFixedProduct', {}, gc.app.currentLang);
            }});
            self.couponVM.couponActionTypesOptions.push( { id : 'LIST_PERCENT_PRODUCT', text : function() {
                return gc.app.i18n('app:modules.coupon.typeActionListPercentProduct', {}, gc.app.currentLang);
            }});
            self.couponVM.couponActionTypesOptions.push( { id : 'LIST_FIXED_PRODUCT', text : function() {
                return gc.app.i18n('app:modules.coupon.typeActionListFixedProduct', {}, gc.app.currentLang);
            }});
            self.couponVM.couponActionTypesOptions.push( { id : 'PERCENT_CART', text : function() {
                return gc.app.i18n('app:modules.coupon.typeActionPercentCart', {}, gc.app.currentLang);
            }});
            self.couponVM.couponActionTypesOptions.push( { id : 'FIXED_CART', text : function() {
                return gc.app.i18n('app:modules.coupon.typeActionFixedCart', {}, gc.app.currentLang);
            }});
            self.couponVM.couponActionTypesOptions.push( { id : 'BUY_X_GET_Y_FREE', text : function() {
                return gc.app.i18n('app:modules.coupon.typeActionBuyXGetYFree', {}, gc.app.currentLang);
            }});
            self.couponVM.couponActionTypesOptions.push( { id : 'BUY_X_GET_Y_FREE_SAME', text : function() {
                return gc.app.i18n('app:modules.coupon.typeActionBuyXGetYFreeSame', {}, gc.app.currentLang);
            }});
            self.couponVM.couponActionTypesOptions.push( { id : 'SPEND_X_GET_Y_FREE', text : function() {
                return gc.app.i18n('app:modules.coupon.typeActionSpendXGetYFree', {}, gc.app.currentLang);
            }});
            self.couponVM.couponActionTypesOptions.push( { id : 'RANGE_PERCENT_CART', text : function() {
                return gc.app.i18n('app:modules.coupon.typeActionRangePercentCart', {}, gc.app.currentLang);
            }});
            self.couponVM.couponActionTypesOptions.push( { id : 'RANGE_FIXED_CART', text : function() {
                return gc.app.i18n('app:modules.coupon.typeActionRangeFixedCart', {}, gc.app.currentLang);
            }});


            var productListArray = [];
            productListAPI.getProductLists().then(function(data){
                _.each(data.data.productLists, function(productList) {
                    productListArray.push({
                            id : productList.id,
                            text : gc.ctxobj.val(productList.label, gc.app.currentUserLang(), "closest")
                        });
                });
                self.couponVM.productListOptions(productListArray);
            });

            couponAPI.getProductAttributes().then(function(data) {
                var rawProductAttributesOption = {};
                data.data.results.each(function(elem, index) {
                    rawProductAttributesOption[elem.value] = elem;
                });
                self.couponVM.rawProductAttributesOptions(rawProductAttributesOption);

                self.couponVM.productAttributesOptions(data.data.results);

                couponAPI.getProductAttributeOptions().then(function(data) {
                    var optionsForProductAttributes = {};
                    data.data['attribute-options'].each(function(elem, index) {
                        if(!optionsForProductAttributes[elem.attributeId])
                        {
                            var array = [];
                            array.push(elem);
                            optionsForProductAttributes[elem.attributeId] = array;
                        }
                        else
                        {
                            optionsForProductAttributes[elem.attributeId].push(elem)
                        }
                    });
                    self.couponVM.optionsForProductAttributes(optionsForProductAttributes)
                    self.couponVM.enableOpt(true);
                });
            });

            couponAPI.getCartAttributes().then(function(data) {
                self.couponVM.cartAttributesOptions(data.data.results);
            });

            couponAPI.getCartItemAttributes().then(function(data) {
                self.couponVM.cartItemAttributesOptions(data.data.results);
            });

            couponAPI.getCouponFilterNodeTypes().then(function(data) {
                self.couponVM.couponFilterNodeTypes(data.data.results);
            });

            couponAPI.getCouponFilterOperators().then(function(data) {
                self.couponVM.couponFilterOperators(data.data.results);
            });

            customerAPI.getCustomerGroups().then(function(data) {
                self.couponVM.customerGroups(data.data.customerGroups);
            });

            priceAPI.getPriceTypes().then(function(data) {
                self.couponVM.priceTypes(data.data.priceTypes);
            });


            if(self.couponVM.isClone()) {
                data = data.substring(6)
            }

            if(!self.couponVM.isNew() || self.couponVM.isClone()) {
                couponAPI.getCoupon(data).then(function(coupon) {
                    self.couponVM.name(coupon.name);

                    gc.app.pageTitle(self.pageTitle());

                    if(!self.couponVM.isClone()) {
                        if (coupon.couponCodeGeneration) {
                            if (coupon.couponCodeGeneration.auto) {
                                self.couponVM.possibleGenerateAdditionalCodes(true);
                            }
                        }
                    }

                    self.couponVM.description(coupon.description);
                    self.couponVM.fromDate(gc.utils.fromServerTime(coupon.fromDate));
                    self.couponVM.toDate(gc.utils.fromServerTime(coupon.toDate));
                    self.couponVM.enabled(coupon.enabled);
                    self.couponVM.usesPerCoupon(coupon.usesPerCoupon);
                    self.couponVM.usesPerCustomer(coupon.usesPerCustomer);
                    self.couponVM.validAfterFirstUse(coupon.validAfterFirstUse);
                    self.couponVM.auto(coupon.auto);
                    self.couponVM.erpCode(coupon.erpCode);

                    self.couponVM.couponCondition(setFilterNode(coupon.couponCondition, null, self.couponVM));
                    self.couponVM.customerGroupIds(coupon.customerGroupIds);
                    self.couponVM.priceTypeIds(coupon.priceTypeIds);

                    self.couponVM.couponAction.type(coupon.couponAction.type);
                    self.couponVM.couponAction.freeShipping(coupon.couponAction.freeShipping);
                    self.couponVM.couponAction.discountAmount(coupon.couponAction.discountAmount);
                    self.couponVM.couponAction.discountQtyStep(coupon.couponAction.discountQtyStep);
                    self.couponVM.couponAction.maximumQtyApplyTo(coupon.couponAction.maximumQtyApplyTo);
                    self.couponVM.couponAction.priceTypeId(coupon.couponAction.priceTypeId);

                    self.couponVM.couponAction.productIds(coupon.couponAction.productIds || []);
                    self.couponVM.couponAction.productListIds(coupon.couponAction.productListIds || []);
                    self.couponVM.couponAction.productSelectionType(coupon.couponAction.productSelectionType);
                    self.couponVM.couponAction.productSortOrder(coupon.couponAction.discountOrder);


                    if(coupon.couponAction.rangeDiscountAmount && coupon.couponAction.rangeDiscountAmount.length > 0){
                        coupon.couponAction.rangeDiscountAmount.each(function(elem, index) {
                            var dscItem = new CouponRangeDiscountVM(self.couponVM.couponAction);
                            dscItem.discountAmount(elem.discountAmount);
                            dscItem.fromAmount(elem.fromAmount);
                            dscItem.toAmount(elem.toAmount);
                            self.couponVM.couponAction.rangeDiscountAmount.push(dscItem);
                        });
                    }

                    if(coupon.couponAction.discountAmounts && coupon.couponAction.discountAmounts.length > 0){
                        coupon.couponAction.discountAmounts.each(function(elem, index) {
                            var dscItem = new CouponListDiscountVM(self.couponVM.couponAction);
                            dscItem.discountAmount(elem);
                            self.couponVM.couponAction.discountAmounts.push(dscItem);
                        });
                    }

                    self.couponVM.couponAction.filter(setFilterNode(coupon.couponAction.filter, null, self.couponVM));

                    function setFilterNode( node, parent, couponVM){
                        if(node == null && parent != null)
                            return null;
                        else if(node == null){
                            var nodeVM = new CouponFilterNodeVM(parent, couponVM);
                            nodeVM.type('BOOLEAN_OPERATION');
                            nodeVM.operation('AND');
                            nodeVM.operationValue(true);
                            return nodeVM;
                        }
                        var nodeVM = new CouponFilterNodeVM(parent, couponVM);
                        nodeVM.type(node.type);
                        nodeVM.operator(node.operator);
                        nodeVM.operation(node.operation);
                        nodeVM.operationValue(node.operationValue);
                        nodeVM.attributeType(node.attributeType);
                        nodeVM.attributeCode(node.attributeCode);
                        nodeVM.value(node.value);
                        if(node.nodes && node.nodes.length > 0){
                            node.nodes.each(function(elem, index) {
                                nodeVM.nodes.push(setFilterNode(elem, nodeVM, couponVM));
                            });
                        }

                        return nodeVM;
                    }

                });
            } else {
                var nodeVM = new CouponFilterNodeVM(null, self.couponVM);
                nodeVM.type('BOOLEAN_OPERATION');
                nodeVM.operation('AND');
                nodeVM.operationValue(true);
                self.couponVM.couponCondition(nodeVM);

                nodeVM = new CouponFilterNodeVM(null, self.couponVM);
                nodeVM.type('BOOLEAN_OPERATION');
                nodeVM.operation('AND');
                nodeVM.operationValue(true);
                self.couponVM.couponAction.filter(nodeVM);
            }

            gc.app.sessionPut('couponVM', self.couponVM);

        },
        attached : function() {
            var self = this;
            /*gc.app.onToolbarEvent({
             save : self.saveData
             });*/
        },
        compositionComplete : function() {
            $('a[data-toggle="tab"]').on('show.bs.tab', function (e) {
                console.log('_________$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$', e);
            });

        },
        detached : function(couponId) {
        },
        deactivate : function() {
        }
    }
});