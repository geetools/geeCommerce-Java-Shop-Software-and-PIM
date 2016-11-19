define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute', 'gc-attribute-tabs', 'gc-attribute/util',  'gc-attribute-tabs/util' ], function(app, ko, gc, productAPI, attrAPI, attrTabAPI, attrUtil, attrTabUtil) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function DynAttributesController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof DynAttributesController)) {
			throw new TypeError("DynAttributesController constructor cannot be called as a function.");
		}

        var self = this;
		this.app = gc.app;
		this.gc = gc;
		this.vm = {};
		this.attributeTab = {};
		//this.formAttributeValues = ko.observableArray([]);

		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate');
	}

	DynAttributesController.prototype = {
		constructor : DynAttributesController,
		activate : function(activateData) {
			var self = this;
			self.vm = activateData.vm;
			self.attributeTab = ko.unwrap(activateData.attributeTab);
			self.hasEditRights = true
			self.hasViewRights = false;

			return attrTabUtil.getAttributesForTabsPromise(self.attributeTab.id(), self.vm, self.vm.data().attributes, "product-lists", self.vm.formAttributeValues);

		},
		saveData : function(view, parent, toolbar) {
			var self = this;
		},
		attached : function(view, parent) {

		}
	}
	
	return DynAttributesController;
});