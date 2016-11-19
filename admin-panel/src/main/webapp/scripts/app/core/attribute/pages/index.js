define([ 'durandal/app', 'gc/childRouter', 'knockout', 'gc/gc' ], function(
		app, router, ko, gc) {
	var childRouter = router.createChildRouter();
	childRouter.makeRelative({
		moduleId : 'core/attribute/pages',
		fromParent : true
	});
	childRouter.mapModule([ {
		route : 'details/:id',
		moduleId : 'details/index',
	}, {
		route : 'details/tabs/base',
		moduleId : 'details/tabs/base',
	}, {
		route : 'details/tabs/options',
		moduleId : 'details/tabs/options',
	}, {
		route : 'details/tabs/input_conditions',
		moduleId : 'details/tabs/input_conditions',
	} ]);
	childRouter.buildNavigationModel();
	childRouter.setMetadata({
		pageTitle : gc.app.i18n('app:modules.attribute.title'),
		pageDescription : gc.app.i18n('app:modules.attribute.subtitle')
	});

	return {
		router : childRouter
	};
});
