define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-slide-show' ], function(app, ko, gc, slideShowAPI) {

    // -----------------------------------------------------------------
    // Product view model
    // -----------------------------------------------------------------
    function SlideShowVM(slideShowId, name) {
        var self = this;
        self.id = slideShowId;
        self.slides = ko.observableArray([]);
        self.name = name;
        self.title = function() {
            var title = '';

            if (self.name) {
                if (!_.isEmpty(self.name)) {
                    title += self.name;
                }
            }

            return title == '' ? self.id : title;
        };

        self.slideTypeOptions = ko.computed(function() {
            var lang = gc.app.currentLang();
            var _options = [ {
                id : 'IMAGE_LINK',
                text : 'link'
            }, {
                id : 'PRODUCT',
                text : 'product'
            } ];

            return _options;
        }, self);

        self.slidePositionOptions = ko.computed(function() {
            var lang = gc.app.currentLang();
            var _options = [ {
                id : 'left',
                text : 'left'
            }, {
                id : 'right',
                text : 'right'
            } ];

            return _options;
        }, self);
    }

    // -----------------------------------------------------------------
    // Media view model (found in observableArray productVM.media).
    // -----------------------------------------------------------------
    function SlideVM(slideId, mediaAssetId, path, webPath, webThumbnailPath, previewImagePath, previewImageWebPath, previewImageWebThumbnailPath, position, mimeType, link, productArticle,
            pricePosition, showFrom, showTo, linkedMediaAsset) {
        var self = this;
        self.id = slideId;
        self.mediaAssetid = mediaAssetId;

        self.path = ko.observable(path);
        self.webPath = ko.observable(webPath);

        var dPath = webPath + "?d=true";
        if (dPath.indexOf("http") == -1) {
            dPath = "https://" + dPath;
        }

        self.downloadPath = ko.observable(dPath);
        self.webThumbnailPath = ko.observable(webThumbnailPath);
        self.previewImagePath = ko.observable(previewImagePath);
        self.previewImageWebPath = ko.observable(previewImageWebPath);
        self.previewImageWebThumbnailPath = ko.observable(previewImageWebThumbnailPath);

        self.linkedMediaAssetId = ko.observable(linkedMediaAsset ? linkedMediaAsset.id : null);

        self.position = ko.observable(position);
        self.link = ko.observable(link);
        self.mimeType = ko.observable(mimeType);
        self.productArticle = ko.observable(productArticle);
        self.pricePosition = ko.observable(pricePosition);
        self.showFrom = ko.observable(gc.utils.fromServerTime(showFrom));
        self.showTo = ko.observable(gc.utils.fromServerTime(showTo));

        self.slideType = ko.observable();
        if (self.productArticle() && self.productArticle() != '') {
            self.slideType("PRODUCT");
        } else {
            self.slideType("IMAGE_LINK");
        }

        self.fileExtension = ko.computed(function() {
            var path = self.path();
            return _.isEmpty(path) ? '' : path.substring(path.lastIndexOf('.') + 1);
        });

        self.isImage = ko.computed(function() {
            return self.mimeType().startsWith('image/');
        });

        self.isFileLink = ko.computed(function() {
            return self.linkedMediaAssetId() != null;
        });

    }

    // -----------------------------------------------------------------
    // Controller
    // -----------------------------------------------------------------
    function SlidesController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof SlidesController)) {
            throw new TypeError("SlidesController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.utils = gc.utils;
        this.slideShowId = null;
        this.slideShowVM = ko.observable({});
        this.dzInited = false;

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'addSlide', 'removeSlide', 'refreshSlides', 'updatePositions', 'activate', 'attached');
    }

    SlidesController.prototype = {
        constructor : SlidesController,
        saveData : function(context) {
            var self = this;
            var updates = [];

            if (!_.isEmpty(self.slideShowVM())) {
                var slides = self.slideShowVM().slides();

                if (!_.isEmpty(slides)) {

                    _.each(slides, function(data) {

                        var updateModel = gc.app.newUpdateModel().id(data.id).field('position', data.position() || 99).field('productArticle', data.productArticle()).field('pricePosition',
                                data.pricePosition()).field('showFrom', gc.utils.toServerTime(gc.utils.startOfTheDay(data.showFrom()))).field('showTo',
                                gc.utils.toServerTime(gc.utils.endOfTheDay(data.showTo())));

                        if (data.isFileLink()) {
                            updateModel.field('link', '');
                        } else {
                            updateModel.field('link', data.link());
                        }

                        if (data.slideType() === 'IMAGE_LINK') {
                            updateModel.field('link', data.link()).field('productArticle', null)
                        } else {
                            updateModel.field('link', null).field('productArticle', data.link())
                        }

                        updates.push(updateModel.data());
                    });

                    slideShowAPI.updateSlides(self.slideShowId, updates).then(function() {
                        context.saved();
                    });
                }
            }
        },
        // ---------------------------------------------------------------------------------
        // Removes image in backend and from observable array.
        // ---------------------------------------------------------------------------------
        removeSlide : function(slideVM, event) {
            var self = this;

            slideShowAPI.removeSlide(slideVM.id, self.slideShowId).then(function(data) {
                var sVM = _.findWhere(self.slideShowVM().slides(), {
                    id : slideVM.id
                });

                self.slideShowVM().slides.remove(sVM);
            });
        },
        // ---------------------------------------------------------------------------------
        // Uploads image to REST service after it has been selected by user.
        // ---------------------------------------------------------------------------------
        addSlide : function(file, dz) {
            var self = this;

            // addMediaAsset() seems to be called again after the field has been cleared,
            // so we make sure that a file has actually been selected.
            if (!_.isUndefined(file)) {
                var fd = new FormData();
                fd.append('file', file);

                // Upload the image and add the returned created object to the observable array.
                slideShowAPI.uploadSlide(self.slideShowId, fd).then(
                        function(slide) {

                            var slideVM = new SlideVM(slide.id, slide.mediaAsset.id, slide.mediaAsset.file.name, slide.mediaAsset.url, slide.mediaAsset.webThumbnailPath, slide.mediaAsset.url,
                                    slide.mediaAsset.url, slide.mediaAsset.url, slide.position, slide.mediaAsset.mimeType, slide.link, slide.productArticle, slide.pricePosition, slide.showFrom,
                                    slide.showTo, slide.linkedMediaAsset);

                            self.slideShowVM().slides.push(slideVM);

                            // Clear the selected image in preview field and input.
                            dz.removeFile(file);
                        });
            }
        },
        // ---------------------------------------------------------------------------------
        // Gets images from backend and re-populates the observable images array.
        // ---------------------------------------------------------------------------------
        refreshSlides : function() {
            var self = this;

            self.slideShowVM().slides.removeAll();

            slideShowAPI.getSlideShow(self.slideShowId).then(
                    function(data) {

                        var slides = _.sortBy(data.slides, function(slide) {
                            return slide.position
                        });
                        _.each(slides, function(slide) {
                            if (slide.mediaAsset) {
                                var slideVM = new SlideVM(slide.id, slide.mediaAsset.id, slide.mediaAsset.file.name, slide.mediaAsset.url, slide.mediaAsset.webThumbnailPath, slide.mediaAsset.url,
                                        slide.mediaAsset.url, slide.mediaAsset.url, slide.position, slide.mediaAsset.mimeType, slide.link, slide.productArticle, slide.pricePosition, slide.showFrom,
                                        slide.showTo, slide.linkedMediaAsset);
                                self.slideShowVM().slides.push(slideVM);
                            }
                        });
                    })
        },
        // ---------------------------------------------------------------------------------
        // Update image positions. Called by jquery-sortable after media-asset has been dropped.
        // ---------------------------------------------------------------------------------
        updatePositions : function(domSlidesList) {
            var self = this;

            var slidesPositions = {};
            domSlidesList.each(function(index, elem) {
                var item = $(elem), pos = item.index() + 1, id = $(item).attr('data-id');

                slidesPositions[id] = pos;
            });

            slideShowAPI.updateSlidesPositions(self.slideShowId, slidesPositions).then(function() {
                self.refreshSlides();
            });
        },
        // ---------------------------------------------
        // Durandal callback.
        // ---------------------------------------------
        activate : function(id) {
            var self = this;
            self.slideShowId = id;
        },
        // ---------------------------------------------
        // Durandal callback.
        // ---------------------------------------------
        attached : function() {
            var self = this;

            gc.app.onSaveEvent(function(context) {
                var id = $('.tab-content>.active').attr('id');

                if (id == 'slides') {
                    self.saveData(context);
                }
            });

            Dropzone.autoDiscover = false;

            $(document).bind('DOMNodeInserted', function(e) {
                var element = e.target;
                var $forms = $(element).find('form.dropzone-files');

                if ($forms.length > 0) {
                    $forms.each(function(index, el) {
                        $form = $(this).get(0);

                        if (!$($form).hasClass('init') && $($form).attr('data-slideid')) {
                            var slideId = $($form).attr('data-slideid');
                            var dz = new Dropzone($form, {
                                url : '/api/v1/slide-shows/' + self.slideShowId + '/slides/' + slideId + '/files'
                            });

                            dz.on("success", function(file, slide) {
                                var updatedSlide = slide.data.slide;

                                _.each(self.slideShowVM().slides(), function(slide) {
                                    if (slide.id == slideId) {
                                        slide.link(updatedSlide.link);
                                        slide.linkedMediaAssetId('new');
                                    }
                                });

                                // Remove file from preview.
                                dz.removeFile(file);
                            });
                            $($form).addClass('init');
                        }
                    });
                }
            });

            $(document).on(
                    'click',
                    '#tab-slide-show-details-slides',
                    function() {
                        var vm = new SlideShowVM(self.slideShowId);
                        self.slideShowVM(vm);

                        slideShowAPI.getSlideShow(self.slideShowId).then(
                                function(data) {
                                    var slides = _.sortBy(data.slides, function(slide) {
                                        return slide.position
                                    });

                                    _.each(slides, function(slide) {
                                        console.log(slide);
                                        if (slide.mediaAsset) {
                                            var slideVM = new SlideVM(slide.id, slide.mediaAsset.id, slide.mediaAsset.file.name, slide.mediaAsset.url, slide.mediaAsset.webThumbnailPath,
                                                    slide.mediaAsset.url, slide.mediaAsset.url, slide.mediaAsset.url, slide.position, slide.mediaAsset.mimeType, slide.link, slide.productArticle,
                                                    slide.pricePosition, slide.showFrom, slide.showTo, slide.linkedMediaAsset);
                                            self.slideShowVM().slides.push(slideVM);
                                        }
                                    });

                                }).then(
                                function(data) {
                                    $('.sortableMediaAssets').sortable({
                                        update : function() {
                                            self.updatePositions($(this).children('tr'));
                                        }
                                    });

                                    /* Dropzone magic. Automatically uploads file and adds the saved entry to list of slides in slideShowVM. */
                                    $('.dropzone-slides').each(
                                            function(index, el) {
                                                $form = $(this).get(0);

                                                if (!self.dzInited) {
                                                    self.dzInited = true;
                                                    var dz = new Dropzone($form, {
                                                        url : '/api/v1/slide-shows/' + self.slideShowId + '/slides/'
                                                    });

                                                    dz.on("success", function(file, slide) {
                                                        var slide = slide.data.slide;
                                                        var slideVM = new SlideVM(slide.id, slide.mediaAsset.id, slide.mediaAsset.file.name, slide.mediaAsset.url, slide.mediaAsset.webThumbnailPath,
                                                                slide.mediaAsset.url, slide.mediaAsset.url, slide.mediaAsset.url, slide.position, slide.mediaAsset.mimeType, slide.link,
                                                                slide.productArticle, slide.pricePosition, slide.showFrom, slide.showTo, slide.linkedMediaAsset);

                                                        // Add new db-entry to product-media-list.
                                                        self.slideShowVM().slides.push(slideVM);
                                                        // Remove file from preview.
                                                        dz.removeFile(file);
                                                    });
                                                }
                                            });

                                    $('#slide-show-slides').addClass('save-button-listen-area');

                                });
                    });
        }
    }

    return SlidesController;
});