define([
        'durandal/app', 'plugins/router', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute'
], function(app, router, ko, gc, productAPI, attrAPI) {

    // -----------------------------------------------------------------
    // Controller
    // -----------------------------------------------------------------
    function ProductFinderResultController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ProductFinderResultController)) {
            throw new TypeError("ProductFinderResultController constructor cannot be called as a function.");
        }

        this.gc = gc;
        this.app = gc.app;
        this.pager = {};
        this.gridTableMenuItems = [];
        this.groupOptions = ko.observableArray([]);

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'computeStatus');
    }

    ProductFinderResultController.prototype = {
        constructor : ProductFinderResultController,
        statusDescription : function(data) {
            var statusDescTxt = '0_unbearbeitet';

            var attr = gc.attributes.find(data.attributes, 'status_description');
            var activeStore = gc.app.sessionGet('activeStore');
            var availableStores = gc.app.confGet('availableStores');

            // If no store is currently selected, just show the current values as text.
            if (_.isEmpty(activeStore) || _.isEmpty(activeStore.id)) {
                statusDescTxt = '';
                var summaryText = '';

                var x = 0;
                _.each(availableStores, function(store) {

                    if (store.id && store.id != '') {

                        var txt = '0_unbearbeitet';

                        if (!_.isUndefined(attr)) {

                            var xOptionIds = attr.xOptionIds;
                            var attributeOptions = attr.attributeOptions;

                            if (!_.isEmpty(attributeOptions) && !_.isEmpty(xOptionIds)) {
                                var optionId = gc.ctxobj.val(xOptionIds, undefined, undefined, store.id);

                                if (optionId) {
                                    var option = _.findWhere(attributeOptions, {
                                        id : optionId[0]
                                    });
                                    txt = gc.ctxobj.val(option.label, gc.app.currentUserLang(), 'closest');
                                }
                            }
                        } else {
                            txt = '0_unbearbeitet';
                        }

                        if (!_.isUndefined(txt)) {
                            if (txt.length > 20) {
                                summaryText += '<span class="gridStoreTxt"> ' + txt.substring(0, 20) + '...</span><br/>';
                            } else {
                                summaryText += '<span class="gridStoreTxt"> ' + txt + '</span><br/>';
                            }

                            x++;
                        }
                    }
                });

                statusDescTxt = summaryText;
            } else {

                if (!_.isUndefined(attr)) {

                    var xOptionIds = attr.xOptionIds;
                    var attributeOptions = attr.attributeOptions;

                    var optionId = gc.ctxobj.val(xOptionIds, undefined, undefined, activeStore.id);

                    if (optionId) {
                        var option = _.findWhere(attributeOptions, {
                            id : optionId[0]
                        });
                        statusDescTxt = gc.ctxobj.val(option.label, gc.app.currentUserLang(), 'closest');
                    }
                }
            }

            return statusDescTxt;
        },
        statusImages : function(data) {
            var txt = '0_unbearbeitet';

            var attr = gc.attributes.find(data.attributes, 'status_image');

            if (!_.isUndefined(attr)) {
                var attributeOptions = attr.attributeOptions;

                if (!_.isEmpty(attributeOptions)) {
                    txt = gc.ctxobj.val(attributeOptions[0].label, gc.app.currentUserLang(), 'closest');
                }
            }

            return txt;
        },
        statusArticle : function(data) {
            var statusDescTxt = '0_nicht für online freigegeben';

            var attr = gc.attributes.find(data.attributes, 'status_article');
            var activeStore = gc.app.sessionGet('activeStore');
            var availableStores = gc.app.confGet('availableStores');

            // If no store is currently selected, just show the current values as text.
            if (_.isEmpty(activeStore) || _.isEmpty(activeStore.id)) {
                statusDescTxt = '';
                var summaryText = '';

                _.each(availableStores, function(store) {

                    if (store.id && store.id != '') {

                        var txt = '0_nicht für online freigegeben';

                        if (!_.isUndefined(attr)) {

                            var xOptionIds = attr.xOptionIds;
                            var attributeOptions = attr.attributeOptions;

                            if (!_.isEmpty(attributeOptions) && !_.isEmpty(xOptionIds)) {
                                var optionId = gc.ctxobj.val(xOptionIds, undefined, undefined, store.id);

                                if (optionId) {
                                    var option = _.findWhere(attributeOptions, {
                                        id : optionId[0]
                                    });
                                    txt = gc.ctxobj.val(option.label, gc.app.currentUserLang(), 'closest');
                                }
                            }
                        } else {
                            txt = '0_unbearbeitet';
                        }

                        if (!_.isUndefined(txt)) {
                            if (txt.length > 20) {
                                summaryText += '<span class="gridStoreTxt"> ' + txt.substring(0, 20) + '...</span><br/>';
                            } else {
                                summaryText += '<span class="gridStoreTxt"> ' + txt + '</span><br/>';
                            }
                        }
                    }
                });

                statusDescTxt = summaryText;
            } else {

                if (!_.isUndefined(attr)) {

                    var xOptionIds = attr.xOptionIds;
                    var attributeOptions = attr.attributeOptions;

                    var optionId = gc.ctxobj.val(xOptionIds, undefined, undefined, activeStore.id);

                    if (optionId) {
                        var option = _.findWhere(attributeOptions, {
                            id : optionId[0]
                        });
                        statusDescTxt = gc.ctxobj.val(option.label, gc.app.currentUserLang(), 'closest');
                    }
                }
            }

            return statusDescTxt;
        },
        group : function(data) {
            var group = this.optionsText(data, "product_group");
            if (group == '') {
                group = this.optionsText(data, "programme");
            }
            return group;

        },
        optionsText : function(data, code) {
            var txt = '';

            var attr = gc.attributes.find(data.attributes, code);

            if (!_.isUndefined(attr)) {
                var attributeOptions = attr.attributeOptions;

                if (!_.isEmpty(attributeOptions)) {
                    txt = gc.ctxobj.val(attributeOptions[0].label, gc.app.currentUserLang(), 'closest');
                }
            }

            return txt;
        },
        computeStatus : function(data) {
            var statusText = '';

            var descStatus = gc.attributes.find(data.attributes, 'status_description');
            var descStatusText = '0';

            if (!_.isUndefined(descStatus)) {
                var attributeOptions = descStatus.attributeOptions;

                if (!_.isEmpty(attributeOptions)) {
                    descStatusText = gc.ctxobj.val(attributeOptions[0].label, gc.app.currentUserLang(), 'closest');
                }
            }

            var imageStatus = gc.attributes.find(data.attributes, 'status_image');
            var imageStatusText = '0';

            if (!_.isUndefined(imageStatus)) {
                var attributeOptions = imageStatus.attributeOptions;

                if (!_.isEmpty(attributeOptions)) {
                    imageStatusText = gc.ctxobj.val(attributeOptions[0].label, gc.app.currentUserLang(), 'closest');
                }
            }

            var articleStatus = gc.attributes.find(data.attributes, 'status_article');
            var articleStatusText = '0';

            if (!_.isUndefined(articleStatus)) {
                var attributeOptions = articleStatus.attributeOptions;

                if (!_.isEmpty(attributeOptions)) {
                    articleStatusText = gc.ctxobj.val(attributeOptions[0].label, gc.app.currentUserLang(), 'closest');
                }
            }

            return (_.isUndefined(descStatusText) ? '0' : descStatusText.substring(0, 1)) + '-' + (_.isUndefined(imageStatusText) ? '0' : imageStatusText.substring(0, 1)) + '-'
                    + (_.isUndefined(articleStatusText) ? '0' : articleStatusText.substring(0, 1));
        },
        activate : function(data) {
            gc.app.pageTitle(gc.app.i18n('app:modules.product.title'));
            gc.app.pageDescription(gc.app.i18n('app:modules.product.subtitle'));

            var attrStatusDesc = gc.app.dataGet('attr:status_description');
            var attrStatusImage = gc.app.dataGet('attr:status_image');
            var attrStatusArticle = gc.app.dataGet('attr:status_article');

            var statusDescSelectOptions = [];
            var statusImageSelectOptions = [];
            var statusArticleSelectOptions = [];

            if (!_.isEmpty(attrStatusDesc) && !_.isEmpty(attrStatusDesc.options)) {
                statusDescSelectOptions.push({
                    value : '',
                    label : gc.app.i18n('app:common.choose')
                });
                _.forEach(attrStatusDesc.options, function(option) {
                    statusDescSelectOptions.push({
                        label : option.label.i18n(),
                        value : option.id
                    });
                });
            }

            if (!_.isEmpty(attrStatusImage) && !_.isEmpty(attrStatusImage.options)) {
                statusImageSelectOptions.push({
                    value : '',
                    label : gc.app.i18n('app:common.choose')
                });
                _.forEach(attrStatusImage.options, function(option) {
                    statusImageSelectOptions.push({
                        label : option.label.i18n(),
                        value : option.id
                    });
                });
            }

            if (!_.isEmpty(attrStatusArticle) && !_.isEmpty(attrStatusArticle.options)) {
                statusArticleSelectOptions.push({
                    value : '',
                    label : gc.app.i18n('app:common.choose')
                });
                _.forEach(attrStatusArticle.options, function(option) {
                    statusArticleSelectOptions.push({
                        label : option.label.i18n(),
                        value : option.id
                    });
                });
            }

            var self = this;

            self.value = data.value;
            self.allChecked = false;
            self.initialValues = data.checked;

            var computedOptions = [];
            computedOptions.push({
                id : '',
                text : gc.app.i18n('app:common.choose')
            });

            attrAPI.getAttributes('product', {
                fields : [
                        'code', 'code2', 'backendLabel', 'editable', 'enabled', 'inputType', 'frontendInput', 'optionAttribute', 'allowMultipleValues', 'i18n', 'options', 'tags', 'label'
                ],
                filter : {
                    code : "product_group, programme"
                }
            }).then(function(response) {
                var attributes = response.data.attributes;

                _.each(attributes, function(attr) {
                    var attrOptions = attr.options;
                    gc.ctxobj.enhance(attrOptions, [
                        'label'
                    ], 'any');
                    _.forEach(attrOptions, function(option) {
                        if (option.label && option.label.i18n) {
                            computedOptions.push({
                                text : option.label.i18n,
                                id : "$opt." + attr.code + "__" + option.id
                            });
                        }
                    });
                });

                self.groupOptions(computedOptions);
            });

            self.pager = data.pager;

            // We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
            return self.pager.load();
        },
        attached : function(view, parent) {
            var self = this;
            self.initCheckboxes(view, parent);
        },
        compositionComplete : function() {
            var self = this;
        },
        initCheckboxes : function(view, parent) {
            var self = this;
            var productIds = self.initialValues();

            for (var i = 0; i < productIds.length; i++) {
                var productId = productIds[i];

                var checkboxEL = $(view).find('table>tbody th.td-select>input[value=' + productId + ']');
                if (checkboxEL) {
                    console.log('INIT CHECKBOX::: ', checkboxEL);
                    checkboxEL.prop('checked', true);
                }
            }
        },
        checkAll : function(model, event) {
            var self = this;
            var tableEL = $(event.target).closest('table');

            var checkboxesEL = $(tableEL).find('th.td-select>input');

            if (!self.allChecked) {
                checkboxesEL.prop('checked', true);
                self.allChecked = true;
            } else {
                checkboxesEL.prop('checked', false);
                self.allChecked = false;
            }
        }
    }

    return ProductFinderResultController;
});