define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-slide-show', 'knockout-validation' ], function(app, ko, router, gc, slideShowAPI, validation) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function SlideShowDetailsIndexController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof SlideShowDetailsIndexController)) {
            throw new TypeError("SlideShowDetailsIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.slideShowId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate');
    }

    SlideShowDetailsIndexController.prototype = {
        constructor : SlideShowDetailsIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.slide-show.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.slide-show.detailsSubtitle',
        activate : function(data) {
            var self = this;
            self.slideShowId(data);

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

    return SlideShowDetailsIndexController;
});