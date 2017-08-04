define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute', 'gc-attribute-tabs' ], function(app, ko, gc, productAPI, attrAPI, attrTabAPI) {
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function DynAttributesController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof DynAttributesController)) {
			throw new TypeError("DynAttributesController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.formAttributeValues = ko.observableArray();
		this.formAllAttributeValues = ko.observableArray([]);

		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate');
	}

	DynAttributesController.prototype = {
		constructor : DynAttributesController,
		activate : function(activateData) {
			var self = this;

			self.formAllAttributeValues = activateData.formAttributeValues;
			self.attributeTab = activateData.attributeTab;
			self.hasEditRights = activateData.hasEditRights;
			self.hasViewRights = activateData.hasViewRights;

			self.formAttributeValues = ko.computed(function() {
				return _.filter(self.formAllAttributeValues(), function (formAttributeValue) {
					return formAttributeValue.attributeTabId == self.attributeTab.id();
				});
			});

			self.formAllAttributeValues.valueHasMutated();
		},
		saveData : function(view, parent, toolbar) {

		},
		attached : function(view, parent) {

		}
	}
	
	return DynAttributesController;
});