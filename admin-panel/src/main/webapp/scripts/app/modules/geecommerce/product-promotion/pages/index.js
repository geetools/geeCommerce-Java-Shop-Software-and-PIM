define([ 'durandal/app', 'gc/childRouter', 'knockout', 'gc/gc' ],
function(app, router, ko, gc) {

	var childRouter = router.createChildRouter().makeRelative({
		moduleId : 'modules/geecommerce/product-promotion/pages',
		fromParent : true
	}).mapModule([ {
		route : 'details/:id',
		moduleId : 'details/index',
	}, {
		route : 'details/tabs/base',
		moduleId : 'details/tabs/base',
	} ]);
	childRouter.buildNavigationModel();
	childRouter.setMetadata({
		pageTitle : gc.app.i18n('app:modules.product-promotion.title'),
		pageDescription : gc.app.i18n('app:modules.product-promotion.subtitle')
	})

	return {
		router : childRouter
	};
});