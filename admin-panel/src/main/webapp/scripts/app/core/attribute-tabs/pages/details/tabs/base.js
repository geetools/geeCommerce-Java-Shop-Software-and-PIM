define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-attribute-tabs', 'gc-attribute' ], function(app, ko, gc, attrTabAPI, attrAPI) {

    // -----------------------------------------------------------------
    // Controller
    // -----------------------------------------------------------------
    function AttributeTabBaseController(options) {

        // Make sure that this object is being called with the 'new'
        // keyword.
        if (!(this instanceof AttributeTabBaseController)) {
            throw new TypeError("AttributeTabBaseController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.attributeTabVM = {};
        this.attributeTargetObjects = ko.observableArray();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate');
    }

    AttributeTabBaseController.prototype = {
        constructor : AttributeTabBaseController,
        saveData : function(context) {
            var self = this;

            // ----------------------------------------------------
            // Save new attribute tab
            // ----------------------------------------------------
            if (self.attributeTabVM.id() == 'new') {

                var newAttributeTab = {};
                newAttributeTab.label = self.attributeTabVM.label();
                newAttributeTab.targetObjectId = self.attributeTabVM.targetObjectId();
                newAttributeTab.position = self.attributeTabVM.position();
                newAttributeTab.showInVariantMaster = self.attributeTabVM.showInVariantMaster();
                newAttributeTab.showInProgramme = self.attributeTabVM.showInProgramme();
                newAttributeTab.showInBundle = self.attributeTabVM.showInBundle();
                newAttributeTab.showInProduct = self.attributeTabVM.showInProduct();
                newAttributeTab.enabled = self.attributeTabVM.enabled();

                attrTabAPI.createAttributeTab(newAttributeTab).then(function(savedAttributeTab) {
                    context.saved();
                    
                    self.attributeTabVM.id(savedAttributeTab.id);

                    // --------------------------------------------------------
                    // General tab
                    // --------------------------------------------------------
                    self.attributeTabVM.label(savedAttributeTab.label);
                    self.attributeTabVM.targetObjectId(savedAttributeTab.targetObjectId);
                    self.attributeTabVM.position(savedAttributeTab.position);
                    self.attributeTabVM.showInVariantMaster(savedAttributeTab.showInVariantMaster);
                    self.attributeTabVM.showInProgramme(savedAttributeTab.showInProgramme);
                    self.attributeTabVM.showInBundle(savedAttributeTab.showInBundle);
                    self.attributeTabVM.showInProduct(savedAttributeTab.showInProduct);
                    self.attributeTabVM.enabled(savedAttributeTab.enabled);

                    gc.app.channel.publish('attributeTab.created', savedAttributeTab);
                });

                // ----------------------------------------------------
                // Update existing attribute
                // ----------------------------------------------------
            } else {

                var updateModel = gc.app.newUpdateModel();

                updateModel.field('label', self.attributeTabVM.label(), true);
                updateModel.field('targetObjectId', self.attributeTabVM.targetObjectId());
                updateModel.field('position', self.attributeTabVM.position());
                updateModel.field('showInVariantMaster', self.attributeTabVM.showInVariantMaster());
                updateModel.field('showInProgramme', self.attributeTabVM.showInProgramme());
                updateModel.field('showInBundle', self.attributeTabVM.showInBundle());
                updateModel.field('showInProduct', self.attributeTabVM.showInProduct());
                updateModel.field('enabled', self.attributeTabVM.enabled());

                attrTabAPI.updateAttributeTab(self.attributeTabVM.id, updateModel).then(function(data) {
                    context.saved();
                });
            }
        },
        activate : function(attributeTabId) {
            var self = this;

            self.attributeTabVM = gc.app.sessionGet('attributeTabVM');
            
            return attrAPI.getAttributeTargetObjects().then(function(data) {
                var reduced = gc.ctxobj.reduce(data.data.attributeTargetObjects, 'id', 'name', gc.app.currentLang(), true, { name: 'text' });
                self.attributeTargetObjects(_.sortBy(reduced, "text"));
            });
        },
        attached : function(view, parent) {
            var self = this;
            
            $('#attributeTabBaseForm').addClass('save-button-listen-area');
            
            gc.app.onSaveEvent(function(context) {
                self.saveData(context);
            });
        },
        detached : function() {
            var self = this;
            gc.app.clearSaveEvent();
        }
    };

    return AttributeTabBaseController;
});