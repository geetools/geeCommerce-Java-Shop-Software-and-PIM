define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-media-asset', 'knockout-validation' ], function(app, ko, router, gc, mediaAssetAPI, validation) {

    function MediaAssetVM(mediaAssetId) {
        var self = this;
        self.mediaAssetid = mediaAssetId;

        self.path = ko.observable();
        self.webPath = ko.observable("");

        self.downloadPath = ko.computed(function() {
            var dPath = self.webPath() + "?d=true";
            if(dPath.indexOf("http") == -1){
                dPath = "https://" + dPath;
            }
            return dPath;
        });
        self.webThumbnailPath = ko.observable();
        self.previewImagePath = ko.observable();
        self.previewImageWebPath = ko.observable();
        self.previewImageWebThumbnailPath = ko.observable();

        self.mimeType = ko.observable("");

        self.size = ko.observable();

        self.rawMetadata = ko.observable();

        self.fileExtension = ko.computed(function() {
            if(!self.path())
                return "";
            return self.path().substring(self.path().lastIndexOf('.')+1);
        });

        self.isImage = ko.computed(function() {
            return self.mimeType().startsWith('image/');
        });

    }

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function MediaAssetBaseController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof MediaAssetBaseController)) {
            throw new TypeError("MediaAssetBaseController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.mediaAssetVM = ko.observable({});
        this.mediaAssetId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate');
    }

    MediaAssetBaseController.prototype = {
        constructor : MediaAssetBaseController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.media-asset.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.media-asset.detailsSubtitle',
        saveData : function(view, parent, toolbar) {
            var self = this;

        },
        activate : function(data) {
            var self = this;
            console.log(data);
            self.mediaAssetId(data);
            var vm = new MediaAssetVM(data);
            self.mediaAssetVM = vm;

            mediaAssetAPI.getMediaAsset(self.mediaAssetId()).then(function(data) {
                vm.path(data.name);
                vm.webPath(data.url);
                vm.webThumbnailPath(data.webThumbnailPath);
                vm.previewImagePath(data.url);
                vm.previewImageWebPath(data.url);
                vm.previewImageWebThumbnailPath(data.url);
                vm.mimeType(data.mimeType);
                vm.size((data.size / 1024).toFixed(2));
                vm.rawMetadata(data.rawMetadata);
            });

        },
        attached : function() {
            var self = this;

        },
        compositionComplete : function() {
            $('a[data-toggle="tab"]').on('show.bs.tab', function (e) {
                console.log('_________$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$', e);
            });

        },
        detached : function() {
        },
        deactivate : function() {
        }
    }

    return MediaAssetBaseController;
});