define([ 'knockout', 'gc/gc', 'gc-media-asset'], function(ko, gc, mediaAssetAPI) {

    function MediaAssetVM(data, controller) {
        var self = this;
        self.id = data.id
        self.name = ko.observableArray(data.name);
        self.url = ko.observable(data.url);
        self.controller = controller;
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
            self.controller.openMediaAsset(self);
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
        self.controller = controller;

        self.maPager = new gc.Pager(mediaAssetAPI.pagingOptions(null, {vmWrapper : function (data) {
            var mediaAssetVM = new MediaAssetVM(data, self.controller);
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
/*        self.mediaAssets = ko.observableArray([]);*/
        self.name = ko.observableArray(data.name);
        self.open = ko.observable(false);
        self.loaded = ko.observable(false);
        self.controller = controller;
        self.maTreePager = undefined;
        self.maPager = undefined;
        self.maPagerLoaded = ko.observable(false);

        self.isRoot = !data.parentId;

        self.addMediaAsset = function (mediaAsset) {
            self.maTreePager.data.push(mediaAsset);
            self.maPager.data.push(mediaAsset);

        }

        self.addDirectory = function (directory) {
            self.directories.push(directory);
        }

        self.hide = function () {
            self.open(false);
        }

        self.show = function () {
            self.open(true);
        }

        self.openDirectory = function () {
            self.controller.openDirectory(self);
            self.show();
        }

        self.loadMediaAssets = function () {
            self.maPager = new gc.Pager(mediaAssetAPI.pagingOptions(self.id, {loadState: false, vmWrapper : function (data) {
                var mediaAssetVM = new MediaAssetVM(data, self.controller);
                return mediaAssetVM;
            }}));


            self.maTreePager = new gc.Pager(mediaAssetAPI.pagingOptions(self.id, {loadState: false, vmWrapper : function (data) {
                var mediaAssetVM = new MediaAssetVM(data, self.controller);
                return mediaAssetVM;
            }}));

            self.maPager.load();
            self.maTreePager.load();
/*            mediaAssetAPI.getMediaAssets(self.id).then(function (data) {
                var mediaAssets = []
                _.each(data.data.mediaAssets, function (mediaAsset) {
                    var mediaAssetVM = new MediaAssetVM(mediaAsset, self.controller);
                    mediaAssets.push(mediaAssetVM);
                });
                self.mediaAssets(mediaAssets);
            })*/
        }

        self.showRename = function () {
            self.controller.showDirRenameModal(self);
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
            self.controller.showDirRemoveModal(self);
        }

        self.removeDirectory = function (directoryId) {
            if(self.directories()){
                _.each(self.directories(), function (directory) {
                    if(directory.id == directoryId){
                        self.directories.remove(directory);
                        return;
                    } else {
                        directory.removeDirectory(directoryId);
                    }
                });
            }
        }

        self.removeMediaAsset = function (mediaAssetId) {
            var removed = false;
            if(self.maTreePager && self.maTreePager.data()){
                _.each(self.maTreePager.data(), function (mediaAsset) {
                   if(mediaAsset.id == mediaAssetId){
                       self.maTreePager.data.remove(mediaAsset);
                       removed = true;
                   }
                });
            }
            if(self.maPager && self.maPager.data()){
                _.each(self.maPager.data(), function (mediaAsset) {
                    if(mediaAsset.id == mediaAssetId){
                        self.maPager.data.remove(mediaAsset);
                        removed = true;
                    }
                });
            }

            if(removed)
                return;

            if(self.directories()){
                _.each(self.directories(), function (directory) {
                    directory.removeMediaAsset(mediaAssetId);
                });
            }
        }

        self.showCreateDirectory = function () {
            self.controller.showDirCreateModal(self)
        }

        self.createDirectory = function () {

        }

        if(self.isRoot){
            self.menu = ko.observableArray([{ text: 'Add Directory', action: self.showCreateDirectory }]);
        } else {
            self.menu = ko.observableArray([{ text: 'Rename', action: self.showRename },
                { text: 'Remove', action: self.showRemove },
                { text: 'Add Directory', action: self.showCreateDirectory }]);
        }


    }

    return {
        addMediaAsset: function (data, controller, directory) {
            var mediaAsset = new MediaAssetVM(data, controller);
            directory.addMediaAsset(mediaAsset);
        },
        addDirectory : function (data, controller, directory) {
            var directoryVM = new DirectoryVM(data, controller);
            directoryVM.loadMediaAssets();
            directory.addDirectory(directoryVM);
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