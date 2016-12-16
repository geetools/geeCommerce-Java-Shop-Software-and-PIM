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

        console.log('@@@@@@@@@@@@@@ IN ATTRIBUTE FORM!!');

        var AttributeVM = require('gc-attribute-vm');
        self.attributeVM = new AttributeVM();

        console.log('@@@@@@@@@@@@@@ IN ATTRIBUTE FORM!! ', self.attributeVM);

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

        self.productTypes = self.options.productTypes || self.attributeVM.productTypes;

        self.frontendInputs = self.options.frontendInputs || self.attributeVM.frontendInputs;

        self.backendTypes = self.options.backendTypes || self.attributeVM.backendTypes;

        self.inputTypes = self.options.inputTypes || self.attributeVM.inputTypes;
    };

    ctor.prototype.saveAndClose = function(viewModel, event) {
        var self = this;
        console.log('----------------- saveAndClose: ', viewModel, event);

        if(self.attributeVM.containsMinimumData()) {
            
        }
    };
    
    
    ctor.prototype.attached = function(view, parent) {
        var self = this;

        console.log('%%%%%%%%%%%%%%%%%%%% ATTACHED::: ', view, parent);
        
//        $(view).on('show.bs.modal', '.modal', function(e) {
//            self.initData();
//        });
    };

    ctor.prototype.detached = function() {
    };

    return ctor;
});