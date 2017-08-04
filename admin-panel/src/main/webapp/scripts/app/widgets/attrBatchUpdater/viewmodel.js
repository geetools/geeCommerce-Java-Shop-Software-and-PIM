define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-attribute', 'gc-attribute/util' ], function(app, composition, ko, i18n, gc, attrAPI, attrUtil) {

    function AttributeValueVM(attribute) {
        var self = this;

        self.attribute = attribute;
        self.attributeId = attribute.id;
        self.code = self.attribute.code;

        self.label = function() {
            return self.attribute.backendLabel;

            return gc.ctxobj.val(self.attribute.backendLabel, gc.app.currentUserLang(), self.mode) || "";
        };

        self.isEditable = function() {
            return self.attribute.editable;
        };

        self.isEnabled = function() {
            return self.attribute.enabled;
        };

        self.isMultiple = function() {
            return self.attribute.allowMultipleValues;
        };

        self.isOption = function() {
            return self.attribute.optionAttribute;
        };

        self.frontendInput = function() {
            return self.attribute.frontendInput;
        };

        self.isI18n = function() {
            return self.attribute.i18n;
        };

        self.isShowField = function() {
            return true;
        };


        self.selectOptions = function() {
            var _options = [];

            _.each(self.attribute.options, function(option) {
                if(!_.isUndefined(option.label)) {
                    _options.push({ id: option.id, text: option.label.i18n });
                }
            });

            return _options;
        };

        self.value = ko.observableArray([]);
    }

	var ctor = function() {
        var self = this;
	    self.gc = gc;

        self.attributes = [];
        self.attributesAsOptions = [];
	    self.attributeValues = ko.observableArray([]);

        self.updateModes = [
            {
                id : 'PARTLY_REPLACE',
                text : gc.app.i18n('app:common.attrBatchUpdaterModePartlyReplace') || "Partly replace value (only adds or updates specified languages)"
            },
            {
                id : 'COMPLETE_REPLACE',
                text : gc.app.i18n('app:common.attrBatchUpdaterModeCompleteReplace') || "Replace complete value (may remove any none specified languages)"
            },
            {
                id : 'REMOVE',
                text : gc.app.i18n('app:common.attrBatchUpdaterModeRemove') || "Remove selected attributes"
            }
        ];

        self.selectedUpdateMode = ko.observable();
        self.headerMessage = ko.observable('0 attributes will be added or updated on 0 products.');
        self.headerWarningMessage = ko.observable('');
	};

	ctor.prototype.activate = function(settings) {
		var self = this;

		if(settings.pager) {
		    self.pager = settings.pager;
		}

        if (settings.visible) {
            self.visible = settings.visible;
        }

        if (settings.displayMode) {
            self.displayMode = settings.displayMode;
        }

        self.linkText = ko.observable(settings.linkText || 'Batch Attribute Updater');

        self.title = settings.title || 'Batch Attribute Updater';

        if(settings.showModalLink === false) {
            self.showModalLink = false;
        } else {
            self.showModalLink = true;
        }

		self.forType = settings.forType;
		self.gridTableData = {};

        self.updateWarnMessage();

		gc.app.channel.subscribe(self.forType + '.gt.onchange', function(data) {
		    self.gridTableData = data;
	        self.updateWarnMessage();

          self.headerMessage(self.attributeValues().length + ' attributes will be added or updated on ' + ( data.numSelectedRows || 0 ) + self.forType + ' objects.');
		});

		self.attributeValues.subscribe(function(newValue) {
	        self.headerMessage(newValue.length + ' attributes will be added or updated on ' + ( self.gridTableData.numSelectedRows || 0 ) + self.forType + ' objects.');
	        self.updateWarnMessage();
		});

        self.mode = settings.mode || 'closest';

        self.apiOptions = settings.apiOptions;

        self.options = [];

        attrAPI.getAttributes(self.forType, self.apiOptions).then(function(data) {
            var attributes = data.data.attributes;
            var attributesAsOptions = [];

            _.forEach(attributes, function(attr) {
                if(!_.isEmpty(attr.options)) {
                    gc.ctxobj.enhance(attr.options, [ 'label' ], 'any');
                }

                attributesAsOptions.push({
                    id : attr.id,
                    text : gc.ctxobj.val(attr.backendLabel, gc.app.currentUserLang(), self.mode) || ""
                });
            });

            self.attributes = attributes;
            self.attributesAsOptions = attributesAsOptions;
        });

        self.selectedAttribute = ko.observable();
        self.selectedAttribute.subscribe(function(attributeId) {
            if(attributeId != '') {
                var _attr = _.findWhere(self.attributes, {id: attributeId});

                if(!_.isEmpty(_attr)) {
                    self.attributeValues.push(new AttributeValueVM(_attr));
                    self.selectedAttribute('');
                }
            }
        });
	};

    ctor.prototype.attached = function(view) {
        var self = this;

        gc.app.showAttrBatchUpdaterModal.subscribe(function(newVal) {
            if(newVal === true) {
                $(view).find(".modal-dialog").first().draggable({
                    handle: ".modal-header"
                });
            }
        });
    };

    ctor.prototype.save = function(viewModel, event) {
        var self = this;

        var updateModel = attrUtil.toNewUpdateModel(self.attributeValues);

        if(self.gridTableData.numSelectedRows > 0) {
          if(self.gridTableData.selectMode === 'all_pages') {
            updateModel.whereIds([]);
            updateModel.whereIgnoreIds(self.gridTableData.uncheckedIds);
            updateModel.whereSearchKeyword(self.pager.searchKeyword());
            updateModel.whereQuery(self.pager.query());

            attrAPI.batchUpdateAttributeValues(updateModel, self.forType);

          } else if(self.gridTableData.selectMode === 'current_page') {
            var objectIds = self.gridTableData.checkedIds;

            updateModel.whereIds(self.gridTableData.checkedIds);

            attrAPI.batchUpdateAttributeValues(updateModel, self.forType);
          }
        }
    };

    ctor.prototype.showBatchAttributeUpdater = function() {
        var self = this;
        self.visible(true);
    };

    ctor.prototype.cancelBatchAttributeUpdater = function() {
        var self = this;
        self.visible(false);
    };

    ctor.prototype.updateWarnMessage = function() {
        var self = this;

        var numSelectedRows = 0;
        var selectMode = '';
        if(self.gridTableData) {
            numSelectedRows = self.gridTableData.numSelectedRows || 0;
            selectMode = self.gridTableData.selectMode;
        }

        if(numSelectedRows === 0) {
            self.headerWarningMessage("No products have been selected for updating. <b>Select products first.</b>");
        } else if((!self.pager.isQuery() && !self.pager.isSearch()) && numSelectedRows > 0 && selectMode == 'all_pages') {
            self.headerWarningMessage("You are about to update <b>all</b> products as no filtering is in place.");
        } else if(numSelectedRows > 1000) {
            self.headerWarningMessage("You are about to update more than <b>1000</b> products.");
        } else {
            self.headerWarningMessage('');
        }
    };

	return ctor;
});
