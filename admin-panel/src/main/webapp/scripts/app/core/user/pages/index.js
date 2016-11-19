define([ 'durandal/app', 'plugins/router', 'knockout', 'gc/gc' ], function(app, router, ko, gc) {
    var childRouter = router.createChildRouter().makeRelative({
        moduleId : 'core/user/pages',
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
        }]).buildNavigationModel();

    gc.app.pageTitle('Attribute Verwalten');
    gc.app.pageDescription('Attribute ansehen und bearbeiten');

    return {
        router : childRouter
    };
});
