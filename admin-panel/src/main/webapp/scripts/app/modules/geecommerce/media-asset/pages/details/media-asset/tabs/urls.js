define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-media-asset', 'knockout-validation', 'gc-media-asset/util'  ], function(app, ko, router, gc, mediaAssetAPI, validation, mediaAssetUtil) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function MediaAssetDetailsIndexController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof MediaAssetDetailsIndexController)) {
            throw new TypeError("MediaAssetDetailsIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.tab = null;
/*        this.mediaAssetId = ko.observable();*/

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate');
    }

    MediaAssetDetailsIndexController.prototype = {
        constructor : MediaAssetDetailsIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.media-asset.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.media-asset.detailsSubtitle',
        activate : function(data) {
            var self = this;
            self.tab = data;

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

    return MediaAssetDetailsIndexController;
});