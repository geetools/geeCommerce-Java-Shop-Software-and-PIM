define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-magazine', 'knockout-validation' ], function(app, ko, router, gc, magazineAPI, validation) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function MagazineDetailsIndexController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof MagazineDetailsIndexController)) {
            throw new TypeError("MagazineDetailsIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.magazineId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate');
    }

    MagazineDetailsIndexController.prototype = {
        constructor : MagazineDetailsIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.magazine.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.magazine.detailsSubtitle',
        activate : function(data) {
            var self = this;
            self.magazineId(data);

        },
        attached : function() {
            var self = this;

        },
        compositionComplete : function() {
        },
        detached : function() {
        },
        deactivate : function() {
        }
    }

    return MagazineDetailsIndexController;
});