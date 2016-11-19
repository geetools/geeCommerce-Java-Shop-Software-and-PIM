define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-product-promotion', 'gc-product-list' ], function(app, ko, router, gc, productPromotionAPI, productListAPI) {

    function ProductPromotionVM(productPromotionId) {
        var self = this;
        self.id = ko.observable(productPromotionId);
        self.label = ko.observableArray([]);
        self.key = ko.observable();
        self.query = ko.observable();
        self.limit = ko.observable();
        self.teaser = ko.observable();

        self.displayLabel = ko.observable("");
        self.targetId = ko.observable();
        self.targetType = ko.observable();
        self.useTargetLabel = ko.observable(false);

        self.enabled = ko.observableArray([]);

        self.isNew = ko.observable(false);

        if(productPromotionId == 'new'){
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
    function ProductPromotionBaseController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ProductPromotionBaseController)) {
            throw new TypeError("ProductPromotionBaseController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.productPromotionVM = {};
        this.productPromotionId = ko.observable();
        this.productLists = ko.observableArray([]);

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'activate', 'attached', 'saveData', 'removeTeaser');
    }

    ProductPromotionBaseController.prototype = {
        constructor : ProductPromotionBaseController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.product-promotion.detailsTitle';
            var vm = ko.gc.unwrap(self.productPromotionVM);

            if(!_.isUndefined(vm)) {
                var name = vm.label;

                if(!_.isEmpty(name)) {
                    title += ': ' + gc.ctxobj.val(vm.name, self.app.currentLang(), 'any');
                }
            }

            return title;
        },
        saveData : function(view, parent, toolbar) {
            var self = this;
            var updateModel = gc.app.newUpdateModel();

            updateModel.field('label', self.productPromotionVM.label(), true);
            updateModel.field('key', self.productPromotionVM.key());
            updateModel.field('limit', self.productPromotionVM.limit());
            updateModel.field('enabled', self.productPromotionVM.enabled(), true);
            updateModel.field('useTargetObjectLabel', self.productPromotionVM.useTargetLabel());

            if(self.productPromotionVM.targetId() && self.productPromotionVM.targetId() != '' ){
                updateModel.field('targetObjectType', 'PRODUCT_LIST');
                updateModel.field('targetObjectId', self.productPromotionVM.targetId())
            }


            if(self.productPromotionVM.isNew()) {
                productPromotionAPI.createProductPromotion(updateModel).then(function(data) {
                    router.navigate('//product-promotions/details/' + data.id);
                    self.productPromotionId(data.id);
                    toolbar.hide();
                })
            } else {
                productPromotionAPI.updateProductPromotion(self.productPromotionId(), updateModel).then(function(data) {
                    console.log(data);
                    self.productPromotionVM.query(data.query)
                    toolbar.hide();
                })
            }
        },
        removeTeaser : function() {
            var self = this;

            productPromotionAPI.removeTeaser(self.productPromotionId()).then(function (data) {
                self.productPromotionVM.teaser(null);
            });
        },
        activate : function(data) {
            var self = this;
            self.productPromotionId(data);
            var vm = new ProductPromotionVM(data);
            self.productPromotionVM = vm;

            var prdoductListArray = [];

            productListAPI.getProductLists().then(function(data){
                prdoductListArray.push( { id : '', text : function() {
                    return gc.app.i18n('app:common.choose', {}, gc.app.currentLang);
                }});

                gc.ctxobj.enhance(data.data.productLists, [ 'label' ],  'any');
                _.each(data.data.productLists, function(option) {
                    if(!_.isUndefined(option.label)){
                        prdoductListArray.push({id: option.id, text: option.label.i18n});
                    }
                });
                self.productLists(prdoductListArray);
            });

            if(!vm.isNew()){
                productPromotionAPI.getProductPromotion(self.productPromotionId()).then(function(data) {

                    vm.key(data.key);
                    vm.query(data.query);
                    vm.label(data.label);
                    vm.limit(data.limit);
                    vm.enabled(data.enabled);

                    vm.displayLabel(data.displayLabel);
                    if(data.useTargetObjectLabel)
                        vm.useTargetLabel(data.useTargetObjectLabel);
                    vm.targetId(data.targetObjectId);
                    vm.targetType(data.targetObjectType);

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
                    var dz = new Dropzone(form, { url: '/api/v1/product-promotions/' + self.productPromotionId() + '/teasers/'});

                    dz.on("sending", function(file, xhr, formData) {
                        var activeStore = gc.app.activeStore();

                        if(!_.isEmpty(activeStore) && !_.isUndefined(activeStore.id)) {
                            xhr.setRequestHeader('X-CB-StoreContext', activeStore.id);
                        }
                    });

                    dz.on("success", function(file, data) {
                        var teaserImage = new TeaserImageVM(data.teaserImage.id, data.teaserImage.name, data.teaserImage.url, data.teaserImage.webThumbnailPath, data.teaserImage.url, data.teaserImage.url, data.teaserImage.url, data.teaserImage.mimeType);
                        self.productPromotionVM.teaser(teaserImage);
                        // Remove file from preview.
                        dz.removeFile(file);
                    });

                    $form.data('dz-init', true);
                }
            });
        },
        attached : function() {
            var self = this;
        }
    }

    return ProductPromotionBaseController;
});