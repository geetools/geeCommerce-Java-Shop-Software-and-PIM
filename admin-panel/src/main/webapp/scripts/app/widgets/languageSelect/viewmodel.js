define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-settings' ], function(app, composition, ko, i18n, gc, settingsAPI) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		self.selectOptions = options.selectOptions;
		
		self.value = options.value;
		
        self.valueKey = options.valueKey || 'iso6391Code';
        
        self.labelKey = options.labelKey || 'label';
		
        self.mode = options.ctxMode || 'any';

        self.apiOptions = options.apiOptions;
		
        self.options = [];
        
        return settingsAPI.getLanguages().then(function (data) {
            var languages = data.data.languages;

            self.options.push({
                id : "",
                text : " "
            });

            _.forEach(languages, function(language) {
                self.options.push({
                    id: language[self.valueKey],
                    text: gc.ctxobj.val(language[self.labelKey], gc.app.currentUserLang(), self.mode) || ""
                });
            });
        })

	};
	
    ctor.prototype.attached = function(view) {
        var self = this;
    };

	return ctor;
});