define([ 'durandal/app', 'gc/childRouter', 'knockout', 'gc/gc' ], function(
		app, router, ko, gc) {
	var childRouter = router.createChildRouter();
	childRouter.makeRelative({
		moduleId : 'core/attribute-group/pages',
		fromParent : true
	});
	childRouter.mapModule([ {
		route : 'details/:id',
		moduleId : 'details/index',
	}, {
		route : 'details/tabs/base',
		moduleId : 'details/tabs/base',
	}, {
		route : 'details/tabs/mapping',
		moduleId : 'details/tabs/mapping',
	} ]);
	childRouter.buildNavigationModel();
	childRouter.setMetadata({
		pageTitle : gc.app.i18n('app:modules.attribute-group.title'),
		pageDescription : gc.app.i18n('app:modules.attribute-group.subtitle')
	});

	return {
		router : childRouter
	};
});