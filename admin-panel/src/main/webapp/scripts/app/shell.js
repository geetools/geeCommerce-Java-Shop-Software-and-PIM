define([ 'plugins/router', 'durandal/app', 'knockout', 'i18next', 'gc/gc', 'gc-account' ], function(router, app, ko, i18n, gc, accountAPI) {

    function ShellController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ShellController)) {
            throw new TypeError("ShellController constructor cannot be called as a function.");
        }

        this.router = router;
        this.gc = gc;
        this.app = gc.app;

        this.selected = ko.observable();
        this.opened = ko.observable();
        this.loggedUser = ko.observable();

        this.contextOptions = ko.observableArray();
        this.languageOptions = ko.observableArray();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'signout', 'touchSession', 'refreshPreloadedData', 'nav', 'initContextOptions', 'activate', 'hasPermission', 'compositionComplete');
    }

    ShellController.prototype = {
        signout : function() {
            accountAPI.deleteSession();
            window.location.href = "";
            // DOESN't reload the page
            // app.setRoot('login');
        },
        touchSession : function() {
            var self = this;
            accountAPI.currentSession().then(function(data) {
                if (!_.isEmpty(data.data) && !_.isEmpty(data.data.name) && !_.isEmpty(data.data.roles)) {
                    gc.app.sessionPut('sessionTimeoutAtMillis', data.data.timeoutAtMillis);
                    gc.app.sessionPut('loggedUserName', data.data.name);
                }
                self.loggedUser(data.data.name);
            }, function(data) {
                gc.app.handleSessionTimeout();
            });
        },
        refreshPreloadedData : function() {
            var self = this;
            self.preloadDataCount = self.preloadDataCount || 0;

            if (self.preloadDataCount > 0) {

                console.time("PRELOAD-TIME");
                $('#refresh-data-icon').css('color', 'red');

                gc.app.preloadData().then(function() {
                    console.timeEnd("PRELOAD-TIME");
                    $('#refresh-data-icon').css('color', 'green');

                    window.setTimeout(function() {
                        $('#refresh-data-icon').css('color', '');
                    }, 5000);
                });
            }

            self.preloadDataCount++;
        },
        nav : ko.computed(function() {
            var session = gc.app.currentSession();

            return ko.utils.arrayFilter(router.navigationModel(), function(route) {
                if (route.roles === '*') {
                    return true;
                } else {
                    var matchedRoles = _.intersection(route.roles, session.roles);

                    if (!_.isEmpty(matchedRoles)) {
                        return true;
                    }
                }

                return false;
            });
        }),
        initLanguageOptions : function() {
            var self = this;
            var options = [];
            
            _.forEach(self.app.conf.availableLanguages(), function(lang) {
                options.push({
                    id : lang.code,
                    text : lang.label
                });
            });
            
            self.languageOptions(options);
        },
        initContextOptions : function() {
            var self = this;
            var options = [];
            
            _.forEach(self.app.conf.availableContexts(), function(ctx) {
                if (ctx.scope == 'global') {
                    options.push({
                        id : '',
                        text : ctx.name
                    });
                } else if (ctx.scope != 'global') {
                    options.push({
                        id : ctx.id,
                        text : '&nbsp;&nbsp;&nbsp;&nbsp;Merchant: ' + ctx.name
                    });

                    _.forEach(ctx.stores, function(store) {
                        options.push({
                            id : store.id,
                            text : '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Store: ' + store.name
                        });

                        _.forEach(store.requestContexts, function(reqCtx) {
                            options.push({
                                id : reqCtx.id,
                                text : '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Website: ' + reqCtx.urlPrefix
                            });
                        });
                    });
                }
            });
            
            console.log('INITIALLIZED SELECT OPTIONS: ', options);
            
            self.contextOptions(options);
        },
        activate : function() {
            var self = this;
            var session = self.app.currentSession();
            
            self.initContextOptions(); 
            self.initLanguageOptions();

            // console.log('router.routes#1: ', router.routes);

            if (_.isEmpty(router.routes)) {

                self.mainNavigation = [ {
                    label : 'app:navigation.catalogue',
                    group : 'catalogue',
                    roles : [ 'admin', 'product-manager', 'product-viewer' ]
                }, {
                    label : 'app:navigation.datamodel',
                    group : 'datamodel',
                    roles : [ 'admin', 'product-manager' ]
                }, {
                    label : 'app:navigation.content',
                    group : 'content',
                    roles : [ 'admin', 'product-manager' ]
                }, {
                    label : 'app:navigation.promotions',
                    group : 'promotions',
                    roles : [ 'admin' ]
                }, {
                    label : 'app:navigation.customers',
                    group : 'customers',
                    roles : [ 'admin', 'product-manager' ]
                }, {
                    label : 'app:navigation.search',
                    group : 'search',
                    roles : [ 'admin' ]
                }, {
                    label : 'app:navigation.permissions',
                    group : 'permissions',
                    roles : [ 'admin' ]
                }, {
                    label : 'app:navigation.config',
                    group : 'config',
                    roles : [ 'admin' ]
                } ];

                // console.log('router.routes#2a: ', router.routes);

                self.routes = [ {
                    route : [ '', 'products*details', 'products*import' ],
                    moduleId : 'modules/geecommerce/product/pages/index',
                    label : 'app:navigation.products',
                    title : 'app:modules.product.title',
                    subtitle : 'app:modules.product.subtitle',
                    icon : 'fa fa-cube fa-fw',
                    group : 'catalogue',
                    nav : true,
                    hash : '#/products',
                    roles : [ 'admin', 'product-manager', 'product-viewer' ]
                }, {
                    route : 'product-lists*details',
                    moduleId : 'modules/geecommerce/product-list/pages/index',
                    label : 'app:navigation.product-lists',
                    title : 'app:modules.product-list.title',
                    subtitle : 'app:modules.product-list.subtitle',
                    icon : 'fa fa-list fa-fw',
                    group : 'catalogue',
                    nav : true,
                    hash : '#/product-lists',
                    roles : [ 'admin', 'product-manager' ]
                }, {
                    route : 'attributes*details',
                    moduleId : 'core/attribute/pages/index',
                    label : 'app:navigation.attributes',
                    title : 'app:modules.attribute.title',
                    subtitle : 'app:modules.attribute.subtitle',
                    icon : 'fa fa-puzzle-piece fa-fw',
                    group : 'datamodel',
                    nav : true,
                    hash : '#/attributes',
                    roles : [ 'admin', 'product-manager' ]
                }, {
                    route : 'attribute-tabs*details',
                    moduleId : 'core/attribute-tabs/pages/index',
                    label : 'app:navigation.attributeTabs',
                    title : 'app:modules.attributetabs.title',
                    subtitle : 'app:modules.attributetabs.subtitle',
                    icon : 'fa fa-folder-o fa-fw',
                    group : 'datamodel',
                    nav : true,
                    hash : '#/attribute-tabs',
                    roles : [ 'admin', 'product-manager' ]
                }, {
                    route : 'attribute-groups*details',
                    moduleId : 'core/attribute-group/pages/index',
                    label : 'app:navigation.attribute-groups',
                    title : 'app:modules.attribute-group.title',
                    subtitle : 'app:modules.attribute-group.subtitle',
                    icon : 'fa fa-share-alt-square fa-fw',
                    group : 'datamodel',
                    nav : true,
                    hash : '#/attribute-groups',
                    roles : [ 'admin', 'product-manager' ]
                }, /*
                     * { route : 'pictograms*details', moduleId : 'modules/geecommerce/pictogram/pages/index', label : 'app:navigation.pictograms', title : 'app:modules.pictogram.title', subtitle :
                     * 'app:modules.pictogram.subtitle', icon : 'fa fa-file-image-o fa-fw', group : 'datamodel', nav : true, hash : '#/pictograms', roles : [ 'admin' ] },
                     */{
                    route : 'content*details',
                    moduleId : 'modules/geecommerce/content/pages/index',
                    label : 'app:navigation.content',
                    title : 'app:modules.content.title',
                    subtitle : 'app:modules.content.subtitle',
                    icon : 'fa fa-file-text fa-fw',
                    group : 'content',
                    nav : true,
                    hash : '#/content',
                    roles : [ 'admin', 'product-manager' ]
                }, {
                    route : 'templates*details',
                    moduleId : 'core/template/pages/index',
                    label : 'app:navigation.templates',
                    title : 'app:modules.template.title',
                    subtitle : 'app:modules.template.subtitle',
                    icon : 'fa fa-file-text fa-fw',
                    group : 'content',
                    nav : true,
                    hash : '#/templates',
                    roles : [ 'admin', 'product-manager' ]
            },
            {
                    route : 'navigation*details',
                    moduleId : 'modules/geecommerce/navigation/pages/index',
                    label : 'app:navigation.navigation',
                    title : 'app:modules.navigation.title',
                    subtitle : 'app:modules.navigation.subtitle',
                    icon : 'fa fa-folder-open fa-fw',
                    group : 'content',
                    nav : true,
                    hash : '#/navigation',
                    roles : [ 'admin' ]
                }, {
                    route : 'slide-shows*details',
                    moduleId : 'modules/geecommerce/slide-show/pages/index',
                    label : 'app:navigation.slideShows',
                    title : 'app:modules.slide-show.title',
                    subtitle : 'app:modules.slide-show.subtitle',
                    icon : 'fa fa-image fa-fw',
                    group : 'content',
                    nav : true,
                    hash : '#/slide-shows',
                    roles : [ 'admin' ]
                }, {
                    route : 'coupons*details',
                    moduleId : 'modules/geecommerce/coupon/pages/index',
                    label : 'app:navigation.coupons',
                    title : 'app:modules.coupon.title',
                    subtitle : 'app:modules.coupon.subtitle',
                    icon : 'fa fa-gift fa-fw',
                    group : 'promotions',
                    nav : true,
                    hash : '#/coupons',
                    roles : [ 'admin' ]
                }, {
                    route : 'product-promotions*details',
                    moduleId : 'modules/geecommerce/product-promotion/pages/index',
                    label : 'app:navigation.product-promotions',
                    title : 'app:modules.product-promotion.title',
                    subtitle : 'app:modules.product-promotion.subtitle',
                    icon : 'fa fa-line-chart fa-fw',
                    group : 'promotions',
                    nav : true,
                    hash : '#/product-promotions',
                    roles : [ 'admin' ]
                }, {
                    route : 'coupon-promotions*details',
                    moduleId : 'modules/geecommerce/coupon-promotion/pages/index',
                    label : 'app:navigation.coupon-promotions',
                    title : 'app:modules.coupon-promotion.title',
                    subtitle : 'app:modules.coupon-promotion.subtitle',
                    icon : 'fa fa-line-chart fa-fw',
                    group : 'promotions',
                    nav : true,
                    hash : '#/coupon-promotions',
                    roles : [ 'admin' ]
                }, {
                    route : 'discount-promotions*details',
                    moduleId : 'modules/geecommerce/discount-promotion/pages/index',
                    label : 'app:navigation.discount-promotions',
                    title : 'app:modules.discount-promotion.title',
                    subtitle : 'app:modules.discount-promotion.subtitle',
                    icon : 'fa fa-gift fa-fw',
                    group : 'promotions',
                    nav : true,
                    hash : '#/discount-promotions',
                    roles : [ 'admin' ]
                }, {
                    route : 'orders*details',
                    moduleId : 'modules/geecommerce/order/pages/index',
                    label : 'app:navigation.orders',
                    title : 'app:modules.order.title',
                    subtitle : 'app:modules.order.subtitle',
                    icon : 'fa fa-shopping-cart fa-fw',
                    group : 'customers',
                    nav : true,
                    hash : '#/orders',
                    roles : [ 'admin' ]
                }, {
                    route : 'customers*details',
                    moduleId : 'modules/geecommerce/customer/pages/index',
                    label : 'app:navigation.customers',
                    title : 'app:modules.customer.title',
                    subtitle : 'app:modules.customer.subtitle',
                    icon : 'fa fa-group fa-fw',
                    group : 'customers',
                    nav : true,
                    hash : '#/customers',
                    roles : [ 'admin' ]
                }, {
                    route : 'search-rewrites*details',
                    moduleId : 'modules/geecommerce/search-rewrite/pages/index',
                    label : 'app:navigation.search-rewrites',
                    title : 'app:modules.search-rewrite.title',
                    subtitle : 'app:modules.search-rewrite.subtitle',
                    icon : 'fa fa-link fa-fw',
                    group : 'search',
                    nav : true,
                    hash : '#/search-rewrites',
                    roles : [ 'admin' ]
                }, {
                    route : 'synonyms*details',
                    moduleId : 'modules/geecommerce/synonym/pages/index',
                    label : 'app:navigation.synonyms',
                    title : 'app:modules.synonym.title',
                    subtitle : 'app:modules.synonym.subtitle',
                    icon : 'fa fa-search fa-fw',
                    group : 'search',
                    nav : true,
                    hash : '#/synonyms',
                    roles : [ 'admin' ]
                }, {
                    route : 'settings',
                    moduleId : 'core/account/pages/settings',
                    label : 'app:navigation.settings',
                    icon : 'fa fa-gears fa-fw',
                    nav : true,
                    hash : '#/settings',
                    roles : '*'
                }, {
                    route : 'media-assets*details',
                    moduleId : 'modules/geecommerce/media-asset/pages/index',
                    label : 'app:navigation.mediaAssets',
                    icon : 'fa fa-file-image-o fa-fw',
                    nav : true,
                    hash : '#/media-assets',
                    group : 'content',
                    roles : [ 'admin', 'product-manager' ]
                }
                //
                // {
                // route : 'magazines*details',
                // moduleId : 'modules/geecommerce/magazine/pages/index',
                // label : 'app:navigation.magazines',
                // icon : 'fa fa-book fa-fw',
                // nav : true,
                // hash : '#/magazines',
                // roles : [ 'admin' ]
                // },
                //				
                // { route : 'context-messages*details', moduleId :
                // 'core/context-message/pages/index', label :
                // 'app:navigation.contextMessages', icon : 'fa fa-file-text
                // fa-fw', nav : true, hash : '#/context-messages', roles : [
                // 'admin' ] },
                //				 
                , {
                    route : 'users*details',
                    moduleId : 'core/user/pages/index',
                    label : 'app:navigation.users',
                    icon : 'fa fa-user fa-fw',
                    nav : true,
                    hash : '#/users',
                    group : 'permissions',
                    roles : [ 'admin' ]
                }, {
                        route : 'roles*details',
                        moduleId : 'core/role/pages/index',
                        label : 'app:navigation.roles',
                        icon : 'fa fa-user fa-fw',
                        nav : true,
                        hash : '#/roles',
                        group : 'permissions',
                        roles : [ 'admin' ]
                }, {
                    route : 'merchants*details',
                    moduleId : 'core/merchant/pages/index',
                    label : 'app:navigation.merchants',
                    icon : 'fa fa-user fa-fw',
                    nav : true,
                    hash : '#/merchants',
                    group : 'config',
                    roles : [ 'admin' ]
                 }//, {
                //     route : 'request-contexts*details',
                //     moduleId : 'core/request-context/pages/index',
                //     label : 'app:navigation.request-contexts',
                //     icon : 'fa fa-user fa-fw',
                //     nav : true,
                //     hash : '#/request-contexts',
                //     group : 'config',
                //     roles : [ 'admin' ]
                // }
                // {
                // , {
                // route : 'marketplace/supplier/merchants*details',
                // moduleId : 'modules/geecommerce/marketplace-supplier/pages/index',
                // label : 'app:navigation.marketplace-supplier-merchants',
                // icon : 'fa fa-industry fa-fw',
                // nav : true,
                // hash : '#/marketplace/supplier/merchants',
                // roles : '*'
                // }, {
                // route : 'marketplace/supplier/productdata*details',
                // moduleId : 'modules/geecommerce/marketplace-supplier/pages/index',
                // label : 'app:navigation.marketplace-supplier-productdata',
                // icon : 'fa fa-industry fa-fw',
                // nav : true,
                // hash : '#/marketplace/supplier/productdata',
                // roles : '*'
                // }, {
                // route : 'marketplace/merchant/suppliers*details',
                // moduleId : 'modules/geecommerce/marketplace-merchant/pages/index',
                // label : 'app:navigation.marketplace-merchant-suppliers',
                // icon : 'fa fa-gears fa-fw',
                // nav : true,
                // hash : '#/marketplace/merchant/suppliers',
                // roles : '*'
                // }, {
                // route : 'marketplace/merchant/productdata*details',
                // moduleId : 'modules/geecommerce/marketplace-merchant/pages/index',
                // label : 'app:navigation.marketplace-merchant-productdata',
                // icon : 'fa fa-gears fa-fw',
                // nav : true,
                // hash : '#/marketplace/merchant/productdata',
                // roles : '*'
                // }, {
                // route : 'configurations*details',
                // moduleId : 'core/configuration/pages/index',
                // label : 'app:navigation.configuration',
                // icon : 'fa fa-gears fa-fw',
                // nav : true,
                // hash : '#/configurations',
                // roles : '*'
                // }, {
                ];

                if (_.isEmpty(self.selected())) {
                    // Get the URL
                    var url = document.location.toString();
                    var location = url.split('#')[1];
                    if (!_.isEmpty(location) && location.indexOf('/') == 0) {
                        location = '#/' + location.split('/')[1];
                        self.selected(self.routes[self.routes.map(function(x) {
                            return x.hash;
                        }).indexOf(location)]);
                    }

                    if (_.isEmpty(self.selected()) && self.selected() != "") {
                        self.selected(self.routes[0]);
                    }
                }

                if (_.isEmpty(self.opened())) {
                    self.opened(self.mainNavigation[self.mainNavigation.map(function(x) {
                        return x.group;
                    }).indexOf(self.selected().group)]);
                }
                
                // Once we have initialized the application, we can continue
                // with the routes.
                router.map(self.routes).buildNavigationModel().mapUnknownRoutes('core/dashboard/pages/index', 'not-found');
            }

            return router.activate();
        },
        compositionComplete : function() {

            // Change hash for page-reload
            $('.tab-pane a').on('shown.bs.tab', function(e) {
                window.location.hash = e.target.hash;
            })

            /*
             * $('.nav-hider').on('click', function () { //if(top) });
             */

        },
        hasPermission : function(roles, allowed) {
            var ret = false;
            for (var i = 0; i < roles.length; i++) {
                if ($.inArray(roles[i], allowed) == -1) {
                    return false;
                } else {
                    ret = true;
                    break;
                }
            }
            return ret;
        }
    }

    return ShellController;
});
