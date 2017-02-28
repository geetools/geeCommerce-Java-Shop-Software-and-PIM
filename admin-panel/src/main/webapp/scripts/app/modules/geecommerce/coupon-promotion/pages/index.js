define([ 'durandal/app', 'plugins/router', 'knockout', 'gc/gc' ], function(app, router, ko, gc) {
	var childRouter = router.createChildRouter().makeRelative({
		moduleId : 'modules/geecommerce/coupon-promotion/pages',
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
	}]).buildNavigationModel();

    childRouter.setMetadata({
        pageTitle : gc.app.i18n('app:modules.coupon-promotion.title'),
        pageDescription : gc.app.i18n('app:modules.coupon-promotion.subtitle')
    });

	return {
		router : childRouter
	};
});