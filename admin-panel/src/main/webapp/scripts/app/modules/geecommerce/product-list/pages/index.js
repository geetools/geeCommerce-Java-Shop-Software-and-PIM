define([ 'durandal/app', 'gc/childRouter', 'knockout', 'gc/gc' ], function(app, router, ko, gc) {
	var childRouter = router.createChildRouter();
	childRouter.makeRelative({
		moduleId : 'modules/geecommerce/product-list/pages',
		fromParent : true
	});
	childRouter.mapModule([ {
		route : 'details/:id',
		moduleId : 'details/index',
	}, {
		route : 'details/tabs/base',
		moduleId : 'details/tabs/base',
	} ]);
	childRouter.buildNavigationModel();
	childRouter.setMetadata({
		pageTitle : gc.app.i18n('app:modules.product-list.title'),
		pageDescription : gc.app.i18n('app:modules.product-list.subtitle')
	})

	return {
		router : childRouter
	};
});