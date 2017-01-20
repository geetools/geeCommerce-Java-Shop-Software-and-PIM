define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc', 'gc-attribute' ], function(app, composition, ko, i18n, gc, attrAPI) {
	var ctor = function() {
	    this.gc = gc;
        this.languageOptions = ko.observableArray();
	};

	ctor.prototype.activate = function() {
		var self = this;
		
        var options = [];
        
        _.forEach(gc.app.conf.availableLanguages(), function(lang) {
            options.push({
                id : lang.code,
                text : lang.label
            });
        });
        
        self.languageOptions(options);
	};
	
    ctor.prototype.attached = function(view) {
        var self = this;
    };

	return ctor;
});