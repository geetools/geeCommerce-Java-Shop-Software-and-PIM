define([ 'knockout', 'gc/gc', 'gc-media-asset'], function(ko, gc, mediaAssetAPI) {

    function MediaAssetVM(data, controller) {
        var self = this;
        self.id = data.id
        self.name = ko.observableArray(data.name);
        self.url = ko.observable(data.url);
        self.contoller = controller;
        self.files = ko.observableArray(data.files);
        self.mimeType = data.mimeType;
        self.data = ko.observable(data)

        self.file = ko.observable(data.file);

        self.selectedFile = ko.observable(data.file);


        self.isSImage = ko.computed(function() {
            return self.selectedFile() && self.selectedFile().mimeType.indexOf("image") != -1;
        });

        self.isSDocument = ko.computed(function() {
            if(self.selectedFile() && self.selectedFile().previewDocumentUrl)
                return true;
            return false;
        });

        self.isEditable = function () {
            return self.mimeType.indexOf("image") >= 0 ;
        }

        self.openMediaAsset = function () {
            self.contoller.openMediaAsset(self);
        }

        self.showRename = function () {
            controller.showMaRenameModal(self);
        }
        //TODO: check on uniqueness
        //return promise, on success hide, on fail show message
        self.rename = function (name) {
            self.name(name);

            var updateModel = gc.app.newUpdateModel();
            updateModel.field('name', name, true);

            mediaAssetAPI.updateMediaAsset(self.id ,updateModel);
        }

        self.showRemove = function () {
            controller.showMaRemoveModal(self)
        }

        self.remove = function () {

        }


        self.menu = ko.observableArray([{ text: 'Rename', action: self.showRename },
            { text: 'Remove', action: self.showRemove }]);
    }

    function SearchVM(data, controller) {
        var self = this;
        self.id = data.query;
        self.query = ko.observable(data.query);
        self.contoller = controller;

        self.maPager = new gc.Pager(mediaAssetAPI.pagingOptions(null, {vmWrapper : function (data) {
            var mediaAssetVM = new MediaAssetVM(data, self.contoller);
            return mediaAssetVM;
        }}));

        self.maPager.searchKeyword(data.query);
        self.maPager._isSearch = true;

        self.name = ko.computed(function () {
            return [{val: self.query()}]
        });
    }

    function DirectoryVM(data, controller) {
        var self = this;
        self.id = data.id;
        self.data = ko.observable(data);
        self.directories = ko.observableArray([]);
        self.mediaAssets = ko.observableArray([]);
        self.name = ko.observableArray(data.name);
        self.open = ko.observable(false);
        self.loaded = ko.observable(false);
        self.contoller = controller;
        self.maPager = undefined;
        self.maPagerLoaded = ko.observable(false);


/*        self.mediaAssets = ko.computed(function() {
            console.log("TESSSSSSSSSSSSSSST");
            console.log(self.maPager)
            if(self.maPagerLoaded() && self.maPager)
                return self.maPager.data();
            return [];
        });*/

        self.addMediaAsset = function (mediaAsset) {
            self.mediaAssets.push(mediaAsset);
        }

        self.hide = function () {
            self.open(false);
        }

        self.show = function () {
            self.open(true);
        }

        self.openDirectory = function () {
            self.contoller.openDirectory(self);
            self.show();
        }

        self.addMediaAssetToPager = function(data){
            self.maPager.data.push(new MediaAssetVM(data, self.contoller));
        }

        self.loadMediaAssets = function () {
            self.maPager = new gc.Pager(mediaAssetAPI.pagingOptions(self.id, {vmWrapper : function (data) {
                var mediaAssetVM = new MediaAssetVM(data, self.contoller);
                return mediaAssetVM;
            }}));



            mediaAssetAPI.getMediaAssets(self.id).then(function (data) {
                var mediaAssets = []
                _.each(data.data.mediaAssets, function (mediaAsset) {
                    var mediaAssetVM = new MediaAssetVM(mediaAsset, self.contoller);
                    mediaAssets.push(mediaAssetVM);
                });
                self.mediaAssets(mediaAssets);
            })
        }

        self.showRename = function () {
            controller.showDirRenameModal(self);
        }

        //TODO: check on uniqueness
        //return promise, on success hide, on fail show message
        self.rename = function (name) {
            self.name(name);

            var updateModel = gc.app.newUpdateModel();
            updateModel.field('name', name, true);

            mediaAssetAPI.updateMediaAssetDirectory(self.id ,updateModel);

        }

        self.showRemove = function () {
            controller.showDirRemoveModal(self);
        }

        self.remove = function () {

        }

        self.showCreateDirectory = function () {
            controller.showDirCreateModal(self)
        }

        self.createDirectory = function () {

        }

        self.menu = ko.observableArray([{ text: 'Rename', action: self.showRename },
            { text: 'Remove', action: self.showRemove },
            { text: 'Add Directory', action: self.showCreateDirectory }]);

    }

    return {
        addMediaAsset: function (data, controller, directory) {
            var mediaAsset = new MediaAssetVM(data, controller);
            directory.addMediaAsset(mediaAsset);
        },
        toSearchVM: function (data, controller) {
            return new SearchVM(data, controller);
        },
        toTree: function (list, controller) {
            var directories = [];
            var parents = [];
            var new_parents = [];


            var roots =  _.filter(list, function(o) { return !o.parentId; });

            _.each(roots, function (dir) {
                var dirVM = new DirectoryVM(dir, controller);
                dirVM.loadMediaAssets();

                directories.push(dirVM)
                parents.push(dirVM)
            });


            while(!_.isEmpty(parents)) {
                new_parents = [];
                _.each(parents, function (dirVM) {
                    var dirs = _.where(list, {parentId: dirVM.id});

                    if(dirs && !_.isEmpty(dirs)){
                        _.each(dirs, function (dir) {
                            var vm = new DirectoryVM(dir, controller);
                            dirVM.directories.push(vm);
                            vm.loadMediaAssets();
                            new_parents.push(vm);
                        });
                    }

                })
                parents = new_parents;
            }

            return directories;
        }
    }

});