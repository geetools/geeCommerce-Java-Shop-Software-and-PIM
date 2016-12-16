define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-attribute-tabs', 'gc-attribute' ], function(app, ko, gc, attrTabAPI, attrAPI) {

    function AttributeTabVM(id) {
        var self = this;
        self.id = ko.observable(id);
        self.label = ko.observableArray();
        self.targetObjectId = ko.observable();
        self.targetObject = ko.observable(); // fetched in separate call.
        self.position = ko.observable();
        self.showInVariantMaster = ko.observable();
        self.showInProgramme = ko.observable();
        self.showInBundle = ko.observable();
        self.showInProduct = ko.observable();
        self.enabled = ko.observable();
        self.attributes = ko.observableArray();

        self.targetObjectId.subscribe(function(value) {
            attrAPI.getAttributeTargetObjects().then(function(data) {
                var targetObj = _.findWhere(data.data.attributeTargetObjects, { id : self.targetObjectId() });
                self.targetObject(targetObj);
            });
        });        
        
        self.isNew = ko.computed(function() {
            return self.id() == 'new';
        });
    }

    // -----------------------------------------------------------------
    // Controller
    // -----------------------------------------------------------------
    function AttributeTabIndexController(options) {

        var self = this;

        // Make sure that this object is being called with the 'new'
        // keyword.
        if (!(this instanceof AttributeTabIndexController)) {
            throw new TypeError("AttributeTabIndexController constructor cannot be called as a function.");
        }

        self.app = gc.app;
        self.attributeTabId = undefined;
        self.attributeTabVM = {};

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate', 'attached');
    }

    AttributeTabIndexController.prototype = {
        constructor : AttributeTabIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'Attributtab';
            var vm = ko.unwrap(self.attributeTabVM);

            if (!_.isUndefined(self.attributeTabVM)) {
                var label = ko.unwrap(vm.label);

                if (!_.isEmpty(label)) {
                    title += ': ' + gc.ctxobj.val(label, self.app.currentLang(), 'any');
                }
            }

            return title;
        },
        pageDescription : 'Attributtabs ansehen und bearbeiten',
        saveData : function() {
            var self = this;

            var updateModel = gc.app.newUpdateModel();
        },
        activate : function(attributeTabId) {
            var self = this;

            if (attributeTabId == 'new') {
                gc.app.pageTitle(gc.app.i18n('app:modules.attributetabs.newAttributeTabTitle'));
                gc.app.pageDescription(gc.app.i18n('app:modules.attributetabs.newAttributeTabDesc'));

                self.attributeTabVM = new AttributeTabVM(attributeTabId);
                gc.app.sessionPut('attributeTabVM', self.attributeTabVM);
            } else {

                gc.app.pageTitle(self.pageTitle());
                gc.app.pageDescription(self.pageDescription);

                return attrTabAPI.getAttributeTab(attributeTabId).then(function(attributeTab) {
                    self.attributeTabId = attributeTab.id;

                    self.attributeTabVM = new AttributeTabVM(attributeTab.id);
                    self.attributeTabVM.label(attributeTab.label);
                    self.attributeTabVM.targetObjectId(attributeTab.targetObjectId);
                    self.attributeTabVM.position(attributeTab.position);
                    self.attributeTabVM.showInVariantMaster(attributeTab.showInVariantMaster);
                    self.attributeTabVM.showInProgramme(attributeTab.showInProgramme);
                    self.attributeTabVM.showInBundle(attributeTab.showInBundle);
                    self.attributeTabVM.showInProduct(attributeTab.showInProduct);
                    self.attributeTabVM.enabled(attributeTab.enabled);

                    gc.app.sessionPut('attributeTabVM', self.attributeTabVM);
                    gc.app.pageTitle(self.pageTitle());
                });
            }
        },
        attached : function() {
            var self = this;
        }
    };

    return AttributeTabIndexController;
});
