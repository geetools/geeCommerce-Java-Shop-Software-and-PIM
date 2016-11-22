define(['durandal/app', 'knockout', 'gc/gc', 'gc-attribute-tabs'], function (app, ko, gc, attrTabsAPI) {
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function AttributeTabGridIndexController(options) {

		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof AttributeTabGridIndexController)) {
			throw new TypeError("AttributeTabGridIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.pager = {};

		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'removeAttributeTab');
	}

	AttributeTabGridIndexController.prototype = {
			
    	app: gc.app,
    	
    	// Fields used for filtering and sorting.
		gridHeaders: [
          {'field' : 'Label', 'label' : 'Name'},
          {'field' : 'Typ', 'label' : 'Typ'},
          {'field' : 'position', 'label' : 'Reihenfolge'},
          {'field' : 'enabled', 'label' : 'Akiviert'},
          {'field' : '', 'label' : ''}
        ],
	    activate: function(data) {
	    	gc.app.pageTitle('Attributtabs Verwalten');
	    	gc.app.pageDescription('Attributtabs ansehen und bearbeiten');
	    	
	    		    	
	    	// Init the pager.
        	this.pager = new gc.Pager(attrTabsAPI.getAttributeTabsPagingOptions());
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return this.pager.load();
	    },
	    removeAttributeTab: function (attributeTab) {
			var self = this;
			var yes = gc.app.i18n('app:common.yes');
			var no = gc.app.i18n('app:common.no');

			app.showMessage(gc.app.i18n('app:modules.attribute-tab.confirmDelete'), gc.ctxobj.val(attributeTab.label, gc.app.currentLang()), [yes, no]).then(function (confirm) {
				if (confirm == yes) {
					attrTabsAPI.removeAttributeTab(attributeTab.id).then(function () {
						self.pager.removeData(attributeTab);
					});
				}
			});
		},
	}

	return AttributeTabGridIndexController;
});