define(['plugins/router', 'durandal/app', 'knockout', 'gc/gc', 'gc-account'], function (router, app, ko, gc, accountAPI) {
	
	function LoginVM(){
        var self = this;

        self.username = ko.observable();
        self.password = ko.observable();
        
    }
	
	//-----------------------------------------------------------------
	// Controller
	//-----------------------------------------------------------------
	function LoginController(options) {
		
		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof LoginController)) {
			throw new TypeError("LoginController constructor cannot be called as a function.");
		}

        this.app = gc.app;
        this.loginVM = {};
		
		_.bindAll(this, 'activate');		
	}
	
	LoginController.prototype = {
		constructor : LoginController,
        login : function() {
            var self = this;
            
			console.log('----------------->>>><<<>>>> 222 activeRoute', self.app.sessionGet('activeInstruction'));            
            
            console.log('::: LOGGGING IN ::: ', self.loginVM, self.loginVM.username());
            
            accountAPI.createSession(self.loginVM.username(), self.loginVM.password()).then(function(data) {
            
            	console.log('LOGGED IN WITH ROLES ::: ', data.data);

				self.app.startSession(data.data);
				
				console.log('TIMEOUT _________ /// ', data, data.data.timeoutAtMillis);
			
			    self.app.sessionPut('sessionTimeoutAtMillis', data.data.timeoutAtMillis);
			    
			    self.app.sessionPut('loggedUserName', data.data.name);
			    
			    //console.log("USER DATA PERMISSIONS",data.data);
			    
			    self.app.sessionPut('loggedUserPermissions', data.data.roles);
				
			    console.log("USER DATA PERMISSIONS",data.data.roles);
			    
				self.app.startSessionTimer();
				
				if(!_.isUndefined(self.app.sessionGet('activeInstruction'))) {
				console.log('????????????????111 ???>> router.redirect()');
				
					router.navigate('//' + self.app.sessionGet('activeInstruction').fragment, {replace: true, trigger: true});
				} else {
				console.log('????????????????111 ???>> app.setRoot()');
					app.setRoot('shell', 'entrance');
				}
            });
		},
	    activate: function(data) {
            var self = this;
			console.log('----------------->>>><<<>>>> 111 activeRoute', router.activeInstruction());            

	    	
	    	gc.app.pageTitle('app:account.loginTitle');
	    	gc.app.pageDescription('app:account.loginSubTitle');
	    	
            self.loginVM = new LoginVM();
	    }
    }
	
	return LoginController;
});