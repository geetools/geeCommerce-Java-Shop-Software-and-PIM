define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-url-rewrite' ], function(app, ko, gc, urlRewriteAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function URLRewriteTabGeneralController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof URLRewriteTabGeneralController)) {
			throw new TypeError("URLRewriteTabGeneralController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.urlRewriteVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate');
	}

	URLRewriteTabGeneralController.prototype = {
		constructor : URLRewriteTabGeneralController,
		saveData : function(view, parent, toolbar) {
			var self = this;

			//----------------------------------------------------
			// Save new url-rewrite.
			//----------------------------------------------------
			if(self.urlRewriteVM.id() == 'new') {
				
				var newUrlRewrite  = {};
				newUrlRewrite.requestURI = self.urlRewriteVM.requestURI();
				newUrlRewrite.requestMethod = self.urlRewriteVM.requestMethod();
				newUrlRewrite.targetURL = self.urlRewriteVM.targetURL();
				
				if(!self.urlRewriteVM.manual()) {
					newUrlRewrite.targetObjectId = self.urlRewriteVM.targetObjectId();
					newUrlRewrite.targetObjectType = self.urlRewriteVM.targetObjectType();
				}
				
				newUrlRewrite.flags = self.urlRewriteVM.flags();
				newUrlRewrite.manual = self.urlRewriteVM.manual();
				newUrlRewrite.enabled = self.urlRewriteVM.enabled();

				urlRewriteAPI.createURLRewrite(newUrlRewrite).then(function(savedUrlRewrite) {
					toolbar.hide();
					
					self.urlRewriteVM.id(savedUrlRewrite.id);
					self.urlRewriteVM.requestURI(savedUrlRewrite.requestURI);
					self.urlRewriteVM.requestMethod(savedUrlRewrite.requestMethod);
					self.urlRewriteVM.targetURL(savedUrlRewrite.targetURL);
					self.urlRewriteVM.targetObjectId(savedUrlRewrite.targetObjectId);
					self.urlRewriteVM.targetObjectType(savedUrlRewrite.targetObjectType);
					self.urlRewriteVM.manual(savedUrlRewrite.manual);
					self.urlRewriteVM.enabled(savedUrlRewrite.enabled);
				});

			} else {
				var updateModel = gc.app.newUpdateModel();
	
				updateModel.field('requestURI', self.urlRewriteVM.requestURI(), true);
				updateModel.field('requestMethod', self.urlRewriteVM.requestMethod());
				updateModel.field('targetURL', self.urlRewriteVM.targetURL());
				updateModel.field('targetObjectId', self.urlRewriteVM.targetObjectId());
				updateModel.field('targetObjectType', self.urlRewriteVM.targetObjectType());
				updateModel.field('flags', self.urlRewriteVM.flags());
				updateModel.field('manual', self.urlRewriteVM.manual());
				updateModel.field('enabled', self.urlRewriteVM.enabled());
				
				urlRewriteAPI.updateURLRewrite(self.urlRewriteVM.id(), updateModel).then(function(data) {
					toolbar.hide();
				});
			}
		},
		activate : function() {
			var self = this;
			
			self.urlRewriteVM = gc.app.sessionGet('urlRewriteVM');
		}
	};

	return URLRewriteTabGeneralController;
});