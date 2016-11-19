define([ 'durandal/app', 'gc/childRouter', 'knockout', 'gc/gc' ], function(app, router, ko, gc) {
	
	var childRouter = router.createChildRouter()
	childRouter.makeRelative({
		moduleId : 'modules/geecommerce/content/pages',
		fromParent : true
	});
	childRouter.mapModule([ {
		route : 'details/:id',
		moduleId : 'details/index'
    }, {
        route : 'details/tabs/base',
        moduleId : 'details/tabs/base'
    }, {
        route : 'details/tabs/design',
        moduleId : 'details/tabs/design'
	} ]);
	childRouter.buildNavigationModel();
	childRouter.setMetadata({
		pageTitle : gc.app.i18n('app:modules.content.title'),
		pageDescription : gc.app.i18n('app:modules.content.subtitle')
	})

	return {
		router : childRouter
	};
});