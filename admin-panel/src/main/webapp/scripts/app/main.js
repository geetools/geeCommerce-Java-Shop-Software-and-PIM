requirejs.config({
    waitSeconds : 0,
    paths : {
        'text' : '../vendor/require/text',
        'durandal' : '../vendor/durandal/js',
        'plugins' : '../vendor/durandal/js/plugins',
        'transitions' : '../vendor/durandal/js/transitions',
        'jquery' : '../vendor/jquery/jquery',
        'jquery.ui' : '../vendor/jquery-ui/js/jquery-ui',
        'jquery.livequery' : '../vendor/jquery-livequery/jquery.livequery.min',
        'knockout' : '../vendor/knockout/knockout-3.2.0',
        'ko-plugins' : '../vendor/knockout/plugins',
        'i18next' : '../vendor/i18next.amd-1.7.3/i18next.amd.withJQuery-1.7.3',
        'bootstrap' : '../vendor/bootstrap-3.3.7/js/bootstrap',
        'bootstrap-affix' : '../vendor/bootstrap/js/affix',
        'bootstrap-alert' : '../vendor/bootstrap/js/alert',
        'bootstrap-button' : '../vendor/bootstrap/js/button',
        'bootstrap-carousel' : '../vendor/bootstrap/js/carousel',
        'bootstrap-collapse' : '../vendor/bootstrap/js/collapse',
        'bootstrap-dropdown' : '../vendor/bootstrap/js/dropdown',
        'bootstrap-modal' : '../vendor/bootstrap/js/modal',
        'bootstrap-popover' : '../vendor/bootstrap/js/popover',
        'bootstrap-scrollspy' : '../vendor/bootstrap/js/scrollspy',
        'bootstrap-tab' : '../vendor/bootstrap/js/tab',
        'bootstrap-tooltip' : '../vendor/bootstrap/js/tooltip',
        'bootstrap-jasny' : '../vendor/bootstrap-jasny/dist/extend/js/jasny-bootstrap.min',
        'bootstrap-datepicker' : '../vendor/bootstrap-datepicker/js/bootstrap-datepicker',
        'bootstrap-colorpicker' : '../vendor/bootstrap-colorpicker/js/bootstrap-colorpicker',
        'bootstrap-select' : '../vendor/bootstrap-select-1.11.2/dist/js/bootstrap-select',
        'bootstrap-slider' : '../vendor/bootstrap-slider/src/js/bootstrap-slider',
        'jquery.tmpl' : '../vendor/jquery-tmpl/jquery.tmpl.min',
        'jquery.easing' : '../vendor/jquery.easing/jquery.easing.1.3.min',
        'jquery.itoggle' : '../vendor/jquery-itoggle/engage.itoggle',
        'jquery.switchbutton' : '../vendor/jquery-switchbutton/jquery.switchButton',
        'jquery.select2' : '../vendor/select2/select2',
        'jquery.nested.sortable' : '../vendor/nested-sortable/jquery.mjs.nestedSortable',
        'jquery.spectrum' : '../vendor/jquery-spectrum/spectrum',
        'wysihtml5' : '../vendor/xing/wysihtml5-0.3.0.min',
        'bootstrap-wysihtml5' : '../vendor/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min',
        'bootstrap-editable' : '../vendor/bootstrap-editable/bootstrap-editable',
        'knockout-x-editable' : '../vendor/bootstrap-editable/knockout-x-editable',
        'knockout-validation' : '../vendor/knockout-validation/knockout.validation',
        'knockout-sortable' : '../vendor/knockout-sortable/knockout-sortable',
        'knockout-contextmenu' : '../vendor/knockout-contextmenu/knockout.contextmenu',
        'knockout-modal' : '../vendor/knockout-modal/knockout.bootstrap.modal',
        'moment' : '../vendor/moment/min/moment-with-locales.min',
        'numeral-de' : '../vendor/adamwdraper-numeral-js/min/languages/de.min',
        'numeral' : '../vendor/adamwdraper-numeral-js/min/numeral.min',
        // 'moment-datepicker' :
        // '../vendor/moment-datepicker/moment-datepicker.min',
        // 'moment-datepicker-ko' :
        // '../vendor/moment-datepicker/moment-datepicker-ko',
        'postal' : '../vendor/postal/postal.min',
        'underscore' : '../vendor/underscore/underscore-min-1.8.3',
        'gc' : '../vendor/geecommerce/js',
        'ckeditor' : '../vendor/ckeditor/ckeditor',
        'ckeditor-jquery' : '../vendor/ckeditor/adapters/jquery',
        'ckeditor-plugins' : '../vendor/ckeditor/plugins',
        'gridstack' : '../vendor/gridstack/dist/gridstack',
        'lodash' : '../vendor/lodash/lodash',
        'atmosphere' : '../vendor/atmosphere/atmosphere',
        'geegrid' : '../vendor/geegrid/geegrid',
        'focusable' : '../vendor/focusable/focus-element-overlay.min',
        'speakingurl' : '../vendor/speakingurl-10.0.0/speakingurl.min',
        'socket.io' : '../vendor/socket.io/socket.io-1.4.5'
    },
    shim : {
        'jquery' : {
            exports : 'jQuery'
        },
        'jquery.ui' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'knockout' : {
            deps : [ 'jquery' ],
            exports : 'ko'
        },
        'bootstrap' : {
            deps : [ 'jquery', 'jquery.ui' ],
            exports : 'jQuery'
        },
        // 'bootstrap-plugins' : {
        // // deps : [ 'jquery', 'jquery.ui' ],
        // exports : 'jQuery'
        // },
        'jquery.select2' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'bootstrap-affix' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'bootstrap-alert' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'bootstrap-button' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'bootstrap-carousel' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'bootstrap-collapse' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'bootstrap-dropdown' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'bootstrap-modal' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'bootstrap-popover' : {
            deps : [ 'jquery', 'bootstrap-tooltip' ],
            exports : 'jQuery'
        },
        'bootstrap-scrollspy' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'bootstrap-tab' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'bootstrap-tooltip' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'bootstrap-jasny' : {
            deps : [ 'jquery', 'jquery.ui' ],
            exports : 'jQuery'
        },
        'bootstrap-datepicker' : {
            deps : [ 'jquery', 'jquery.ui' ],
            exports : 'jQuery'
        },
        'bootstrap-colorpicker' : {
            deps : [ 'jquery', 'jquery.ui' ],
            exports : 'jQuery'
        },
        'bootstrap-slider' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
        'bootstrap-select' : {
            deps : [ 'jquery', 'jquery.ui' ],
            exports : 'jQuery'
        },
        'jquery.tmpl' : {
            deps : [ 'jquery', 'jquery.ui' ],
            exports : 'jQuery'
        },
        'jquery.easing' : {
            deps : [ 'jquery', 'jquery.ui' ],
            exports : 'jQuery'
        },
        'jquery.itoggle' : {
            deps : [ 'jquery', 'jquery.ui', 'jquery.easing' ],
            exports : 'jQuery'
        },
        'jquery.switchbutton' : {
            deps : [ 'jquery', 'jquery.ui' ],
            exports : 'jQuery'
        },
        'jquery.nested.sortable' : {
            deps : [ 'jquery', 'jquery.ui' ],
            exports : 'jQuery'
        },
        // 'ko-plugins/ko_file' : {
        // deps : [ 'jquery', 'ko' ]
        // },
        'wysihtml5' : {
            exports : 'wysihtml5'
        },
        'moment' : {
            exports : 'moment'
        },
        'numeral-de' : {
            deps : [ 'numeral' ]
        },
        'bootstrap-wysihtml5' : {
            deps : [ 'jquery', 'wysihtml5' ],
            exports : 'jQuery'
        },
        'bootstrap-editable' : {
            deps : [ 'jquery', 'bootstrap-popover' ],
            exports : 'jQuery'
        },
        'knockout-validation' : {
            deps : [ 'knockout' ]
        },
        'knockout-sortable' : {
            deps : [ 'knockout', 'jquery', 'jquery.ui' ],
            exports : 'jQuery'
        },
        'knockout-contextmenu' : {
            deps : [ 'knockout' ]
        },
        'knockout-modal' : {
            deps : [ 'knockout', 'jquery', 'bootstrap-modal' ]
        },
        'ckeditor' : {
            deps : [ 'jquery' ],
            exports : 'CKEDITOR'
        },
        'ckeditor-jquery' : {
            deps : [ 'jquery', 'ckeditor' ]
        },
        'gridstack' : {
            deps : [ 'lodash', 'jquery.ui' ]
        },
        'geegrid' : {
            deps : [ 'jquery'/* ,'lodash' */],
            exports : 'jQuery'
        },
        'focusable' : {
            deps : [ 'jquery' ],
            exports : 'jQuery'
        },
    // 'knockout-x-editable' : {
    // deps : [ 'knockout', 'jquery', 'bootstrap-editable' ],
    // exports : 'jQuery'
    // },
    // 'moment-datepicker' : {
    // deps : [ 'jquery', 'moment'],
    // exports : 'jQuery'
    // },
    // 'moment-datepicker-ko' : {
    // deps : [ 'jquery', 'knockout', 'moment'],
    // exports : 'jQuery'
    // }
    },
    packages : [ {
        name : 'gc-dashboard',
        location : 'core/dashboard/scripts',
        main : 'api'
    }, {
        name : 'gc-attribute',
        location : 'core/attribute/scripts',
        main : 'api'
    }, {
        name : 'gc-attribute-vm',
        location : 'core/attribute/models',
        main : 'model'
    }, {
        name : 'gc-attribute-group',
        location : 'core/attribute-group/scripts',
        main : 'api'
    }, {
        name : 'gc-user',
        location : 'core/user/scripts',
        main : 'api'
    }, {
        name : 'gc-widget',
        location : 'core/widget/scripts',
        main : 'api'
    }, {
        name : 'gc-attribute-tabs',
        location : 'core/attribute-tabs/scripts',
        main : 'api'
    }, {
        name : 'gc-import',
        location : 'core/import/scripts',
        main : 'api'
    }, {
        name : 'gc-product',
        location : 'modules/geecommerce/product/scripts',
        main : 'api'
    }, {
        name : 'gc-product-list',
        location : 'modules/geecommerce/product-list/scripts',
        main : 'api'
    }, {
        name : 'gc-price',
        location : 'modules/geecommerce/price/scripts',
        main : 'api'
    }, {
        name : 'gc-customer',
        location : 'modules/geecommerce/customer/scripts',
        main : 'api'
    }, {
        name : 'gc-order',
        location : 'modules/geecommerce/order/scripts',
        main : 'api'
    }, {
        name : 'gc-coupon',
        location : 'modules/geecommerce/coupon/scripts',
        main : 'api'
    }, {
        name : 'gc-navigation',
        location : 'modules/geecommerce/navigation/scripts',
        main : 'api'
    }, {
        name : 'gc-slide-show',
        location : 'modules/geecommerce/slide-show/scripts',
        main : 'api'
    }, {
        name : 'gc-magazine',
        location : 'modules/geecommerce/magazine/scripts',
        main : 'api'
    }, {
        name : 'gc-context-message',
        location : 'core/context-message/scripts',
        main : 'api'
    }, {
        name : 'gc-pictogram',
        location : 'modules/geecommerce/pictogram/scripts',
        main : 'api'
    }, {
        name : 'gc-media-asset',
        location : 'modules/geecommerce/media-asset/scripts',
        main : 'api'
    }, {
        name : 'gc-product-promotion',
        location : 'modules/geecommerce/product-promotion/scripts',
        main : 'api'
    }, {
        name : 'gc-discount-promotion',
        location : 'modules/geecommerce/discount-promotion/scripts',
        main : 'api'
    }, {
        name : 'gc-url-rewrite',
        location : 'core/url-rewrite/scripts',
        main : 'api'
    }, {
        name : 'gc-account',
        location : 'core/account/scripts',
        main : 'api'
    }, {
        name : 'gc-content',
        location : 'modules/geecommerce/content/scripts',
        main : 'api'
    }, {
        name : 'gc-synonym',
        location : 'modules/geecommerce/synonym/scripts',
        main : 'api'
    }, {
        name : 'gc-marketplace-merchant',
        location : 'modules/geecommerce/marketplace-merchant/scripts',
        main : 'api'
    }, {
        name : 'gc-marketplace-supplier',
        location : 'modules/geecommerce/marketplace-supplier/scripts',
        main : 'api'
    }, {
        name : 'gc-search-rewrite',
        location : 'modules/geecommerce/search-rewrite/scripts',
        main : 'api'
    }, {
        name : 'gc-conf',
        location : 'core/configuration/scripts',
        main : 'api'
    }, {
        name : 'gc-content-layout',
        location : 'modules/geecommerce/content-layout/scripts',
        main : 'api'
    }, {
        name : 'gc-content-editor',
        location : 'modules/geecommerce/content-editor/scripts',
        main : 'api'
    } ],
    map : {
        '*' : {
            'jquery-ui/mouse' : 'jquery.ui',
            'jquery-ui/draggable' : 'jquery.ui',
            'jquery-ui/core' : 'jquery.ui',
            'jquery-ui/widget' : 'jquery.ui',
            'jquery-ui/resizable' : 'jquery.ui'
        }
    }
});

