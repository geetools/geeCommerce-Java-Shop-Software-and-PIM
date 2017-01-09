define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-product-list', 'knockout-validation', 'gc-attribute', 'gc-attribute/util', 'gc-attribute-tabs/util' ], function(app, ko, router, gc, productListAPI, validation, attrAPI, attrUtil,attrTabsUtil) {

    function FilterAttribute(productListVM){
        var self = this;

        self.productListVM = productListVM;
        self.id = ko.observable();

        self.deleteFilterAttribute = function(){
            self.productListVM.filterAttributes.remove(self);
        };

    }

    function ProductListAttributeValueVM(valueId, attributeId, backendLabel, code, code2, value, editable, enabled, inputType, frontendInput, isOptionAttribute, allowMultipleValues, i18n, options) {
        var self = this;
        self.valueId = valueId;
        self.attributeId = attributeId;
        self.backendLabel = backendLabel;
        self.code = code;
        self.code2 = code2;
        self.value = ko.observableArray(value);
        self.isEditable = editable;
        self.isEnabled = enabled;
        self.inputType = inputType;
        self.frontendInput = frontendInput;
        self.isOption = isOptionAttribute;
        self.isMultiple = allowMultipleValues;
        self.isI18n = i18n;
        self.options = options;
        self.hasChanged = false;
        // self.isShowField = true;

        // Callback for widget i18nEditor.
        self.unjsonDescriptionPanels = function(data) {
            var asJson = null;
            var asText = '';

            try
            {
                asJson = JSON.parse(data);
            }
            catch(e)
            {
                // exeption
            }

            if(asJson === null) {
                asText = data;
            } else {
                _.each(asJson, function(row) {
                    asText += row.title + row.body;
                });
            }

            return asText;
        };

        self.selectOptions = ko.computed(function() {

            var _options = [];
            _options.push( { id : '', text : function() {
                return gc.app.i18n('app:common.choose', {}, gc.app.currentLang);
            }});

            _.each(self.options, function(option) {
                if(!_.isUndefined(option.label)){
                    _options.push({id: option.id, text: option.label.i18n});
                }
            });

            return _options || [];
        });
    }

    function ProductListVM(productListId) {
        var self = this;
        
        if(productListId == 'new') {
        	self.id = ko.observable();
        } else {
        	self.id = ko.observable(productListId);
        }
        
        self.label = ko.observableArray([]);
        self.key = ko.observable();
        self.query = ko.observable();
        self.queryNode = ko.observable();
        self.filter = ko.observable();
        self.sale = ko.observable(false);
        self.special = ko.observable(false);
        self.data = ko.observable();

        self.attributes = ko.observableArray([]);

        self.formAttributeValues = ko.observableArray();

        self.attributeValues = ko.observableArray();

        self.isNew = ko.observable(false);

        if(productListId == 'new') {
            self.isNew(true);
        }

        self.autogenerate = ko.observable(false);
        self.rewriteUrl = ko.observableArray([]).extend({
            validation: {
                async: true,
                validator: function (val, param, callback) {
                    var selfValid = this;
                    var updateModel = gc.app.newUpdateModel();
                    updateModel.field('rewriteUrl', val, true);
                    
                    console.log('!!!!!!!!!!!!!!!!!!!!!!!!! ', self.id());
                    
                    var res = productListAPI.isUrlUnique(self.id(), updateModel).then(function(result) {
                        var notUnique = [];
                        var isUnique = true;
                        for (var key in result.data.results){
                            if(!result.data.results[key]){
                                isUnique = false;
                                notUnique.push(key);
                            }
                        }
                        if(!isUnique){
                            selfValid.message = 'Url should be unique (' + notUnique.join() + ')';
                        }
                        callback(isUnique);
                    });
                },
                message: 'Url should be unique',
                onlyIf: function () { return !(self.autogenerate()); }
            }
        });
        self.showFriendlyUrl = ko.computed(function(){
            var val = self.rewriteUrl();

            if(!val)
                return false;

            for (i = 0; i < val.length; ++i) {
                if(val[i].val && val[i].val.length > 0)
                    return true;
            }
            return false;
        })


        self.productAttributes = ko.observableArray();
        self.filterAttributes = ko.observableArray();

        self.selectedFilterAttribute = ko.observable();

        self.addFilterAttribute = function() {
            var filterAttribute = new FilterAttribute(self);
            self.selectedFilterAttribute(filterAttribute);
            self.filterAttributes.push(filterAttribute);
        };

        self.isFilterAttributeSelected = function(filterAttribute) {
            return filterAttribute === self.selectedFilterAttribute();
        };


    }

    function ProductListQueryNodeVM(parent, productListVM){
        var self = this;
        console.log(productListVM)
        self.productListVM = productListVM;
        self.type = ko.observable();
        self.operator = ko.observable();
        self.attrVal = ko.observable();
        self.attrCode = ko.observable();
        self.attrCode.subscribe(function (code) {
            var attr = _.findWhere(self.productListVM.attributeValues(), { code : code });

            var attrOptions = attr.options;
            gc.ctxobj.enhance(attrOptions, [ 'label' ], 'any');

            var atr = new ProductListAttributeValueVM(
                undefined,
                attr.id,
                attr.backendLabel,
                attr.code,
                attr.code2,
                [],
                attr.editable,
                attr.enabled,
                attr.inputType,
                attr.frontendInput,
                attr.optionAttribute,
                attr.allowMultipleValues,// || true, // set all multiples
                attr.i18n,
                attrOptions);
            atr.isMultiple = true;
            atr.isShowField = true;
            self.attrVal(atr);

        });
        self.value = ko.observable();
        self.nodes = ko.observableArray([]);
        self.parent = ko.observable(parent);

        self.newNodeType = ko.observable();

        self.deleteNode = function(){
            self.parent().nodes.remove(self);
        };

        self.addNode = function(target, reason){
            if( !(reason === 'nochange' || reason === 'save')) return;

            var nodeVM = new ProductListQueryNodeVM(self, self.productListVM);

            if(self.newNodeType() === 'BOOLEAN'){
                nodeVM.type('BOOLEAN');
                nodeVM.operator('AND');
            } else {
                nodeVM.type('ATTRIBUTE');

            }
            self.nodes.push(nodeVM);
        }

        self.convertNode = function () {
            var obj = {};
            obj["type"] = self.type();
            obj["operator"] = self.operator();
            if(self.attrVal()){
                var attr_obj = {}
                obj["val"] = attr_obj;
                if(self.attrVal().isOption){
                    attr_obj["opt_id"] = self.attrVal().value()
                } else {
                    attr_obj["val"] = self.attrVal().value()
                }
                attr_obj["attr_id"] = self.attrVal().attributeId;
            }
            if(self.nodes() && self.nodes().length > 0){
                var nodes = []

                _.each(self.nodes(), function (node) {
                    nodes.push(node.convertNode())
                })
                obj["nodes"] = nodes;
            }
            return obj;

        }
    }

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function ProductListDetailsIndexController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ProductListDetailsIndexController)) {
            throw new TypeError("ProductListDetailsIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.productListVM = ko.observable({});
        this.productListId = ko.observable();
        this.attributes = ko.observable({});
        this.tabs = ko.observableArray([]);

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate');
    }

    ProductListDetailsIndexController.prototype = {
        constructor : ProductListDetailsIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.product-list.detailsTitle';
            var vm = ko.gc.unwrap(self.productListVM);

            if(!_.isUndefined(vm)) {
                var name = vm.label;

                if(!_.isEmpty(name)) {
                    title += ': ' + gc.ctxobj.val(vm.name, self.app.currentLang(), 'any');
                }
            }

            return title;
        },
        pageDescription : 'app:modules.product-list.detailsSubtitle',
        saveData : function(view, parent, toolbar) {
            var self = this;
            self.productListVM = gc.app.sessionGet('productListVM');
            setValueToNode(self.productListVM.queryNode());
            function setValueToNode(nodeVM){
                if(nodeVM.attrVal()){
                    var val = {};
                    val.attr_id = nodeVM.attrVal().attributeId;
                    val.opt_id = nodeVM.attrVal().value;
                    nodeVM.val = val;
                    nodeVM.value(val);

                }
                if(nodeVM.nodes() && nodeVM.nodes().length > 0){
                    _.each(nodeVM.nodes(), function(node) {
                        setValueToNode(node);
                    });
                }
            }



            var updateModel = gc.app.newUpdateModel();
            updateModel.field('label', self.productListVM.label(), true);
            updateModel.field('key', self.productListVM.key());
            updateModel.field('sale', self.productListVM.sale());
            updateModel.field('special', self.productListVM.special());
            updateModel.field('filter', self.productListVM.filter());

            var filterAttributeIds = [];
            if(self.productListVM.filterAttributes() && self.productListVM.filterAttributes().length > 0){
                _.each(self.productListVM.filterAttributes(), function(attr) {
                    filterAttributeIds.push(attr.id());
                });
            }
            updateModel.field('filterAttributeIds', filterAttributeIds);

            updateModel.field('queryNode', JSON.stringify(self.productListVM.queryNode().convertNode())); //JSON.stringify(.convertNode()));//ko.toJSON(self.productListVM.queryNode(),["type", "operator", "attrVal", "value", "val", "nodes", "attributeId", "optionIds", "attr_id", "opt_id"]));

            attrUtil.toUpdateModel(ko.gc.unwrap(self.productListVM.formAttributeValues), self.productListVM.data() ? self.productListVM.data().attributes : null, updateModel);


            if(self.productListVM.isNew()) {
                productListAPI.createProductList(updateModel).then(function(data) {
                    var updateModel = gc.app.newUpdateModel();
                    updateModel.field('rewriteUrl', self.productListVM.rewriteUrl(), true);
                    updateModel.field('auto', self.productListVM.autogenerate());
                    productListAPI.updateRewriteUrl(data.id, updateModel).then(function(data) {
                        productListAPI.getRewriteUrl(data.id).then(function(data) {
                            if(data.data.urlRewrite.requestURI){
                                self.productListVM.rewriteUrl(data.data.urlRewrite.requestURI);
                                self.productListVM.autogenerate(false);
                            }
                        });
                    });
                    router.navigate('//product-lists/details/' + data.id);
                    toolbar.hide();
                })
            } else {
                productListAPI.updateProductList(self.productListId(), updateModel).then(function(data) {
                    console.log(data);
                    self.productListVM.query(data.query)
                    var updateModel = gc.app.newUpdateModel();
                    updateModel.field('rewriteUrl', self.productListVM.rewriteUrl(), true);
                    updateModel.field('auto', self.productListVM.autogenerate());
                    productListAPI.updateRewriteUrl(self.productListId(), updateModel).then(function(data) {
                        productListAPI.getRewriteUrl(self.productListId()).then(function(data) {
                            if(data.data.urlRewrite.requestURI){
                                self.productListVM.rewriteUrl(data.data.urlRewrite.requestURI);
                                self.productListVM.autogenerate(false);
                            }
                        });
                    });
                    toolbar.hide();
                })
            }


        },
        loadAttributes: function(vm){
            var self = this;
            attrAPI.getAttributes('product', { fields : [ 'code', 'code2', 'backendLabel', 'editable', 'enabled', 'inputType', 'frontendInput', 'optionAttribute', 'allowMultipleValues', 'i18n', 'options', 'tags', 'label', 'showInQuery', 'group', 'includeInProductListFilter'] } ).then(function( response ) {

                var attributes = response.data.attributes;
                
                console.log('************ ATTRIBUTES RESPONSE:::: ', attributes);
                
             //   vm.attributes(attributes);
                var fAV = [];
                _.each(attributes, function(attr) {
                    if(attr.showInQuery){
                        attr.label = gc.ctxobj.val(attr.backendLabel, gc.app.currentUserLang(), 'closest');
                        fAV.push(attr);
                    }

                    if (attr.group == "PRODUCT" && attr.includeInProductListFilter) {
                      //  var foundPrdListAttr = _.findWhere(self.productListVM().attributes(), { attributeId : attr.id });
                        var backendLabel = attr.backendLabel;
                        vm.productAttributes.push({id: attr.id, text: /*backendLabel.i18n*/gc.ctxobj.val(backendLabel, gc.app.currentUserLang(), 'closest')});
                    }


                });
                vm.attributeValues(fAV);
                setAttributeToNode(vm.queryNode());
                function setAttributeToNode(nodeVM){
                    if(nodeVM.value()){
                        var attr = _.findWhere(attributes, { id : nodeVM.value().attributeId });

                        var attrOptions = attr.options;
                        gc.ctxobj.enhance(attrOptions, [ 'label' ], 'any');

                        var atr = new ProductListAttributeValueVM(
                            nodeVM.value().id,
                            nodeVM.value().attributeId,
                            nodeVM.value().attribute.backendLabel,
                            nodeVM.value().attribute.code,
                            nodeVM.value().attribute.code2,
                            attr.optionAttribute ? nodeVM.value().optionIds : nodeVM.value().value,
                            attr.editable,
                            attr.enabled,
                            attr.inputType,
                            attr.frontendInput,
                            attr.optionAttribute,
                            attr.allowMultipleValues,// || true, // set all multiples
                            attr.i18n,
                            attrOptions);
                        atr.isMultiple = true;
                        atr.isShowField = true;
                        nodeVM.attrVal(atr);

                    }
                    if(nodeVM.nodes() && nodeVM.nodes().length > 0){
                        _.each(nodeVM.nodes(), function(node) {
                            setAttributeToNode(node);
                        });
                    }
                }

            })

        },
        activate : function(data) {
            var self = this;
            self.productListId(data);
            var vm = new ProductListVM(data);
            self.productListVM(vm);
            gc.app.sessionPut('productListVM', self.productListVM);

            if(!vm.isNew()){
                productListAPI.getProductList(self.productListId()).then(function(data) {

                    vm.key(data.key);
                    vm.query(data.query);
                    vm.label(data.label);
                    vm.sale(data.sale);
                    vm.special(data.special);
                    vm.data(data);

                    if(data.attributes)
                        vm.attributes(data.attributes);

                    vm.queryNode(setQueryNode(data.queryNode, null, vm));

                    if(data.filterAttributeIds){
                        data.filterAttributeIds.each(function(elem, index) {
                            var filterAttribute = new FilterAttribute(vm);
                            filterAttribute.id(elem);
                            vm.filterAttributes.push(filterAttribute);
                        });
                    }

                    self.loadAttributes(vm);

                });

                productListAPI.getRewriteUrl(self.productListId()).then(function(data) {
                    if(data.data.urlRewrite.requestURI){
                        vm.rewriteUrl(data.data.urlRewrite.requestURI);
                    }
                });
            } else {
                vm.queryNode(setQueryNode(null, null, vm))
                self.loadAttributes(vm);
            }


            function setQueryNode( node, parent, productListVM){
                if(node == null && parent != null)
                    return null;
                else if(node == null){
                    var nodeVM = new ProductListQueryNodeVM(parent, productListVM);
                    nodeVM.type('BOOLEAN');
                    nodeVM.operator('AND');
                    return nodeVM;
                }
                var nodeVM = new ProductListQueryNodeVM(parent, productListVM);
                nodeVM.type(node.type);
                nodeVM.operator(node.operator);
                nodeVM.value(node.value);
                if(node.nodes && node.nodes.length > 0){
                    node.nodes.each(function(elem, index) {
                        nodeVM.nodes.push(setQueryNode(elem, nodeVM, productListVM));
                    });
                }

                return nodeVM;
            }

            return attrTabsUtil.getTabsPromise(vm, "product_list", self.tabs);
        },
        attached : function() {
            var self = this;
        },
        compositionComplete : function() {
            $('a[data-toggle="tab"]').on('show.bs.tab', function (e) {
                console.log('_________$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$', e);
            });

        },
        detached : function() {
        },
        deactivate : function() {
        }
    }

    return ProductListDetailsIndexController;
});