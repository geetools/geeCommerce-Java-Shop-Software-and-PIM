define(
		[ 'durandal/app', 'postal', 'knockout', 'gc/gc', 'gc-navigation',
				'gc-navigation/util' ],
		function(app, postal, ko, gc, navAPI, navUtil) {

			// -----------------------------------------------------------------
			// Controller
			// -----------------------------------------------------------------
			function NavigationDetailsIndexController(options) {
				console.log("Navigation details index start");
				// Make sure that this object is being called with the 'new'
				// keyword.
				if (!(this instanceof NavigationDetailsIndexController)) {
					throw new TypeError(
							"TABS: NavigationDetailsIndexController constructor cannot be called as a function.");
				}
				
				this.app = gc.app;
				this.nodeVM = ko.observable();
				this.showNodeVM = ko.observable(false);
				this.productLists = ko.observableArray();
				this.cmsPages = ko.observableArray();
				this.nodeTypes = ko.observableArray();

				this.iconSystemPath = ko.observable();
				console.log("Navigation details index end");
				
				// Solves the 'this' problem when a DOM event-handler is fired.
				_.bindAll(this, 'activate');
			}
			
			

			NavigationDetailsIndexController.prototype = {
				constructor : NavigationDetailsIndexController,
				activate : function(nodeVM) {
					console.log("TABS: Navigation details index activate start");
					var self = this;
					if (nodeVM) {
						self.showNodeVM(true);
					} else {
						self.showNodeVM(false);
					}

					console.log(nodeVM)

					if(nodeVM) {
						var rootEl = nodeVM;
						while(rootEl.parent()){
							rootEl = rootEl.parent();
						}

						this.iconSystemPath("system/navigation/" + rootEl.key() + "/icons")
					}
					console.log(this.iconSystemPath())

					self.nodeVM = nodeVM;
					self.productLists(self.app.sessionGet('product_list'));
					self.cmsPages(self.app.sessionGet('cms_pages'));



					var nodeTypes = [];

					nodeTypes.push( { id : '', text : function() {
						return "";
					}});
					nodeTypes.push( { id : 'PRODUCT_LIST', text : function() {
						return gc.app.i18n('app:modules.navigation.nodeTypeProductList', {}, gc.app.currentLang);
					}});
					nodeTypes.push( { id : 'CMS', text : function() {
						return gc.app.i18n('app:modules.navigation.nodeTypeCmsPage', {}, gc.app.currentLang);
					}});
					nodeTypes.push( { id : 'LINK', text : function() {
						return gc.app.i18n('app:modules.navigation.nodeTypeLink', {}, gc.app.currentLang);
					}});


					self.nodeTypes(nodeTypes);

					if (!_.isEmpty(nodeVM)) {
					}
					console.log("Navigation details index activate end");
					
					//if (attributeTabId == 'new') {
					//	gc.app.pageTitle('New navigation');
					//	gc.app.pageDescription('Create a new attribute tab');
					//}
				}

			}

			return NavigationDetailsIndexController;
		});