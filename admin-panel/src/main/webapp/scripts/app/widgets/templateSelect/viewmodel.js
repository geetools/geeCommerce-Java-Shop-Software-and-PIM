define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-template' ], function(app, composition, ko, i18n, gc, templateAPI) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		self.selectOptions = options.selectOptions;
		
		self.value = options.value;
		
        self.valueKey = options.valueKey || 'id';
        
        self.labelKey = options.labelKey || 'label';
		
		self.forType = options.forType;
		
        self.mode = options.ctxMode || 'any';

        self.apiOptions = options.apiOptions;
		
        self.options = [];
		
        return templateAPI.getTemplates().then(function (data) {
            var templates = data.data.templates;

            self.options.push({
                id : "",
                text : " "
            });

            _.forEach(templates, function(template) {
                self.options.push({
                    id: template[self.valueKey],
                    text: gc.ctxobj.val(template[self.labelKey], gc.app.currentUserLang(), self.mode) || ""
                });
            });
        })

	};
	
    ctor.prototype.attached = function(view) {
        var self = this;
    };

	return ctor;
});