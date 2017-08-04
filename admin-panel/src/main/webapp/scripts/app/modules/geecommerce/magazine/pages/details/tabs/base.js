define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-magazine', 'knockout-validation' ], function(app, ko, router, gc, magazineAPI, validation) {

    function MagazineVM(magazineId) {
        var self = this;
        self.id = ko.observable(magazineId);
        self.title = ko.observableArray();
        self.teaserText = ko.observableArray();
        self.teaser = ko.observable();
        self.show = ko.observableArray([]);
        self.enabled = ko.observableArray([]);

        var d = new Date();
        d.setMonth( d.getMonth( ) + 1 );
        self.showFrom = ko.observable(new Date().toISOString()).extend({ date: true });
        self.showTo = ko.observable(d).extend({ date: true});
        self.validFrom = ko.observable(new Date().toISOString()).extend({ date: true });
        self.validTo = ko.observable(d).extend({ date: true});

        self.isNew = ko.observable(false);

        if(magazineId == 'new'){
            self.isNew(true);
        }

        self.showTeaser =  ko.computed(function() {
            if(self.teaser())
                return true;
            return false;
        }, self);

    }

    function TeaserImageVM(mediaAssetId, path, webPath, webThumbnailPath, previewImagePath, previewImageWebPath, previewImageWebThumbnailPath, mimeType) {
        var self = this;
        self.mediaAssetid = mediaAssetId;

        self.path = ko.observable(path);
        self.webPath = ko.observable(webPath);

        var dPath = webPath + "?d=true";
        if(dPath.indexOf("http") == -1){
            dPath = "https://" + dPath;
        }

        self.downloadPath = ko.observable(dPath);
        self.webThumbnailPath = ko.observable(webThumbnailPath);
        self.previewImagePath = ko.observable(previewImagePath);
        self.previewImageWebPath = ko.observable(previewImageWebPath);
        self.previewImageWebThumbnailPath = ko.observable(previewImageWebThumbnailPath);

        self.mimeType = ko.observable(mimeType);

        self.fileExtension = ko.computed(function() {
            return self.path().substring(self.path().lastIndexOf('.')+1);
        });

        self.isImage = ko.computed(function() {
            return self.mimeType().startsWith('image/');
        });

    }

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function MagazineBaseController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof MagazineBaseController)) {
            throw new TypeError("MagazineBaseController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.magazineVM = {};
        this.magazineId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate');
    }

    MagazineBaseController.prototype = {
        constructor : MagazineBaseController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.magazine.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.magazine.detailsSubtitle',
        saveData : function(view, parent, toolbar) {
            var self = this;

            var updateModel = gc.app.newUpdateModel();
            updateModel.field('title', self.magazineVM.title(), true);
            updateModel.field('teaserText', self.magazineVM.teaserText(), true);
            updateModel.field('showFrom', gc.utils.toServerTime(gc.utils.startOfTheDay(self.magazineVM.showFrom())));
            updateModel.field('showTo', gc.utils.toServerTime(gc.utils.endOfTheDay(self.magazineVM.showTo())));
            updateModel.field('validFrom', gc.utils.toServerTime(gc.utils.startOfTheDay(self.magazineVM.validFrom())));
            updateModel.field('validTo', gc.utils.toServerTime(gc.utils.endOfTheDay(self.magazineVM.validTo())));
            updateModel.field('enabled', self.magazineVM.enabled(), true);


            if(self.magazineVM.isNew()) {
                magazineAPI.createMagazine(updateModel).then(function(data) {
                    router.navigate('//magazines/details/' + data.id);
                    toolbar.hide();
                })
            } else {
                magazineAPI.updateMagazine(self.magazineId(), updateModel).then(function(data) {
                    toolbar.hide();
                })
            }


        },
        removeTeaser : function() {
            var self = this;

            magazineAPI.removeTeaser(self.magazineId()).then(function (data) {
                self.magazineVM.teaser(null);
            });
        },
        activate : function(data) {
            var self = this;
            self.magazineId(data);
            var vm = new MagazineVM(data);
            self.magazineVM = vm;

            if(!vm.isNew()){
                magazineAPI.getMagazine(self.magazineId()).then(function(data) {
                    vm.title(data.title);
                    vm.teaserText(data.teaserText);
                    vm.showFrom(gc.utils.fromServerTime(data.showFrom));
                    vm.showTo(gc.utils.fromServerTime(data.showTo));
                    vm.validFrom(gc.utils.fromServerTime(data.validFrom));
                    vm.validTo(gc.utils.fromServerTime(data.validTo));
                    vm.enabled(data.enabled);

                    if(data.teaserImage){
                        var teaserImage = new TeaserImageVM(data.teaserImage.id, data.teaserImage.name, data.teaserImage.url, data.teaserImage.webThumbnailPath, data.teaserImage.url, data.teaserImage.url, data.teaserImage.url, data.teaserImage.mimeType);
                        vm.teaser(teaserImage);
                    }
                    
                });
            }
            
            Dropzone.autoDiscover = false;

			$('.dropzone-teaser').livequery(function() {
                var form = $(this).get(0);
                var $form = $(form);
                
				var dzInitialized = $form.data('dz-init');

				if(!dzInitialized) {
                    var dz = new Dropzone(form, { url: '/api/v1/magazines/' + self.magazineId() + '/teasers/'});

                    dz.on("sending", function(file, xhr, formData) {
                        var activeStore = gc.app.activeStore();

                        if(!_.isEmpty(activeStore) && !_.isUndefined(activeStore.id)) {
                            xhr.setRequestHeader('X-CB-StoreContext', activeStore.id);
                        }
                    });

                    dz.on("success", function(file, data) {
                        var teaserImage = new TeaserImageVM(data.teaserImage.id, data.teaserImage.name, data.teaserImage.url, data.teaserImage.webThumbnailPath, data.teaserImage.url, data.teaserImage.url, data.teaserImage.url, data.teaserImage.mimeType);
                        self.magazineVM.teaser(teaserImage);
                        // Remove file from preview.
                        dz.removeFile(file);
                    });
				
					$form.data('dz-init', true);
				}
          });
        }
    }

    return MagazineBaseController;
});