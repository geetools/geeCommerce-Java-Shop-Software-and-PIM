define([ 'durandal/app', 'plugins/router', 'knockout', 'gc/gc' ], function(app, router, ko, gc) {
	var childRouter = router.createChildRouter().makeRelative({
		moduleId : 'core/template/pages',
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
	
	gc.app.pageTitle('Template verwalten');
	gc.app.pageDescription('Template ansehen und bearbeiten');

	return {
		router : childRouter
	};
});
