define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-attribute' ], function(app, composition, ko, i18n, gc, attrAPI) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		self.selectOptions = options.selectOptions;
		
		self.value = options.value;
		
        self.valueKey = options.valueKey || 'id';
        
        self.labelKey = options.labelKey || 'backendLabel';
		
		self.forType = options.forType;
		
        self.mode = options.mode || 'closest';

        self.apiOptions = options.apiOptions;

        console.log("OPTIONS")
        console.log(self.apiOptions)
		
        self.options = [];
		
        return attrAPI.getAttributes(self.forType, self.apiOptions).then(function(data) {
            var attributes = data.data.attributes;
            self.options.push({
                id : '',
                text : " "
            });
            _.forEach(attributes, function(attr) {
                self.options.push({
                    id : attr[self.valueKey],
                    text : gc.ctxobj.val(attr[self.labelKey], gc.app.currentUserLang(), self.mode) || ""
                });
            });
        });
	};
	
    ctor.prototype.attached = function(view) {
        var self = this;
    };

	return ctor;
});