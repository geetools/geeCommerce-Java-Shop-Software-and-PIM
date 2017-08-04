define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-pictogram', 'knockout-validation', 'gc-attribute' ], function(app, ko, router, gc, pictogramAPI, validation, attrAPI) {

    function AttributePosVM(attrId) {
        var self = this;

        self.attr_id = attrId;
        self.x = ko.observable();
        self.y = ko.observable();
        self.label = ko.observable();

        self.labelPreview =  ko.computed(function() {
            return self.label() + '<br/>' + '123 cm';
        }, self);

        self.onDragEnd = function(data, event) {
            var parentOffset = $("#templateWrapper").offset();
            //or $(this).offset(); if you really just want the current element's offset
            var relX = event.pageX - parentOffset.left;
            var relY = event.pageY - parentOffset.top;
            self.x(relX);
            self.y(relY);
        };

        self.showOnImage =  ko.computed(function() {
            if(self.x() > 0 && self.y() > 0)
                return true;
            return false;
        }, self);

        self.top =  ko.computed(function() {
            return self.y() + 'px';
        }, self);

        self.left =  ko.computed(function() {
            return self.x() + 'px';
        }, self);

        self.subscriptionX = self.x.subscribe(function(newValue) {
            if(self.x() && newValue > 0){
                $toolbar = $("#pictogramBaseForm").closest('form').find('.toolbar-trigger').first();
                // Make sure that the save/cancel toolbar sees the change.
                $toolbar.click();
                $toolbar.trigger('change');
            };
        });

        self.subscriptionY = self.y.subscribe(function(newValue) {
            if(self.y() && newValue > 0){
                $toolbar = $("#pictogramBaseForm").closest('form').find('.toolbar-trigger').first();
                // Make sure that the save/cancel toolbar sees the change.
                $toolbar.click();
                $toolbar.trigger('change');
            };
        });

    }

    function PictogramVM(pictogramId) {
        var self = this;

        self.pictogramMA = ko.observable();
        self.templateMA = ko.observable();
        self.unit = ko.observableArray([]);


        self.dimensionAttributes = ko.observableArray([]);
        self.dimensionAttributeOptions = ko.observableArray([])

        self.productGroups = ko.observableArray([]);
        self.productGroupOptions = ko.observableArray([]);

        self.showPictogram =  ko.computed(function() {
            if(self.pictogramMA())
                return true;
            return false;
        }, self);

        self.showTemplate =  ko.computed(function() {
            if(self.templateMA())
                return true;
            return false;
        }, self);

        self.dimensionAttributesPos = ko.observableArray([]);

        self.subscription = self.dimensionAttributes.subscribe(function(newValue) {
            var positions = [];

            _.each(self.dimensionAttributes(), function(attrId) {
                var aVM = _.findWhere(self.dimensionAttributesPos(), { attr_id: attrId});
                if(!aVM){
                    var label = _.findWhere(self.dimensionAttributeOptions(), { id: attrId});
                    aVM = new AttributePosVM(attrId);
                    aVM.x(0);
                    aVM.y(0);
                    if(label)
                        aVM.label(label.text);
                }
                positions.push(aVM);
            });

            self.dimensionAttributesPos(positions);
        });

    }

    function MediaAssetVM(mediaAssetId) {
        var self = this;
        self.mediaAssetId = mediaAssetId;

        self.path = ko.observable();
        self.webPath = ko.observable("");

        self.downloadPath = ko.computed(function() {
            var dPath = self.webPath() + "?d=true";
            if(dPath.indexOf("http") == -1){
                dPath = "https://" + dPath;
            }
            return dPath;
        });
        self.webThumbnailPath = ko.observable();
        self.previewImagePath = ko.observable();
        self.previewImageWebPath = ko.observable();
        self.previewImageWebThumbnailPath = ko.observable();

        self.mimeType = ko.observable("");

        self.size = ko.observable();

        self.rawMetadata = ko.observable();

        self.fileExtension = ko.computed(function() {
            if(!self.path())
                return "";
            return self.path().substring(self.path().lastIndexOf('.')+1);
        });

        self.isImage = ko.computed(function() {
            return self.mimeType().startsWith('image/');
        });

    }

    //-----------------------------------------------------------------
    // Controller
    //-----------------------------------------------------------------
    function PictogramBaseController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof PictogramBaseController)) {
            throw new TypeError("PictogramBaseController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.pictogramVM = ko.observable({});
        this.pictogramId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate');
    }

    PictogramBaseController.prototype = {
        constructor : PictogramBaseController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.media-asset.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.media-asset.detailsSubtitle',
        onDrop : function(data, model){
        },
        saveData : function(view, parent, toolbar) {
            var self = this;

            var updateModel = gc.app.newUpdateModel();
            updateModel.field('dimensionAttributeIds', self.pictogramVM.dimensionAttributes());
            updateModel.field('productGroupIds', self.pictogramVM.productGroups());
            updateModel.field('unit', self.pictogramVM.unit(),true);
            updateModel.field('positions', ko.toJSON(self.pictogramVM.dimensionAttributesPos(),["attr_id", "x", "y"]));

            pictogramAPI.updatePictogram(self.pictogramId(), updateModel).then(function(data) {
                toolbar.hide();
            });
        },
        activate : function(data) {
            var self = this;
            self.pictogramId(data);
            var vm = new PictogramVM(data);
            self.pictogramVM = vm;

            attrAPI.getAttributes('product', { fields : [ 'code', 'code2', 'backendLabel', 'editable', 'enabled', 'inputType', 'frontendInput', 'optionAttribute', 'allowMultipleValues', 'i18n', 'options', 'tags', 'label', 'showInQuery', 'group', 'includeInProductListFilter', 'dimensionAttribute'] } ).then(function( response ) {

                var attributes = response.data.attributes;

                var attrOptions = [];
                var prdGroupOptions = [];
                _.each(attributes, function(attr) {
                    if(attr.dimensionAttribute ){
                        var backendLabel = attr.backendLabel;
                        attrOptions.push({id: attr.id, text: /*backendLabel.i18n*/gc.ctxobj.val(backendLabel, gc.app.currentUserLang(), 'closest')});
                    }

                    if(attr.code == "product_group"){
                        _.each(attr.options, function(option) {
                            var label = option.label;
                            prdGroupOptions.push({id: option.id, text: gc.ctxobj.val(label, gc.app.currentUserLang(), 'closest')});
                        });
                    }
                });
                vm.dimensionAttributeOptions(attrOptions);
                vm.productGroupOptions(prdGroupOptions);
            }).then(function(){
                pictogramAPI.getPictogram(self.pictogramId()).then(function(data) {
                    vm.dimensionAttributes(data.dimensionAttributeIds);
                    vm.productGroups(data.productGroupIds);
                    vm.unit(data.unit);

                    _.each(data.dimensionPositions, function(dimensionPosition) {
                        var dPos = _.findWhere(vm.dimensionAttributesPos(), { attr_id: dimensionPosition.attributeId});
                        if(dPos){
                            dPos.x(dimensionPosition.x);
                            dPos.y(dimensionPosition.y)
                        }
                    });

                    if(data.pictogram) {
                        var ma = new MediaAssetVM(data.pictogram.id);
                        ma.path(data.pictogram.name);
                        ma.webPath(data.pictogram.url);
                        ma.webThumbnailPath(data.pictogram.webThumbnailPath);
                        ma.previewImagePath(data.pictogram.url);
                        ma.previewImageWebPath(data.pictogram.url);
                        ma.previewImageWebThumbnailPath(data.pictogram.url);
                        ma.mimeType(data.pictogram.mimeType);
                        ma.size((data.pictogram.size / 1024).toFixed(2));
                        ma.rawMetadata(data.pictogram.rawMetadata);
                        vm.pictogramMA(ma);
                    }

                    if(data.template) {
                        var ma = new MediaAssetVM(data.template.id);
                        ma.path(data.template.name);
                        ma.webPath(data.template.url);
                        ma.webThumbnailPath(data.template.webThumbnailPath);
                        ma.previewImagePath(data.template.url);
                        ma.previewImageWebPath(data.template.url);
                        ma.previewImageWebThumbnailPath(data.template.url);
                        ma.mimeType(data.template.mimeType);
                        ma.size((data.template.size / 1024).toFixed(2));
                        ma.rawMetadata(data.template.rawMetadata);
                        vm.templateMA(ma);
                    }

                });
            });

            Dropzone.autoDiscover = false;

            $('.dropzone-teaser').livequery(function() {
                var form = $(this).get(0);
                var $form = $(form);

                var dzInitialized = $form.data('dz-init');

                if(!dzInitialized) {
                    var dz = new Dropzone(form, { url: '/api/v1/pictograms/' + self.pictogramId() + '/template/'});

                    dz.on("sending", function(file, xhr, formData) {
                        var activeStore = gc.app.activeStore();

                        if(!_.isEmpty(activeStore) && !_.isUndefined(activeStore.id)) {
                            xhr.setRequestHeader('X-CB-StoreContext', activeStore.id);
                        }
                    });

                    dz.on("success", function(file, data) {
                         if(data.data.mediaAsset) {
                            var templateImage = new MediaAssetVM(data.data.mediaAsset.id);
                             templateImage.path(data.data.mediaAsset.name);
                             templateImage.webPath(data.data.mediaAsset.url);
                             templateImage.webThumbnailPath(data.data.mediaAsset.webThumbnailPath);
                             templateImage.previewImagePath(data.data.mediaAsset.url);
                             templateImage.previewImageWebPath(data.data.mediaAsset.url);
                             templateImage.previewImageWebThumbnailPath(data.data.mediaAsset.url);
                             templateImage.mimeType(data.data.mediaAsset.mimeType);
                             templateImage.size((data.data.mediaAsset.size / 1024).toFixed(2));
                             templateImage.rawMetadata(data.data.mediaAsset.rawMetadata);
                             self.pictogramVM.templateMA(templateImage);
                        }

                        // Remove file from preview.
                        dz.removeFile(file);
                    });

                    $form.data('dz-init', true);
                }
            });

        },
        loadAttributes: function(vm){
            var self = this;

        },
        attached : function() {
            var self = this;

        },
        compositionComplete : function() {
        },
        detached : function() {
        },
        deactivate : function() {
        }
    }

    return PictogramBaseController;
});