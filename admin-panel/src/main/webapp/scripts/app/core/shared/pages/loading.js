define(['durandal/app', 'knockout', 'gc/gc'], function (app, ko, gc) {

	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function LoadingController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof LoadingController)) {
			throw new TypeError("LoadingController constructor cannot be called as a function.");
		}

        this.app = gc.app;
        this.loadingVM = {};
		
		_.bindAll(this, 'activate');		
	}
	
	LoadingController.prototype = {
		constructor : LoadingController,
	    activate: function(data) {
            var self = this;
	    	
	    	gc.app.pageTitle('app:common.loadingTitle');
	    	gc.app.pageDescription('app:common.loadingSubTitle');
	    }
    }
	
	return LoadingController;
});