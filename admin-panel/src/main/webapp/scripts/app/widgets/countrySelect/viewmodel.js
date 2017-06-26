define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-settings' ], function(app, composition, ko, i18n, gc, settingsAPI) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		self.selectOptions = options.selectOptions;
		
		self.value = options.value;
		
        self.valueKey = options.valueKey || 'code';

        self.labelKey = options.labelKey || 'name';

        self.mode = options.ctxMode || 'any';

        self.apiOptions = options.apiOptions;
		
        self.options = [];

        return settingsAPI.getCountries().then(function (data) {
            var countries = data.data.countries;

            self.options.push({
                id : "",
                text : " "
            });

            _.forEach(countries, function(country) {
                self.options.push({
                    id: country[self.valueKey],
                    text: gc.ctxobj.val(country[self.labelKey], gc.app.currentUserLang(), self.mode) || ""
                });
            });
        })

	};
	
    ctor.prototype.attached = function(view) {
        var self = this;
    };

	return ctor;
});