define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-attribute', 'gc-attribute/util', 'gc-attribute-tabs' ], function(app, ko, gc, attrAPI, attrUtil, attrTabAPI) {

    function TabVM(vm, data) {
        var self = this;

        self.vm = vm;
        self.data = ko.observable(data);
        self.label = ko.observable();

        ko.computed(function() {
            var tab = ko.unwrap(self.data);
            gc.ctxobj.enhance(tab, [ 'label' ]);
            self.label(tab.label);
        });

        self.id = function() {
            var tab = ko.unwrap(self.data);
            return tab.id;
        };

        self.isEnabled = function() {
            var tab = ko.unwrap(self.data);
            return tab.enabled;
        }

        /*
         * self.isShowInProduct = function() { var tab = ko.unwrap(self.data); return tab.showInProduct; }
         * 
         * self.isShowInProgramme = function() { var tab = ko.unwrap(self.data); return tab.showInProgramme; }
         * 
         * self.isShowInVariantMaster = function() { var tab = ko.unwrap(self.data); return tab.showInVariantMaster; }
         */
        self.show = ko.computed(function() {
            return true;
            /*
             * var tab = ko.unwrap(self.data);
             * 
             * var showForProductType = false
             * 
             * if(self.productVM.isProgramme()) { showForProductType = self.isShowInProgramme(); } else if(self.productVM.isVariantMaster()) { showForProductType = self.isShowInVariantMaster(); } else {
             * showForProductType = self.isShowInProduct(); }
             * 
             * return self.productVM.productExists() && self.productVM.isCategorized() && showForProductType;
             */
        });

        // -----------------------------------------------------------------------------------------
        // Optional callback stored in DB: Should anything be rendered prior to the form fields.
        // -----------------------------------------------------------------------------------------
        self.executePreRenderCallback = function(formAttributeValues) {
            var tab = ko.unwrap(self.data);

            if (!_.isUndefined(tab.preRenderCallback)) {
                var fn = (new Function(tab.preRenderCallback));

                if (typeof fn === "function") {
                    return fn(ko, gc, tab, formAttributeValues);
                }
            }
        }

        // -----------------------------------------------------------------------------------------
        // Optional callback stored in DB: Should anything be rendered after to the form fields.
        // -----------------------------------------------------------------------------------------
        self.executePostRenderCallback = function(formAttributeValues) {
            var tab = ko.unwrap(self.data);

            if (!_.isUndefined(tab.postRenderCallback)) {
                var fn = (new Function(tab.postRenderCallback));

                if (typeof fn === "function") {
                    return fn(ko, gc, tab, formAttributeValues);
                }
            }
        }

        // -----------------------------------------------------------------------------------------
        // Optional callback stored in DB: Should the current attribute be rendered?
        // -----------------------------------------------------------------------------------------
        self.executeDisplayAttributeCallback = function(formAttributeValues, attrVal) {
            var tab = ko.unwrap(self.data);

            if (!_.isUndefined(tab.displayAttributeCallback)) {
                var fn = (new Function(tab.displayAttributeCallback));

                if (typeof fn === "function") {
                    return fn(ko, gc, tab, formAttributeValues, attrVal);
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        // -----------------------------------------------------------------------------------------
        // Optional callback stored in DB: Should the attributes belonging to this tab be rendered?
        // -----------------------------------------------------------------------------------------
        self.executeDisplayAttributesCallback = function(formAttributeValues) {
            var tab = ko.unwrap(self.data);

            if (!_.isUndefined(tab.displayAttributesCallback)) {
                var fn = (new Function(tab.displayAttributesCallback));

                if (typeof fn === "function") {
                    return fn(ko, gc, tab, formAttributeValues);
                }
            } else {
                return true;
            }
        }
    }

    return {
        getTabsPromise : function(vm, code, tabsObservable) {
            return attrAPI.getAttributeTargetObjects({
                filter : {
                    code : code
                }
            }).then(function(data) {
                return attrTabAPI.getAttributeTabs(data.data.attributeTargetObjects[0].id).then(function(data) {
                    var tabs = []
                    _.each(data.data.attributeTabs, function(tab) {
                        tabs.push(new TabVM(vm, tab));
                    });
                    tabsObservable(tabs);
                });
            });

            // return attrTabAPI.getAttributeTabs(type).then(function(data) {
            // var tabs = []
            // _.each(data.data.attributeTabs, function(tab) {
            // tabs.push(new TabVM(vm, tab));
            // });
            // tabsObservable(tabs);
            // });
        },
        getAttributesForTabsPromise : function(attributeTabId, modelObject, modelAttributes, collection, formAttributeValues) {

            var self = this;
            console.log(attributeTabId);

            return attrTabAPI.getAttributeTabMapping(attributeTabId).then(
                    function(data) {

                        var attributeTabMappings = data.data.attributeTabMappings;

                        var attributeIds = _.pluck(attributeTabMappings, 'attributeId');

                        if (!_.isEmpty(attributeIds)) {
                            return attrAPI.getInputConditionsFor({
                                fields : [ 'whenAttributeId', 'hasOptionIds', 'showAttributeId', 'showOptionsHavingTag', 'applyToProductTypes' ],
                                filter : {
                                    showAttributeId : attributeIds.join()
                                }
                            }).then(function(response) {
                                self.attributeInputConditions = response.data.attributeInputConditions;
                            }).then(
                                    function(data) {

                                        return attrAPI.getAttributes('product',
                                                {
                                                    fields : [ 'code', 'code2', 'backendLabel', 'editable', 'enabled', 'inputType', 'frontendInput', 'optionAttribute', 'allowMultipleValues', 'i18n',
                                                            'options', 'tags', 'label', 'productTypes', 'scopes' ],
                                                    filter : {
                                                        id : attributeIds.join()
                                                    }
                                                }).then(
                                                function(response) {
                                                    var attributes = response.data.attributes;

                                                    // The attributes come unsorted, so we make sure that we restore the tab-mapping-order again.
                                                    var sortedAttributes = [];
                                                    _.each(attributeIds, function(attrId) {
                                                        var foundAttr = _.findWhere(attributes, {
                                                            id : attrId
                                                        });
                                                        if (!_.isEmpty(foundAttr)) {
                                                            sortedAttributes.push(foundAttr);
                                                        }
                                                    });

                                                    var formAttributeValuesArray = [];

                                                    _.each(sortedAttributes, function(attr) {

                                                        formAttributeValuesArray.push(attrUtil.getAttribute(attr, attributeTabId, modelObject, modelAttributes, formAttributeValues,
                                                                self.attributeInputConditions, collection));
                                                    });

                                                    ko.utils.arrayPushAll(formAttributeValues, formAttributeValuesArray);
                                                    formAttributeValues.valueHasMutated();

                                                })
                                    });
                        }
                    });

        }
    }
});