define(function(require) {

    // ----------------------------------------------------------
    // Require framework
    // ----------------------------------------------------------

    var system = require('durandal/system');
    var app = require('durandal/app');
    var viewLocator = require('durandal/viewLocator');
    var binder = require('durandal/binder');
    var router = require('plugins/router');
    var widget = require('plugins/widget');
    var $ = require('jquery');
    var ko = require('knockout');
    var kv = require('knockout-validation');
    var postal = require('postal');

    $.whenall = function(arr) {
        return $.when.apply($, arr);
    };

    jQuery.browser = {};
    (function() {
        jQuery.browser.msie = false;
        jQuery.browser.version = 0;
        if (navigator.userAgent.match(/MSIE ([0-9]+)\./)) {
            jQuery.browser.msie = true;
            jQuery.browser.version = RegExp.$1;
        }
    })();
    /*
     * $.ajaxSetup({ statusCode: { 401: function(){ console.log('Session timeout. Redirecting to login page.'); app.setRoot('login'); } } });
     */
    Dropzone.autoDiscover = false;

    system.debug(true);

    // ----------------------------------------------------------
    // Require global libs
    // ----------------------------------------------------------

    require('jquery.ui');
    require('jquery.livequery');
    require('jquery.tmpl');
    require('jquery.easing');
    require('jquery.select2');
    require('jquery.nested.sortable');

    require('moment');
    require('numeral');
    require('numeral-de');
    
    require('bootstrap');
    require('bootstrap-tooltip');
    require('bootstrap-affix');
    require('bootstrap-alert');
    require('bootstrap-button');
    require('bootstrap-carousel');
    require('bootstrap-collapse');
    require('bootstrap-colorpicker');
    require('bootstrap-dropdown');
    require('bootstrap-modal');
    require('bootstrap-popover');
    require('bootstrap-scrollspy');
    require('bootstrap-tab');

    require('bootstrap-jasny');
    require('bootstrap-datepicker');
    require('bootstrap-wysihtml5');
    require('bootstrap-editable');
    require('bootstrap-select');
    require('bootstrap-slider');
    require('jquery.switchbutton');
    require('knockout-sortable');
    require('knockout-contextmenu');
    require('knockout-modal');
    require('ckeditor');
    require('ckeditor-jquery');

    var i18n = require('i18next');

    // Add mapping plugin to knockout so that we do no have to include it every
    // time.
    var koMapping = require('ko-plugins/knockout.mapping-2.4.1');
    var koDragdrop = require('ko-plugins/knockout.dragdrop');
    var koDragdropHtml5 = require('ko-plugins/knockout.dragdrop.html5');
    var koWrap = require('ko-plugins/knockout.wrap-0.1');
    var koGC = require('ko-plugins/geecommerce');

    ko.mapping = koMapping;
    ko.wrap = koWrap;
    ko.gc = koGC;
    // require('ko-plugins/ko_file');

    // ----------------------------------------------------------
    // CommerceBoard plugins
    // ----------------------------------------------------------

    var gc = require('gc/gc');
    var gcUtils = require('gc/utils');
    var gcContexts = require('gc/contexts');
    var gcCache = require('gc/cache');
    var gcCtxObj = require('gc/contextobject');
    var gcAttributes = require('gc/attributes');
    var gcImages = require('gc/images');
    var gcRest = require('gc/rest');
    var gcPager = require('gc/pager');
    var gcSecurity = require('gc/security');
    var App = require('gc/app');
    var BaseViewModel = require('gc/baseViewModel');
    var AttributeVM = require('gc-attribute-vm');

        
    
    gc.cache = gcCache;
    gc.utils = gcUtils;
    gc.contexts = gcContexts;
    gc.ctxobj = gcCtxObj;
    gc.attributes = gcAttributes;
    gc.images = gcImages;
    gc.rest = gcRest;
    gc.Pager = gcPager;
    gc.security = gcSecurity;

    // ----------------------------------------------------------
    // Custom knockout bindings
    // ----------------------------------------------------------

    ko.bindingHandlers.ctxValue = require('gc/bindings/ctxValue');
    ko.bindingHandlers.ctxChecked = require('gc/bindings/ctxChecked');
    ko.bindingHandlers.ctxSelect = require('gc/bindings/ctxSelect');
    ko.bindingHandlers.ctxSelectPlain = require('gc/bindings/ctxSelectPlain');
    ko.bindingHandlers.ctxText = require('gc/bindings/ctxText');
    ko.bindingHandlers.ctxHtml = require('gc/bindings/ctxHtml');
    ko.bindingHandlers.ctxBool = require('gc/bindings/ctxBool');
    ko.bindingHandlers.ctxVisible = require('gc/bindings/ctxVisible');
    ko.bindingHandlers.i18nValue = require('gc/bindings/i18nValue');
    ko.bindingHandlers.i18nText = require('gc/bindings/i18nText');
    ko.bindingHandlers.i18nTextDefault = require('gc/bindings/i18nTextDefault');
    ko.bindingHandlers.i18nHtml = require('gc/bindings/i18nHtml');
    ko.bindingHandlers.i18nBool = require('gc/bindings/i18nBool');
    ko.bindingHandlers.i18nGrid = require('gc/bindings/i18nGrid');
    ko.bindingHandlers.i18nAttr = require('gc/bindings/i18nAttr');
    ko.bindingHandlers.i18nAttrOption = require('gc/bindings/i18nAttrOption');
    ko.bindingHandlers.attrValue = require('gc/bindings/attrValue');
    ko.bindingHandlers.bindIframe = require('gc/bindings/bindIframe');
    ko.bindingHandlers.attrText = require('gc/bindings/attrText');
    ko.bindingHandlers.attrLabelText = require('gc/bindings/attrLabelText');
    ko.bindingHandlers.attrHtml = require('gc/bindings/attrHtml');
    ko.bindingHandlers.popover = require('gc/bindings/popover');
    ko.bindingHandlers.datepicker = require('gc/bindings/datepicker');
    ko.bindingHandlers.colorpicker = require('gc/bindings/colorPicker');
    ko.bindingHandlers.editable = require('gc/bindings/editable');
    ko.bindingHandlers.select = require('gc/bindings/select');
    ko.bindingHandlers.select2 = require('gc/bindings/select2');
    ko.bindingHandlers.bool = require('gc/bindings/bool');
    ko.bindingHandlers.i18n = require('gc/bindings/i18n');
    ko.bindingHandlers.i18nPlaceholder = require('gc/bindings/i18nPlaceholder');
    ko.bindingHandlers.i18nJqAuto = require('gc/bindings/i18nJqAuto');
    ko.bindingHandlers.format = require('gc/bindings/format');
    ko.bindingHandlers.readonly = require('gc/bindings/readonly');
    ko.bindingHandlers.storesText = require('gc/bindings/storesText');
    ko.bindingHandlers.globalText = require('gc/bindings/globalText');
    ko.bindingHandlers.globalBool = require('gc/bindings/globalBool');
    ko.bindingHandlers.ckeditorInline = require('gc/bindings/ckeditorInline');
    ko.bindingHandlers.productSelector = require('gc/bindings/productSelector');
    ko.bindingHandlers.productPicker = require('gc/bindings/productPicker');
    ko.bindingHandlers.productViewer = require('gc/bindings/productViewer');
    ko.bindingHandlers.maViewer = require('gc/bindings/maViewer');
    ko.bindingHandlers.maUploader = require('gc/bindings/maUploader');
    ko.bindingHandlers.sliderValue = require('gc/bindings/sliderValue');
    ko.bindingHandlers.currencyValue = require('gc/bindings/currencyValue');

    kv.makeBindingHandlerValidatable("datepicker");
    kv.configure({
        decorateElement : true
    });

    console.log(ko.bindingHandlers);

    widget.registerKind('toolbar');
    widget.registerKind('loader');
    widget.registerKind('alertInfo');
    widget.registerKind('alertSuccess');
    widget.registerKind('alertWarn');
    widget.registerKind('alertError');
    widget.registerKind('i18nEditor');
    widget.registerKind('contextInfo');
    widget.registerKind('contextValues');
    widget.registerKind('contextEditor');
    widget.registerKind('navToolbar');
    widget.registerKind('mediaAssets');
    widget.registerKind('gridTable');
    widget.registerKind('queryBuilder');
    widget.registerKind('attrSelect');
    widget.registerKind('newAttributeForm');
    kv.makeBindingHandlerValidatable("i18nValue");

    kv.rules['require_i18n'] = {
        validator : function(val, lang) {
            console.log(val);
            if (!val)
                return false;

            if (lang == 'any') {
                for (i = 0; i < val.length; ++i) {
                    if (val[i].val && val[i].val.length > 0)
                        return true;
                }
            } else {
                for (i = 0; i < val.length; ++i) {
                    if (_.isEmpty(lang)) {
                        if (!val[i].l) {
                            if (val[i].val && val[i].val.length > 0)
                                return true;
                        }
                    } else {
                        if (val[i].l && val[i].l == lang && val[i].val.length > 0)
                            return true;
                    }
                }
            }

            return false;

        },
        message : 'This field is required.'
    };

    ko.validation.registerExtenders();

    ko.setTemplateEngine(new ko.nativeTemplateEngine());

    ko.utils.extendObservable = function(target, source) {
        var prop, srcVal, tgtProp, srcProp, isObservable = false;

        for (prop in source) {

            if (!source.hasOwnProperty(prop)) {
                continue;
            }

            if (ko.isWriteableObservable(source[prop])) {
                isObservable = true;
                srcVal = source[prop]();
            } else if (typeof (source[prop]) !== 'function') {
                srcVal = source[prop];
            }

            if (ko.isWriteableObservable(target[prop])) {
                target[prop](srcVal);
            } else if (target[prop] === null || target[prop] === undefined) {

                target[prop] = isObservable ? ko.observable(srcVal) : srcVal;

            } else if (typeof (target[prop]) !== 'function') {
                target[prop] = srcVal;
            }

            isObservable = false;
        }
    };

    // ------------------------------------------------------------
    // Knockout extension for managing context objects.
    // ------------------------------------------------------------
    ko.extenders.manageCtxObj = function(target, option) {
        target.subscribe(function(newValue) {
            var unwrappedCtxObj = ko.unwrap(option.ctxObj);
            if (option && option.context && option.context.scope != 'global') {
                gc.ctxobj.set(unwrappedCtxObj, undefined, newValue, option.context);
            } else {
                gc.ctxobj.set(unwrappedCtxObj, undefined, newValue);
            }

            if (ko.isObservable(option.ctxObj)) {
                option.ctxObj(unwrappedCtxObj);
            }
        });

        return target;
    };

    ko.utils.clone = function(obj, emptyObj) {
        var json = ko.toJSON(obj);
        var js = JSON.parse(json);

        return ko.utils.extendObservable(emptyObj, js);
    };

    // ----------------------------------------------------------
    // Configure CommerceBoard Application
    // ----------------------------------------------------------

    gc.app = new App({
        defaultLanguage : 'de',
        secondaryLanguage : 'en'
    });

    // Register availableLanguages as ko-observable.
    gc.app.confPut('availableLanguages', ko.observableArray([]));
    gc.app.confPut('availableCountries', ko.observableArray([]));
    gc.app.confPut('availableCurrencies', ko.observableArray([]));

    gc.app.confPut('controlPanelId', 4336136636210100);

    // Get available languages via REST.
    return gc.rest.get({
        url : '/api/v1/settings'
    }).then(function(data) {
        console.log('Settings loaded: ', data);

        var settings = data.data.settings;

        var defaultEditLang = settings.defaultEditLanguage || 'en';
        var fallbackEditLang = settings.fallbackEditLanguage || 'en';
        var defaultUserLang = settings.defaultUserLanguage || 'en';
        var fallbackUserLang = settings.fallbackUserLanguage || 'en';

        gc.app.confPut('defaultEditLang', defaultEditLang);
        gc.app.confPut('fallbackEditLang', fallbackEditLang);
        gc.app.confPut('defaultUserLang', defaultUserLang);
        gc.app.confPut('fallbackUserLang', fallbackUserLang);

        // Available languages.
        var langMap = gc.utils.toLanguageMap(settings.availableLaguages);
        var langCodes = _.pluck(langMap, 'code');
        gc.app.confPut('availableLanguages', langMap);
        gc.app.confPut('availableLangCodes', langCodes);

        var userLangMap = gc.utils.toLanguageMap(settings.availableUserLanguages);
        var userLangCodes = _.pluck(userLangMap, 'code');
        gc.app.confPut('availableUserLanguages', userLangMap);
        gc.app.confPut('availableUserLangCodes', userLangCodes);

        // Available countries.
        var countryMap = gc.utils.toCountryMap(settings.availableCountries);
        countryMap.unshift({
            code : '',
            label : 'Alle'
        });
        gc.app.confPut('availableCountries', countryMap);

        // Available currencies.
        gc.app.confPut('availableCurrencies', gc.utils.toCurrencyMap(settings.availableCurrencies));

        // Images subdomain.
        gc.app.confPut('productImagesSubdomain', settings.productImagesSubdomain);
        // Images URI-prefix.
        gc.app.confPut('productImagesUriPrefix', settings.productImagesUriPrefix);
        // Which attributes do we want to preload.
        gc.app.confPut('preloadAttributes', settings.preloadAttributes);

        gc.app.confPut('merchants', settings.merchants);
        gc.app.confPut('stores', settings.stores);
        gc.app.confPut('requestContexts', settings.requestContexts);

        gc.app.confPut('serverTimezoneOffset', settings.timezoneOffset * -1);

        gc.app.confPut('logoURI', settings.logoURI);
        gc.app.confPut('logoText', settings.logoText);
        gc.app.confPut('loginLogoURI', settings.loginLogoURI);
        gc.app.confPut('loginLogoText', settings.loginLogoText);

    }).then(function() {

        var lng = gc.app.cookieGet('lng');
        var fallbackUserLang = gc.app.confGet('fallbackUserLang');

        if (_.isEmpty(lng) || lng == 'undefined') {
            var lng = window.navigator.userLanguage || window.navigator.language;

            if (lng.indexOf('-') != -1) {
                lng = lng.substring(0, lng.indexOf('-'));
            }

            console.log('--- Language from Browser: ', lng);

            if (!_.isUndefined(lng) && !_.contains(gc.app.confGet('availableUserLangCodes'), lng)) {
                lng = gc.app.confGet('defaultUserLang');
                console.log('--- Using default user language: ', lng);
            }

            if (_.isEmpty(lng)) {
                lng = gc.app.confGet('fallbackUserLang');
                console.log('--- Using fallback user language: ', lng);
            }

            if (_.isEmpty(lng)) {
                lng = 'en';
                console.log('--- Using default hard-coded language: ', lng);
            }

            console.log('--- Adding language to cookie: ', lng);

            gc.app.cookiePut('lng', lng, (365 * 10));
        } else {
            console.log('--- Using cookie language: ', lng);
        }

        if (lng == fallbackUserLang) {
            fallbackUserLang = 'en';
        }

        // moment.lang(lng, fallbackUserLang);
        moment.locale(lng);
        
        numeral.language(lng);
        
        console.log('TEST-NUMBER !!!!!!!!!!!! ' + numeral('12.99').format('0.00'));

        var i18NOptions = {
            detectFromHeaders : false,
            lng : lng,
            fallbackLng : fallbackUserLang,
            supportedLngs : gc.app.confGet('availableUserLangCodes'),
            ns : 'app',
            resGetPath : 'locales/__lng__/__ns__.json',
            useCookie : false,
            debug : false
        };

        console.log('--- i18NOptions: ', i18NOptions);

        app.title = 'CommerceBoard';

        app.configurePlugins({
            router : true,
            dialog : true,
            widget : true
        });

        // -------------------------------------------------------
        // Start the durandal app.
        // -------------------------------------------------------
        app.start().then(function() {

            console.time("PRELOAD-TIME");

            // Preload the attributes.
            gc.app.preloadData().then(function() {
                console.timeEnd("PRELOAD-TIME");

                // Replace 'viewmodels' in the moduleId with 'views' to locate the view.
                // Look for partial views in a 'views' folder in the root.
                viewLocator.useConvention();

                i18n.init(i18NOptions, function() {
                    // Call localization on view before binding...
                    binder.binding = function(obj, view) {
                        $(view).i18n();
                    };

                    // Setup stores.
                    gc.app.initContexts();

                    // Check if user is already logged in.
                    var accountAPI = require('gc-account');

                    accountAPI.currentSession().then(function(data) {
                        // If the user is already logged in, we send
                        // him straight to the shell.
                        if (!_.isEmpty(data.data) && !_.isEmpty(data.data.name) && !_.isEmpty(data.data.roles)) {
                            gc.app.startSession(data.data);

                            gc.app.sessionPut('sessionTimeoutAtMillis', data.data.timeoutAtMillis);
                            gc.app.sessionPut('loggedUserName', data.data.name);
                            gc.app.sessionPut('loggedUserPermissions', data.data.roles);

                            gc.app.startSessionTimer();

                            app.setRoot('shell', 'entrance');
                        }
                        // No session exists yet if we receive a 404,
                        // so we send him to the login page.
                    }, function(data) {
                        console.log('FAAAAAAILL :::: ', data);
                        app.setRoot('login');
                    });

                });

                gc.app.sessionPut('userLanguage', i18NOptions.lng);

                $('body').removeClass('cover');
            });

        });
    });
});
