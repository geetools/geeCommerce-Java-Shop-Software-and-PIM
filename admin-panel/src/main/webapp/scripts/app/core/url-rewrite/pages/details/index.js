define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-url-rewrite' ], function(app, ko, gc, urlRewriteAPI) {

	function URLRewriteVM(id) {
		var self = this;
        self.id = ko.observable(id);
		self.requestURI = ko.observableArray();
		self.requestMethod = ko.observable();
		self.targetURL = ko.observable();
		self.targetObjectId = ko.observable();
		self.targetObjectType = ko.observable();
		self.flags = ko.observableArray();
		self.manual = ko.observable();
		self.enabled = ko.observable();
		
		self.isNew = ko.computed(function() {
			return self.id() == 'new';
		});
		
		ko.computed(function() {
			var isManual = self.manual();
			var targetObjectType = self.targetObjectType();
			var targetObjectId = self.targetObjectId();
			
			if(!isManual && !_.isEmpty(targetObjectType) && !_.isEmpty(targetObjectId)) {
				if(targetObjectType == 'PRODUCT_LIST') {
					self.targetURL('/catalog/product-list/view/' + targetObjectId);
				} else if(targetObjectType == 'PRODUCT') {
					self.targetURL('/catalog/product/view/' + targetObjectId);
				} else if(targetObjectType == 'RETAIL_STORE') {
					self.targetURL('/store/view/' + targetObjectId);
				}
			}
		});
	}	

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function URLRewriteIndexController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof URLRewriteIndexController)) {
			throw new TypeError("URLRewriteIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.urlRewriteVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

	URLRewriteIndexController.prototype = {
		constructor : URLRewriteIndexController,
		pageTitle : function() {
			var self = this;
			var title = 'URL-Rewrite';
			var vm = ko.unwrap(self.urlRewriteVM);
			
			if(!_.isUndefined(vm)) {
				var requestURI = ko.unwrap(vm.requestURI);

				if(!_.isEmpty(requestURI)) {
					title += ': ' + gc.ctxobj.val(requestURI, self.app.currentLang(), 'any');
				}
			}
			
			return title;
		},
		pageDescription : 'Rewrite-URLs ansehen und bearbeiten',
		saveData : function() {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();
		},
		activate : function(urlRewriteId) {
			var self = this;

			self.urlRewriteVM = new URLRewriteVM(urlRewriteId);
			gc.app.sessionPut('urlRewriteVM', self.urlRewriteVM);
			
			if(urlRewriteId == 'new') {
				gc.app.pageTitle('New URL-Rewrite');
				gc.app.pageDescription('Create a new URL-Rewrite');
			} else {
				gc.app.pageTitle(self.pageTitle());
				gc.app.pageDescription(self.pageDescription);
				
				return urlRewriteAPI.getURLRewrite(urlRewriteId).then(function(urlRewrite) {
				
					self.urlRewriteVM.id(urlRewrite.id);
					self.urlRewriteVM.requestURI(urlRewrite.requestURI);
					self.urlRewriteVM.requestMethod(urlRewrite.requestMethod);
					self.urlRewriteVM.targetURL(urlRewrite.targetURL);
					self.urlRewriteVM.targetObjectId(urlRewrite.targetObjectId);
					self.urlRewriteVM.targetObjectType(urlRewrite.targetObjectType);
					self.urlRewriteVM.flags(urlRewrite.flags);
					self.urlRewriteVM.manual(urlRewrite.manual);
					self.urlRewriteVM.enabled(urlRewrite.enabled);
	
					gc.app.pageTitle(self.pageTitle());
				});
			}
		},
		attached : function() {
			var self = this;
		}
	};

	return URLRewriteIndexController;
});
