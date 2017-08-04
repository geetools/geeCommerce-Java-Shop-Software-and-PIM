define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-media-asset', 'knockout-validation', 'gc-media-asset/util'  ], function(app, ko, router, gc, mediaAssetAPI, validation, mediaAssetUtil) {

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function MediaAssetEditController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof MediaAssetEditController)) {
            throw new TypeError("MediaAssetEditController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.tab = null;
/*        this.mediaAssetId = ko.observable();*/

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'attached', 'launchEditor');
    }

    MediaAssetEditController.prototype = {
        constructor : MediaAssetEditController,
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

            $('#tab-media-asset-edit-' + self.tab.id ).click(function() {

                if(window.tabId != self.tab.id ){
      /*              if(window.tabId){
                        jQuery("#image-editor-" + window.tabId).detach().appendTo('image-editor-' + self.tab.id)
                    }
*/
                    window.tabId = self.tab.vm.selectedFile.id;

                    self.toDataUrl(self.tab.vm.selectedFile().url, function(base64Img) {
                        if(window.featherEditor) {
                            window.featherEditor.close(true);
                            self.launchEditor(base64Img);
                        } else {
                            window.featherEditor = new Aviary.Feather({
                                apiKey: '030fddb3d8224cb0bb75c6a191f591cc',
                                theme: 'light', // Check out our new 'light' and 'dark' themes!
                                tools: 'all',
                                noCloseButton: true,
                                onSave: function (imageID, newURL) {
                                    var url = encodeURIComponent(newURL);
                                    mediaAssetAPI.updateMediaAssetFromUrl(/*self.tab.id*/ window.tabId, url).then(function (data) {
                                    });
                                    // var img = document.getElementById(imageID);
                                    // img.src = newURL;
                                },
                                onError: function (errorObj) {
                                    alert(errorObj.message);
                                },
                                onLoad: function () {
                                    self.launchEditor(base64Img);
                                }
                            });
                        }
                    });
                }
            });






            //self.launchEditor();
        },
        toDataUrl: function(url, callback) {
            var xhr = new XMLHttpRequest();
            xhr.responseType = 'blob';
            xhr.onload = function() {
                var reader = new FileReader();
                reader.onloadend = function() {
                    callback(reader.result);
                }
                reader.readAsDataURL(xhr.response);
            };
            xhr.open('GET', url);
            xhr.send();
        },
        launchEditor: function (url) {
            if(url){
                window.featherEditor.launch({
                    image: 'image-' + this.tab.id,
                    appendTo: 'image-editor-' + this.tab.id,
                    url: url
                });
            } else {
                window.featherEditor.launch({
                    image: 'image-' + this.tab.id,
                    appendTo: 'image-editor-' + this.tab.id,
                    url: this.tab.url
                });
            }

        },
        compositionComplete : function() {
        },
        detached : function() {
        },
        deactivate : function() {
        }
    }

    return MediaAssetEditController;
});