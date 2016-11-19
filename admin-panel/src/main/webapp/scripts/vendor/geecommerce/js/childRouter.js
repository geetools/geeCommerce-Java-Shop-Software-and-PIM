define([ 'durandal/app', 'plugins/router', 'knockout', 'gc/gc' ], function(
		app, router, ko, gc) {
	/*
	 * Wrapper around Durandal router plugin to enable us set default routing properties shared across modules
	 */
	router._createChildRouter = router.createChildRouter;
	router.createChildRouter = function() {
		var childRouter = router._createChildRouter();
		//childRouter._map = childRouter.map;
		childRouter.mapModule = function(moduleRoutes) {
		//childRouter.map = function(values) {
			var routes = [{
				route : [ '', 'grid' ],
				moduleId : 'grid/index',
			}];
			if (!_.isEmpty(moduleRoutes)) {
				routes = routes.concat(moduleRoutes);
			}
			//childRouter._map(values);
			childRouter.map(routes);
			return childRouter;
		};
		childRouter.setMetadata = function(data) {
			//PREVIOUS IMPLEMENTATION
//			//Sets the page title and subtitle
//			//gc.app.pageTitle(data.pageTitle);
//			//gc.app.pageDescription(data.pageDescription);
			return childRouter;
		};
		
//PREVIOUS IMPLEMENTATION
//		childRouter.defaultActivate = function() {
//			console.log("defaultActivate start");
//			console.log("defaultActivate end");
//			return childRouter;
//		};
//		childRouter.defaultAttached = function() {
//			console.log("defaultAttached 1 start");
//			// Get the URL
//			var url = document.location.toString();
//			var location = url.split('#')[1]
//			if (!_.isEmpty(location)) {
//				location = location.split('/')[1];
//			}
//			if (_.isEmpty(location)) {
//
//				if (!_.isEmpty(app.defaultLocation)) {
//					location = app.defaultLocation;
//				} else {
//					console.err("Error: Current URL location unknown.");
//					return;
//				}
//
//			}
//			
//			// Get active pane
//			var $iconRef = $('.tab-pane a[href="#/' + location + '"]');
//			
//			childRouter.$icon = $iconRef.parent();
//			childRouter.$iconParent = childRouter.$icon.parent();
//			childRouter.$iconPane = childRouter.$iconParent.parent();
//			
//			// Get parent tab ID
//			var parentID = childRouter.$iconPane.attr('id');
//			
//			// Get active tab
//			var $tabRef = $('[data-target="#' + parentID + '"]');
//			
//			childRouter.$tab = $tabRef.parent();
//			childRouter.$tabParent = childRouter.$tab.parent();
//			
//			console.log("defaultAttached 1 end");
//			console.log("defaultAttached 2 start");
//			
//			// Reopen last used tab from URL
//
//			// Activate icon
//			childRouter.$icon.addClass('active');
//
//			// Activate pane
//			childRouter.$iconPane.addClass('active');
//			// $active.parent().tab('show') ;
//
//			// Activate tab
//			childRouter.$tab.addClass('active');
//			// $parent.parent().show();
//			
//			console.log("defaultAttached 2 end");
//			
//			return childRouter;
//		};
//		
//		childRouter.defaultDetached = function() {
//			console.log("defaultDetached start");
//			// Reopen last used tab from URL
//
//			// Deactivate icon
//			childRouter.$iconParent.children().removeClass('active');
//
//			// Deactivate pane
//			childRouter.$iconPane.removeClass('active');
//			// $active.parent().tab('show') ;
//
//			// Deactivate tab
//			childRouter.$tabParent.children().removeClass('active');
//			// $parent.parent().show();
//			
//			console.log("defaultDetached end");
//			
//			return childRouter;
//		};
//		
//		childRouter.defaultCompositionComplete = function() {
//			console.log("defaultCompositionComplete start");
//			console.log("defaultCompositionComplete end");
//			return childRouter;
//		}

//BREAKS THE ROUTER
//		childRouter.compositionComplete = function() {
//			console.log("compositionComplete start");
//			//childRouter.defaultCompositionComplete();
//			console.log("compositionComplete end");
//			//return childRouter;
//		};
		
		childRouter.activate = function() {
			console.log("activate start");
			//childRouter.defaultActivate();
			console.log("activate end");
			//return childRouter;
		};
		
		childRouter.detached = function() {
			console.log("detached start");
			//childRouter.defaultDetached();
			console.log("detached end");
			//return childRouter;
		};
		
		childRouter.attached = function() {
			console.log("attached start");
			//childRouter.defaultAttached();
			console.log("attached end");
			//return childRouter;
		};
		
//		childRouter.canActivate = function() {
//			console.log("can activate");
//			return true;
//		};
//		
//		childRouter.canActivate = function() {
//			console.log("can activate");
//			return true;
//		};
		
		return childRouter;
	}

	return router;
	
});