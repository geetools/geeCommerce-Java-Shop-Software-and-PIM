define([ 'durandal/app', 'plugins/router', 'knockout', 'gc/gc' ], function(app, router, ko, gc) {
	var childRouter = router.createChildRouter().makeRelative({
		moduleId : 'core/request-context/pages',
		fromParent : true
	}).map([ {
		route : [ '', 'grid' ],
		moduleId : 'grid/index',
	}, {
		route : 'details/:id',
		moduleId : 'details/index',
	}, {
		route : 'details/tabs/general',
		moduleId : 'details/tabs/general',
	} ]).buildNavigationModel();
	
	gc.app.pageTitle('Request-contexts verwalten');
	gc.app.pageDescription('URL-Rewrites ansehen und bearbeiten');

	return {
		router : childRouter
	};
});
