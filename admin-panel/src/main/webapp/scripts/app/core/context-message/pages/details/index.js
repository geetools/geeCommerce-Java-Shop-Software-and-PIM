define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-context-message', 'knockout-validation' ], function(app, ko, router, gc, contextMessageAPI, validation) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function ContextMessageDetailsIndexController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof  ContextMessageDetailsIndexController)) {
            throw new TypeError(" ContextMessageDetailsIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.contextMessageId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate');
    }

    ContextMessageDetailsIndexController.prototype = {
        constructor : ContextMessageDetailsIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.context-message.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.context-message.detailsSubtitle',
        activate : function(data) {
            var self = this;
            self.contextMessageId(data);

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

    return  ContextMessageDetailsIndexController;
});