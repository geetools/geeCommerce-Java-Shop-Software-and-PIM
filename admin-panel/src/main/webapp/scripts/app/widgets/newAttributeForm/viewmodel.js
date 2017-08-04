define([
        'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-attribute'
], function(app, composition, ko, i18n, gc, attrAPI) {
    var ctor = function() {
    };

    ctor.prototype.activate = function(options) {
        var self = this;

        self.app = gc.app;

        self.options = options || {};

        self.targetObjectCode = self.options.forType;

        var AttributeVM = require('gc-attribute-vm');
        self.attributeVM = new AttributeVM();

        self.showModal = ko.observable(false);

        self.value = self.options.value;
        self.title = self.options.title || 'New Attribute';

        self.initData();

        // The context objectc must always be an array.
        if (_.isUndefined(self.value) && _.isFunction(self.value)) {
            self.value([]);
        }

        return attrAPI.getAttributeTargetObjects({
            filter : {
                code : self.targetObjectCode
            }
        }).then(function(result) {
            if (!_.isEmpty(result.data.attributeTargetObjects) && result.data.attributeTargetObjects.length == 1 && result.data.attributeTargetObjects[0].code == self.targetObjectCode) {
                self.attributeVM.targetObject(result.data.attributeTargetObjects[0]);
                self.attributeVM.targetObjectId(result.data.attributeTargetObjects[0].id);
            }
        });
    };

    ctor.prototype.initData = function() {
        var self = this;

        self.productTypeValues = self.options.productTypes || self.attributeVM.productTypeValues;

        self.frontendInputValues = self.options.frontendInputs || self.attributeVM.frontendInputValues;

        self.backendTypeValues = self.options.backendTypes || self.attributeVM.backendTypeValues;

        self.inputTypeValues = self.options.inputTypes || self.attributeVM.inputTypeValues;
    };

    ctor.prototype.saveAndClose = function(viewModel, event) {
        var self = this;

        if (self.attributeVM.containsMinimumData()) {
            self.initProgressBar();

            var newAttribute = self.toRestObject(self.attributeVM);

            gc.app.updateProgressBar(50);

            attrAPI.createAttribute(newAttribute).then(function(savedAttribute) {
                gc.app.updateProgressBar(100);
                gc.app.resetProgressBar();
                
                self.attributeVM.id(savedAttribute.id);
                gc.app.channel.publish('attribute.created', savedAttribute);
            });
        }
    };

    ctor.prototype.toRestObject = function(attributeVM) {
        var newAttribute = {};
        newAttribute.targetObjectId = attributeVM.targetObjectId();
        newAttribute.type = attributeVM.type();
        newAttribute.backendLabel = attributeVM.backendLabel();
        newAttribute.productTypes = attributeVM.productTypes();
        newAttribute.frontendLabel = attributeVM.frontendLabel();
        newAttribute.code = attributeVM.code();
        newAttribute.enabled = true;
        newAttribute.searchable = false;
        newAttribute.includeInProductListFilter = false;
        newAttribute.includeInProductListQuery = false;
        newAttribute.includeInSearchFilter = false;
        newAttribute.allowNewOptionsViaImport = attributeVM.allowNewOptionsViaImport();
        newAttribute.editable = attributeVM.editable();
        newAttribute.frontendInput = attributeVM.frontendInput();
        newAttribute.frontendOutput = attributeVM.frontendOutput();
        newAttribute.inputType = attributeVM.inputType();
        newAttribute.backendType = attributeVM.backendType();
        newAttribute.allowMultipleValues = attributeVM.allowMultipleValues();
        newAttribute.i18n = attributeVM.i18n();
        return newAttribute;
    };

    ctor.prototype.initProgressBar = function(view, parent) {
        gc.app.initProgressBar();
        gc.app.updateProgressBar(25);
    };

    ctor.prototype.attached = function(view, parent) {
        var self = this;

        // $(view).on('show.bs.modal', '.modal', function(e) {
        // self.initData();
        // });
    };

    ctor.prototype.detached = function() {
    };

    return ctor;
});