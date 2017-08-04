define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-media-asset', 'knockout-validation', 'gc-media-asset/util'  ], function(app, ko, router, gc, mediaAssetAPI, validation, mediaAssetUtil) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function MediaAssetSearchIndexController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof MediaAssetSearchIndexController)) {
            throw new TypeError("MediaAssetSearchIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.tab = null;

        _.bindAll(this, 'activate');
    }

    MediaAssetSearchIndexController.prototype = {
        constructor : MediaAssetSearchIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.media-asset.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.media-asset.detailsSubtitle',
        activate : function(data) {
            var self = this;
            self.tab = data.tab;

            return self.tab.vm.maPager.load();
        },
        attached : function() {
            var self = this;
            
        },
        compositionComplete : function() {
            var self = this;
            self.tab.vm.maPager.activateSubscribers();
        },
        detached : function() {
        },
        deactivate : function() {
        }
    }

    return MediaAssetSearchIndexController;
});