define([ 'durandal/app', 'postal', 'knockout', 'plugins/router', 'gc/gc', 'gc-navigation', 'gc-navigation/util', 'gc-product-list', 'gc-content' ], function(app, postal, ko, router, gc, navAPI,
        navUtil, productListAPI, cmsAPI) {

    function NavigationVM(id) {
        var self = this;

        self.id = ko.observable();

        self.isNew = ko.computed(function() {
            return self.id() == 'new';
        });
    }

    // -----------------------------------------------------------------
    // Controller
    // -----------------------------------------------------------------
    function NavigationIndexController(options) {

        // Make sure that this object is being called with the 'new'
        // keyword.
        if (!(this instanceof NavigationIndexController)) {
            throw new TypeError("NavigationIndexController constructor cannot be called as a function.");
        }

        var self = this;

        self.app = gc.app;
        self.navigationIndexController = {};

        self.navTree = ko.observable();
        self.activeView = ko.observable();
        self.productLists = ko.observableArray();
        self.cmsPages = ko.observableArray();
        self.labelsMap = null;

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'activateView', 'attached', 'removeNode', 'saveTree', 'notifyChange', 'addNode');
    }

    NavigationIndexController.prototype = {
        constructor : NavigationIndexController,
        activateView : function(node) {
            var self = this;
            self.activeView({
                model : 'modules/geecommerce/navigation/pages/details/tabs/index',
                transition : 'entrance',
                activationData : node
            });
        },
        saveTree : function(context) {
            var self = this;

            var treeUpdateObject = self.navTree().toObject(0);

           // console.log(treeUpdateObject);

            navAPI.saveNavigationTree(treeUpdateObject).then(function(data) {
                if (treeUpdateObject._id) {
                    context.saved();
                } else {
                    router.navigate('//navigation/details/' + data.data.navigationItem.id);
                    context.saved();
                }
            });

        },
        initTree : function() {
            var self = this;
            var stopHandler = function(event) {
                // var target = ko.dataFor(event.target);
                serialized = $('ol.sortable').nestedSortable('serialize');

                self.notifyChange();
            };

            $('ol.sortable').nestedSortable({
                handle : 'div',
                items : 'li',
                startCollapsed : true,
                // isTree:
                // true,
                tolerance : 'pointer',
                toleranceElement : '> div',
                stop : stopHandler
            });

            $('.disclose').on('click', function() {
                // alert("DISCLOSE CLICKED!");
                $(this).closest('li').toggleClass('mjs-nestedSortable-collapsed').toggleClass('mjs-nestedSortable-expanded');
            });

        },
        activate : function(id) {

            var self = this;

            // self.navigationIndexController = new NavigationIndexController(id);
            // gc.app.sessionPut('navigationIndexController', self.navigationIndexController);
            // console.log("ACTIVATE: DATA:", id);
            // console.log(data);

            self.addTopLevelNode = gc.app.i18n('app:modules.navigation.addTopLevelNode');
            self.addNodeTooltip = gc.app.i18n('app:modules.navigation.addNodeTooltip');
            self.removeNodeTooltip = gc.app.i18n('app:modules.navigation.removeNodeTooltip');

            var labelsMap = {};

            cmsAPI.getContents().then(function(data) {
                var cmsPages = [];

                cmsPages.push({
                    id : '',
                    text : function() {
                        return gc.app.i18n('app:common.choose', {}, gc.app.currentLang);
                    }
                });

                gc.ctxobj.enhance(data.data.contents, [ 'label' ], 'any');

                _.each(data.data.contents, function(option) {
                    if (!_.isUndefined(option.label)) {

                        cmsPages.push({
                            id : option.id,
                            text : option.label.i18n
                        });
                        labelsMap[option.id] = option.label.i18n;
                    } else {
                        cmsPages.push({
                            id : option.id,
                            text : option.key
                        });
                        labelsMap[option.id] = option.key;
                    }
                });

                self.cmsPages(cmsPages);
                self.app.sessionPut('cms_pages', self.cmsPages);
            });

            productListAPI.getProductLists().then(function(data) {

                var productLists = [];

                productLists.push({
                    id : '',
                    text : function() {
                        return gc.app.i18n('app:common.choose', {}, gc.app.currentLang);
                    }
                });

                gc.ctxobj.enhance(data.data.productLists, [ 'label' ], 'any');

                _.each(data.data.productLists, function(option) {
                    if (!_.isUndefined(option.label)) {

                        labelsMap[option.id] = option.label.i18n;

                        productLists.push({
                            id : option.id,
                            text : option.label.i18n
                        });
                    }
                });

                self.productLists(productLists);
                self.labelsMap = labelsMap;
                self.app.sessionPut('product_list', self.productLists);

            });

            if (!(id == 'new')) {
                // Tree exists, fetch data from server
                return navAPI.getNavigationItems(id).then(function(data) {

                    gc.app.sessionPut('getNavigationItems start', data);

                    var treeData = navUtil.toTree(data.data.navigationItems, self, false);

                    self.navTree(treeData);

                    self.navigationVM = new NavigationVM(data.data.navigationItems[0].id);
                    self.navigationVM.id(data.data.navigationItems[0].id);

                    gc.app.sessionPut('getNavigationItems stop: navigationVM', self.navigationVM);

                });
            } else {
                // Tree doesn't exist, create a new tree
                self.navTree(navUtil.toTree({}, self, true));

                self.navigationVM = new NavigationVM(id);
                self.navigationVM.id(id);

                gc.app.sessionPut('navigationVM', self.navigationVM);
            }
        },
        attached : function(data) {
            var self = this;
            self.initTree();
            self.activeView('modules/geecommerce/navigation/pages/details/tabs/index');

            $('#navigationBaseForm').addClass('save-button-listen-area');

            gc.app.onSaveEvent(function(context) {
                self.saveTree(context);
            });
        },
        detached : function() {
            var self = this;
            gc.app.clearSaveEvent();
        },
        notifyChange : function() {
            $toolbar = $("#nav-details-content").closest('form').find('.toolbar-trigger').first();
            // Make sure that the save/cancel toolbar sees the change.
            $toolbar.click();
            $toolbar.trigger('change');
        },
        addNode : function(node, event) {
            // Has to be before expanding the nested sortable or 'if'
            // test will fail
            var self = this;
            var newNode = node.addNode();

            self.activateView(newNode);

            // Expand to show newly created child
            var $parent = $('.dd').find('*').filter(function() {
                return $(this).attr('data-id') == node.id;
            });

            // alert($parent.prop('tagName'));
            if ($parent.prop('tagName') == 'LI') {
                if (!$parent.hasClass('mjs-nestedSortable-expanded')) {
                    $parent.addClass('mjs-nestedSortable-expanded');
                }
                if ($parent.hasClass('mjs-nestedSortable-collapsed')) {
                    $parent.removeClass('mjs-nestedSortable-collapsed');
                }
            } else {
                console.log("LI not found");
            }

            $('.disclose').unbind('click');
            $('.disclose').on('click', function() {
                // alert("DISCLOSE CLICKED!");
                $(this).closest('li').toggleClass('mjs-nestedSortable-collapsed').toggleClass('mjs-nestedSortable-expanded');
            });

            self.notifyChange();

            self.activateView(newNode);
        },

        addTopNode : function() {
            var self = this;
            self.navTree().addNode();
        },

        removeNode : function(node) {
            var self = this;
            var yes = gc.app.i18n('app:common.yes');
            var no = gc.app.i18n('app:common.no');

            app.showMessage('Are you sure you want to delete node' + ' "' + node.displayLabel() + '"?', 'Delete node', [ yes, no ]).then(function(confirm) {
                if (confirm == yes) {
                    node.deleteNode();
                    self.notifyChange();
                }
            });
        }

    }

    return NavigationIndexController;
});