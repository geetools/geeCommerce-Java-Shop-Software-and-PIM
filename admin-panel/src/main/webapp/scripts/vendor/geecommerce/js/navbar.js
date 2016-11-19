define([ 'plugins/router', 'durandal/app', 'knockout', 'i18next', 'gc/gc' ], function(router, app, ko, i18n, gc) {

	/**
	 * Class constructor.
	 */
	function Navbar(options) {

		// Make sure that this object is being called with the 'new' keyword.
		if (!(this instanceof Navbar)) {
			throw new TypeError("Navber constructor cannot be called as a function.");
		}

		options = options || {};

		// Solves the 'this' problem when a DOM event-handler is fired.
		_.bindAll(this, 'update');
	}

	Navbar.prototype = {
		constructor : Navbar,
		update : function() {
			
		}
		
	};
	
	return Navbar;
});