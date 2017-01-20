define([ 'plugins/router', 'durandal/app', 'postal', 'knockout', 'i18next', 'gc/gc', 'gc/configuration', 'gc/session', 'gc/app-data', 'gc/updateModel', 'gc/contextModel', 'gc-attribute', 'gc-attribute-tabs',
        'gc-product', 'numeral' ], function(router, app, postal, ko, i18next, gc, Configuration, Session, AppData, UpdateModel, ContextModel, attrAPI, attrTabsAPI, productAPI, numeral) {

    "use strict";

    function App(options) {

        if (!(this instanceof App)) {
            throw new TypeError("App constructor cannot be called as a function.");
        }

        var self = this;

        this.options = options || {};

        // Initialize configuration.
        this.configuration = new Configuration();
        this.conf = this.configuration.map; // Shortcut for
        // HTML-templates.

        // Initialize session.
        this.session = new Session();
        this.sess = this.session.map; // Shortcut for HTML-templates.

        // Initialize app-data.
        this.appData = new AppData();
        this.data = this.appData.map; // Shortcut for HTML-templates.

        // Set the initial language.
        this.sess['selectedLanguage'] = ko.observable(options.defaultLanguage);
        this.sess['selectedLanguage'].subscribe(function(lang) {
            self.triggerLocaleElements(lang);
            app.trigger('language:change', lang);
        });

        this.sess['userLanguage'] = ko.observable(options.defaultLanguage);
        this.sess['userLanguage'].subscribe(function(lang) {
            // Update the i18next messages.
            i18next.setLng(lang, function() {
                $('[data-i18n]').i18n();
            });

            numeral.language(lang);            
            self.cookiePut('lng', lang, (365 * 10));
        });

        this.sess['activeContext'] = ko.observable({});
        this.sess['activeContextId'] = ko.observable();
        this.sess['activeContextId'].subscribe(function(activeContextId) {
            if (!_.isEmpty(activeContextId)) {
                var ctxMap = self.conf['contextMap']();
                var activeContext = ctxMap[activeContextId];

                if (!_.isEmpty(activeContext)) {
                    self.sess['activeContext'](activeContext);
                    app.trigger('context:change', activeContext);
                    $('.ctx-active-icon').show();
                }
            } else {
                self.sess['activeContext']({});
                app.trigger('context:change', {});
                $('.ctx-active-icon').hide();
            }
        });

        this.sess['activeContext'].subscribe(function(activeCtx) {
            console.log('NEEEEEEEEEEEEEEW active CTX !!!! ', activeCtx);
            
            if(activeCtx.scope == 'global')
                return;
            
            var currentLang = self.currentLang();
            var ctxLang;
            var langCount = 0;
            
            if(activeCtx.scope == 'merchant') {
                if (activeCtx.stores) {
                    for (var s = 0; s < activeCtx.stores.length; s++) {
                        var store = activeCtx.stores[s];
                        
                        if (store.requestContexts) {
                            for (var r = 0; r < store.requestContexts.length; r++) {
                                var reqCtx = store.requestContexts[r];

                                if(ctxLang === undefined || ctxLang !== reqCtx.language)
                                    langCount++;
                                
                                ctxLang = reqCtx.language;
                            }
                        }
                    }
                }
            } else if(activeCtx.scope == 'store') {
                if (activeCtx.requestContexts) {
                    for (var r = 0; r < activeCtx.requestContexts.length; r++) {
                        var reqCtx = activeCtx.requestContexts[r];

                        if(ctxLang === undefined || ctxLang !== reqCtx.language)
                            langCount++;
                        
                        ctxLang = reqCtx.language;
                    }
                }
            } else if(activeCtx.scope == 'request_context') {
                langCount = 1;
                ctxLang = activeCtx.language;
            }
            
            console.log('???????????????????????????????? ', langCount, ctxLang, currentLang);
            
            if(langCount === 1 && ctxLang !== currentLang) {
                self.sess.selectedLanguage(ctxLang);
            }
        });
        
        this.sess['activeSession'] = ko.observable({});

        this.pageTitle = ko.observable('Dashboard');
        this.pageDescription = ko.observable('Overview and latest statistics');

        this.showTitle = ko.observable(true);

        this.pageTitle.subscribe(function(newValue) {
            self.showTitle(true);
        });

        this.i18nPageTitle = ko.computed(function() {
            var title = self.pageTitle();
            self.sess['activeContext']();

            if (title.startsWith('app:')) {
                var lang = ko.unwrap(self.sess['userLanguage']);
                return i18next.t(title, {
                    lng : lang
                });
            } else {
                return title;
            }
        });

        this.i18nPageDescription = ko.computed(function() {
            var desc = self.pageDescription();

            if (desc.startsWith('app:')) {
                var lang = ko.unwrap(self.sess['userLanguage']);
                return i18next.t(desc, {
                    lng : lang
                });
            } else {
                return desc;
            }
        }, this);

        this.isContextSelected = function() {
            var self = this;
            var activeContext = self.sess['activeContext']();
            return !_.isEmpty(activeContext) && !_.isEmpty(activeContext.id);
        };

        this.activeContext = function() {
            var self = this;

            if (self.isContextSelected()) {
                return self.sess['activeContext']();
            } else {
                return;
            }
        };

        this.showToolbar = ko.observable(false);

        this.setupChannels();
        this.setupEditListener();
        this.saveSubscriptions = ko.observableArray([]);
        this.saveInterval;
        this.saveContextModel = ko.observable();
        this.saveMakeCopy = ko.observable(false);
        this.showSaveIcon = ko.observable(false);
        this.showSaveModal = ko.observable(false);
        this.showFlashMessageModal = ko.observable(false);
        this.showQueryBuilderModal = ko.observable(false);
        this.showAttrBatchUpdaterModal = ko.observable(false);
        this.flashMsg;
        this._toolbarSaveSubscription = null;
        this.navToolbar = ko.observable(false);
        
        _.bindAll(this, 'setupChannels', 'defaultLanguage', 'secondaryLanguage', 'i18n', 'message', 'onToolbarEvent', 'registerFormWithToolbar', 'setupEditListener', 'triggerLocaleElements', 'currentLang',
                'currentUserLang', 'alertInfo', 'preloadData', 'confPut', 'confGet', 'confRemove', 'sessionPut', 'sessionGet', 'sessionRemove', 'cookiePut', 'cookieGet', 'dataPut', 'dataGet',
                'dataRemove', 'startOnChangeListener', 'stopOnChangeListener', 'saveData', 'onSaveEvent', 'triggerSaveAlert', 'cancelSaveData', 'initProgressBar', 'updateProgressBar', 'resetProgressBar', 'flashMessage',
                'availableContexts', 'contextMap', 'availableMerchants', 'availableStores', 'availableRequestContexts', 'setNavToolbar', 'unsetNavToolbar');
    }

    App.prototype = {
        constructor : App,
        remainingTime : ko.observable(),
        setupChannels : function() {
            this.channel = postal.channel('app');
        },
        unsubscribeAll : function(subscriptions) {
            if (!subscriptions)
                return;

            if (_.isArray(subscriptions)) {
                for (var i = 0; i < subscriptions.length; i++) {
                    if (subscriptions[i]) {
                        subscriptions[i].unsubscribe();
                    }
                }
            } else {
                subscriptions.unsubscribe();
            }
        },
        defaultLanguage : function() {
            return this.options.defaultLanguage;
        },
        secondaryLanguage : function() {
            return this.options.secondaryLanguage;
        },
        message : function(key, lang, opts) {
            var self = this;
            
            if(_.isEmpty(key) || _.isEmpty(lang))
                return undefined;

            if(_.isFunction(lang)) {
                lang = lang();
            }
            
            var options = _.extend({}, {
                lng : lang
            }, opts);
            
            return i18next.t(key, options);
        },
        i18n : function(key, opts, langCallback) {
            var self = this;
            var callback = undefined;

            return ko.unwrap(ko.pureComputed(function() {
                if (!_.isUndefined(langCallback) && (_.isFunction(langCallback) || ko.isObservable(langCallback))) {
                    callback = langCallback;
                } else {
                    callback = self.currentUserLang;
                }
                
                var lang = callback();
                var options = _.extend({}, {
                    lng : lang
                }, opts);
                
                return i18next.t(key, options);
            }), self);
        },
        onToolbarEvent : function(options) {
            options = options || {};

            // Only one form can subscribe to the toolbar-save-button at a time.
            if (!_.isUndefined(this._toolbarSaveSubscription) && !_.isNull(this._toolbarSaveSubscription)) {
                this._toolbarSaveSubscription.unsubscribe();
            }

            // Call callback-handler function of last subscriber.
            this._toolbarSaveSubscription = gc.app.channel.subscribe('toolbar.save.event', function() {
                options.save();
            });
        },
        registerFormWithToolbar : function(containerId) {
            $(document).on('change', containerId + ' :input', function(evt) {
                $('#info-toolbar-outer').fadeIn(600);
            });
        },
        setupEditListener : function() {
            // Listen for changes on input fields so that we can display
            // the
            // toolbar accordingly.
            $(document).on('change', ':input', function(evt) {
                $('#info-toolbar-outer').fadeIn(600);
            });

            // Publish to the 'toolbar.save.event' topic to inform subscriber
            // that the save-button has been clicked.
            $(document).on('click', '#info-toolbar-outer [data-btn-event="save"]', function(evt) {
                gc.app.channel.publish('toolbar.save.event', $(this));
            });

            // If the user clicks cancel, we just hide the toolbar for now.
            $(document).on('click', '#info-toolbar-outer [data-btn-event="cancel"]', function(evt) {
                $('#info-toolbar-outer').fadeOut(300);
            });
        },
        alertInfo : function(title, message, titleArgs, messageArgs) {
            var self = this;

            var html = '<div class="alert alert-info">' + '<button class="close" aria-hidden="true" data-dismiss="alert" type="button">×</button>' + '<i class="fa fa-exclamation-circle"></i>'
                    + '<strong>' + self.i18n(title, titleArgs) + '</strong>' + '<span>' + self.i18n(message, messageArgs) + '</span>' + '</div>';

            return html;
        },
        flashMessage : function(status, title, message) {
            var self = this;
            var flashMsg = {title: title, message: message};
            
            if(status == 'success') {
                flashMsg.class = 'fa fa-check-circle success-icon';
            } else if(status == 'warn')  {
                flashMsg.class = 'fa fa-exclamation-triangle warn-icon';
            } else if(status == 'error')  {
                flashMsg.class = 'fa fa-times-circle error-icon';
            } else {
                flashMsg.class = 'fa fa-info-circle info-icon';
            }
            
            self.flashMsg = flashMsg;
            self.showFlashMessageModal(true);
            
            setTimeout(function() {
                self.showFlashMessageModal(false);
                self.flashMsg = undefined;
            }, 30000);
        },
        startOnChangeListener : function() {
            var self = this;

            $(document).on('change', '.save-button-listen-area :input, .save-button-listen-area textarea', function(evt) {
                self.triggerSaveAlert(evt);
            });
            
            self.showSaveIcon(true);
        },
        stopOnChangeListener : function(displayIcon) {
            var self = this;
            var displayIcon = displayIcon || false;
            
            self.showSaveIcon(displayIcon);
            
            $(document).off('change', '.save-button-listen-area :input, .save-button-listen-area textarea');
            
            if (self.saveInterval) {
                clearInterval(self.saveInterval);
                self.saveInterval = false;
            }
        },
        triggerSaveAlert : function(event) {
            var self = this;
            
            $('#save-data-container>a>i').addClass('active-save-icon');
            $('.save-data-active-icon').show();
            
            if (!self.saveInterval) {
                var interval = setInterval(function() {
                    $('.save-data-icon, .save-data-active-icon').fadeOut(500);
                    $('.save-data-icon, .save-data-active-icon').fadeIn(500);
                }, 3000);

                self.saveInterval = interval;
            }
        },
        onSaveEvent : function(callback, ctxModel) {
            var self = this;
            self.saveSubscriptions.push(gc.app.channel.subscribe('save-event', callback));
            self.saveContextModel(ctxModel);
            self.startOnChangeListener();
        },
        clearSaveEvent: function() {
            var self = this;
            self.stopOnChangeListener();
            self.unsubscribeAll(self.saveSubscriptions());
            self.saveContextModel(false);            
        },
        saveData : function(shellModel, event) {
            var self = this;

            self.initProgressBar();
            self.updateProgressBar(25);
            self.stopOnChangeListener(true);
            
            if (self.saveInterval) {
                clearInterval(self.saveInterval);
                self.saveInterval = false;
            }

            // Small hack as the interval confuses things by setting the display="inline" style.
            $('.save-data-active-container>i').addClass('save-data-active-icon-zero');
            $('.save-data-active-container>i').removeClass('save-data-active-icon');
            $('.save-data-active-container>i').css('display', '');

            setTimeout(function() {
                $('.save-data-active-container>i').removeClass('save-data-active-icon-zero');
                $('.save-data-active-container>i').addClass('save-data-active-icon');
                $('.save-data-active-container>i').css('display', '');
            }, 3000);

            $('#save-data-container>a>i').removeClass('active-save-icon');
            $('.save-data-active-icon').hide();

            if (self.saveSubscriptions().length > 0) {
                $('#save-data-container>a>i').hide();
                $('#save-data-processing-icon').show();

                gc.app.channel.publish('save-event', {
                    saved : function(callback) {
                        self.updateProgressBar(100);
                        
                        setTimeout(function() {
                            $('#save-data-processing-icon').hide();
                            $('#save-data-container>a>i').show();
                            self.startOnChangeListener();
                        }, 300);

                        setTimeout(function() {
                            self.showSaveModal(false);
                            self.saveMakeCopy(false);
                            
                            if(!_.isUndefined(callback) && _.isFunction(callback)) {
                                callback();
                            }
                        }, 1500);
                        
                        self.resetProgressBar();
                    },
                    event: event
                });
            }
        },
        cancelSaveData : function(shellModel, event) {
            var self = this;

            self.stopOnChangeListener(true);
            
            if (self.saveInterval) {
                clearInterval(self.saveInterval);
                self.saveInterval = false;
            }

            // Small hack as the interval confuses things by setting the display="inline" style.
            $('.save-data-active-container>i').addClass('save-data-active-icon-zero');
            $('.save-data-active-container>i').removeClass('save-data-active-icon');
            $('.save-data-active-container>i').css('display', '');

            setTimeout(function() {
                $('.save-data-active-container>i').removeClass('save-data-active-icon-zero');
                $('.save-data-active-container>i').addClass('save-data-active-icon');
                $('.save-data-active-container>i').css('display', '');
            }, 3000);

            $('#save-data-container>a>i').removeClass('active-save-icon');
            $('.save-data-active-icon').hide();
            
            self.startOnChangeListener();
        },
        initProgressBar : function() {
            $('.save-progress-bar.progress .progress-bar').removeClass('progress-bar-hidden');
        },
        updateProgressBar : function(percentCompleted) {
            $('.save-progress-bar.progress .progress-bar').attr('aria-valuenow', percentCompleted).css('width', percentCompleted + '%');            
        },
        resetProgressBar : function() {
            var self = this;
            
            setTimeout(function() {
                $('.save-progress-bar.progress .progress-bar').addClass('progress-bar-success');
            }, 1000);
            
            setTimeout(function() {
                $('.save-progress-bar.progress .progress-bar').addClass('progress-bar-hidden');
                
                self.updateProgressBar(0);
                $('.save-progress-bar.progress .progress-bar').removeClass('progress-bar-success');
            }, 2000);
        },
        setNavToolbar : function(toolbar) {
            var self = this;
            self.navToolbar(toolbar);
        },
        unsetNavToolbar : function() {
            var self = this;
            self.navToolbar(false);
        },
        preloadData : function() {
            var self = this;

            console.log('!!!!!!!!!! PRELOADING DATA !!!!!!!!!!');

            var preloadAttributes = self.conf['preloadAttributes'];
            var promises = [];

            // -----------------------------------------------------------
            // Preload attribute-target-objects.
            // -----------------------------------------------------------
            promises.push(attrAPI.getAttributeTargetObjects().then(function(data) {
                var attributeTargetObjects = data.data.attributeTargetObjects;
                gc.cache.put('attributeTargetObjects', attributeTargetObjects);
            }));

            // -----------------------------------------------------------
            // Preload attribute-tabs.
            // -----------------------------------------------------------
            promises.push(attrTabsAPI.getAttributeTabs().then(function(data) {
                var attributeTabs = data.data.attributeTabs;
                // console.log('Adding attribute-tabs to cache');
                gc.cache.put('attributeTabs', attributeTabs);
            }));

            // -----------------------------------------------------------
            // Preload attribute-tab-attributes.
            // -----------------------------------------------------------
            promises.push(attrTabsAPI.getAttributeTabMapping().then(function(data) {
                var attributeTabMappings = data.data.attributeTabMappings;
                // console.log('Adding attribute-tab-mappings to cache');
                gc.cache.put('attributeTabMappings', attributeTabMappings);
            }));

            // -----------------------------------------------------------
            // Preload attribute-conditions.
            // -----------------------------------------------------------

            promises.push(attrAPI.getInputConditionsFor({
                fields : [ 'whenAttributeId', 'hasOptionIds', 'showAttributeId', 'showOptionsHavingTag', 'applyToProductTypes' ]
            }).then(function(response) {
                var attributeInputConditions = response.data.attributeInputConditions;
                // console.log('Adding input-conditions to cache');
                gc.cache.put('inputConditions', attributeInputConditions);
            }));

            var attrPromise = attrAPI.getAttributes({
                nocache : true
            }).then(function(data) {
                if (data && !data.isFromCache && data.data.attributes) {
                    var attributes = data.data.attributes;
                    // console.log('Adding attributes to cache');
                    gc.cache.put('attributes', attributes);
                }
            }).then(function() {
                _.each(preloadAttributes, function(attrCode) {

                    // console.log('Preloading attribute');

                    var promise = attrAPI.getAttributes({
                        fields : [ 'code', 'options', 'label' ],
                        filter : {
                            code : attrCode
                        }
                    }).then(function(data) {
                        var attributes = data.data.attributes;

                        if (!_.isEmpty(attributes)) {
                            var attr = attributes[0];
                            gc.ctxobj.decorate(attr.options, 'any');

                            self.data['attr:' + attr.code] = ko.observable(attr);

                            // console.log('Adding attribute: ', attr.code, self.data);
                        }
                    });
                });

                promises.push(attrPromise);

                // Get list of attributes
                promises.push(attrAPI.getAttributes({
                    fields : [ 'code', 'backendLabel' ],
                    filter : {
                        group : 'PRODUCT',
                        enabled : true
                    }
                }).then(function(data) {
                    gc.ctxobj.decorate(data.data.attributes);
                    self.data['productAttributes'] = ko.observable(data.data.attributes);
                }));

                promises.push(productAPI.getMediaTypes().then(function(data) {
                    gc.ctxobj.decorate(data.data.catalogMediaTypes, 'any');
                    self.data['catalogMediaTypes'] = ko.observable(data.data.catalogMediaTypes);
                }));
            });

            return $.when.apply($, promises);
        },
        initContexts : function() {
            var self = this;
            var _contexts = [];
            var _flatContextMap = {};
            var merchants = self.confGet('merchants');
            if (!_.isEmpty(merchants)) {
                _contexts.push({
                    id : undefined,
                    name : self.i18n('app:common.global'),
                    iconPathXS : undefined,
                    scope : 'global'
                });

                _.each(merchants, function(merchant) {
                    var stores = [];

                    _.each(merchant.stores, function(store) {
                        var requestContexts = [];

                        _.each(self.confGet('requestContexts'), function(reqCtx) {
                            if (reqCtx.merchantId == merchant.id && reqCtx.storeId == store.id) {
                                var _reqCtx = {
                                    id : reqCtx.id,
                                    urlPrefix : reqCtx.urlPrefix,
                                    urlType : reqCtx.urlType,
                                    language : reqCtx.language,
                                    country : reqCtx.country,
                                    merchantId : reqCtx.merchantId,
                                    storeId : reqCtx.storeId,
                                    viewId : reqCtx.viewId,
                                    scope : 'request_context',
                                    scopeLabel : 'Website',
                                    name : reqCtx.urlPrefix
                                };

                                requestContexts.push(_reqCtx);
                                _flatContextMap[reqCtx.id] = _reqCtx;
                            }
                        });

                        var _store = {
                            id : store.id,
                            name : store.name,
                            iconPathXS : store.iconPathXS,
                            iconPathS : store.iconPathS,
                            scope : 'store',
                            scopeLabel : 'Store',
                            requestContexts : requestContexts
                        };

                        stores.push(_store);
                        _flatContextMap[store.id] = _store;
                    });

                    var _merchant = {
                        id : merchant.id,
                        name : merchant.companyName,
                        scope : 'merchant',
                        scopeLabel : 'Merchant',
                        stores : stores
                    };

                    _contexts.push(_merchant);
                    _flatContextMap[merchant.id] = _merchant;

                });
            }

            self.conf['availableContexts'] = ko.observable(_contexts);
            self.conf['contextMap'] = ko.observable(_flatContextMap);
        },
        availableContexts : function() {
            var self = this;
            return self.conf['availableContexts']();
        },
        contextMap : function() {
            var self = this;
            return self.conf['contextMap']();
        },
        availableMerchants : function() {
            var self = this;
            return _.where(self.conf['contextMap'](), {scope : 'merchant'});
        },
        availableStores : function() {
            var self = this;
            return _.where(self.conf['contextMap'](), {scope : 'store'});
        },
        availableRequestContexts : function() {
            var self = this;
            return _.where(self.conf['contextMap'](), {scope : 'request_context'});
        },
        activeRequestCtx : function() {
            var activeContext = gc.app.sessionGet('activeContext');
            var availableContexts = gc.app.confGet('availableContexts');

            var activeMerchant;
            var activeStore;
            var activeRequestCtx;
            var firstRequestCtx;
            var closestRequestCtx;

            for (var m = 0; m < availableContexts.length; m++) {
                if (!_.isUndefined(activeRequestCtx) || !_.isUndefined(closestRequestCtx)) {
                    break;
                }

                var merchant = availableContexts[m];

                if (merchant.scope == 'global') {
                    continue;
                }

                if (activeContext.scope == 'merchant' && activeContext.id == merchant.id) {
                    activeMerchant = merchant;
                }

                if (merchant.stores) {
                    for (var s = 0; s < merchant.stores.length; s++) {
                        var store = merchant.stores[s];

                        if (activeContext.scope == 'store' && activeContext.id == store.id) {
                            activeStore = store;
                        }

                        if (store.requestContexts) {
                            for (var r = 0; r < store.requestContexts.length; r++) {
                                var reqCtx = store.requestContexts[r];

                                if (_.isUndefined(firstRequestCtx)) {
                                    firstRequestCtx = reqCtx;
                                }

                                if (activeContext.scope == 'request_context' && activeContext.id == reqCtx.id) {
                                    activeRequestCtx = reqCtx;
                                } else if ((!_.isUndefined(activeStore) || !_.isUndefined(activeMerchant)) && _.isUndefined(closestRequestCtx)) {
                                    closestRequestCtx = reqCtx;
                                }
                            }
                        }
                    }
                }
            }

            if (!_.isUndefined(activeRequestCtx)) {
                return activeRequestCtx;
            } else if (!_.isUndefined(closestRequestCtx)) {
                return closestRequestCtx;
            } else {
                return firstRequestCtx;
            }
        },
        /*
         * reqCtxForActiveStore : function() { var self = this; var reqContexts = self.confGet('requestContexts'); var activeStore = self.sess['activeStore'](); var foundReqCtx = reqContexts[0];
         * 
         * if (!_.isEmpty(activeStore) && !_.isEmpty(activeStore.id)) { foundReqCtx = _.findWhere(reqContexts, { storeId : activeStore.id }); }
         * 
         * return foundReqCtx; },
         */
        startSessionTimer : function() {
            var self = this;

            var days, hours, minutes, seconds;

            setInterval(function() {
                var currentTime = gc.utils.toServerTime(new Date());
                var targetTime = self.sess['sessionTimeoutAtMillis'];

                var secondsLeft = (targetTime - currentTime) / 1000;

                days = parseInt(secondsLeft / 86400);
                secondsLeft = secondsLeft % 86400;

                hours = parseInt(secondsLeft / 3600);
                secondsLeft = secondsLeft % 3600;

                minutes = parseInt(secondsLeft / 60);
                seconds = parseInt(secondsLeft % 60);

                if (minutes < 0) {
                    minutes = 0;
                }

                if (seconds < 0) {
                    seconds = 0;
                }

                self.remainingTime(pad(minutes, 2) + ":" + pad(seconds, 2));
            }, 1000);
        },
        startSession : function(session) {
            this.sess['activeSession'](session);
            gc.app.channel.publish('app.session.started', session);
        },
        handleSessionTimeout : function() {
            // User is not logged in anymore.
            var activeInstruction = router.activeInstruction();

            if (activeInstruction) {
                window.location.replace('/#/' + activeInstruction.fragment);
                window.location.reload();
            } else {
                if (_.isEmpty(router.routes)) {
                    app.setRoot('shell', 'entrance');
                } else {
                    window.location.replace('/');
                    window.location.reload();
                }
            }
        },
        onSessionStarted : function(callback) {
            gc.app.channel.subscribe('app.session.started', function(session) {
                callback(session);
            });
        },
        isSessionActive : function() {
            return !_.isUndefined(this.sess['activeSession']());
        },
        currentSession : function() {
            return this.sess['activeSession']();
        },
        // ---------------------------------------------------------------
        // Configuration functions.
        // ---------------------------------------------------------------
        confPut : function(key, value) {
            this.configuration.put(key, value);
        },
        confGet : function(key) {
            return this.configuration.get(key);
        },
        confKGet : function(key) {
            return this.configuration.koGet(key);
        },
        confRemove : function(key) {
            this.configuration.remove(key);
        },
        // ---------------------------------------------------------------
        // Session functions.
        // ---------------------------------------------------------------
        sessionPut : function(key, value) {
            this.session.put(key, value);
        },
        sessionGet : function(key) {
            return this.session.get(key);
        },
        sessionKGet : function(key) {
            return this.session.koGet(key);
        },
        sessionRemove : function(key) {
            this.session.remove(key);
        },
        localStoragePut : function(key, value) {
            if (_.isString(value)) {
                localStorage.setItem(key, value);
            } else {
                localStorage.setItem(key, JSON.stringify(value));
            }
        },
        localStorageGet : function(key) {
            var json = localStorage.getItem(key);
            var data = JSON.parse(json);

            return data;
        },
        localStorageRemove : function(key) {
            localStorage.removeItem(key);
        },
        cookiePut : function(cname, cvalue, exdays) {
            var expires = "";
            if (exdays) {
                var d = new Date();
                d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
                expires = "expires=" + d.toGMTString();
            }
            document.cookie = cname + "=" + cvalue + "; " + expires + "; path=/";
        },
        cookieGet : function(cname) {
            var name = cname + "=";
            var ca = document.cookie.split(';');
            for (var i = 0; i < ca.length; i++) {
                var c = ca[i].trim();
                if (c.indexOf(name) == 0)
                    return c.substring(name.length, c.length);
            }
            return "";
        },
        // ---------------------------------------------------------------
        // Data functions.
        // ---------------------------------------------------------------
        dataPut : function(key, value) {
            this.appData.put(key, value);
        },
        dataGet : function(key) {
            return this.appData.get(key);
        },
        dataKGet : function(key) {
            return this.appData.koGet(key);
        },
        dataRemove : function(key) {
            this.appData.remove(key);
        },
        currentUserLang : function() {
            return this.sessionGet('userLanguage');
        },
        currentLang : function() {
            return this.sessionGet('selectedLanguage');
        },
        ctxVal : function(ctxObject, lang, mode, store) {
            return gc.ctxobj.val(ctxObject, lang, mode, store);
        },
        triggerLocaleElements : function(lang) {
            // Trigger click on all elements having the data-locale
            // attribute
            // set to the currently selected language.
            $('[data-locale="' + lang + '"]').each(function(index) {
                $(this).trigger('click');
            });
        },
        newUpdateModel : function() {
            return new UpdateModel();
        },
        newContextModel : function() {
            return new ContextModel();
        }
    }

    return App;
});