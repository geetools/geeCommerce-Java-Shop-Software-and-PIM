define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-attribute-group', 'knockout-validation', 'gc-attribute' ], function(app, ko, router, gc, attributeGroupAPI, validation, attrAPI) {

    // -----------------------------------------------------------------
    // Controller
    // -----------------------------------------------------------------
    function AttributeGroupBaseController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof AttributeGroupBaseController)) {
            throw new TypeError("AttributeGroupBaseController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.attributeGroupVM = [];
        this.attributeGroupId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate');
    }

    AttributeGroupBaseController.prototype = {
        constructor : AttributeGroupBaseController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.attribute-group.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.attribute-group.detailsSubtitle',
        saveData : function(context) {
            var self = this;

            var updateModel = gc.app.newUpdateModel();
            updateModel.field('code', self.attributeGroupVM.code());
            updateModel.field('label', self.attributeGroupVM.label(), true);
            updateModel.field('position', self.attributeGroupVM.position());

            if (self.attributeGroupVM.isNew()) {
                attributeGroupAPI.createAttributeGroup(updateModel).then(function(data) {
                    router.navigate('//attribute-groups/details/' + data.id);
                    context.saved();
                })
            } else {
                attributeGroupAPI.updateAttributeGroup(self.attributeGroupId(), updateModel).then(function(data) {
                    context.saved();
                })
            }
        },
        activate : function(data) {
            var self = this;
            self.attributeGroupId(data);
            self.attributeGroupVM = gc.app.sessionGet('attributeGroupVM');
        },
        attached : function(view, parent) {
            var self = this;

            $('#attributeGroupBaseForm').addClass('save-button-listen-area');

            gc.app.onSaveEvent(function(context) {
                self.saveData(context);
            });
        },
        detached : function() {
            var self = this;
            gc.app.clearSaveEvent();
        }
    }

    return AttributeGroupBaseController;
});