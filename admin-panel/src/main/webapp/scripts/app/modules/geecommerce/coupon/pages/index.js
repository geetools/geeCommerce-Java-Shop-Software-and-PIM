define([ 'durandal/app', 'gc/childRouter', 'knockout', 'gc/gc' ],
function(app, router, ko, gc) {

		var childRouter = router.createChildRouter().makeRelative({
		moduleId : 'modules/geecommerce/coupon/pages',
		fromParent : true
	}).mapModule([ {
		route : [ '', 'grid' ],
		moduleId : 'grid/index'
	}, {
		route : 'details/:id',
		moduleId : 'details/index'
	}, {
		route : 'details/tabs/base',
		moduleId : 'details/tabs/base'
	}, {
		route : 'details/tabs/condition',
		moduleId : 'details/tabs/condition'
	}, {
		route : 'details/tabs/action',
		moduleId : 'details/tabs/action'
	}, {
		route : 'details/tabs/codes',
		moduleId : 'details/tabs/codes'
	} ]);
	childRouter.buildNavigationModel();
	childRouter.setMetadata({
		pageTitle : gc.app.i18n('app:modules.coupon.title'),
		pageDescription : gc.app.i18n('app:modules.coupon.subtitle')
	})

	return {
		router : childRouter
	};
});