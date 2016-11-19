define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-magazine' ], function(app, ko, gc, magazineAPI) {

    //-----------------------------------------------------------------
    // Product view model
    //-----------------------------------------------------------------
    function MagazineVM(magazineId) {
        var self = this;
        self.id = magazineId;
        self.pages = ko.observableArray([]);
      /*  self.title = function() {
            var title = '';

            if(self.name) {
                if(!_.isEmpty(self.name)) {
                    title += self.name;
                }
            }

            return title == '' ? self.id : title;
        };*/
    }

    //-----------------------------------------------------------------
    // Media view model (found in observableArray productVM.media).
    //-----------------------------------------------------------------
    function PageVM(pageId, mediaAssetId, path, webPath, webThumbnailPath, previewImagePath, previewImageWebPath, previewImageWebThumbnailPath, position, mimeType, preview) {
        var self = this;
        self.id = pageId
        self.mediaAssetid = mediaAssetId;

console.log('PATH:::::::::::::::: ', path);

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


        self.position = ko.observable(position);
        self.preview = ko.observable(preview);
        self.mimeType = ko.observable(mimeType);

        self.fileExtension = ko.computed(function() {
            return self.path().substring(self.path().lastIndexOf('.')+1);
        });

        self.isImage = ko.computed(function() {
            return self.mimeType().startsWith('image/');
        });

        self.name = ko.computed(function() {
            return self.path().substring(self.path().indexOf('-')+1);
        });

    }

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function PagesController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof PagesController)) {
            throw new TypeError("PagesController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.utils = gc.utils;
        this.magazineId = null;
        this.magazineVM = ko.observable({});
        this.preview = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'addPage', 'removePage', 'refreshPages', 'updatePositions', 'activate', 'attached');
    }

    PagesController.prototype = {
        constructor : PagesController,
        saveData : function(view, parent, toolbar, args) {
            var self = this;

            var updates = [];

            if(!_.isEmpty(self.magazineVM())) {
                var pages = self.magazineVM().pages();

                if(!_.isEmpty(pages)) {

                    _.each(pages, function(data) {

                        if(self.preview() == data.id){
                            data.preview(true);
                        } else {
                            data.preview(false);
                        }

                        var updateModel = gc.app.newUpdateModel()
                            .id(data.id)
                            .field('preview', data.preview())
                            .field('position', data.position() || 99);



                        updates.push(updateModel.data());
                    });

                    magazineAPI.updatePages(self.magazineId, updates).then(function() {
                        toolbar.hide();
                    });
                }
            }
        },
        //---------------------------------------------------------------------------------
        // Removes image in backend and from observable array.
        //---------------------------------------------------------------------------------
        removePage : function(pageVM, event) {
            var self = this;

            magazineAPI.removePage(pageVM.id, self.magazineId).then(function (data) {
                var sVM = _.findWhere(self.magazineVM().pages(), { id: pageVM.id });

                self.magazineVM().pages.remove(sVM);
            });
        },
        //---------------------------------------------------------------------------------
        // Uploads image to REST service after it has been selected by user.
        //---------------------------------------------------------------------------------
        addPage : function( file, dz) {
            var self = this;

            // addMediaAsset() seems to be called again after the field has been cleared,
            // so we make sure that a file has actually been selected.
            if(!_.isUndefined(file)) {
                var fd = new FormData();
                fd.append( 'file', file );

                // Upload the image and add the returned created object to the observable array.
                magazineAPI.uploadPage(self.magazineId, fd).then(function (page) {

                    var pageVM = new PageVM(page.id, page.mediaAsset.id, page.mediaAsset.name, page.mediaAsset.url, page.mediaAsset.webThumbnailPath, page.mediaAsset.url, page.mediaAsset.url, page.mediaAsset.url, page.position, page.mediaAsset.mimeType, page.preview);

                    self.magazineVM().pages.push(pageVM);
                    if(pageVM.preview())
                        self.preview(pageVM.id)

                    // Clear the selected image in preview field and input.
                    dz.removeFile(file);
                });
            }
        },
        //---------------------------------------------------------------------------------
        // Gets images from backend and re-populates the observable images array.
        //---------------------------------------------------------------------------------
        refreshPages : function() {
            var self = this;

            self.magazineVM().pages.removeAll();

            magazineAPI.getMagazine(self.magazineId).then(function(data) {

                var pages = _.sortBy(data.pages, function (page) {return page.position});
                _.each(pages, function(page) {
                    var pageVM = new PageVM(page.id, page.mediaAsset.id, page.mediaAsset.name, page.mediaAsset.url, page.mediaAsset.webThumbnailPath, page.mediaAsset.url, page.mediaAsset.url, page.mediaAsset.url, page.position, page.mediaAsset.mimeType, page.preview);
                    self.magazineVM().pages.push(pageVM);
                    if(pageVM.preview())
                        self.preview(pageVM.id)
                });
            })
        },
        //---------------------------------------------------------------------------------
        // Update image positions. Called by jquery-sortable after media-asset has been dropped.
        //---------------------------------------------------------------------------------
        updatePositions : function(domPagesList) {
            var self = this;

            var pagesPositions = {};
            domPagesList.each(function(index, elem) {
                var item = $(elem),
                    pos = item.index()+1,
                    id = $(item).attr('data-id');

                pagesPositions[id] = pos;
            });

            magazineAPI.updatePagesPositions(self.magazineId, pagesPositions).then(function() {
                self.refreshPages();
            });
        },
        // ---------------------------------------------
        // Durandal callback.
        // ---------------------------------------------
        activate : function(id) {
            var self = this;

            self.magazineId = id;
        },
        // ---------------------------------------------
        // Durandal callback.
        // ---------------------------------------------
        attached : function() {
            var self = this;

            Dropzone.autoDiscover = false;
            
            $(document).on('click', '#tab-magazine-details-pages', function() {
                var vm = new MagazineVM(self.magazineId);
                self.magazineVM(vm);

                magazineAPI.getMagazine(self.magazineId).then(function(data) {
                    var pages = _.sortBy(data.pages, function (page) {return page.position});

                    _.each(pages, function(page) {
                        var pageVM = new PageVM(page.id, page.mediaAsset.id, page.mediaAsset.name, page.mediaAsset.url, page.mediaAsset.webThumbnailPath, page.mediaAsset.url, page.mediaAsset.url, page.mediaAsset.url, page.position, page.mediaAsset.mimeType, page.preview);
                        if(pageVM.preview())
                            self.preview(pageVM.id)
                        self.magazineVM().pages.push(pageVM);
                    });
                }).then(function(data) {
                    $('.sortableMediaAssets').sortable({
                        update: function() {
                            self.updatePositions($(this).children('tr'));
                        }
                    });

					$('.dropzone-pages').livequery(function() {
		                var form = $(this).get(0);
		                var $form = $(form);
		                
						var dzInitialized = $form.data('dz-init');
		
						if(!dzInitialized) {
                            var dz = new Dropzone(form, { url: '/api/v1/magazines/' + self.magazineId + '/pages/'});

                            dz.on("sending", function(file, xhr, formData) {
                                var activeStore = gc.app.activeStore();

                                if(!_.isEmpty(activeStore) && !_.isUndefined(activeStore.id)) {
                                    xhr.setRequestHeader('X-CB-StoreContext', activeStore.id);
                                }
                            });

                            dz.on("success", function(file, page) {
                                var page = page.data.page;
                                var pageVM = new PageVM(page.id, page.mediaAsset.id, page.mediaAsset.name, page.mediaAsset.url, page.mediaAsset.webThumbnailPath, page.mediaAsset.url, page.mediaAsset.url, page.mediaAsset.url, page.position, page.mediaAsset.mimeType, page.preview);
                                if(pageVM.preview())
                                    self.preview(pageVM.id)
                                // Add new db-entry to product-media-list.
                                self.magazineVM().pages.push(pageVM);
                                // Remove file from preview.
                                dz.removeFile(file);
                            });
                            
							$form.data('dz-init', true);
                        }
                    });
                });
            });
        }
    }

    return PagesController;
});