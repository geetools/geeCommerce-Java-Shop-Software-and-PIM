define([ 'durandal/app', 'plugins/router', 'knockout', 'gc/gc' ], function(
		app, router, ko, gc) {
	/*
	 * Wrapper around Durandal router plugin to enable us set default routing properties shared across modules
	 */
	router._createChildRouter = router.createChildRouter;
	router.createChildRouter = function() {
		var childRouter = router._createChildRouter();
		childRouter.mapModule = function(moduleRoutes) {
			var routes = [{
				route : [ '', 'grid' ],
				moduleId : 'grid/index',
			}];
			if (!_.isEmpty(moduleRoutes)) {
				routes = routes.concat(moduleRoutes);
			}
			childRouter.map(routes);
			return childRouter;
		};
		childRouter.setMetadata = function(data) {
			return childRouter;
		};
		
		return childRouter;
	}

	return router;
	
});