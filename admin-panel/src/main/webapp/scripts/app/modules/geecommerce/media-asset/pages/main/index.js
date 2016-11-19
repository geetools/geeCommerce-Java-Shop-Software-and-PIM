define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-media-asset', 'knockout-validation','gc-media-asset/util'  ], function(app, ko, router, gc, mediaAssetAPI, validation, mediaAssetUtil) {

    function TabVM(vm, type) {
        var self = this;

        self.type = type;
        self.vm = vm;
        self.id = vm.id;
        self.label = ko.observableArray();
        if(type == "directory" || type == "media-asset" || type == 'search'){
            self.label = vm.name;
        }

    }
    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function MediaAssetDetailsIndexController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof MediaAssetDetailsIndexController)) {
            throw new TypeError("MediaAssetDetailsIndexController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.directories = ko.observableArray([]);
        this.tabs = ko.observableArray([]);
        this.searchKeyword = ko.observable('');

        //modal
        this.vm = ko.observable();
        this.vmName = ko.observableArray([]);
        this.isDirRenamePopupOpen = ko.observable(false);
        this.isDirCreatePopupOpen = ko.observable(false);
        this.isDirRemovePopupOpen = ko.observable(false);
        this.isMaRenamePopupOpen = ko.observable(false);
        this.isMaRemovePopupOpen = ko.observable(false);


        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'attached', 'openDirectory', 'closeTab', 'search', 'showDirRenameModal', 'renameDir',
                    'showMaRenameModal', 'renameMa', 'showDirCreateModal', 'createDir');
    }

    MediaAssetDetailsIndexController.prototype = {
        constructor : MediaAssetDetailsIndexController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.media-asset.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.media-asset.detailsSubtitle',
        search: function(){
            var self = this;
            var data = {};
            data.query = self.searchKeyword();
            var searchVM = mediaAssetUtil.toSearchVM(data, self);
            var tabVM = new TabVM(searchVM, "search");
            this.tabs.push(tabVM);
            $('#tab-link-' + tabVM.id).tab('show')
        },
        openDirectory: function(directory){
            var tabs = _.where(this.tabs(), {id: directory.id});

            if(tabs && !_.isEmpty(tabs)){
                $('#tab-link-' + tabs[0].id).tab('show')
            } else {
                var tabVM = new TabVM(directory, "directory");
                this.tabs.push(tabVM);
                $('#tab-link-' + tabVM.id).tab('show')
            }

        },
        openMediaAsset: function (mediaAsset) {
            var tabs = _.where(this.tabs(), {id: mediaAsset.id});

            if(tabs && !_.isEmpty(tabs)){
                $('#tab-link-' + tabs[0].id).tab('show')
            } else {
                var tabVM = new TabVM(mediaAsset, "media-asset");
                this.tabs.push(tabVM);
                $('#tab-link-' + tabVM.id).tab('show')
            }
        },
        closeTab: function (tabVM) {
            this.tabs.remove(function(tab) {
                return tab.id == tabVM.id;
            });
        },
        activate : function() {
            var self = this;

            return mediaAssetAPI.getMediaAssetDirectories().then(
                function (data) {
                    var dirs = mediaAssetUtil.toTree(data.data.mediaAssetDirectories, self);
                    self.directories(dirs);

                    /*  self.navigationVM = new NavigationVM(data.data.navigationItems[0].id);
                     self.navigationVM.id(data.data.navigationItems[0].id);

                     gc.app.sessionPut('getNavigationItems stop: navigationVM', self.navigationVM);*/

                });
        },
        attached : function() {
            var self = this;

            $('#media-asset-directories').resizable({
                handles: 'e, s',
                minWidth:100,
                maxWidth:900,
                resize:function(event,ui){
                    var x=ui.element.outerWidth();
                    var y=ui.element.outerHeight();
                    var ele=ui.element;
                    var factor = $(this).parent().width()-x;
                    var f2 = $(this).parent().width() * .02999;
                    console.log(f2);
                    $.each(ele.siblings(),function(idx,item){

                        ele.siblings().eq(idx).css('height',y+'px');
                        //ele.siblings().eq(idx).css('width',(factor-41)+'px');
                        ele.siblings().eq(idx).width((factor-f2)+'px');

                    });
                }
            });
            $('#media-asset-content').resizable({
                handles: 's',
                resize:function(event,ui){

                    var y=ui.element.outerHeight();
                    var ele=ui.element;

                    $.each(ele.siblings(),function(idx,item){
                        ele.siblings().eq(idx).css('height',y+'px');
                    });
                }
            });

            /*gc.app.onToolbarEvent({
             save : self.saveData
             });*/
        },
        showDirRenameModal: function (data) {
            this.vm(data)
            this.vmName(data.name());
            this.isDirRenamePopupOpen(true);
        },
        //TODO: check on uniqueness
        renameDir: function (data) {
            if(this.vm()){
                this.vm().rename(this.vmName());

            }
            this.isDirRenamePopupOpen(false);

        },
        showMaRenameModal: function (data) {
            this.vm(data)
            this.vmName(data.name());
            this.isMaRenamePopupOpen(true);
        },
        //TODO: check on uniqueness
        renameMa: function (data) {
            if(this.vm()){
                this.vm().rename(this.vmName());
            }
            this.isMaRenamePopupOpen(false);
        },

        showDirCreateModal: function (data) {
            this.vm(data)
            this.vmName([]);
            this.isDirCreatePopupOpen(true);

            console.log(data)
            console.log(this.vm());
        },
        //TODO: check on uniqueness
        createDir: function (data) {

            var newDir  = {};

            console.log(data)
            console.log(this.vm());
            newDir.parentId = this.vm().id;
            newDir.name = this.vmName();

            mediaAssetAPI.createMediaAssetDirectory(newDir).then(function (data) {
                console.log(data);
            });

            this.isDirCreatePopupOpen(false);
        },

        showDirRemoveModal: function (data) {
            this.vm(data)
            this.isDirRemovePopupOpen(true);
        },

        removeDir: function (data) {
            var dirId = this.vm().id;
            mediaAssetAPI.removeMediaAssetDirectory(dirId);

            this.isDirRemovePopupOpen(false);
        },

        showMaRemoveModal: function (data) {
            this.vm(data)
            this.isMaRemovePopupOpen(true);
        },

        removeMa: function (data) {

            this.isMaRemovePopupOpen(false);
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

    return MediaAssetDetailsIndexController;
});