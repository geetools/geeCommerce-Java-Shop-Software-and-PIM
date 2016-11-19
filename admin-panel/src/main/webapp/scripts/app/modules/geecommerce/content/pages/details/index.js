define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-content', 'knockout-validation' ], function(app, ko, router, gc, contentAPI, validation) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function ContentDetailsIndexController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ContentDetailsIndexController)) {
            throw new TypeError("ContentDetailsIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.contentId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate');
    }

    ContentDetailsIndexController.prototype = {
        constructor : ContentDetailsIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.content.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.content.detailsSubtitle',
        activate : function(data) {
            var self = this;
            self.contentId(data);

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

    return ContentDetailsIndexController;
});