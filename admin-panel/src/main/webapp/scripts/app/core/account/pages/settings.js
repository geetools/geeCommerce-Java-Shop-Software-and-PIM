define(['durandal/app', 'knockout', 'gc/gc'], function (app, ko, gc) {
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function AccountSettingsController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof AccountSettingsController)) {
			throw new TypeError("AccountSettingsController constructor cannot be called as a function.");
		}

		this.app = gc.app;
		
		_.bindAll(this, 'activate');		
	}
	
	AccountSettingsController.prototype = {
		constructor : AccountSettingsController,
	    activate: function(data) {
	    	gc.app.pageTitle('app:account.settingsTitle');
	    	gc.app.pageDescription('app:account.settingsSubTitle');
	    }
    }
	
	return AccountSettingsController;
});