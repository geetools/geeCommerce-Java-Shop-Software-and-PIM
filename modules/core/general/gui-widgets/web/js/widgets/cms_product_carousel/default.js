define(['jquery', 'bootstrap', 'gc/gc', 'catalog/api', 'catalog/utils/media', 'jquery-swipe', 'jquery-magnific-popup', 'jquery-slick'],
    function ($, Bootstrap, gc, catalogAPI, mediaUtil) {

        return {
            init: function (widgetParams) {

                console.log("WIDGET PARAMS!!!")
                console.log(widgetParams);
                console.log($('#' + widgetParams.widgetId));

                var self = this;

                var mediaElement = '#' + widgetParams.widgetId;

                function ProductVM() {
                    var self = this;

                    self.id = widgetParams.productId;
                    self.mainImage = {};
                    self.galleryImages = [];
                }

                var productVM = new ProductVM();

                // ---------------------------------------------------------------
                // Set up images.
                // ---------------------------------------------------------------

                catalogAPI.getEnabledViewImages(productVM.id).then(function (response) {
                    console.log('images', response.data.catalogMediaAssets);

                    var _mainImage = _.findWhere(response.data.catalogMediaAssets, {productMainImage: true})
                    var _galleryImages = _.where(response.data.catalogMediaAssets, {productGalleryImage: true})

                    console.log('mainImage', _mainImage);
                    console.log('galleryImages', _galleryImages);

                    if(_mainImage)
                        productVM.mainImage = {
                            origImage: _mainImage.path,
                            largeImage: _mainImage.webDetailPath ? _mainImage.webDetailPath : mediaUtil.buildImageURL(_mainImage.path, 330, 330),
                            thumbnail: _mainImage.webThumbnailPath ? _mainImage.webThumbnailPath : mediaUtil.buildImageURL(_mainImage.path, 60, 60),
                            zoomImage: _mainImage.webZoomPath ? _mainImage.webZoomPath : mediaUtil.buildImageURL(_mainImage.path, 1024, 1024),
                            index: 0
                        };
                    else {
                        productVM.mainImage = {
                            origImage: "",
                            largeImage: "",
                            thumbnail: "",
                            zoomImage: "",
                            index: 0
                        };
                    }

                    var idx = 1;
                    _.each(_galleryImages, function (image) {
                        productVM.galleryImages.push({
                            origImage: image.path,
                            largeImage: image.webDetailPath ? image.webDetailPath : mediaUtil.buildImageURL(image.path, 330, 330),
                            thumbnail: image.webThumbnailPath ? image.webThumbnailPath : mediaUtil.buildImageURL(image.path, 50, 50),
                            zoomImage: image.webZoomPath ? image.webZoomPath : mediaUtil.buildImageURL(image.path, 1024, 1024),
                            index: idx++
                        });
                    });

                    self.renderCarouselImages(productVM, mediaElement);

                    // Subscribe to change variant message
                    gc.app.channel.subscribe('product.variants', function (data) {
                        if (!_.isUndefined(data) && productVM.id == data.variantMasterId) {
                            console.log('CAROUSEL:: got the variants message ' + data.origImage);
                            self.moveToImage(data.origImage, mediaElement);
                        }
                    });
                });

            },
            renderCarouselImages: function (productVM, mediaElement) {

                console.log("renderImages::prdMedia = " + mediaElement);

                $(mediaElement).empty();

                gc.app.render({
                    slice: 'gui-widgets/cms_product_carousel/carousel',
                    data: {mainImage: productVM.mainImage, galleryImages: productVM.galleryImages},
                    process: true,
                    target: mediaElement
                }, function (data) {

                    var targetEL = data.target;

                    // Start the bootstrap carousel for the main image.
                    $(mediaElement + ' .main-carousel').carousel({interval: false});

                    $(mediaElement + ' .main-carousel:visible').swipe({
                        swipeLeft: function (event, direction, distance, duration, fingerCount) {
                            console.log('CMS_PRODUCT_CAROUSEL SWIPE!! ', $(this), event);

                            $(mediaElement + ' .main-carousel').carousel('next');
                        },
                        swipeRight: function (event, direction, distance, duration, fingerCount) {
                            console.log('CMS_PRODUCT_CAROUSEL SWIPE!! ', $(this), event);
                            $(mediaElement + ' .main-carousel').carousel('prev');
                        },
                        threshold: 0
                    });

                    // Start the zoom plugin for the main image.
                    $zoomLinkEL = targetEL.find('.zoom-link');
                    $zoomLinkEL.magnificPopup({
                        gallery: {
                            enabled: true
                        },
                        type: 'image'
                    });

                    // Start the slick slider for the thumbnails.
                    $(mediaElement + ' #prd-img-thumbnails>div>ul').slick({
                        infinite: false,
                        slidesToShow: 5,
                        slidesToScroll: 3,
                        centerPadding: '50px',
                        vertical: true,
                        arrows: true,
                        prevArrow: '<button type="button" class="slick-prev"></button>',
                        nextArrow: '<button type="button" class="slick-next"></button>',
                        responsive: [
                            {
                                breakpoint: 750,
                                settings: {
                                    infinite: false,
                                    slidesToShow: 5,
                                    slidesToScroll: 2,
                                    arrows: true,
                                    centerPadding: '40px',
                                    vertical: false,
                                    prevArrow: '<button type="button" class="slick-prev"></button>',
                                    nextArrow: '<button type="button" class="slick-next"></button>',
                                }
                            },
                            {
                                breakpoint: 450,
                                settings: {
                                    infinite: true,
                                    slidesToShow: 4,
                                    arrows: false,
                                    centerPadding: '80px',
                                    vertical: false,
                                    prevArrow: '<button type="button" class="slick-prev"></button>',
                                    nextArrow: '<button type="button" class="slick-next"></button>',
                                }
                            }
                        ]
                    });

                    // Display large image depending on which thumbnail the user hovers over.
                    $(document).on('click', mediaElement + ' #prd-img-thumbnails>div>ul li', function () {
                        $(mediaElement + ' .main-carousel').carousel($(this).data('slick-index'));
                    });
                });
            },
            moveToImage: function (imageURI, mediaElement) {
                console.log('CMS_PRODUCT_CAROUSEL moveToImage_____ ', imageURI);

                var foundImageEL = $(mediaElement + ' #prd-img-thumbnails>div>ul').find("[data-orig='" + imageURI + "']");

                if (!_.isEmpty(foundImageEL)) {
                    var foundListEL = foundImageEL.first().closest('li');

                    if (!_.isEmpty(foundImageEL)) {
                        var idx = foundListEL.data('slick-index');

                        console.log('CMS_PRODUCT_CAROUSEL moveToImage_____ ', foundImageEL, foundListEL, foundListEL.data('slick-index'));


                        $(mediaElement + ' #prd-img-thumbnails>div>ul').slick('slickGoTo', idx);
                        $(mediaElement + ' .main-carousel').carousel(idx + 1);
                    }
                }
            }
        }
    });




