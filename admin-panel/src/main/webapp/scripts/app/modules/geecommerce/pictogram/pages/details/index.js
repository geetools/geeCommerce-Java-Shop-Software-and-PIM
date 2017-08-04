define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-pictogram', 'knockout-validation' ], function(app, ko, router, gc, pictogramAPI, validation) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function PictogramDetailsIndexController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof PictogramDetailsIndexController)) {
            throw new TypeError("PictogramDetailsIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.pictogramId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate');
    }

    PictogramDetailsIndexController.prototype = {
        constructor : PictogramDetailsIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.pictogram.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.pictogram.detailsSubtitle',
        activate : function(data) {
            var self = this;
            self.pictogramId(data);

        },
        attached : function() {
            var self = this;
            /*gc.app.onToolbarEvent({
             save : self.saveData
             });*/
        },
        compositionComplete : function() {
        },
        detached : function() {
        },
        deactivate : function() {
        }
    }

    return PictogramDetailsIndexController;
});