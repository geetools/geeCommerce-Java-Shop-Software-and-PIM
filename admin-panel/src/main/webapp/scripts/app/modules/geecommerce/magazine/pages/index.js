define([ 'durandal/app', 'plugins/router', 'knockout', 'gc/gc' ], function(app, router, ko, gc) {
	var childRouter = router.createChildRouter().makeRelative({
		moduleId : 'modules/geecommerce/magazine/pages',
		fromParent : true
	}).map([ {
		route : [ '', 'grid' ],
		moduleId : 'grid/index',
	}, {
		route : 'details/:id',
		moduleId : 'details/index',
	}, {
		route : 'details/tabs/base',
		moduleId : 'details/tabs/base',
	} ]).buildNavigationModel();

    gc.app.pageTitle(gc.app.i18n('app:modules.magazine.title'));
    gc.app.pageDescription(gc.app.i18n('app:modules.magazine.subtitle'));

	return {
		router : childRouter
	};
});