define([ 'durandal/app', 'knockout', 'plugins/router', 'gc/gc', 'gc-product', 'gc-attribute', 'gc-product/util' ], function(app, ko, router, gc, productAPI, attrAPI, productUtil) {

    function EditVM($root) {
        var self = this;
        self.$root = $root;

        self.isDirty = ko.observable();

        self.id = ko.observable();
        self.id2 = ko.observable();
        self.articleStatus = ko.observableArray([]);

        self.articleStatusIcon = ko.computed(function() {
            return productUtil.getProductStatusIcon(self.articleStatus(), self.$root.articleStatuses());
        });

        self.descriptionStatus = ko.observableArray();
        self.descriptionStatusLabel = ko.computed(function() {
            var lang = gc.app.currentLang();
            var activeContext = gc.app.sessionGet('activeContext');
            return productUtil.getProductDescriptionStatusLabel(self.descriptionStatus(), self.$root.descriptionStatuses());
        });

        self.descriptionStatusIcon = ko.computed(function() {
            return productUtil.getProductDescriptionStatusIcon(self.descriptionStatus(), self.$root.descriptionStatuses());
        });

        self.imageStatus = ko.observableArray();

        self.imageStatusLabel = ko.computed(function() {
            return productUtil.getProductImageStatusLabel(self.imageStatus());
        });

        self.imageStatusIcon = ko.computed(function() {
            return productUtil.getProductImageStatusIcon(self.imageStatus());
        });

        self.mainImageURL = ko.observable();
        self.isVariant = ko.observable(false);
        self.isVariantMaster = ko.observable(false);
        self.isProgramme = ko.observable(false);
        self.isBundle = ko.observable(false);

        self.showLinkToMaster = ko.computed(function() {
            return self.isVariant() && !self.isVariantMaster();
        });

        self.descriptionText = ko.computed(function() {
            if (self.isProgramme()) {
                return gc.app.i18n('app:modules.product.baseTabDescProgramme', {}, gc.app.currentUserLang);
            } else if (self.isBundle()) {
                return gc.app.i18n('app:modules.product.baseTabDescBundle', {}, gc.app.currentUserLang);
            } else if (self.isVariantMaster()) {
                return gc.app.i18n('app:modules.product.baseTabDescVariantMaster', {}, gc.app.currentUserLang);
            } else if (self.isVariant()) {
                return gc.app.i18n('app:modules.product.baseTabDescVariant', {}, gc.app.currentUserLang) + ' <a href="#/products/details/' + self.parentId() + '" target="_blank">' + self.parentName()
                        + '</a>.';
            } else {
                return gc.app.i18n('app:modules.product.baseTabDescStandardArticle', {}, gc.app.currentUserLang);
            }
        });

        self.parentId = ko.observable();
        self.parentName = ko.observable(gc.app.i18n('app:modules.product.article', {}, gc.app.currentUserLang));

        self.visible = ko.observableArray([]);

        self.visibleStatusIcon = ko.computed(function() {
            return productUtil.getProductVisibleIcon(self.visible());
        });

        self.visibleInProductList = ko.observableArray([]);
        self.saleable = ko.observableArray([]);
        self.includeInFeeds = ko.observableArray([]);
        self.special = ko.observableArray([]);
        self.sale = ko.observableArray([]);
        self.status = ko.observableArray([]);
        self.type = ko.observable();
        self.enabled = ko.computed({
            read : function() {
                var _status = ko.unwrap(self.status);
                return !_.isUndefined(_status) && self.status == 'ENABLED' ? true : false;
            },
            write : function(value) {
                if (value)
                    self.status('ENABLED');
                else
                    self.status('DISABLED');
            }
        });

        self.articleNumber = ko.observableArray([]);
        self.name = ko.observableArray([]);
        self.name2 = ko.observableArray();
        self.productGroup = ko.observableArray([]);
        self.programme = ko.observableArray([]);
        self.bundleGroup = ko.observableArray([]);

        self.ean = ko.observableArray();
        self.brand = ko.observableArray();
        self.supplier = ko.observableArray();
        self.manufacturer = ko.observableArray();

        // @TODO do friendly urls!!
        self.friendlyURL = ko.observableArray();
        self.isSystemURLChange = false;
        self.ignoreNextURLChange = false;
        self.showFriendlyURLWarning = ko.observable(false);
        self.showFriendlyURLSuccess = ko.observable(false);

        self.previewURL = ko.computed(function() {
            // var reqCtx = gc.app.reqCtxForActiveStore();
            //
            // var reviewUrl = gc.app.ctxVal(self.rewriteUrl(), gc.app.currentLang());
            // if(!reviewUrl){
            // reviewUrl = "/catalog/product/view/" + self.id();
            // }
            //
            // return 'https://' + reqCtx.urlPrefix + reviewUrl + '?xref=adm&xpage=preview&xt=' + new Date().getTime();
        });

        self.refreshURL = ko.computed(function() {
            // var reqCtx = gc.app.reqCtxForActiveStore();
            //
            // var reviewUrl = gc.app.ctxVal(self.rewriteUrl(), gc.app.currentLang());
            // if(!reviewUrl){
            // reviewUrl = "/catalog/product/view/" + self.id();
            // }
            //
            // return 'https://' + reqCtx.urlPrefix + reviewUrl + '?xref=adm&xpage=refresh&xt=' + new Date().getTime();
        });
    }

    // -----------------------------------------------------------------
    // Controller
    // -----------------------------------------------------------------
    function ProductBaseController(options) {

        // Make sure that this object is being called with the 'new' keyword.
        if (!(this instanceof ProductBaseController)) {
            throw new TypeError("ProductBaseController constructor cannot be called as a function.");
        }

        this.gc = gc;
        this.app = gc.app;
        this.subscriptions = [];
        this.productVM = {};

        this.productGroups = ko.observableArray([]);
        this.programmes = ko.observableArray([]);
        this.bundleGroups = ko.observableArray([]);
        this.articleStatuses = ko.observableArray([]);
        this.descriptionStatuses = ko.observableArray([]);
        this.editVM = new EditVM(this);

        // Solves the 'this' problem when a DOM event-handler is fired.
        _.bindAll(this, 'createProduct', 'cancelNewProduct', 'saveData', 'initOptions', 'activate');
    }

    ProductBaseController.prototype = {
        constructor : ProductBaseController,
        activate : function(data) {
            var self = this;

            self.initOptions();
            self.productVM = gc.app.sessionKGet('productVM');
            var vm = self.productVM();

            if (!vm.isNew()) {
                var productId = data;
                var prdData = ko.unwrap(vm.data());

                self.editVM.id(productId);
                self.editVM.type(prdData.type);
                self.editVM.status(prdData.status);
                self.editVM.visible(prdData.visible);
                self.editVM.visibleInProductList(prdData.visibleInProductList);
                self.editVM.saleable(prdData.saleable);
                self.editVM.includeInFeeds(prdData.includeInFeeds);
                self.editVM.special(prdData.special);
                self.editVM.sale(prdData.sale);

                self.editVM.isVariant(vm.isVariant());
                self.editVM.isVariantMaster(vm.isVariantMaster());
                self.editVM.isProgramme(vm.isProgramme());
                self.editVM.isBundle(vm.isBundle());
                self.editVM.parentId(prdData.parentId);

                if (vm.isVariant()) {
                    productAPI.getProduct(prdData.parentId).then(function(data) {
                        var prd = data;
                        var parentName = "";
                        var name = gc.attributes.find(prd.attributes, 'name').value;
                        var name2 = gc.attributes.find(prd.attributes, 'name2').value;
                        if (!_.isEmpty(name)) {
                            parentName += gc.ctxobj.closest(name);
                        }

                        if (!_.isEmpty(name2)) {
                            parentName += ' ' + gc.ctxobj.closest(name2);
                        }

                        self.editVM.parentName(parentName);
                    });
                }

                self.editVM.mainImageURL(gc.images.buildImageURL(prdData.mainImageURI, 100, 100, '/img/no-image.jpg'));

                self.editVM.id2(data.id2);
                self.editVM.articleNumber(gc.attributes.find(prdData.attributes, 'article_number').value);
                self.editVM.name(gc.attributes.find(prdData.attributes, 'name').value);
                self.editVM.name2(gc.attributes.find(prdData.attributes, 'name2').value);
                self.editVM.productGroup(gc.attributes.find(prdData.attributes, 'product_group').optionIds);
                self.editVM.programme(gc.attributes.find(prdData.attributes, 'programme').optionIds);
                self.editVM.bundleGroup(gc.attributes.find(prdData.attributes, 'bundle_group').optionIds);

                self.editVM.articleStatus(gc.attributes.find(prdData.attributes, 'status_article').xOptionIds);
                self.editVM.descriptionStatus(gc.attributes.find(prdData.attributes, 'status_description').xOptionIds);
                self.editVM.imageStatus(gc.attributes.find(prdData.attributes, 'status_image').attributeOptions);

                self.editVM.ean(gc.attributes.find(prdData.attributes, 'ean').value);
                self.editVM.brand(gc.attributes.find(prdData.attributes, 'brand').value);
                self.editVM.supplier(gc.attributes.find(prdData.attributes, 'supplier').value);
                self.editVM.manufacturer(gc.attributes.find(prdData.attributes, 'manufacturer').value);

                // We'll remove the system fallback URI first as ideally we'll want to add a SEF URI at some point.
                gc.ctxobj.unsetWhere(prdData.uri, undefined, undefined, undefined, /^\/catalog\/product\/view\/[\d]+/);
                self.editVM.friendlyURL(gc.ctxobj.clone(prdData.uri));

                var friendlyURL = self.editVM.friendlyURL();

                // Here we listen for possible changes from observables that are used for creating the search engine friendly URL.
                ko.computed(function() {
                    var isProgramme = self.editVM.isProgramme();
                    var isBundle = self.editVM.isBundle();
                    var brand = self.editVM.brand();
                    var name = self.editVM.name();
                    var productGroup = self.editVM.productGroup();
                    var programme = self.editVM.programme();
                    var bundleGroup = self.editVM.bundleGroup();
                    var defaultLang = gc.app.defaultLanguage();

                    // No data to work with here, so we just return.
                    if((_.isEmpty(productGroup) && _.isEmpty(programme) && _.isEmpty(bundleGroup)) || _.isEmpty(name))
                        return;

                    var requestContexts = gc.app.availableRequestContexts();
                    var activeContext = gc.app.sessionGet('activeContext');

                    if(isProgramme === true || isBundle === true) {

                    } else {
                        // We do not want to override already saved values as they may have been indexed by search engines.
                        // Therefore we make the initial check using the original value form the server (prdData.uri).
                        var globalURI = gc.ctxobj.plain(prdData.uri);
                        var isUpdateObservable = false;

                        // Only create a new global URL if one has not previously already been saved.
                        if(_.isEmpty(globalURI)) {
                            var productGroups = ko.unwrap(self.productGroups);
                            var i18nProductGroup = gc.contexts.textValue(productGroup[0], productGroups, defaultLang);
                            var i18nName = gc.ctxobj.val(name, defaultLang, undefined, activeContext);
                            var brandName = gc.ctxobj.closest(brand, defaultLang, activeContext);

                            if(!_.isEmpty(i18nProductGroup) && !_.isEmpty(i18nName) && !_.isEmpty(brandName)) {
                                // When we have all the required information, we can create the new URL.
                                globalURI = productUtil.newURI(defaultLang, i18nProductGroup, brandName, i18nName);

                                if(!_.isEmpty(globalURI)) {
                                    gc.ctxobj.set(friendlyURL, undefined, globalURI);
                                    isUpdateObservable = true;
                                }
                            }
                        }

                        // Now we'll attempt to do the same for all request contexts (websites).
                        if(!_.isEmpty(requestContexts)) {
                            for (var i = 0; i < requestContexts.length; i++) {
                                var reqCtx = requestContexts[i];

                                if(reqCtx !== undefined && reqCtx.id !== undefined && !_.isEmpty(reqCtx.language)) {
                                    // We do not want to override already saved values as they may have been indexed by search engines.
                                    // Therefore we make the initial check using the original value form the server (prdData.uri).
                                    var reqCtxURI = gc.ctxobj.val(prdData.uri, undefined, undefined, reqCtx);
                                    var originalGlobalURI = gc.ctxobj.plain(prdData.uri);
                                    var reqCtxLang = reqCtx.language;
                                    var sameLang = reqCtxLang === defaultLang;
                                    var savedUriAlreadyExists = sameLang === true && !_.isEmpty(originalGlobalURI);

                                    // Only create a new URL if one does not exist in the database yet for this request context.
                                    // The URL is also considered to exist when a global value exists and the global
                                    // language is equal to this request context's language.
                                    if(_.isEmpty(reqCtxURI) && !savedUriAlreadyExists) {
                                        var productGroups = ko.unwrap(self.productGroups);
                                        var i18nProductGroup = gc.contexts.textValue(productGroup[0], productGroups, reqCtxLang);
                                        var i18nName = gc.ctxobj.val(name, reqCtxLang, undefined, activeContext);
                                        var brandName = gc.ctxobj.closest(brand, reqCtxLang, activeContext);

                                        if(!_.isEmpty(i18nProductGroup) && !_.isEmpty(i18nName) && !_.isEmpty(brandName)) {
                                            // When we have all the required information, we can create the new URL for this request-context.
                                            var reqCtxURI = productUtil.newURI(reqCtxLang, i18nProductGroup, brandName, i18nName);

                                            if(globalURI !== reqCtxURI) {
                                                gc.ctxobj.set(friendlyURL, undefined, reqCtxURI, reqCtx, globalURI, reqCtxURI);
                                                isUpdateObservable = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if(isUpdateObservable === true) {
                        self.editVM.isSystemURLChange = true;
                        self.editVM.friendlyURL(friendlyURL);
                    }
                }, self);

                self.editVM.isSystemURLChange = false;
                self.editVM.friendlyURL.subscribe(function(newVal) {
                    // Reset again so that the following change is not longer ignored.
                    if(self.editVM.ignoreNextURLChange === true) {
                        self.editVM.ignoreNextURLChange = false;
                        return;
                    }

                    // Make request to server to check validity of URL.
                  var reqData = gc.app.newUpdateModel();
                  reqData.variable('friendlyURL', self.editVM.friendlyURL(), true);
                  productAPI.getValidFriendlyURL(self.editVM.id(), reqData).then(function(data) {

                      // If the new URL and the URL returned from the server are equal, no change was made on the server side.
                      if(gc.ctxobj.equals(newVal, data)) {
                          // Show the user icons to indicate that has change has been accepted.
                          if(!self.editVM.isSystemURLChange) {
                              self.editVM.showFriendlyURLSuccess(true);
                              self.editVM.showFriendlyURLWarning(false);
                          } else {
                              // Do not bother to show icons when it is a system change.
                              self.editVM.isSystemURLChange = false;
                              self.editVM.showFriendlyURLSuccess(false);
                              self.editVM.showFriendlyURLWarning(false);
                          }
                          // If the new URL and the URL returned from the server are NOT equal, changes have been made
                          // on the server side to make the URL unique.
                      } else {
                          if(!self.editVM.isSystemURLChange) {
                              // Show the user a warning icon to indicate that the entered URL has been changed by the server.
                              self.editVM.showFriendlyURLWarning(true);
                              self.editVM.showFriendlyURLSuccess(false);
                              self.editVM.ignoreNextURLChange = true;
                              self.editVM.friendlyURL(data);
                          } else {
                              // Do not bother to show icons when it is a system change.
                              self.editVM.isSystemURLChange = false;
                              self.editVM.showFriendlyURLSuccess(false);
                              self.editVM.showFriendlyURLWarning(false);
                              self.editVM.ignoreNextURLChange = true;
                              self.editVM.friendlyURL(data);
                          }
                      }
                  });
                });
            }

            self.subscriptions.push(gc.app.channel.subscribe('product.data.refreshed', function(data) {
                self.editVM.articleStatus(gc.attributes.find(data.attributes, 'status_article').xOptionIds);
                self.editVM.descriptionStatus(gc.attributes.find(data.attributes, 'status_description').xOptionIds);
                self.editVM.imageStatus(gc.attributes.find(data.attributes, 'status_image').attributeOptions);
                self.editVM.mainImageURL(gc.images.buildImageURL(data.mainImageURI, 100, 100, '/img/no-image.jpg'));
            }));
        },
        initOptions : function() {
            var self = this;

            // var attrStatusArticle = gc.app.dataGet('attr:status_article');
            // var attrStatusDescription = gc.app.dataGet('attr:status_description');
            // var attrProductGroup = gc.app.dataGet('attr:product_group');
            // var attrProgramme = gc.app.dataGet('attr:programme');

            var attrStatusArticle;
            var attrStatusDescription;
            var attrProductGroup;
            var attrProgramme;
            var attrBundleGroup;

            attrAPI.getAttributes('product', {
                filter : {
                    code : 'status_article'
                }
            }).then(function(data) {
                if(_.isEmpty(data.data.attributes) || data.data.attributes.length > 1 || data.data.attributes[0].code != 'status_article') {
                    console.log('An error occured because either no "status_article" attribute could be found or more than 1 was returned.');
                } else {
                    attrStatusArticle = data.data.attributes[0];
                }
            });

            attrAPI.getAttributes('product', {
                filter : {
                    code : 'status_description'
                }
            }).then(function(data) {
                if(_.isEmpty(data.data.attributes) || data.data.attributes.length > 1 || data.data.attributes[0].code != 'status_description') {
                    console.log('An error occured because either no "status_description" attribute could be found or more than 1 was returned.');
                } else {
                    attrStatusDescription = data.data.attributes[0];
                }
            });

            attrAPI.getAttributes('product', {
                filter : {
                    code : 'product_group'
                }
            }).then(function(data) {
                if(_.isEmpty(data.data.attributes) || data.data.attributes.length > 1 || data.data.attributes[0].code != 'product_group') {
                    console.log('An error occured because either no "product_group" attribute could be found or more than 1 was returned.');
                } else {
                    attrProductGroup = data.data.attributes[0];
                }
            });

            attrAPI.getAttributes('product', {
                filter : {
                    code : 'programme'
                }
            }).then(function(data) {
                if(_.isEmpty(data.data.attributes) || data.data.attributes.length > 1 || data.data.attributes[0].code != 'programme') {
                    console.log('An error occured because either no "programme" attribute could be found or more than 1 was returned.');
                } else {
                    attrProgramme = data.data.attributes[0];
                }
            });

            attrAPI.getAttributes('product', {
                filter : {
                    code : 'bundle_group'
                }
            }).then(function(data) {
                if(_.isEmpty(data.data.attributes) || data.data.attributes.length > 1 || data.data.attributes[0].code != 'bundle_group') {
                    console.log('An error occured because either no "bundle_group" attribute could be found or more than 1 was returned.');
                } else {
                    attrBundleGroup = data.data.attributes[0];
                }
            });

            if (!_.isEmpty(attrStatusArticle) && !_.isEmpty(attrStatusArticle.options)) {
                _.forEach(attrStatusArticle.options, function(option) {
                    self.articleStatuses.push({
                        id : option.id,
                        text : option.label.i18n,
                        code : gc.ctxobj.global(option.label)
                    });
                });
            }

            if (!_.isEmpty(attrStatusDescription) && !_.isEmpty(attrStatusDescription.options)) {
                _.forEach(attrStatusDescription.options, function(option) {
                    self.descriptionStatuses.push({
                        id : option.id,
                        text : option.label.i18n,
                        code : gc.ctxobj.global(option.label)
                    });
                });
            }

            if (!_.isEmpty(attrProductGroup) && !_.isEmpty(attrProductGroup.options)) {
                self.productGroups.push({
                    id : '',
                    text : function(lang) {
                        if(lang === undefined) {
                            return gc.app.i18n('app:modules.product.newProductGroupSelectTitle', {}, gc.app.currentLang);
                        } else {
                            return gc.app.i18n('app:modules.product.newProductGroupSelectTitle', {}, lang);
                        }
                    }
                });
                _.forEach(attrProductGroup.options, function(option) {
                    if (option && option.id && option.label) {
                        self.productGroups.push({
                            id : option.id,
                            text : option.label.i18n
                        });
                    }
                });
            }

            if (!_.isEmpty(attrProgramme) && !_.isEmpty(attrProgramme.options)) {
                self.programmes.push({
                    id : '',
                    text : function() {
                        return gc.app.i18n('app:modules.product.newProgrammeSelectTitle', {}, gc.app.currentLang);
                    }
                });
                _.forEach(attrProgramme.options, function(option) {
                    if (option && option.id && option.label) {
                        self.programmes.push({
                            id : option.id,
                            text : option.label.i18n
                        });
                    }
                });
            }

            if (!_.isEmpty(attrBundleGroup) && !_.isEmpty(attrBundleGroup.options)) {
                self.bundleGroups.push({
                    id : '',
                    text : function() {
                        return gc.app.i18n('app:modules.product.newBundleGroupSelectTitle', {}, gc.app.currentLang);
                    }
                });
                _.forEach(attrBundleGroup.options, function(option) {
                    if (option && option.id && option.label) {
                        console.log("----")
                        console.log(option.label.i18n)
                        self.bundleGroups.push({
                            id : option.id,
                            text : option.label.i18n
                        });
                    }
                });
            }
        },
        createProduct : function(view, parent, loader) {
            var self = this;

            gc.app.initProgressBar();
            gc.app.updateProgressBar(25);

            var vm = self.productVM();
            var newProduct = {};

            var attrProductGroup = gc.app.dataGet('attr:product_group');
            var attrProgramme = gc.app.dataGet('attr:programme');
            var attrBundleGroup = gc.app.dataGet('attr:bundle_group');

            if (vm.isProgramme() && !_.isUndefined(self.editVM.programme())) {
                newProduct.type = 'PROGRAMME';
                newProduct.attributes = [ {
                    attributeId : attrProgramme.id,
                    "optionIds" : self.editVM.programme()
                } ];
            } else if (vm.isVariantMaster() && !_.isUndefined(self.editVM.productGroup())) {
                newProduct.type = 'VARIANT_MASTER';
                newProduct.attributes = [ {
                    attributeId : attrProductGroup.id,
                    "optionIds" : self.editVM.productGroup()
                } ];
            } else if (vm.isBundle() && !_.isUndefined(self.editVM.bundleGroup())) {
                newProduct.type = 'BUNDLE';
                newProduct.attributes = [ {
                    attributeId : attrBundleGroup.id,
                    "optionIds" : self.editVM.bundleGroup()
                } ];
            } else if (!_.isUndefined(self.editVM.productGroup())) {
                newProduct.type = 'PRODUCT';
                newProduct.attributes = [ {
                    attributeId : attrProductGroup.id,
                    "optionIds" : self.editVM.productGroup()
                } ];
            }

            productAPI.createProduct(newProduct).then(function(data) {
                gc.app.updateProgressBar(50);

                // Reset the loading image.
                loader.reset();

                self.editVM.id(data.id);
                self.editVM.type(data.type);
                self.editVM.status(data.status);
                self.editVM.visible(data.visible);
                self.editVM.visibleInProductList(data.visibleInProductList);
                self.editVM.saleable(data.saleable);
                self.editVM.includeInFeeds(data.includeInFeeds);
                self.editVM.special(data.special);
                self.editVM.sale(data.sale);
                self.editVM.productGroup(gc.attributes.find(data.attributes, 'product_group').optionIds);
                self.editVM.programme(gc.attributes.find(data.attributes, 'programme').optionIds);
                self.editVM.bundleGroup(gc.attributes.find(data.attributes, 'bundle_group').optionIds);

                gc.app.updateProgressBar(100);

                gc.app.resetProgressBar();

                gc.app.channel.publish('product.created', data);

                gc.app.flashMessage('success', gc.app.i18n('app:modules.product.fmCreateSuccessTitle'), gc.app.i18n('app:modules.product.fmCreateSuccessBody', {productName: 'Some name - 12345', productURI: 'Some  URI ---'}));
            });
        },
        cancelNewProduct : function() {

        },
        saveData : function(context) {
            var self = this;

            var vm = self.productVM();
            var updateModel = gc.app.newUpdateModel();

            updateModel.context(vm.contextModel());
            updateModel.saveAsNewCopy(gc.app.saveMakeCopy());

            updateModel.attr('article_number', self.editVM.articleNumber());
            updateModel.attr('name', self.editVM.name());
            updateModel.attr('name2', self.editVM.name2());
            updateModel.field('visible', self.editVM.visible(), true);

            updateModel.attr('ean', self.editVM.ean());
            updateModel.attr('brand', self.editVM.brand());
            updateModel.attr('supplier', self.editVM.supplier());
            updateModel.attr('manufacturer', self.editVM.manufacturer());

            updateModel.field('visibleInProductList', self.editVM.visibleInProductList(), true);
            updateModel.field('saleable', self.editVM.saleable(), true);
            updateModel.field('includeInFeeds', self.editVM.includeInFeeds(), true);
            updateModel.variable('friendlyURL', self.editVM.friendlyURL(), true);
            /* updateModel.field('showCartButton', self.editVM.showCartButton(), true); */

            if (vm.isProgramme()) {
                updateModel.field('special', self.editVM.special(), true);
                updateModel.field('sale', self.editVM.sale(), true);
            }

            if (vm.isProgramme() && !_.isEmpty(self.editVM.programme())) {
                updateModel.options('programme', self.editVM.programme());
            }

            if (vm.isBundle() && !_.isEmpty(self.editVM.bundleGroup())) {
                updateModel.options('bundle_group', self.editVM.bundleGroup());
            }

            if (!vm.isProgramme() && !vm.isBundle() && !_.isEmpty(self.editVM.productGroup())) {
                updateModel.options('product_group', self.editVM.productGroup());
            }

            if (vm.isCategorized() && !_.isEmpty(self.editVM.articleStatus())) {
                updateModel.xOptions('status_article', self.editVM.articleStatus());
            }

            self.addDynamicFormAttributes(updateModel);

             productAPI.updateProduct(self.editVM.id(), updateModel).then(function(data) {

                 // Update progress bar.
                 gc.app.updateProgressBar(50);

                 if(gc.app.saveMakeCopy() === true) {
                     context.saved(function() {
                         router.navigate('//products');
                     });
                 } else {
                     productAPI.getProduct(self.editVM.id()).then(function(data) {
                         gc.app.updateProgressBar(75);

                         vm.data(data);

                         self.editVM.descriptionStatus(gc.attributes.find(data.attributes, 'status_description').xOptionIds);
                         self.editVM.imageStatus(gc.attributes.find(data.attributes, 'status_image').attributeOptions);

                         self.editVM.isSystemURLChange = true;
                         self.editVM.friendlyURL(data.uri);

                         context.saved(function() {
                             // Optionally do something after progress bar is complete.
                         });
                     });
                 }
             });
        },
        addDynamicFormAttributes: function(updateModel) {
            var self = this;
            var vm = self.productVM();
            var formAttributes = ko.gc.unwrap(vm.formAttributeValues());

console.log('------------------>>> formAttributes ', formAttributes);

            for (var i = 0; i < formAttributes.length; i++) {
                var formAttributeArray = formAttributes[i];

                for (var j = 0; j < formAttributeArray.length; j++) {
                    var attrVal = ko.gc.unwrap(formAttributeArray[j]);

                    if(attrVal.isEditable /* && attrVal.hasChanged */) {
                        var isOptOut = gc.ctxobj.plain(attrVal.optOut);

                        if(!_.isUndefined(isOptOut)) {
                            updateModel.optOut(attrVal.code, attrVal.optOut);
                        }

                        if(attrVal.isOption) {
                            var foundPrdAttr = _.findWhere(vm.data().attributes, { attributeId : attrVal.attributeId });

                            // Allow resetting of value if product already has attribute and new value is empty.
                            if((!_.isUndefined(attrVal.value) && !_.isEmpty(attrVal.value)) || !_.isUndefined(foundPrdAttr)) {
                                updateModel.options(attrVal.code, attrVal.value);
                            }
                        } else {
                            updateModel.attr(attrVal.code, attrVal.value);
                        }
                    }
                }
            }
        },
        attached : function(view, parent) {
            var self = this;
            var vm = self.productVM();

            $('#productBaseForm').addClass('save-button-listen-area');

            gc.app.onSaveEvent(function(context) {
                self.saveData(context);
            }, vm.contextModel());
        },
        detached : function() {
            var self = this;

            gc.app.unsubscribeAll(self.subscriptions);
            gc.app.clearSaveEvent();

// self.editVM.articleStatusIcon.dispose();
// self.editVM.descriptionStatusLabel.dispose();
// self.editVM.descriptionStatusIcon.dispose();
// self.editVM.showLinkToMaster.dispose();
// self.editVM.descriptionText.dispose();
// self.editVM.imageStatusLabel.dispose();
// self.editVM.imageStatusIcon.dispose();
// self.editVM.visibleStatusIcon.dispose();
// self.editVM.enabled.dispose();
// self.editVM.showFriendlyUrl.dispose();
// self.editVM.previewURL.dispose();
// self.editVM.friendlyURL.dispose();
        }
    }

    return ProductBaseController;
});
