define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-request-context' ], function(app, ko, gc, requestContextAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function RequestContextTabGeneralController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof RequestContextTabGeneralController)) {
			throw new TypeError("RequestContextTabGeneralController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.requestContextVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate');
	}

    RequestContextTabGeneralController.prototype = {
		constructor : RequestContextTabGeneralController,
		saveData : function(view, parent, toolbar) {
			var self = this;



			//----------------------------------------------------
			// Save new url-rewrite.
			//----------------------------------------------------
			if(self.requestContextVM.id() == 'new') {
				
				var newRequestContext  = {};

                newRequestContext.merchantId = self.requestContextVM.merchant();
                newRequestContext.storeId = self.requestContextVM.store();
                newRequestContext.viewId = self.requestContextVM.view();
				newRequestContext.language = self.requestContextVM.language();
				newRequestContext.country = self.requestContextVM.country();
                newRequestContext.urlPrefix = self.requestContextVM.urlPrefix();
                newRequestContext.urlType = self.requestContextVM.urlType();

                requestContextAPI.createRequestContext(newRequestContext).then(function(savedRequestContext) {
					toolbar.hide();
                    router.navigate('//request-contexts/details/' + data.id);

				});

			} else {
				var updateModel = gc.app.newUpdateModel();
	
				updateModel.field('merchantId', self.requestContextVM.merchant());
				updateModel.field('storeId', self.requestContextVM.store());
				updateModel.field('viewId', self.requestContextVM.view());
				updateModel.field('language', self.requestContextVM.language());
				updateModel.field('country', self.requestContextVM.country());
				updateModel.field('urlPrefix', self.requestContextVM.urlPrefix());
				updateModel.field('urlType', self.requestContextVM.urlType());

                requestContextAPI.updateRequestContext(self.requestContextVM.id(), updateModel).then(function(data) {
					toolbar.hide();
				});
			}
		},
		activate : function() {
			var self = this;
			
			self.requestContextVM = gc.app.sessionGet('requestContextVM');
		}
	};

	return RequestContextTabGeneralController;
});