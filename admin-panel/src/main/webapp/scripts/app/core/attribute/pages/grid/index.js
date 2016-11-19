define(['durandal/app', 'knockout', 'gc/gc', 'gc-attribute'], function (app, ko, gc, attrAPI) {
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function AttributeGridIndexController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof AttributeGridIndexController)) {
			throw new TypeError("AttributeGridIndexController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		this.pager = {};
		
		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'activate', 'removeAttribute');
	}

	AttributeGridIndexController.prototype = {
		constructor : AttributeGridIndexController,
    	removeAttribute: function(attribute) {
    		var self = this;
    		var yes = gc.app.i18n('app:common.yes');
    		var no = gc.app.i18n('app:common.no');
    		
			app.showMessage(gc.app.i18n('app:modules.attribute.confirmDelete'), gc.ctxobj.val(attribute.backendLabel, gc.app.currentLang()), [yes, no]).then(function(confirm) {
				if(confirm == yes) {
		    		attrAPI.removeAttribute(attribute.id).then(function() {
		    			self.pager.removeData(attribute);
		    		});
				}
			});
    	},
	    activate: function(data) {
	    	var self = this;

	    	gc.app.pageTitle('Attribute Verwalten');
	    	gc.app.pageDescription('Attribute ansehen und bearbeiten');

            // Pager columns
            var pagerColumns = [
                {'name' : 'backendLabel', 'label' : 'app:modules.attribute.gridColName', cookieKey : 'bl', type : 'ContextObject', wildcard : true},
                {'name' : 'code', 'label' : 'app:modules.attribute.gridColCode', cookieKey : 'c'},
                {'name' : 'frontendInput', 'label' : 'app:modules.attribute.gridColInputType', cookieKey : 'fi', 'selectOptions' :
                    [
                        { label: gc.app.i18n('app:common.choose'), value: '' },
                        { label: gc.app.i18n('app:modules.attribute.typeTEXT'), value: 1 },
                        { label: gc.app.i18n('app:modules.attribute.typeRICHTEXT'), value: 2 },
                        { label: gc.app.i18n('app:modules.attribute.typeCOMBOBOX'), value: 7 },
                        { label: gc.app.i18n('app:modules.attribute.typeBOOLEAN'), value: 6 },
                        { label: gc.app.i18n('app:modules.attribute.typeSELECT'), value: 3 }
                    ]},
                {'name' : 'inputType', 'label' : 'app:modules.attribute.gridColValidation', cookieKey : 'it', 'selectOptions' :
                    [
                        { label: gc.app.i18n('app:common.choose'), value: '' },
                        { label: gc.app.i18n('app:modules.attribute.valMANDATORY'), value: 1 },
                        { label: gc.app.i18n('app:modules.attribute.valOPTIONAL'), value: 2 },
                        { label: gc.app.i18n('app:modules.attribute.valOPTOUT'), value: 3 }
                    ]},
                {'name' : 'group', 'label' : 'app:modules.attribute.gridColType', cookieKey : 'g'},
                {'name' : 'enabled', 'label' : 'app:modules.attribute.gridColStatus', cookieKey : 'e'},
                {'name' : '', 'label' : 'app:common.action'}
            ];
	    	
	    	// Init the pager.
        	self.pager = new gc.Pager(attrAPI.getPagingOptions({ columns : pagerColumns }));
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return self.pager.load();
	    },
	    compositionComplete : function() {
	    	var self = this;
	    	self.pager.activateSubscribers();
	    }
    }
    
	return AttributeGridIndexController;
});