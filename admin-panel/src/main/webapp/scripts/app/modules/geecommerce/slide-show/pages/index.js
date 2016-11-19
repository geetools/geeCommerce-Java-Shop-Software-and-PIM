define([ 'durandal/app', 'gc/childRouter', 'knockout', 'gc/gc' ],
		function(app, router, ko, gc) {

			var childRouter = router.createChildRouter()
			childRouter.makeRelative({
				moduleId : 'modules/geecommerce/slide-show/pages',
				fromParent : true
			})
			childRouter.mapModule([ {
				route : 'details/:id',
				moduleId : 'details/index',
			}, {
				route : 'details/tabs/base',
				moduleId : 'details/tabs/base',
			} ]);
			childRouter.buildNavigationModel();
			childRouter.setMetadata({
				pageTitle : gc.app.i18n('app:modules.slide-show.title'),
				pageDescription : gc.app.i18n('app:modules.slide-show.subtitle')
			})

			return {
				router : childRouter
			};
		});