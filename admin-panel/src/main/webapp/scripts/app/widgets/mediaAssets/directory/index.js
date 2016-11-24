define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-media-asset', 'knockout-validation', 'gc-media-asset/util'  ], function(app, ko, router, gc, mediaAssetAPI, validation, mediaAssetUtil) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function MediaAssetsDirectoryController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof MediaAssetsDirectoryController)) {
            throw new TypeError("MediaAssetsDirectoryController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.directory = null;
        this.mediaAssetId = ko.observable();
        /*        this.mediaAssetId = ko.observable();*/

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'selectMediaAsset');
    }

    MediaAssetsDirectoryController.prototype = {
        constructor : MediaAssetsDirectoryController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.media-asset.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.media-asset.detailsSubtitle',
        activate : function(data) {
            var self = this;
            self.directory = data.directory;
            self.directory.maPager.limit(10);
            gc.app.sessionPut('selectedMediaAsset', null);
            return self.directory.maPager.load();

        },
        selectMediaAsset: function (mediaAsset) {
            this.mediaAssetId(mediaAsset.id);
            gc.app.sessionPut('selectedMediaAsset', mediaAsset.id);
        },
        attached : function() {
            var self = this;
            
        },
        compositionComplete : function() {
            var self = this;
        },
        detached : function() {
        },
        deactivate : function() {
        }
    }

    return MediaAssetsDirectoryController;
});