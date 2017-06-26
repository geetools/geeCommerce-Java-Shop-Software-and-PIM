define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-request-context' ], function(app, ko, gc, requestContextAPI) {

	function RequestContextVM(id) {
		var self = this;
        self.id = ko.observable(id);

        self.merchant = ko.observable();
        self.store = ko.observable();
        self.view = ko.observable();

        self.language = ko.observable();
        self.country = ko.observable();

        self.urlPrefix = ko.observable();
        self.urlType = ko.observable();

		self.isNew = ko.computed(function() {
			return self.id() == 'new';
		});

	}	

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function RequestContextIndexController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof RequestContextIndexController)) {
			throw new TypeError("RequestContextIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.requestContextVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

    RequestContextIndexController.prototype = {
		constructor : RequestContextIndexController,
		pageTitle : function() {
			var self = this;
			var title = 'Request-context';
			return title;
		},
		pageDescription : 'Request-contexts ansehen und bearbeiten',
		saveData : function() {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();
		},
		activate : function(requestContextId) {
			var self = this;

			self.requestContextVM = new RequestContextVM(requestContextId);
			gc.app.sessionPut('requestContextVM', self.requestContextVM);
			
			if(requestContextId == 'new') {
				gc.app.pageTitle('New Request-Context');
				gc.app.pageDescription('Create a new Request-Context');
			} else {
				gc.app.pageTitle(self.pageTitle());
				gc.app.pageDescription(self.pageDescription);
				
				return requestContextAPI.getRequestContext(requestContextId).then(function(requestContext) {
				
					self.requestContextVM.id(requestContext.id);
					self.requestContextVM.merchant(requestContext.merchantId);
					self.requestContextVM.store(requestContext.storeId);
					self.requestContextVM.view(requestContext.viewId);
					self.requestContextVM.language(requestContext.language);
					self.requestContextVM.country(requestContext.country);
					self.requestContextVM.urlPrefix(requestContext.urlPrefix);
					self.requestContextVM.urlType(requestContext.urlType);

					gc.app.pageTitle(self.pageTitle());
				});
			}
		},
		attached : function() {
			var self = this;
		}
	};

	return RequestContextIndexController;
});
