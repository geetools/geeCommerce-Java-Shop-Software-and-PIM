define([ 'durandal/app', 'durandal/composition', 'knockout', 'gc/gc',  'gc-media-asset','gc-media-asset/util'], function(app, composition, ko, gc, mediaAssetAPI, mediaAssetUtil) {
	var ctor = function () {
		this.isShowMediaAssets = ko.observable(false);
		this.directories = ko.observableArray([]);
		this.directory = ko.observable();
	};

	ctor.prototype.activate = function (settings) {
		var self = this;
		this.settings = settings;

		return mediaAssetAPI.getMediaAssetDirectories().then(
			function (data) {
				var dirs = mediaAssetUtil.toTree(data.data.mediaAssetDirectories, self);
				self.directories(dirs);
			});
	};

	ctor.prototype.attached = function () {
		jQuery('.media-assets-modal').appendTo("body");
/*		$('.media-assets-modal').draggable({
			handle: ".media-assets-modal-header"
		});*/
	};

	ctor.prototype.openDirectory = function(directory){
		this.directory(directory);
	};

	ctor.prototype.showMediaAssets = function() {
		this.isShowMediaAssets(true);
	};

	ctor.prototype.cancelSelect = function() {
		this.isShowMediaAssets(false);
	};

	ctor.prototype.saveSelect = function() {
		var mediaAssetId = gc.app.sessionGet('selectedMediaAsset');
		if(mediaAssetId) {
			this.settings.value(mediaAssetId);
		}
		this.isShowMediaAssets(false);
	};

	return ctor;
});