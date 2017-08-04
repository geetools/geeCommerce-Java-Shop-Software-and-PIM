define([
        'durandal/app', 'durandal/composition', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute', 'gc-attribute/util', 'slick'
], function(app, composition, ko, gc, productAPI, attrAPI, attrUtil) {

    var ctor = function() {
        this.gc = gc;
        this.app = gc.app;
        this.pager = {};
        this.visible = ko.observable(false);
        this.showModalLink = true;
    };

    ctor.prototype.activate = function(settings) {
        var self = this;

        self.value = settings.value;
        self.currentlyCheckedValues = ko.observableArray([]);
        self.currentProducts = ko.observableArray([]);
        self.isSlicked = false;
        self.linkText = ko.observable(settings.linkText || 'Product Finder');
        
        self.currentlyCheckedValues.subscribe(function(checkedProductIds) {
            productAPI.getProducts(checkedProductIds, {
                fields : [
                        'id', 'id2', 'ean', 'mainImageURI'
                ],
                attributes : [
                        'name', 'name2', 'article_number', 'product_group', 'programme', 'bundle_group'
                ]
            }).then(function(response) {
                var el = $(self.view).find('.product-finder-carousel');

                if (self.isSlicked === true) {
                    $(el).slick('unslick');
                }

                if (self.currentProducts() && self.currentProducts().length > 0) {
                    self.currentProducts([]);
                }

                el.empty();

                self.currentProducts(response.data.products);

                if (self.isSlicked === true) {
                    // $(el).slick('unslick');
                    $(el).slick();
                } else {
                    $(el).slick();
                    self.isSlicked = true;
                }
            });
        });

        if (!_.isUndefined(settings.products)) {
            self.products = settings.products;
            self.productAs = settings.productAs || {};

            var initialValues = ko.unwrap(self.value);
            if(!_.isEmpty(initialValues)) {
                productAPI.getProducts(initialValues, self.productAs).then(function(response) {
                    self.products(response.data.products);
                    self.setLinkText(response.data.products);
                });
            }
            
            self.value.subscribe(function(newValue) {
                if (_.isEmpty(newValue)) {
                    self.products([]);
                    self.setLinkText();
                } else {
                    productAPI.getProducts(newValue, self.productAs).then(function(response) {
                        self.products(response.data.products);
                        self.setLinkText(response.data.products);
                    });
                }
            });
        }

        if (settings.visible) {
            self.visible = settings.visible;
        }

        if (settings.showModalLink) {
            self.showModalLink = settings.showModalLink;
        }

        // Pager columns
        var pagerColumns = [
                {
                    'name' : 'type',
                    'label' : 'app:modules.product.gridColType',
                    cookieKey : 't',
                    'selectOptions' : [
                            {
                                label : gc.app.i18n('app:common.choose'),
                                value : ''
                            }, {
                                label : gc.app.i18n('app:modules.product.typePRODUCT'),
                                value : 1
                            }, {
                                label : gc.app.i18n('app:modules.product.typeVARIANT_MASTER'),
                                value : 2
                            }, {
                                label : gc.app.i18n('app:modules.product.typePROGRAMME'),
                                value : 4
                            }
                    ]
                }, {
                    'name' : 'group',
                    combined : true,
                    'label' : 'app:modules.product.gridColGroup',
                    cookieKey : 'g'
                }, {
                    'name' : '$attr.ean',
                    'label' : 'app:modules.product.gridColEan',
                    cookieKey : 'ean'
                }, {
                    'name' : '$attr.article_number',
                    'label' : 'app:modules.product.gridColArticleNo',
                    cookieKey : 'an'
                }, {
                    'name' : '$attr.name',
                    'label' : 'app:modules.product.gridColName',
                    cookieKey : 'n'
                }
        ];

        // Init the pager.
        self.pager = new gc.Pager(productAPI.pagingOptions({
            columns : pagerColumns,
            multiContext : true,
            limit : 5,
            cookieName : 'pgr_products_fndr'
        }));
    };

    ctor.prototype.setLinkText = function(prdArray) {
        var self = this;
        
        if (!_.isEmpty(prdArray)) {
            var _linkText = '';
            
            if (prdArray.length > 1) {
                var x = 0;
                for (var i = 0; i < prdArray.length; i++) {
                    var prd = prdArray[i];
                    var artNo = gc.attributes.find(prd.attributes, 'article_number').value;

                    if(!_.isEmpty(artNo)) {
                        if (x > 0) {
                            _linkText += ', ';
                        }

                        _linkText += gc.ctxobj.global(artNo);
                    }

                    if(i > 10)
                        break;
                    
                    x++;
                }
            } else {
                var prd = prdArray[0];

                var artNo = gc.attributes.find(prd.attributes, 'article_number').value;
                _linkText = gc.ctxobj.global(artNo);
            }
            
            if(!_.isEmpty(_linkText)) {
                self.linkText(_linkText);
            } else {
                self.linkText('Product Finder');
            }         
        } else {
            self.linkText('Product Finder');
        }
    };

    ctor.prototype.compositionComplete = function() {
        var self = this;
        self.pager.activateSubscribers();
    };

    ctor.prototype.attached = function(view, parent) {
        var self = this;
        self.view = view;
        self.parent = parent;

    };

    ctor.prototype.showProductFinder = function() {
        var self = this;
        self.visible(true);

        if (self.isSlicked === true) {
            var el = $(self.view).find('.product-finder-carousel');
            $(el).slick();
        }
    };

    ctor.prototype.cancelProductFinder = function() {
        var self = this;
        self.visible(false);
    };

    ctor.prototype.useCheckedProducts = function(model, event) {
        var self = this;
        self.value(self.currentlyCheckedValues());
    };

    ctor.prototype.useCheckedProductsAndClose = function(model, event) {
        var self = this;
        self.value(self.currentlyCheckedValues());
        self.visible(false);
    };

    return ctor;
});