define([ 'durandal/app', 'plugins/router', 'knockout', 'gc/gc' ], function(app, router, ko, gc) {
	var childRouter = router.createChildRouter().makeRelative({
		moduleId : 'core/merchant/pages',
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
	
	gc.app.pageTitle('Merchants verwalten');
	gc.app.pageDescription('Merchants ansehen und bearbeiten');

	return {
		router : childRouter
	};
});
