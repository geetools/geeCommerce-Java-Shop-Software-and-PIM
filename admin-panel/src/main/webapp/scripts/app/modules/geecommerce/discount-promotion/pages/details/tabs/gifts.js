define([ 'durandal/app', 'knockout', 'gc/gc', 'gc-discount-promotion' ], function(app, ko, gc, discountPromotionAPI) {

    //-----------------------------------------------------------------
    // Product view model
    //-----------------------------------------------------------------
    function DiscountPromotionVM(discountPromotionId, name) {
        var self = this;
        self.id = discountPromotionId;
        self.gifts = ko.observableArray([]);
        self.name = name;
        self.title = function() {
            var title = '';

            if(self.name) {
                if(!_.isEmpty(self.name)) {
                    title += self.name;
                }
            }

            return title == '' ? self.id : title;
        };

    }

    //-----------------------------------------------------------------
    // Media view model (found in observableArray productVM.media).
    //-----------------------------------------------------------------
    function GiftVM(giftId, mediaAssetId, path, webPath, webThumbnailPath, previewImagePath, previewImageWebPath,
                     previewImageWebThumbnailPath, position, mimeType, name, description) {
        var self = this;
        self.id = giftId;
        self.mediaAssetid = mediaAssetId;

        self.path = ko.observable(path);
        self.webPath = ko.observable(webPath);

        var dPath = webPath + "?d=true";
        if(dPath.indexOf("http") == -1){
            dPath = "http://" + dPath;
        }

        self.downloadPath = ko.observable(dPath);
        self.webThumbnailPath = ko.observable(webThumbnailPath);
        self.previewImagePath = ko.observable(previewImagePath);
        self.previewImageWebPath = ko.observable(previewImageWebPath);
        self.previewImageWebThumbnailPath = ko.observable(previewImageWebThumbnailPath);
        self.mimeType = ko.observable(mimeType);

        self.position = ko.observable(position);

        self.name = ko.observableArray(name);
        self.description = ko.observableArray(description);

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
    function ActionGiftsController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ActionGiftsController)) {
            throw new TypeError("ActionGiftsController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.utils = gc.utils;
        this.discountPromotionId = null;
        this.discountPromotionVM = ko.observable({});
        this.dzInited = false;

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'addGift', 'removeGift', 'refreshGifts', 'updatePositions', 'activate', 'attached');
    }

    ActionGiftsController.prototype = {
        constructor : ActionGiftsController,
        saveData : function(view, parent, toolbar, args) {
            var self = this;

            var updates = [];

            if(!_.isEmpty(self.discountPromotionVM())) {
                var gifts = self.discountPromotionVM().gifts();

                if(!_.isEmpty(gifts)) {

                    _.each(gifts, function(data) {

                        var updateModel = gc.app.newUpdateModel()
                            .id(data.id)
                            .field('position', data.position() || 99)
                            .field('name', data.name(), true)
                            .field('description', data.description(), true);

                        updates.push(updateModel.data());
                    });

                    discountPromotionAPI.updateGifts(self.discountPromotionId, updates).then(function() {
                        toolbar.hide();
                    });
                }
            }
        },
        //---------------------------------------------------------------------------------
        // Removes image in backend and from observable array.
        //---------------------------------------------------------------------------------
        removeGift : function(giftVM, event) {
            var self = this;

            discountPromotionAPI.removeGift(giftVM.id, self.discountPromotionId).then(function (data) {
                var sVM = _.findWhere(self.discountPromotionVM().gifts(), { id: giftVM.id });

                self.discountPromotionVM().gifts.remove(sVM);
            });
        },
        //---------------------------------------------------------------------------------
        // Uploads image to REST service after it has been selected by user.
        //---------------------------------------------------------------------------------
        addGift : function( file, dz) {
            var self = this;

            // addMediaAsset() seems to be called again after the field has been cleared,
            // so we make sure that a file has actually been selected.
            if(!_.isUndefined(file)) {
                var fd = new FormData();
                fd.append( 'file', file );

                // Upload the image and add the returned created object to the observable array.
                discountPromotionAPI.uploadGift(self.discountPromotionId, fd).then(function (gift) {

                    var giftVM = new GiftVM(gift.id, gift.mediaAsset.id, gift.mediaAsset.name, gift.mediaAsset.url,
                        gift.mediaAsset.webThumbnailPath, gift.mediaAsset.url, gift.mediaAsset.url, gift.mediaAsset.url,
                        gift.position, gift.mediaAsset.mimeType, gift.name, gift.description);

                    self.discountPromotionVM().gifts.push(giftVM);

                    // Clear the selected image in preview field and input.
                    dz.removeFile(file);
                });
            }
        },
        //---------------------------------------------------------------------------------
        // Gets images from backend and re-populates the observable images array.
        //---------------------------------------------------------------------------------
        refreshGifts : function() {
            var self = this;

            self.discountPromotionVM().gifts.removeAll();

            discountPromotionAPI.getDiscountPromotion(self.discountPromotionId).then(function(data) {

                var gifts = _.sortBy(data.gifts, function (gift) {return gift.position});
                _.each(gifts, function(gift) {
                    var giftVM = new GiftVM(gift.id, gift.mediaAsset.id, gift.mediaAsset.name, gift.mediaAsset.url,
                        gift.mediaAsset.webThumbnailPath, gift.mediaAsset.url, gift.mediaAsset.url, gift.mediaAsset.url,
                        gift.position, gift.mediaAsset.mimeType, gift.name, gift.description);

                    self.discountPromotionVM().gifts.push(giftVM);
                });
            })
        },
        //---------------------------------------------------------------------------------
        // Update image positions. Called by jquery-sortable after media-asset has been dropped.
        //---------------------------------------------------------------------------------
        updatePositions : function(domGiftsList) {
            var self = this;

            var giftsPositions = {};
            domGiftsList.each(function(index, elem) {
                var item = $(elem),
                    pos = item.index()+1,
                    id = $(item).attr('data-id');

                giftsPositions[id] = pos;
            });

            discountPromotionAPI.updateGiftsPositions(self.discountPromotionId, giftsPositions).then(function() {
                self.refreshGifts();
            });
        },
        // ---------------------------------------------
        // Durandal callback.
        // ---------------------------------------------
        activate : function(id) {
            var self = this;

            self.discountPromotionId = id;
        },
        // ---------------------------------------------
        // Durandal callback.
        // ---------------------------------------------
        attached : function() {
            var self = this;

            Dropzone.autoDiscover = false;


            $(document).on('click', '#tab-discount-promotion-details-gifts', function() {
                var vm = new DiscountPromotionVM(self.discountPromotionId);
                self.discountPromotionVM(vm);

                discountPromotionAPI.getDiscountPromotion(self.discountPromotionId).then(function(data) {
                    var gifts = _.sortBy(data.gifts, function (gift) {return gift.position});

                    _.each(gifts, function(gift) {
                        var giftVM = new GiftVM(gift.id, gift.mediaAsset.id, gift.mediaAsset.name, gift.mediaAsset.url,
                            gift.mediaAsset.webThumbnailPath, gift.mediaAsset.url, gift.mediaAsset.url, gift.mediaAsset.url,
                            gift.position, gift.mediaAsset.mimeType, gift.name, gift.description);
                        self.discountPromotionVM().gifts.push(giftVM);
                    });

                }).then(function(data) {
                    $('.sortableMediaAssets').sortable({
                        update: function() {
                            self.updatePositions($(this).children('tr'));
                        }
                    });

                    /* Dropzone magic. Automatically uploads file and adds the saved entry to list of slides in slideShowVM. */
                    $('.dropzone-gifts').each(function( index, el ) {
                        $form = $(this).get(0);

                        if(!self.dzInited){
                            self.dzInited = true;
                            var dz = new Dropzone($form, { url: '/api/v1/discount-promotions/' + self.discountPromotionId + '/gifts/'});
                            dz.on("sending", function(file, xhr, formData) {
                                var activeStore = gc.app.activeStore();

                                if(!_.isEmpty(activeStore) && !_.isUndefined(activeStore.id)) {
                                    xhr.setRequestHeader('X-CB-StoreContext', activeStore.id);
                                }
                            });

                            dz.on("success", function(file, gift) {
                                var gift = gift.data.actionGift;
                                var giftVM = new GiftVM(gift.id, gift.mediaAsset.id, gift.mediaAsset.name, gift.mediaAsset.url,
                                    gift.mediaAsset.webThumbnailPath, gift.mediaAsset.url, gift.mediaAsset.url, gift.mediaAsset.url,
                                    gift.position, gift.mediaAsset.mimeType, gift.name, gift.description);

                                // Add new db-entry to product-media-list.
                                self.discountPromotionVM().gifts.push(giftVM);
                                // Remove file from preview.
                                dz.removeFile(file);
                            });
                        }
                    });
                });
            });
        }
    }

    return ActionGiftsController;
});