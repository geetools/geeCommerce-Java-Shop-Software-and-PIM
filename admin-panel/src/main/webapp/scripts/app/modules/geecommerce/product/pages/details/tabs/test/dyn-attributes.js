define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-product', 'gc-attribute', 'gc-attribute-tabs', 'gc-attribute/util',  'gc-attribute-tabs/util' ], function(app, ko, gc, productAPI, attrAPI, attrTabAPI, attrUtil, attrTabUtil) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function ProductDynAttributesController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof ProductDynAttributesController)) {
			throw new TypeError("ProductDynAttributesController constructor cannot be called as a function.");
		}

        var self = this;
		this.app = gc.app;
		this.gc = gc;
		this.productId = undefined;
		this.product = {};
		this.attributeTab = {};
		this.formAttributeValues = ko.observableArray([]);

		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate');
	}

	ProductDynAttributesController.prototype = {
		constructor : ProductDynAttributesController,
		activate : function(activateData) {
			var self = this;

			self.product = gc.app.sessionKGet('productVM');
			self.productId = activateData.productId;
			self.attributeTab = ko.unwrap(activateData.attributeTab);
			self.hasEditRights = gc.security.isInRole('product-manager') || gc.security.isInRole('admin');
			self.hasViewRights = gc.security.isInRole('product-viewer');

			var modelObject = self.product().unwrap();

			return attrTabUtil.getAttributesForTabsPromise(self.attributeTab.id(), modelObject, modelObject.data.attributes, "products", self.formAttributeValues);

		},
		saveData : function(view, parent, toolbar) {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();

			attrUtil.toUpdateModel(ko.gc.unwrap(self.formAttributeValues), self.product().data().attributes, updateModel);
			
			console.timeEnd('SAVE DYN-ATTRIBUTES');
			
			productAPI.updateProduct(self.productId, updateModel).then(function() {
				toolbar.hide();
				gc.app.channel.publish('product.changed', self.productId);
			})
		},
		attached : function(view, parent) {
			var tabbedPaneContent = $(parent);
			var tabbedPaneContentId = tabbedPaneContent.attr('id');
			var tabId = tabbedPaneContentId.replace('content', 'link');
			
			$('#' + tabId).click(function() {
				if(tabId == 'tab-link-4336136636210190' 
					|| tabId == 'tab-link-4341127984210100'
					|| tabId == 'tab-link-4336136636210155'
					|| tabId == 'tab-link-4336136636210189'
					) {
					$('#header-store-pills').hide();
				} else {
					$('#header-store-pills').show();
				}
			});
		}
	}
	
	return ProductDynAttributesController;
});