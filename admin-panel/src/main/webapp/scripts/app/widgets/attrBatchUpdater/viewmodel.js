define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-attribute' ], function(app, composition, ko, i18n, gc, attrAPI) {
    
    function AttributeValueVM(attribute) {
        var self = this;
        
        self.attribute = attribute;
        self.attributeId = attribute.id;
        self.code = self.attribute.code;
        
        self.label = function() {
            console.log('==================> attr.backendLabel:: ', self.attribute.backendLabel);
            
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
        
        self.comboboxValues = [];
        
        self.suggestions = [];
        
        self.getSuggestions = function() {
            
        };
        
        self.getOptions = function() {
            
        };
        
        self.addOption = function(data) {
            
        };
        
        self.removeOption = function(data) {
            
        };
        
        self.selectOptions = function() {
            console.log('OOOOOOOOOOOOOOOPTIOOOOOOOOOONS :::: ', self.attribute);
            
            var _options = [];
            
            _.each(self.attribute.options, function(option) {
                if(!_.isUndefined(option.label)) {
                    _options.push({ id: option.id, text: option.label.i18n });
                }
            });

            console.log('OOOOOOOOOOOOOOOPTIOOOOOOOOOONS :::: ', _options);
            
            
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
	};

	ctor.prototype.activate = function(settings) {
		var self = this;
		
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
		
        self.mode = settings.mode || 'closest';

        self.apiOptions = settings.apiOptions;

        console.log("OPTIONS")
        console.log(self.apiOptions)
		
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
            
            console.log('~~~~~~~~~~~~~~~ SELECTED ATTRIBUTE :::: ', self.attributes);
            console.log('~~~~~~~~~~~~~~~ SELECTED ATTRIBUTE :::: ', attributeId, _.findWhere(self.attributes, {id: attributeId}));
            
            if(attributeId != '') {
                var _attr = _.findWhere(self.attributes, {id: attributeId});
                
                if(!_.isEmpty(_attr)) {
                    self.attributeValues.push(new AttributeValueVM(_attr));
                    self.selectedAttribute('');
                }
            }
        });
        
        self.attributeValues.subscribe(function(newAttrVal) {
            console.log('&&&&&&&&&&&&&&&&&&&&&&&&&&& ', newAttrVal);
        });
        
	};
	
    ctor.prototype.attached = function(view) {
        var self = this;
        
        gc.app.showAttrBatchUpdaterModal.subscribe(function(newVal) {
            if(newVal === true) {
                console.log('--------------------------> view::: ', newVal, view, $(view).find(".modal-dialog").first());
                
                $(view).find(".modal-dialog").first().draggable({
                    handle: ".modal-header"
                });        
            }
        });
    };
    
    ctor.prototype.save = function(viewModel, event) {
        var self = this;
        console.log('----------------- save: ', viewModel, event);
    };
    
    ctor.prototype.showBatchAttributeUpdater = function() {
        var self = this;
        self.visible(true);
    };

    ctor.prototype.cancelBatchAttributeUpdater = function() {
        var self = this;
        self.visible(false);
    };
    
	return ctor;
});