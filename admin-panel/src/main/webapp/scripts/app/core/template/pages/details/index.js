define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-template' ], function(app, ko, gc, templateAPI) {

	function TemplateVM(id) {
		var self = this;
        self.id = ko.observable(id);
		self.uri = ko.observable();
		self.label = ko.observableArray([]);
		self.template = ko.observable();

        self.contextModel = ko.observable();

		self.isNew = ko.computed(function() {
			return self.id() == 'new';
		});

		self.initContext = function(data) {
            gc.contexts.attachContextInfo(data, self);
        }

	}	

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function TemplateIndexController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof TemplateIndexController)) {
			throw new TypeError("TemplateIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.templateVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'attached');
	}

    TemplateIndexController.prototype = {
		constructor : TemplateIndexController,
		pageTitle : function() {
			var self = this;
			var title = gc.app.i18n('app:modules.template.title');
			
			return title;
		},
		saveData : function() {
			var self = this;
			
			var updateModel = gc.app.newUpdateModel();
		},
		activate : function(templateId) {
			var self = this;

			self.templateVM = new TemplateVM(templateId);
			gc.app.sessionPut('templateVM', self.templateVM);
			
			if(templateId == 'new') {
				gc.app.pageTitle('New Template');
			} else {
				gc.app.pageTitle(self.pageTitle());
				
				return templateAPI.getTemplate(templateId).then(function(template) {

					self.templateVM.id(template.id);
					self.templateVM.label(template.label);
					self.templateVM.uri(template.uri);
					self.templateVM.template(template.template);
					self.templateVM.initContext(template);
	
					gc.app.pageTitle(self.pageTitle());
				});
			}
		},
		attached : function() {
			var self = this;
		}
	};

	return TemplateIndexController;
});
