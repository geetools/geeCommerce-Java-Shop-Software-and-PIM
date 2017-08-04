define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc' ], function(app, composition, ko, i18n, gc) {
	var ctor = function() {
	};

	

//	{title: 'CMS', items: [{label: 'Mobile', icon: 'fa fa-mobile-phone', click: setMobileMode},
//	{label: 'Tablet', icon: 'fa fa-tablet', click: setTabletMode},
//	{label: 'Desktop', icon: 'fa fa-desktop', click: setDesktopMode},
//	{label: 'Preview', icon: 'fa fa-external-link ', href: contentVM.previewURL}
//	]}
	
	ctor.prototype.activate = function(options) {
		var self = this;
		this.toolbar = ko.observable(false);
		    
		gc.app.navToolbar.subscribe(function(newToolbar) {
		    self.toolbar(newToolbar);
		});
	};

	return ctor;
});