define([
        'knockout', 'gc/gc', 'speakingurl'
], function(ko, gc, getSlug) {
    var AttributeVM = function(attributeId) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof AttributeVM)) {
            throw new TypeError("AttributeVM constructor cannot be called as a function.");
        }

        var self = this;

        self.id = ko.observable(attributeId);

        // --------------------------------------------------------
        // General tab
        // --------------------------------------------------------
        self.code = ko.observable();
        self.code2 = ko.observable();
        self.editable = ko.observable();
        self.enabled = ko.observable();
        self.targetObjectId = ko.observable();
        self.targetObject = ko.observable(); // fetched in separate call.
        self.type = ko.observable();
        self.scopes = ko.observableArray([]);
        self.backendLabel = ko.observableArray([]);
        self.frontendLabel = ko.observableArray([]);
        self.frontendFormat = ko.observableArray([]);
        self.options = ko.observableArray([]);
        self.searchable = ko.observable();
        self.frontendInput = ko.observable();
        self.frontendOutput = ko.observable();
        self.inputType = ko.observable();
        self.backendType = ko.observable();
        self.includeInProductListFilter = ko.observable();
        self.showInQuery = ko.observable();
        self.includeInSearchFilter = ko.observable();
        self.showInProductDetails = ko.observable();
        self.allowMultipleValues = ko.observable();
        self.i18n = ko.observable();
        self.linkedAttributeIds = ko.observableArray([]);
        self.productTypes = ko.observableArray([]);
        self.dimensionAttribute = ko.observable();
        self.allowNewOptionsViaImport = ko.observable();

        // --------------------------------------------------------
        // Validation properties
        // --------------------------------------------------------

        self.validationMin = ko.observableArray([]);
        self.validationMax = ko.observableArray([]);
        self.validationMinLength = ko.observableArray([]);
        self.validationMaxLength = ko.observableArray([]);
        self.validationFuture = ko.observableArray([]);
        self.validationPast = ko.observableArray([]);
        self.validationAssertTrue = ko.observableArray([]);
        self.validationAssertFalse = ko.observableArray([]);
        self.validationPattern = ko.observableArray([]);
        self.validationScript = ko.observableArray([]);
        self.validationMessage = ko.observableArray([]);

        // --------------------------------------------------------
        // Product list filter tab
        // --------------------------------------------------------
        self.productListFilterType = ko.observable();
        self.productListFilterIndexFields = ko.observableArray([]);
        self.productListFilterKeyAlias = ko.observable();
        self.productListFilterFormatLabel = ko.observable();
        self.productListFilterFormatValue = ko.observable();
        self.productListFilterParseValue = ko.observable();
        self.productListFilterMulti = ko.observable();
        self.productListFilterInheritFromParent = ko.observable();
        self.productListFilterIncludeChildren = ko.observable();
        self.productListFilterPosition = ko.observable();

        self.frontendInput.subscribe(function(newValue) {
            if (newValue == 'BOOLEAN') {
                self.backendType('BOOLEAN');
            } else {
                self.backendType('STRING');
            }
        });

        self.frontendLabel.subscribe(function(newValue) {
            self.backendLabel(newValue);

            var defLang = gc.app.defaultLanguage();
            var label = gc.ctxobj.val(newValue, defLang);
            var labelSlug = getSlug(label).replace(/\-/g, '_');
            self.code(labelSlug);
        });

        self.isNew = ko.computed(function() {
            return self.id() == 'new';
        });

        self.isEditable = ko.computed(function() {
            return self.editable() == true;
        });

        self.isCodeEditable = ko.observable(true);

        self.isVirtual = ko.computed(function() {
            return self.type() == 'VIRTUAL';
        });

        self.isProductAttribute = ko.computed(function() {
            return self.targetObject() && self.targetObject().code == 'product';
        });

        self.isOptionsAttribute = ko.computed(function() {
            return self.frontendInput() == 'SELECT';
        });

        self.showOptionsTab = ko.computed(function() {
            return !self.isNew() && !self.isVirtual() && self.isOptionsAttribute();
        });

        self.showProductListFilterTab = ko.computed(function() {
            return !self.isNew() && self.isProductAttribute() && self.includeInProductListFilter() == true;
        });

        self.showInputConditionsTab = ko.computed(function() {
            return !self.isNew() && !self.isVirtual();
        });

        self.showBackendType = ko.computed(function() {
            return self.frontendInput() != 'RICHTEXT' && self.frontendInput() != 'COMBOBOX' && self.frontendInput() != 'BOOLEAN' && self.frontendInput() != 'SELECT';
        });

        self.showLengthValidation = ko.computed(function() {
            return self.backendType() == 'STRING' && (self.frontendInput() == 'TEXT' || self.frontendInput() == 'RICHTEXT');
        });

        self.showMinMaxValidation = ko.computed(function() {
            return (self.backendType() == 'INTEGER' || self.backendType() == 'LONG' || self.backendType() == 'DOUBLE' || self.backendType() == 'SHORT' || self.backendType() == 'FLOAT')
                    && self.frontendInput() == 'TEXT';
        });

        self.showBooleanValidation = ko.computed(function() {
            return self.backendType() == 'BOOLEAN';
        });

        self.showDateValidation = ko.computed(function() {
            return self.backendType() == 'DATE';
        });

        self.productTypeValues = [
                {
                    id : 'PRODUCT',
                    text : 'Produkt'
                }, {
                    id : 'VARIANT_MASTER',
                    text : 'Variantenmaster'
                }, {
                    id : 'PROGRAMME',
                    text : 'Programm'
                }, {
                    id : 'BUNDLE',
                    text : 'Bundle'
                }
        ];

        self.frontendInputValues = [
                {
                    id : 'TEXT',
                    text : 'Freitext'
                }, {
                    id : 'RICHTEXT',
                    text : 'Richtext'
                }, {
                    id : 'COMBOBOX',
                    text : 'Combobox'
                }, {
                    id : 'BOOLEAN',
                    text : 'Ja/Nein-Schalter'
                }, {
                    id : 'SELECT',
                    text : 'Werteliste'
                }
        ];

        self.backendTypeValues = [
                {
                    id : 'STRING',
                    text : 'Text'
                }, {
                    id : 'INTEGER',
                    text : 'Number'
                }, {
                    id : 'LONG',
                    text : 'Large Number'
                }, {
                    id : 'DOUBLE',
                    text : 'Decimal Number'
                }, {
                    id : 'BOOLEAN',
                    text : 'Boolean'
                }, {
                    id : 'DATE',
                    text : 'Date'
                }, {
                    id : 'FLOAT',
                    text : 'Small Decimal Number'
                }, {
                    id : 'SHORT',
                    text : 'Small Number'
                }
        ];

        self.inputTypeValues = [
                {
                    id : 'OPTOUT',
                    text : 'Opt-out'
                }, {
                    id : 'MANDATORY',
                    text : 'Pflicht'
                }, {
                    id : 'OPTIONAL',
                    text : 'Freiwillig'
                }
        ];

        // Set default values.
        self.frontendInput('TEXT');
        self.frontendOutput('TEXT');
        self.type('DEFAULT');

        self.containsMinimumData = function() {
            if (_.isEmpty(self.targetObjectId())) {
                return false;
            }

            if (self.editable() !== true && self.editable() !== false) {
                return false;
            }

            if (_.isEmpty(self.frontendLabel())) {
                return false;
            }

            if (_.isEmpty(self.backendLabel())) {
                return false;
            }

            if (_.isEmpty(self.code())) {
                return false;
            }

            if (_.isEmpty(self.inputType())) {
                return false;
            }

            if (_.isEmpty(self.frontendInput())) {
                return false;
            }

            if (_.isEmpty(self.backendType())) {
                return false;
            }

            if (self.allowMultipleValues() !== true && self.allowMultipleValues() !== false) {
                return false;
            }

            if (self.i18n() !== true && self.i18n() !== false) {
                return false;
            }

            if (self.allowNewOptionsViaImport() !== true && self.allowNewOptionsViaImport() !== false) {
                return false;
            }

            return true;
        }
    };

    return AttributeVM;
});