define([ 'durandal/app', 'plugins/router', 'knockout', 'gc/gc' ], function(app, router, ko, gc) {
	var childRouter = router.createChildRouter().makeRelative({
		moduleId : 'core/configuration/pages',
		fromParent : true
	}).map([ {
		route : [ '', 'grid' ],
		moduleId : 'grid/index'
	}, {
		route : 'details/:id',
		moduleId : 'details/index'
	}, {
		route : 'details/tabs/base',
		moduleId : 'details/tabs/base'
	} ]).buildNavigationModel();

	gc.app.pageTitle(gc.app.i18n('app:modules.configuration.title'));
	gc.app.pageDescription(gc.app.i18n('app:modules.configuration.subtitle'));

	return {
		router : childRouter
	};
});