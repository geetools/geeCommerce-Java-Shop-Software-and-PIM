define([ 'knockout' ], function(ko) {

	"use strict";

	function Configuration(options) {

		if (!(this instanceof Configuration)) {
			throw new TypeError("Configuration constructor cannot be called as a function.");
		}

		this.options = options || {};

		this.map = {};

		_.bindAll(this, 'put', 'get', 'remove');
	}

	Configuration.prototype = {
		constructor : Configuration,
		put : function(key, value) {
			var oldVal = this.map[key];

			if (!_.isUndefined(oldVal) && !_.isNull(oldVal) && ko.isObservable(oldVal)) {
				oldVal(ko.unwrap(value));
			} else {
				this.map[key] = value;
			}
		},
		get : function(key) {
			return ko.unwrap(this.map[key]);
		},
		koGet : function(key) {
			return this.map[key];
		},
		remove : function(key) {
			delete this.map[key];
		}
	}

	return Configuration;
});