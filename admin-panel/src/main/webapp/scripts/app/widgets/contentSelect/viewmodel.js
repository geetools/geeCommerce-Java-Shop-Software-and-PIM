define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-content' ], function(app, composition, ko, i18n, gc, contentAPI) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		self.selectOptions = options.selectOptions;
		
		self.value = options.value;
		
        self.valueKey = options.valueKey || 'id';
        
        self.labelKey = options.labelKey || 'name';
		
		self.forType = options.forType;
		
        self.mode = options.ctxMode || 'any';

        self.apiOptions = options.apiOptions;
		
        self.options = [];
		
        return contentAPI.getContents(self.forType).then(function (data) {
            var contents = data.data.contents;

            self.options.push({
                id : "",
                text : " "
            });

            _.forEach(contents, function(content) {
                self.options.push({
                    id: content[self.valueKey],
                    text: gc.ctxobj.val(content[self.labelKey], gc.app.currentUserLang(), self.mode) || ""
                });
            });
        })

        // return attrAPI.getAttributes(self.forType, self.apiOptions).then(function(data) {
        //     var attributes = data.data.attributes;
        //
        //     _.forEach(attributes, function(attr) {
        //         self.options.push({
        //             id : attr[self.valueKey],
        //             text : gc.ctxobj.val(attr[self.labelKey], gc.app.currentUserLang(), self.mode) || ""
        //         });
        //     });
        // });
	};
	
    ctor.prototype.attached = function(view) {
        var self = this;
    };

	return ctor;
});