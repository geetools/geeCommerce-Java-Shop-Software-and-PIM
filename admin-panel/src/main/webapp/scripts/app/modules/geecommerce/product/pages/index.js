define([ 'durandal/app', 'gc/childRouter', 'knockout', 'gc/gc' ], function( app, router, ko, gc ) {
	var childRouter = router.createChildRouter();
	childRouter.makeRelative({
		moduleId : 'modules/geecommerce/product/pages',
		fromParent : true
	});
	childRouter.mapModule([ {
		route : 'details/history/:id/:version',
		moduleId : 'details/history/index',
	}, {
		route : 'details/history/tabs/base',
		moduleId : 'details/history/tabs/base',
	}, {
        route : 'details/:id',
        moduleId : 'details/index',
    }, {
        route : 'details/tabs/base',
        moduleId : 'details/tabs/base',
    }, {
        route : 'import',
        moduleId : 'import/index',
    }, {
        route : 'import/tabs/base',
        moduleId : 'import/tabs/base',
    } ]);
	childRouter.buildNavigationModel();
	childRouter.setMetadata({
		pageTitle : gc.app.i18n('app:modules.product.title'),
		pageDescription : gc.app.i18n('app:modules.product.subtitle')
	});

	return {
		router : childRouter
	};
});