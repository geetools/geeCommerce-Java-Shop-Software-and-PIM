define([
        'durandal/app', 'durandal/composition', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute', 'gc-attribute/util'
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

        if (settings.visible) {
            self.visible = settings.visible;
        }

        if (settings.showModalLink) {
            self.showModalLink = settings.showModalLink;
        }

        // Pager columns
        var pagerColumns = [
                {
                    'name' : '$attr.manufacturer',
                    'label' : 'app:modules.product.gridColManufacturer',
                    cookieKey : 'mf'
                }, {
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
                    'name' : '$attr.brand',
                    'label' : 'app:modules.product.gridColBrand',
                    cookieKey : 'b'
                }, {
                    'name' : '$attr.name',
                    'label' : 'app:modules.product.gridColName',
                    cookieKey : 'n'
                }, {
                    'name' : '$attr.supplier',
                    'label' : 'app:modules.product.gridColSupplier',
                    cookieKey : 'sp'
                }, {
                    'name' : 'deleted',
                    'label' : 'app:modules.product.gridColDeleted',
                    cookieKey : 'd',
                    'selectOptions' : [
                            {
                                label : gc.app.i18n('app:common.no'),
                                value : false
                            }, {
                                label : gc.app.i18n('app:common.yes'),
                                value : true
                            }, {
                                label : gc.app.i18n('app:common.all'),
                                value : ''
                            },
                    ]
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

    ctor.prototype.compositionComplete = function() {
        var self = this;
        self.pager.activateSubscribers();
    };

    ctor.prototype.attached = function(view, parent) {
        var self = this;
    };


    ctor.prototype.showProductFinder = function() {
        var self = this;
        self.visible(true);
    };
    
    ctor.prototype.cancelProductFinder = function() {
        var self = this;
        self.visible(false);
    };
    
    ctor.prototype.processCheckedProducts = function(model, event) {
        var self = this;
        var tableEL = $(event.target).closest('.modal-body');
        var checkboxesEL = $(tableEL).find('table>tbody th.td-select>input');
        var productIds = self.value.removeAll();

        checkboxesEL.each(function(idx, checkbox) {
            var isChecked = $(checkbox).is(':checked');
            var idx = productIds.indexOf($(checkbox).val());

            if(idx === -1 && isChecked === true) {
                productIds.push($(checkbox).val());
            } else if(idx !== -1 && isChecked === false) {
                productIds.splice(idx);
            }
        });

        self.value(productIds);
    };

    ctor.prototype.useCheckedProducts = function(model, event) {
        var self = this;
        self.processCheckedProducts(model, event);
    };

    ctor.prototype.useCheckedProductsAndClose = function(model, event) {
        var self = this;
        self.processCheckedProducts(model, event);
        self.visible(false);
    };
    
    
    return ctor;
});