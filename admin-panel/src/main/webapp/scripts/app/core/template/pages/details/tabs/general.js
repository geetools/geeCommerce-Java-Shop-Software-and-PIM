define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-template' ], function(app, ko, router, gc, templateAPI) {

	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function TemplateTabGeneralController(options) {

		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof TemplateTabGeneralController)) {
			throw new TypeError("TemplateTabGeneralController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.templateVM = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'saveData', 'activate', 'detached', 'attached');
	}

	TemplateTabGeneralController.prototype = {
		constructor : TemplateTabGeneralController,
		saveData : function(context) { //view, parent, toolbar) {
			var self = this;

			//----------------------------------------------------
			// Save new url-rewrite.
			//----------------------------------------------------
			if(self.templateVM.id() == 'new') {
				
				var newTemplate  = {};
                newTemplate.uri = self.templateVM.uri();
                newTemplate.label = self.templateVM.label();
                newTemplate.template = self.templateVM.template();

                templateAPI.createTemplate(newTemplate).then(function(savedTemplate) {
                    gc.app.updateProgressBar(75);
					// if(toolbar)
					// 	toolbar.hide();

                    context.saved(function() {
                        router.navigate('//templates/details/' + savedTemplate.id);
                    });
				});

			} else {
				var updateModel = gc.app.newUpdateModel();
	
				updateModel.field('label', self.templateVM.label(), true);
				updateModel.field('uri', self.templateVM.uri());
				updateModel.field('template', self.templateVM.template());
                updateModel.context(self.templateVM.contextModel());
                updateModel.saveAsNewCopy(gc.app.saveMakeCopy());

                templateAPI.updateTemplate(self.templateVM.id(), updateModel).then(function(data) {
                    gc.app.updateProgressBar(75);
                    gc.app.resetProgressBar();

                    if(gc.app.saveMakeCopy()) {
                        context.saved(function() {
                            router.navigate('//templates/details/' + data.data.template.id);
                        });
					} else {
                        context.saved(function() {

                        });
					}

				});
			}
		},
		activate : function() {
			var self = this;
			
			self.templateVM = gc.app.sessionGet('templateVM');
		},
        attached : function(view, parent) {
            var self = this;

            $('#templateGeneralForm').addClass('save-button-listen-area');

            gc.app.onSaveEvent(function(context) {
                self.saveData(context);
            }, self.templateVM.contextModel());
        },
		detached : function() {
            var self = this;

            gc.app.clearSaveEvent();

        }
	};

	return TemplateTabGeneralController;
});