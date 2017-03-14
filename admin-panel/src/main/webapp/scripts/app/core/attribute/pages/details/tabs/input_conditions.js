define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-attribute' ], function(app, ko, gc, attrAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function AttributeInputConditionsController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof AttributeInputConditionsController)) {
			throw new TypeError("AttributeInputConditionsController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.attributeId = undefined;
		this.attributeVM = {};
		this.attributeInputConditions = ko.observableArray([]);
		this.newAttributeInputConditions = ko.observableArray();
		// Attributes that have selectable options - not free text.
		this.optionAttributes = ko.observableArray([]);
		this.availableOptionGroupingTags = ko.observableArray([]);
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'appendAttributeOptions', 'loadAttributeInputConditions', 'addNewInputConditionRow', 'removeNewInputConditionRow', 'removeInputCondition', 'activate', 'attached');
	}

	AttributeInputConditionsController.prototype = {
		constructor : AttributeInputConditionsController,
    	// Fields used for filtering and sorting.
		gridHeaders: [
          {'field' : 'whenAttributeId', 'label' : 'Wenn Attribut vorhanden'},
          {'field' : 'hasOptionIds', 'label' : 'Wenn Optionen ausgewählt'}, 
          {'field' : 'showOptionsHavingTag', 'label' : 'Dann zeige Optionen'},
          {'field' : 'applyToProductTypes', 'label' : 'Anwenden auf Produttypen'},
          {'field' : '', 'label' : ''}
        ],
        
        
        // The pager takes care of filtering, sorting and paging functionality.
		saveData : function(view, parent, toolbar) {
			var self = this;

			var createPromise = undefined;
			var updatePromise = undefined;
			
			//-----------------------------------------
			// New Options
			//-----------------------------------------
			var newAttributeInputConditions = ko.toJS(self.newAttributeInputConditions);
			
			if(_.isArray(newAttributeInputConditions) && !_.isEmpty(newAttributeInputConditions)) {
				createPromise = attrAPI.createInputConditions(self.attributeId, newAttributeInputConditions);
			}

			// Make sure that we always have a promise, so that the next step is executed,
			if(_.isUndefined(createPromise)) {
				createPromise = $.when({});
			}
			
			//-----------------------------------------
			// Updates
			//-----------------------------------------
			
			var updates = [];
			var attributeInputConditions = ko.toJS(self.attributeInputConditions);
			
			if(_.isArray(attributeInputConditions) && !_.isEmpty(attributeInputConditions)) {
				createPromise.then(function() {
					
					_.each(attributeInputConditions, function(data) {
						var updateModel = gc.app.newUpdateModel()
							.id(data.id)
							.field('whenAttributeId', data.whenAttributeId)
							.field('hasOptionIds', data.hasOptionIds)
							.field('showAttributeId', data.showAttributeId)
							.field('showOptionsHavingTag', data.showOptionsHavingTag)
							.field('applyToProductTypes', data.applyToProductTypes);
						
						updates.push(updateModel.data());
					});
					
					updatePromise = attrAPI.updateInputConditions(self.attributeId, updates);
				});
			}
			
			// Make sure that we always have a promise, so that the next step is executed,
			if(_.isUndefined(updatePromise)) {
				updatePromise = $.when({});
			}
			
			$.when(createPromise, updatePromise).done(function(data1, data2) {
				self.attributeInputConditions([]);
				self.newAttributeInputConditions([]);
				
				self.loadAttributeInputConditions(self.attributeId);
				
				toolbar.hide();
			});
		},
		addNewInputConditionRow : function(data, element) {
			var self = this;
			
			var newAttributeInputCondition = {
				whenAttributeId: ko.observable(),
				hasOptionIds: ko.observableArray([]),
				/* If the above values are set, we want to show THIS attribute. */
				showAttributeId: self.attributeId,
				showOptionsHavingTag: ko.observable(),
				optionsForAttribute: ko.observableArray([]),
				/* Apply rule only to specific product types. */
				applyToProductTypes: ko.observableArray([])
			}
			
			// Subscribe to whenAttributeId so that we can load its options, when it has been selected.
			newAttributeInputCondition.whenAttributeId.subscribe(function(attributeId) {
				self.appendAttributeOptions(newAttributeInputCondition);
			});
			
			self.newAttributeInputConditions.push(newAttributeInputCondition);
		},
		removeNewInputConditionRow : function(data, element) {
			var self = this;
			self.newAttributeInputConditions.remove(data);
		},
		removeInputCondition : function(data, element) {
			var self = this;
			var _data = ko.toJS(data);
			
			attrAPI.removeInputCondition(self.attributeId, _data.id).then(function() {
				self.attributeInputConditions.remove(data);
			});
		},
		appendAttributeOptions : function(inputCondition) {
			var self = this;
			
			var whenAttributeId = ko.unwrap(inputCondition.whenAttributeId);
			
			console.log('whenAttributeId ----------------- ', whenAttributeId);
			
			
			if(!_.isUndefined(inputCondition) && !_.isUndefined(whenAttributeId)) {
				
				return attrAPI.getAttributeOptions(whenAttributeId).then(function(data) {
					var attributeOptions = data.data['attribute-options'];
	        		gc.ctxobj.decorate(attributeOptions, 'any');

	        		// Turn the data into something that x-editable understands. Did not work otherwise.
	        		var editableOptionsData = [];
	        		_.each(attributeOptions, function(data) {
	        			if(data.id && data.label) {
		        			editableOptionsData.push( { id: data.id, text: data.label.i18n } );
	        			}
	        		});

	        		inputCondition.optionsForAttribute(editableOptionsData);
				});
			}
		},
		loadAttributeInputConditions : function(attributeId) {
			var self = this;

			return attrAPI.getInputConditions(attributeId).then(function(data) {
        		var attributeInputConditions = ko.unwrap(ko.mapping.fromJS(data.data.attributeInputConditions));

        		var promises = [];
        		
        		_.each(attributeInputConditions, function(inputCondition) {
        			
        			if(_.isUndefined(inputCondition.showOptionsHavingTag)) {
        				inputCondition.showOptionsHavingTag = ko.observable();
        			}

        			if(_.isUndefined(inputCondition.hasOptionIds)) {
        				inputCondition.hasOptionIds = ko.observableArray();
        			}

        			if(_.isUndefined(inputCondition.applyToProductTypes)) {
        				inputCondition.applyToProductTypes = ko.observableArray();
        			}
        			
        			inputCondition.optionsForAttribute = ko.observableArray([]);
        			
        			promises.push(self.appendAttributeOptions(inputCondition));
        			
    				// Subscribe to whenAttributeId so that we can load its options, when it is changed.
    				inputCondition.whenAttributeId.subscribe(function(whenAttributeId) {
    					// Reset if attribute has changed.
    					inputCondition.hasOptionIds([]);
    					inputCondition.optionsForAttribute([]);
    					
    					self.appendAttributeOptions(inputCondition);
    				});
        		});
        		
           		self.attributeInputConditions(attributeInputConditions);
           		
           		return $.whenall( promises );
        	});
		},
		activate : function(attributeId) {
			var self = this;
			
			self.attributeVM = gc.app.sessionGet('attributeVM');
			self.attributeId = attributeId;
			
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
       		return attrAPI.getOptionAttributes({ enabled: true, targetObjectId: 2, fields: [ 'backendLabel', 'code', 'code2', 'allowMultipleValues', 'enabled' ] }).then(function(data) {
        		gc.ctxobj.enhance(data.data.attributes, [ 'backendLabel' ], 'any');

        		// Turn the data into something that x-editable understands. Did not work otherwise.
        		var editableOptionsData = [];
        		_.each(data.data.attributes, function(data) {
        			editableOptionsData.push( { id: data.id, text: data.backendLabel.i18n } );
        		});

       			self.optionAttributes(editableOptionsData);
       		}).then(function() {
           		return attrAPI.getOptionGroupingTags(self.attributeId).then(function(data) {
            		var editableOptionTagsData = [];
            		_.each(data, function(tag) {
            			editableOptionTagsData.push( { id: tag, text: tag } );
            		});
            		
           			self.availableOptionGroupingTags(editableOptionTagsData);
           		});
       		}).then(function() {
       			return self.loadAttributeInputConditions(attributeId);
       		});
		},
		attached : function() {
			var self = this;
			
			// For the first tab, we register the save event immediately and not
			// just on click.
			gc.app.onToolbarEvent({
				save : self.saveData
			});

			// Subscribe again if someone leaves the tab and comes back again.
			$(document).on('click', '#tab-attribute-details-inputconditions', function() {
				// Callback for save/cancel toolbar.
				gc.app.onToolbarEvent({
					save : self.saveData
				});
			});
		}
	};

	return AttributeInputConditionsController;
});