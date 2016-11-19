define([ 'durandal/app', 'durandal/composition', 'knockout', 'i18next', 'gc/gc' ], function(app, composition, ko, i18n, gc) {
	var ctor = function() {
	};

	ctor.prototype.activate = function(options) {
		var self = this;
		
		this.options = options || {};
		
		// Source path of loading image. 
		this.src = options.src;

		// Supply additional classes to the loader element 
		this.css = options.css || '';

		// What event should take place for the loader to appear.
		this.event = options.event;

		// Should any elements disappear when the loading image appears?
		this.toggle = options.toggle;

		// Add hide() callback function to VM?
		this.hookCallbacksTo = options.hookCallbacksTo;

		this.run = options.run;
	};

	ctor.prototype.resetLoader = function(params) {
		var $loaderEl = params.loaderEl;
		var $toggleEl = params.toggleEl;
		
		$loaderEl.fadeOut(300, function() {
			if(!_.isUndefined($toggleEl)) {
				$toggleEl.fadeIn(300);
			}
		});
	};
	
	 // Once the html-elements are attached, we can start listening for changes
	 // to the form or for clicks on one of the buttons.
	ctor.prototype.compositionComplete = function(view, parent) {
		var self = this;

		var $loaderEl = $(view).find('div.gc-loader');
		var $toggleEl = undefined;
		
		if(!_.isUndefined(self.toggle)) {
			$toggleEl = $(self.toggle);
		}

		if(!_.isUndefined(self.event)) {
			var pos = self.event.indexOf(':');
			var evt = self.event.substring(0, pos);
			var evtSelector = self.event.substring(pos+1);

			if(!_.isUndefined(evt) && !_.isUndefined(evtSelector)) {
				
				// If a hookCallbacksTo object has been specified, we hook some function on to it,
				// so that it is possible reset the loader when desired. An alternative to this would be
				// to tell this widget what function to call when the event is triggered. We can then
				// pass an extra object, which has appropriate functionalities for doing this.
				if(!_.isUndefined(this.hookCallbacksTo)) {
					var obj = ko.unwrap(self.hookCallbacksTo);
					obj.$gc = obj.$gc || {};
					
					obj.$gc.resetLoader = function() {
						self.resetLoader({loaderEl: $loaderEl, toggleEl: $toggleEl});

						// Just in case the reset comes to quickly, we repeat the process after 1 second.
						_.delay(self.resetLoader, 1000, {loaderEl: $loaderEl, toggleEl: $toggleEl});						
					};

					if(ko.isObservable(self.hookCallbacksTo)) {
						self.hookCallbacksTo(obj);
					}
				}
				
				// Show loader when event has taken place on the target object.
				$( document ).on(evt.trim(), evtSelector.trim(), function(e) {
					
					if(!_.isUndefined($toggleEl)) {
						$toggleEl.fadeOut(300, function() {
							$loaderEl.fadeIn(300);
						});
					} else {
						$loaderEl.fadeIn(300);
					}
					
					// If a function to run was specified, execute it now.
					if(!_.isUndefined(self.run) && _.isFunction(self.run)) {
						self.run(view, parent, {
							reset : function() {
								self.resetLoader({loaderEl: $loaderEl, toggleEl: $toggleEl});
							
								// Just in case the reset comes to quickly, we repeat the process after 1 second.
								_.delay(self.resetLoader, 1000, {loaderEl: $loaderEl, toggleEl: $toggleEl});						
							}
						});
					}
				});
			}
		}
	};

	return ctor;
});