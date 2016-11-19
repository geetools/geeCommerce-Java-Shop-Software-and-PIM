define([ 'durandal/app', 'plugins/router', 'knockout', 'gc/gc' ], function(app, router, ko, gc) {
	var childRouter = router.createChildRouter().makeRelative({
		moduleId : 'core/context-message/pages',
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

    gc.app.pageTitle(gc.app.i18n('app:modules.context-message.title'));
    gc.app.pageDescription(gc.app.i18n('app:modules.context-message.subtitle'));

	return {
		router : childRouter
	};
});