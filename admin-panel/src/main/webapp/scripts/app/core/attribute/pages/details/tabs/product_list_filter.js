define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-attribute' ], function(app, ko, gc, attrAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function AttributeBaseController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof AttributeBaseController)) {
			throw new TypeError("AttributeBaseController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.attributeVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate');
	}

	AttributeBaseController.prototype = {
		constructor : AttributeBaseController,
		saveData : function(view, parent, toolbar) {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();
			updateModel.field('includeInProductListFilter', self.attributeVM.includeInProductListFilter());
			updateModel.field('productListFilterType', self.attributeVM.productListFilterType());
			updateModel.field('productListFilterIndexFields', self.attributeVM.productListFilterIndexFields());
			updateModel.field('productListFilterKeyAlias', self.attributeVM.productListFilterKeyAlias(), true);
			updateModel.field('productListFilterFormatLabel', self.attributeVM.productListFilterFormatLabel(), true);
			updateModel.field('productListFilterFormatValue', self.attributeVM.productListFilterFormatValue(), true);
			updateModel.field('productListFilterParseValue', self.attributeVM.productListFilterParseValue(), true);
			updateModel.field('productListFilterMulti', self.attributeVM.productListFilterMulti());
			updateModel.field('productListFilterInheritFromParent', self.attributeVM.productListFilterInheritFromParent());
			updateModel.field('productListFilterIncludeChildren', self.attributeVM.productListFilterIncludeChildren());
			updateModel.field('productListFilterPosition', self.attributeVM.productListFilterPosition());
			
			attrAPI.updateAttribute(self.attributeVM.id(), updateModel).then(function(data) {
				toolbar.hide();
			});
		},
		activate : function(attributeId) {
			var self = this;
			
			self.attributeVM = gc.app.sessionGet('attributeVM');
		},
	};

	return AttributeBaseController;
});