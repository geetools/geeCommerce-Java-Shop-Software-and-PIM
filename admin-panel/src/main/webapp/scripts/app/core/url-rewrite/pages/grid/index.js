define(['durandal/app', 'knockout', 'gc/gc', 'gc-url-rewrite'], function (app, ko, gc, urlRewriteAPI) {
	
    return {
    	app: gc.app,
        // The pager takes care of filtering, sorting and paging functionality.
    	pager: {},
	    activate: function(data) {
	    	var self = this;
	    
	    	gc.app.pageTitle('URL-Rewrites Verwalten');
	    	gc.app.pageDescription('URL-Rewrites ansehen und bearbeiten');
	    		    	
	        // Pager columns
	        var pagerColumns = [
	            {'name' : 'requestURI', 'label' : 'app:modules.urlrewrite.gridColRequestURI', 'type' : 'ContextObject', cookieKey : 'ru'},
	            {'name' : 'requestMethod', 'label' : 'app:modules.urlrewrite.gridColRequestMethod', cookieKey : 'rm', 'selectOptions' :
	                [
	                    { label: gc.app.i18n('app:common.choose'), value: '' },
	                    { label: 'GET', value: 'GET' },
	                    { label: 'POST', value: 'POST' }
	                ]},
	            {'name' : 'targetObjectType', 'label' : 'app:modules.urlrewrite.gridColTargetObjectType', cookieKey : 'tot', 'selectOptions' :
	                [
	                    { label: gc.app.i18n('app:common.choose'), value: '' },
	                    { label: gc.app.i18n('app:modules.urlrewrite.typePRODUCT_LIST'), value: 1 },
	                    { label: gc.app.i18n('app:modules.urlrewrite.typePRODUCT'), value: 2 },
	                    { label: gc.app.i18n('app:modules.urlrewrite.typeRETAIL_STORE'), value: 3 },
	                ]},
				{'name' : 'targetObjectId', 'label' : gc.app.i18n('app:modules.urlrewrite.gridColTargetObjectId'), cookieKey : 'toi'},
	            {'name' : 'enabled', 'label' : 'app:modules.urlrewrite.gridColEnabled', cookieKey : 'e', 'selectOptions' :
	                [
	                    { label: gc.app.i18n('app:common.choose'), value: '' },
	                    { label: gc.app.i18n('app:common.true'), value: 'true' },
	                    { label: gc.app.i18n('app:common.false'), value: 'false' }
	                ]},
//	            {'name' : '', 'label' : 'app:common.action'}
	        ];
	    		    	
	    	// Init the pager.
        	self.pager = new gc.Pager(urlRewriteAPI.getPagingOptions({ columns : pagerColumns }));
        	// We return the promise so that durandaljs knows to wait for the asynchronous REST-call.
        	return self.pager.load();
	    },
	    compositionComplete : function() {
	    	var self = this;
	    	self.pager.activateSubscribers();
	    }
    }
});