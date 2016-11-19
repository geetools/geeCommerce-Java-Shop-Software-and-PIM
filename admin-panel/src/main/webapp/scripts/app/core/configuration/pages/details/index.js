define(
		[ 'durandal/app', 'postal', 'knockout', 'gc/gc' ],
		function(app, postal, ko, gc) {

			// -----------------------------------------------------------------
			// Controller
			// -----------------------------------------------------------------
			function ConfigurationDetailsIndexController(options) {

				// Make sure that this object is being called with the 'new'
				// keyword.
				if (!(this instanceof ConfigurationDetailsIndexController)) {
					throw new TypeError(
							"ConfigurationDetailsIndexController constructor cannot be called as a function.");
				}

				this.app = gc.app;
				this.activeView = ko.observable();
				this.activeNode = ko.observable();

				// Solves the 'this' problem when a DOM event-handler is fired.
				_.bindAll(this, 'activate', 'activateView', 'attached', 'notifyChange');
			}

			ConfigurationDetailsIndexController.prototype = {
				constructor : ConfigurationDetailsIndexController,
				activateView : function(child) {
					var self = this;

					self
							.activeView({
								model : 'core/configuration/pages/details/index',
								transition : 'entrance',
								activationData : child
							});

				},
				activate : function(data) {

					var self = this;
					console.log(data);
					self.activeNode = data;

					return;
				},
				attached : function(data) {

					var self = this;
					self.activeView('core/configuration/pages/details/index');
					
					
				},
				notifyChange : function() {
					$toolbar = $("#nav-details-content").closest('form').find(
							'.toolbar-trigger').first();
					// Make sure that the save/cancel toolbar sees the change.
					$toolbar.click();
					$toolbar.trigger('change');
				}

			}

			return ConfigurationDetailsIndexController;
		});