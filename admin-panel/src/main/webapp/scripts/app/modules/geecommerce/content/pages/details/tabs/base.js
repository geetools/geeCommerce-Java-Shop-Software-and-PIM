define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-content', 'gc-content-layout', 'knockout-validation', 'gc-widget', 'gc-media-asset' ], function(app, ko, router, gc, contentAPI,
        contentLayoutAPI, validation, widgetAPI, mediaAssetAPI) {
    function ContentVM(contentId) {
        var self = this;
        self.contentId = contentId;

        self.type = ko.observable('PAGE');
        self.pageType = ko.observable('STATIC');

        self.expertMode = ko.observable(false);
        self.selectMode = ko.observable(false);
        self.simpleNodeSettings = {};

        self.name = ko.observableArray([]);
        self.description = ko.observableArray([]);

        self.layout = ko.observable('');

        self.isNew = ko.observable(false);
        self.key = ko.observable();

        if (contentId == 'new') {
            self.isNew(true);
        }

        self.previewProduct = ko.observable();

        self.previewURL = ko.computed(function() {
            if (self.isNew()) {
                return "#"
            }
            var reqCtx = gc.app.activeRequestCtx();
            if (self.type() == 'PARTIAL') {
                return 'https://' + reqCtx.urlPrefix + "/content/preview/" + contentId + '?xt=' + new Date().getTime();
            } else {
                return 'https://' + reqCtx.urlPrefix + "/content/page/" + contentId + '?xt=' + new Date().getTime();
            }
        });

        self.autogenerate = ko.observable(false);

        self.rewriteUrl = ko.observableArray([]).extend({
            validation : {
                async : true,
                validator : function(val, param, callback) {
                    var selfValid = this;
                    var updateModel = gc.app.newUpdateModel();
                    updateModel.field('rewriteUrl', val, true);
                    var res = contentAPI.isUrlUnique(self.contentId, updateModel).then(function(result) {
                        var notUnique = [];
                        var isUnique = true;
                        for ( var key in result.data.results) {
                            if (!result.data.results[key]) {
                                isUnique = false;
                                notUnique.push(key);
                            }
                        }
                        if (!isUnique) {
                            selfValid.message = 'Url should be unique (' + notUnique.join() + ')';
                        }
                        callback(isUnique);
                    });
                },
                message : 'Url should be unique',
                onlyIf : function() {
                    return !(self.autogenerate());
                }
            }
        });

        self.showFriendlyUrl = ko.computed(function() {
            return true;
            // TODO: how it is working now in products?
            var val = self.rewriteUrl();

            if (!val)
                return false;

            for (i = 0; i < val.length; ++i) {
                if (val[i].val && val[i].val.length > 0)
                    return true;
            }
            return false;
        });

        self.version = ko.observable();
    }

    // -----------------------------------------------------------------
    // Controller
    // -----------------------------------------------------------------
    function ContentBaseController() {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ContentBaseController)) {
            throw new TypeError("ContentBaseController constructor cannot be called as a function.");
        }

        this.app = gc.app;
        this.contentVM = ko.observable({});
        this.contentId = ko.observable();

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'saveData', 'activate');
    }

    ContentBaseController.prototype = {
        constructor : ContentBaseController,
        pageTitle : function() {
            var self = this;
            var title = 'app:modules.content.detailsTitle';
            return title;
        },
        pageDescription : 'app:modules.content.detailsSubtitle',
        contentLayoutChoice : ko.observableArray([]),
        gc : gc,
        saveData : function(context) {
            var self = this;

            var contentUpdateModel = gc.app.newUpdateModel();
            contentUpdateModel.field('key', self.contentVM.key());
            contentUpdateModel.field('type', self.contentVM.type());
            contentUpdateModel.field('pageType', self.contentVM.pageType());
            contentUpdateModel.field('name', self.contentVM.name(), true);
            contentUpdateModel.field('description', self.contentVM.description(), true);
            contentUpdateModel.field('previewProductId', self.contentVM.previewProduct());
            if (self.contentVM.layout())
                contentUpdateModel.field('layoutId', self.contentVM.layout());

            if (self.contentVM.isNew()) {
                contentAPI.createContent(contentUpdateModel).then(function(data) {

                    var updateModel = gc.app.newUpdateModel();
                    updateModel.field('rewriteUrl', self.contentVM.rewriteUrl(), true);
                    updateModel.field('auto', self.contentVM.autogenerate());
                    contentAPI.updateRewriteUrl(data.id, updateModel).then(function(urlRewriteData) {
                        /*
                         * contentAPI.getRewriteUrl(self.editVM.id()).then(function(data) { if(data.data.urlRewrite.requestURI){ self.editVM.rewriteUrl(data.data.urlRewrite.requestURI);
                         * self.editVM.autogenerate(false); } });
                         */
                        router.navigate('//content/details/' + data.id);
                        context.saved();
                    });

                });
            } else {
                contentAPI.updateContent(self.contentVM.contentId, contentUpdateModel).then(function(data) {
                    var updateModel = gc.app.newUpdateModel();
                    updateModel.field('rewriteUrl', self.contentVM.rewriteUrl(), true);
                    updateModel.field('auto', self.contentVM.autogenerate());
                    contentAPI.updateRewriteUrl(self.contentVM.contentId, updateModel).then(function(data) {
                        contentAPI.getRewriteUrl(self.contentVM.contentId).then(function(data) {
                            if (data.data.urlRewrite.requestURI) {
                                self.contentVM.rewriteUrl(data.data.urlRewrite.requestURI);
                                self.contentVM.autogenerate(false);
                            }
                        });
                    });

                    context.saved();
                });
            }

        },
        activate : function(data) {
            var self = this;
            self.contentId(data);
            var vm = new ContentVM(data);
            self.contentVM = vm;

            vm.simpleNodeSettings = self.simpleNodeSettings;

            var promises = [];
            
            promises.push(contentLayoutAPI.getContentLayouts().then(function(data) {

                var contentLayouts = []
                contentLayouts.push({
                    id : '',
                    text : function() {
                        return gc.app.i18n('app:common.choose', {}, gc.app.currentLang);
                    }
                });

                if (data.data.contentLayouts) {
                    data.data.contentLayouts.forEach(function(entry) {
                        gc.ctxobj.enhance(entry, [ 'label' ], 'any');
                        contentLayouts.push({
                            id : entry.id,
                            text : entry.label.i18n
                        });
                    });
                }

                self.contentLayoutChoice(contentLayouts);

                if (self.contentVM.type() == "PAGE" && self.contentLayoutChoice().length > 1) {
                    self.contentVM.layout(self.contentLayoutChoice()[1].id);
                }
            }));

            if(self.contentId() != "new") {
                promises.push(contentAPI.getContent(self.contentId()).then(function (data) {
                    vm.key(data.key);
                    vm.type(data.type);
                    vm.pageType(data.pageType || "STATIC");
                    vm.layout(data.layoutId);
                    vm.name(data.name);
                    vm.description(data.description || []);
                    vm.previewProduct(data.previewProductId);

                    contentAPI.getRewriteUrl(self.contentId()).then(function (data) {
                        if (data.data.urlRewrite.requestURI) {
                            vm.rewriteUrl(data.data.urlRewrite.requestURI);
                        }
                    });
                }));
            }

            return $.when.apply($, promises);
        },
        attached : function(view, parent) {
            var self = this;

            $('#contentBaseForm').addClass('save-button-listen-area');

            gc.app.onSaveEvent(function(context) {
                var id = $('#view-content-details>.tab-content>.active').attr('id');
               
                if(id == 'base') {
                    self.saveData(context);
                }
            });
        },
        detached : function() {
            var self = this;
            gc.app.clearSaveEvent();
        }
    }

    return ContentBaseController;
});