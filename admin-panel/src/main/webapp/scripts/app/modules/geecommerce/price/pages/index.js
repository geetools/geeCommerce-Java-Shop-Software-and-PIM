define([ 'durandal/app', 'plugins/router', 'knockout' ], function(app, router, ko) {
	var childRouter = router.createChildRouter().makeRelative({
		moduleId : 'modules/geecommerce/product/pages',
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

	childRouter.updateDocumentTitle = function(instance, instruction) {
		router.updateDocumentTitle(instance, instruction);
	};

	return {
		router : childRouter
	};
});