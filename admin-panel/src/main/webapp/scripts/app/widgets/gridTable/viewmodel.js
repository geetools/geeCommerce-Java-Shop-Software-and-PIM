define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc' ], function(app, composition, ko, i18n, gc) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;

		self.pager = options.pager;
		
		self.columns = options.columns;
		
		self.searchURI = options.searchURI;
		
		self.menuItems = options.menuItems;

        var options = [];
        
        _.forEach(self.pager.limitOptions(), function(item) {
            options.push({
                id : item.value,
                text : item.label
            });
        });
		
		self.limitOptions = options;
	};
	
    ctor.prototype.attached = function(view) {
        var self = this;
    };

	return ctor;
});