package com.geecommerce.core;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.geecommerce.core.bootstrap.Bootstrapper;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.config.EnvProps;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.enums.RequestType;
import com.geecommerce.core.inject.ModuleInjector;
import com.geecommerce.core.inject.SystemInjector;
import com.geecommerce.core.message.Context;
import com.geecommerce.core.message.MessageBus;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.QueryMetadata;
import com.geecommerce.core.service.SequenceGenerator;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.Pojo;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.service.persistence.jdbc.JDBC;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.repository.AttributeOptions;
import com.geecommerce.core.system.attribute.repository.AttributeTargetObjects;
import com.geecommerce.core.system.attribute.repository.Attributes;
import com.geecommerce.core.system.helper.ContextMessageHelper;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.ConfigurationProperty;
import com.geecommerce.core.system.model.ContextMessage;
import com.geecommerce.core.system.model.ContextTree;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.service.ConfigurationService;
import com.geecommerce.core.system.service.ContextMessageService;
import com.geecommerce.core.system.service.SystemService;
import com.geecommerce.core.system.service.UrlRewriteService;
import com.geecommerce.core.system.user.pojo.ClientSession;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.IdGenerator;
import com.geecommerce.core.type.IncrementIdGenerator;
import com.geecommerce.core.util.Requests;
import com.geecommerce.core.web.DefaultServletRequestWrapper;
import com.geecommerce.core.web.SessionValue;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleLoader;
import com.geemvc.view.GeemvcKey;
import com.google.inject.Singleton;

@Singleton
public class DefaultApp implements App {
    private static final Logger LOG = LogManager.getLogger(DefaultApp.class);

    private static final char DELIMITER = '/';

    private static final String STORE_HEADER = "X-CB-StoreContext";

    private static final String PREVIEW_HEADER = "X-CB-Preview";

    private static final String REFRESH_HEADER = "X-CB-Refresh";

    private static final String EDIT_HEADER = "X-CB-Edit";

    private static final String XPAGE_PARAMETER = "xpage";

    private static final String XPAGE_PARAMETER_PREVIEW = "preview";

    private static final String XPAGE_PARAMETER_REFRESH = "refresh";

    private static final String XPAGE_PARAMETER_EDIT = "edit";

    private static final String XPAGE_REFERRER_PARAMETER_PREVIEW = "xpage=preview";

    private static final String XPAGE_REFERRER_PARAMETER_REFRESH = "xpage=refresh";

    private static final String XPAGE_REFERRER_PARAMETER_EDIT = "xpage=edit";

    private static final String IS_PREVIEW_REQUEST_KEY = "request/preview";

    private static final String IS_REFRESH_REQUEST_KEY = "request/refresh";

    private static final String IS_EDIT_REQUEST_KEY = "request/edit";

    private static final String IS_EDIT_REQUEST_ALLOWED_KEY = "request/edit/allowed";

    private static final String KEY_BASE_CURRENCY = "general/currency/base_currency";

    private static final String KEY_DEFAULT_DISPLAY_PRICE = "general/currency/default_display_currency";

    private static final String DEFAULT_SYSTEM_CHARSET = "UTF-8";

    private static final String HTTP_SCHEME_CONFIG_KEY = "general/web/http/scheme";

    private static final String HTTPS_SCHEME_CONFIG_KEY = "general/web/https/scheme";

    private static final String DEFAULT_HTTP_SCHEME = "http";

    private static final String DEFAULT_HTTPS_SCHEME = "https";

    private static final String SKIN_DIR = "skin";

    private static final String JS_DIR = "js";

    private static final String RESOURCES_DIR = "resources";

    private static final String KEY_SECURITY_XPAGE_EDIT_FILTER = "security/xpage/edit/filter";

    private static final String KEY_SECURITY_XPAGE_EDIT_ALLOWED_IP = "security/xpage/edit/allowed_ip";

    private static final String KEY_IS_APP_INITIALIZED = "general/app/initialized";

    private static final String KEY_TARGET_MODULE = "general/app/target-module";

    // REST client
    private static final String KEY_AUTHENTICATED_CLIENT = "authenticated.client";

    private static final String[] HEADERS_TO_TRY = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED",
        "HTTP_VIA", "REMOTE_ADDR" };

    // ### IMPORTANT note on using member variables in this class:
    // This is a singleton - do NOT set any member variables for setting state!
    // Use the registry-methods instead.
    // This will ensure that object state is available during the processing of
    // the current thread.
    // com.geecommerce.core.AppRegistry is used for this which stores everything in a
    // ThreadLocal map.

    @Override
    public final void bootstrap(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            Bootstrapper.run(request, response);
        } catch (InstantiationException e) {
            e.printStackTrace();
            LOG.fatal("Error while trying to bootstrap the application!");
            LOG.catching(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LOG.fatal("Error while trying to bootstrap the application!");
            LOG.catching(e);
        }
    }

    @Override
    public final void init() {
    }

    @Override
    public final Id nextId() {
        int numTries = SystemConfig.GET.intVal(SystemConfig.IDGENERATOR_NUM_TRIES);
        int waitUntilRetry = SystemConfig.GET.intVal(SystemConfig.IDGENERATOR_WAIT_UNTIL_RETRY);

        if (numTries == 0) {
            throw new NullPointerException(
                "You must set the 'idgenerator.num_tries' property in the system configuration. Please check that the properties 'idgenerator.datacenter_id' and 'idgenerator.worker_id' have also been set. Also ensure that the id-combination is unique across all instances!");
        } else if (numTries > 40) {
            System.out.println("Warning: Setting numTries to 40 because the configured value is too high.");
            numTries = 40;
        }

        if (waitUntilRetry == 0) {
            waitUntilRetry = 20;
        }

        for (int i = 0; i < numTries; i++) {
            try {
                return new Id(IdGenerator.nextId(SystemConfig.GET.intVal(SystemConfig.IDGENERATOR_DATACENTER_ID), SystemConfig.GET.intVal(SystemConfig.IDGENERATOR_WORKER_ID)));
            } catch (Throwable t) {
                System.out.println("The IDGenerator threw an exception. Trying agaim. Attempt: " + (i + 1) + "/" + numTries + ". " + t.getMessage());

                try {
                    Thread.sleep(waitUntilRetry);
                } catch (InterruptedException e2) {
                }
            }
        }

        throw new IllegalStateException("FATAL: Unable to generate new ID!");
    }

    @Override
    public final <T> T nextIncrementId(String name) {
        int numTries = SystemConfig.GET.intVal(SystemConfig.IDGENERATOR_NUM_TRIES);
        int waitUntilRetry = SystemConfig.GET.intVal(SystemConfig.IDGENERATOR_WAIT_UNTIL_RETRY);

        if (numTries == 0) {
            throw new NullPointerException(
                "You must set the 'idgenerator.num_tries' property in the system configuration. Please check that the properties 'idgenerator.datacenter_id' and 'idgenerator.worker_id' have also been set. Also ensure that the id-combination is unique across all instances!");
        } else if (numTries > 40) {
            System.out.println("Warning: Setting numTries to 40 because the configured value is too high.");
            numTries = 40;
        }

        if (waitUntilRetry == 0) {
            waitUntilRetry = 20;
        }

        for (int i = 0; i < numTries; i++) {
            try {
                return IncrementIdGenerator.GET.nextId(name);
            } catch (Throwable t) {
                System.out.println("The IDGenerator threw an exception. Trying again. Attempt: " + (i + 1) + "/" + numTries + ". " + t.getMessage());

                try {
                    Thread.sleep(waitUntilRetry);
                } catch (InterruptedException e2) {
                }
            }
        }

        throw new IllegalStateException("FATAL: Unable to generate new increment ID!");
    }

    @Override
    public final Id nextSequenceNumber(final String seqName) {
        int numTries = SystemConfig.GET.intVal(SystemConfig.IDGENERATOR_NUM_TRIES);
        int waitUntilRetry = SystemConfig.GET.intVal(SystemConfig.IDGENERATOR_WAIT_UNTIL_RETRY);

        if (numTries == 0) {
            throw new NullPointerException(
                "You must set the 'idgenerator.num_tries' property in the system configuration. Please check that the properties 'idgenerator.datacenter_id' and 'idgenerator.worker_id' have also been set. Also ensure that the id-combination is unique across all instances!");
        } else if (numTries > 40) {
            System.out.println("Warning: Setting numTries to 40 because the configured value is too high.");
            numTries = 40;
        }

        if (waitUntilRetry == 0) {
            waitUntilRetry = 20;
        }

        for (int i = 0; i < numTries; i++) {
            try {
                return new Id(SequenceGenerator.nextSequenceNumber(seqName));
            } catch (Throwable t) {
                System.out.println("The SequenceGenerator threw an exception. Trying again. Attempt: " + (i + 1) + "/" + numTries + ". " + t.getMessage());

                try {
                    Thread.sleep(waitUntilRetry);
                } catch (InterruptedException e2) {
                }
            }
        }

        throw new IllegalStateException("FATAL: Unable to generate new sequence!");
    }

    @Override
    public final String message(final String message) {
        ContextMessageService service = getService(ContextMessageService.class);
        ContextMessageHelper helper = getHelper(ContextMessageHelper.class);

        return service.getOrSetMessage(helper.toKey(message), ContextObjects.global(message)).getValue().getString();
    }

    @Override
    public final String message(final String defMessage, final String message, String lang) {
        ContextMessageService service = getService(ContextMessageService.class);
        ContextMessageHelper helper = getHelper(ContextMessageHelper.class);

        ContextObject<String> ctxMessage = ContextObjects.global(defMessage);
        ctxMessage.add(lang, message);
        return service.getOrSetMessage(helper.toKey(defMessage), ctxMessage).getValue().getString();
    }

    @Override
    public final String message(final String message, final String lang) {
        return message(message, lang, null, null, null, null);
    }

    @Override
    public final String message(final String message, final String lang, final String message2, final String lang2) {
        return message(message, lang, message2, lang2, null, null);
    }

    @Override
    public final String message(final String message, final String lang, final String message2, final String lang2, final String message3, final String lang3) {
        ContextMessage contextMessage = contextMessage(message, lang, message2, lang2, message3, lang3);
        return contextMessage.getMessage();
    }

    @Override
    public final ContextMessage contextMessage(final String message) {
        return contextMessage(message, null, null, null, null, null);
    }

    @Override
    public final ContextMessage contextMessage(final String message, final String lang) {
        return contextMessage(message, lang, null, null, null, null);
    }

    @Override
    public final ContextMessage contextMessage(final String message, final String lang, final String message2, final String lang2) {
        return contextMessage(message, lang, message2, lang2, null, null);
    }

    @Override
    public final ContextMessage contextMessage(final String message, final String lang, final String message2, final String lang2, final String message3, final String lang3) {
        ContextMessageService service = getService(ContextMessageService.class);
        ContextMessageHelper helper = getHelper(ContextMessageHelper.class);

        ContextMessage cm = null;

        try {
            String key = helper.toKey(message);

            if (previewHeaderExists() || refreshHeaderExists()) {
                String registryKey = new StringBuilder("cm/").append(key).toString();

                cm = registryGet(registryKey);

                if (cm == null) {
                    if (Str.isEmpty(lang)) {
                        cm = service.getOrSetMessage(key, ContextObjects.global(message));
                    } else {
                        ContextObject<String> ctxObj = ContextObjects.forLanguage(message, lang);

                        if (!Str.isEmpty(lang2) && !Str.isEmpty(message2))
                            ctxObj.add(lang2, message2);

                        if (!Str.isEmpty(lang3) && !Str.isEmpty(message3))
                            ctxObj.add(lang3, message3);

                        cm = service.getOrSetMessage(key, ctxObj);
                    }

                    registryPut(registryKey, cm);
                }
            } else {
                if (Str.isEmpty(lang)) {
                    cm = service.getOrSetMessage(key, ContextObjects.global(message));
                } else {
                    ContextObject<String> ctxObj = ContextObjects.forLanguage(message, lang);

                    if (!Str.isEmpty(lang2) && !Str.isEmpty(message2))
                        ctxObj.add(lang2, message2);

                    if (!Str.isEmpty(lang3) && !Str.isEmpty(message3))
                        ctxObj.add(lang3, message3);

                    cm = service.getOrSetMessage(key, ctxObj);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }

        return cm;
    }

    @Override
    public final <K, V> Cache<K, V> cache(String name) {
        return inject(CacheManager.class).getCache(name);
    }

    @Override
    public final <T> T inject(final Class<T> type) {
        if (Reflect.hasCorePackagePrefix(type)) {
            return SystemInjector.get().getInstance(type);
        } else {
            return ModuleInjector.get().getInstance(type);
        }
    }

    @Override
    public final void injectMembers(final Object instance) {
        if (instance == null)
            return;

        if (Reflect.hasCorePackagePrefix(instance.getClass())) {
            SystemInjector.get().injectMembers(instance);
        } else {
            ModuleInjector.get().injectMembers(instance);
        }
    }

    @Override
    public final <T extends Model> T getModel(final Class<T> model) {
        if (Reflect.hasCorePackagePrefix(model)) {
            return SystemInjector.get().getInstance(model);
        } else {
            return ModuleInjector.get().getInstance(model);
        }
    }

    @Override
    public final <T extends Pojo> T getPojo(final Class<T> pojo) {
        if (Reflect.hasCorePackagePrefix(pojo)) {
            return SystemInjector.get().getInstance(pojo);
        } else {
            return ModuleInjector.get().getInstance(pojo);
        }
    }

    @Override
    public final <T extends Helper> T getHelper(final Class<T> helper) {
        if (Reflect.hasCorePackagePrefix(helper)) {
            return SystemInjector.get().getInstance(helper);
        } else {
            return ModuleInjector.get().getInstance(helper);
        }
    }

    @Override
    public final <T extends Injectable> T getInjectable(final Class<T> injectable) {
        if (Reflect.hasCorePackagePrefix(injectable)) {
            return SystemInjector.get().getInstance(injectable);
        } else {
            return ModuleInjector.get().getInstance(injectable);
        }
    }

    @Override
    public final <T extends Repository> T getRepository(final Class<T> repository) {
        if (Reflect.hasCorePackagePrefix(repository)) {
            return SystemInjector.get().getInstance(repository);
        } else {
            return ModuleInjector.get().getInstance(repository);
        }
    }

    @Override
    public final <T extends Service> T getService(final Class<T> service) {
        if (Reflect.hasCorePackagePrefix(service)) {
            return SystemInjector.get().getInstance(service);
        } else {
            return ModuleInjector.get().getInstance(service);
        }
    }

    @Override
    public final <T extends Service> T getSystemService(final Class<T> service) {
        return SystemInjector.get().getInstance(service);
    }

    @Override
    public final String getSystemCharset() {
        // String charset = registryGet(SYSTEM_CHARSET_CONFIG_KEY);
        //
        // if (charset == null) {
        // charset = cpStr_(SYSTEM_CHARSET_CONFIG_KEY, DEFAULT_SYSTEM_CHARSET);
        // registryPut(SYSTEM_CHARSET_CONFIG_KEY, charset);
        // }
        //
        // return charset;

        return DEFAULT_SYSTEM_CHARSET;
    }

    @Override
    public final Locale getCurrentLocale() {
        ApplicationContext appCtx = getApplicationContext();
        RequestContext requestCtx = appCtx.getRequestContext();

        return requestCtx.getLocale();
    }

    @Override
    public final String getBaseCurrency() {
        return cpStr_(KEY_BASE_CURRENCY);
    }

    @Override
    public final String getDisplayCurrency() {
        return cpStr_(KEY_DEFAULT_DISPLAY_PRICE);
    }

    @Override
    public final String getDefaultLanguage() {
        return cpStr_(ConfigurationKey.I18N_DEFAULT_LANGUAGE);
    }

    @Override
    public final boolean previewHeaderExists() {
        DefaultServletRequestWrapper request = (DefaultServletRequestWrapper) getServletRequest();

        // if (true)
        // return false;

        if (request == null)
            return false;

        Boolean isPreview = registryGet(IS_PREVIEW_REQUEST_KEY);

        if (isPreview == null) {
            String referrer = Requests.getReferrer(request);

            String xpage = request.getUncheckedParameter(XPAGE_PARAMETER);

            isPreview = request.getUncheckedHeader(PREVIEW_HEADER) != null || XPAGE_PARAMETER_PREVIEW.equals(xpage) || (referrer != null && referrer.contains(XPAGE_REFERRER_PARAMETER_PREVIEW));

            registryPut(IS_PREVIEW_REQUEST_KEY, isPreview);
        }

        if (!isPreview)
            isPreview = editHeaderExists();

        return isPreview;
    }

    @Override
    public final boolean refreshHeaderExists() {
        DefaultServletRequestWrapper request = (DefaultServletRequestWrapper) getServletRequest();

        // if (true)
        // return false;

        if (request == null)
            return false;

        Boolean isRefresh = registryGet(IS_REFRESH_REQUEST_KEY);

        if (isRefresh == null) {
            String referrer = Requests.getReferrer(request);

            String xpage = request.getUncheckedParameter(XPAGE_PARAMETER);

            isRefresh = request.getUncheckedHeader(REFRESH_HEADER) != null || XPAGE_PARAMETER_REFRESH.equals(xpage) || (referrer != null && referrer.contains(XPAGE_REFERRER_PARAMETER_REFRESH));

            registryPut(IS_REFRESH_REQUEST_KEY, isRefresh);
        }

        return isRefresh;
    }

    @Override
    public final boolean editHeaderExists() {
        DefaultServletRequestWrapper request = (DefaultServletRequestWrapper) getServletRequest();

        // if (true)
        // return false;

        if (request == null)
            return false;

        Boolean isEdit = registryGet(IS_EDIT_REQUEST_KEY);

        if (isEdit == null) {
            String referrer = Requests.getReferrer(request);

            String xpage = request.getUncheckedParameter(XPAGE_PARAMETER);

            isEdit = request.getUncheckedHeader(EDIT_HEADER) != null || XPAGE_PARAMETER_EDIT.equals(xpage) || (referrer != null && referrer.contains(XPAGE_REFERRER_PARAMETER_EDIT));

            registryPut(IS_EDIT_REQUEST_KEY, isEdit);
        }

        return isEdit;
    }

    @Override
    public final boolean editAllowed() {
        Boolean isEditAllowed = registryGet(IS_EDIT_REQUEST_ALLOWED_KEY);

        if (isEditAllowed == null) {
            isEditAllowed = true;
            if (cpBool_(KEY_SECURITY_XPAGE_EDIT_FILTER, false)) {
                List<String> allowedIpList = cpStrList_(KEY_SECURITY_XPAGE_EDIT_ALLOWED_IP);
                if (!allowedIpList.contains(getClientIpAddress()))
                    isEditAllowed = false;
            }
            registryPut(IS_EDIT_REQUEST_ALLOWED_KEY, isEditAllowed);
        }
        return isEditAllowed;
    }

    @Override
    public final void initRequestType() {
        HttpServletRequest request = getServletRequest();

        if (request == null)
            return;

        if (Requests.isAPIRequest(request.getRequestURI())) {
            registryPut(RegistryKey.REQUEST_TYPE, RequestType.API);
        } else {
            registryPut(RegistryKey.REQUEST_TYPE, RequestType.FRONTEND);
        }
    }

    @Override
    public final boolean isGetRequest() {
        HttpServletRequest request = getServletRequest();

        if (request == null)
            return false;

        return Requests.isGetRequest(request);
    }

    @Override
    public final boolean isPostRequest() {
        HttpServletRequest request = getServletRequest();

        if (request == null)
            return false;

        return Requests.isPostRequest(request);
    }

    @Override
    public final boolean isSecureRequest() {
        HttpServletRequest request = getServletRequest();

        if (request == null)
            return false;

        return Requests.isSecureRequest(request);
    }

    @Override
    public final boolean isAjaxRequest() {
        HttpServletRequest request = getServletRequest();

        if (request == null)
            return false;

        return Requests.isAjaxRequest(request);
    }

    @Override
    public final boolean isMediaRequest() {
        HttpServletRequest request = getServletRequest();

        if (request == null)
            return false;

        String uri = request.getRequestURI();

        return Requests.isMediaRequest(uri);
    }

    @Override
    public final boolean isAPIRequest() {
        HttpServletRequest request = getServletRequest();

        if (request == null)
            return false;

        String uri = request.getRequestURI();

        return Requests.isAPIRequest(uri);
    }

    @Override
    public final boolean isWebRequest() {
        HttpServletRequest request = getServletRequest();

        if (request == null || request.getRequestURI() == null)
            return false;

        return true;
    }

    @Override
    public final boolean isTemplateRequest() {
        HttpServletRequest request = getServletRequest();

        if (request == null)
            return false;

        String uri = request.getRequestURI();

        String templateSuffix = SystemConfig.GET.val(SystemConfig.APPLICATION_TEMPLATE_SUFFIX);

        return templateSuffix != null && uri.endsWith(templateSuffix);
    }

    @Override
    public final boolean isMultipartRequest() {
        HttpServletRequest request = getServletRequest();

        if (request == null)
            return false;

        return Requests.isMultipartRequest(request);
    }

    @Override
    public final boolean isExternalHost(String host) {
        HttpServletRequest request = getServletRequest();

        if (request == null)
            return true;

        String currentHost = Requests.getHost(request);

        return !currentHost.equals(host);
    }

    @Override
    public final boolean isErrorPage() {
        HttpServletRequest request = getServletRequest();
        HttpServletResponse response = getServletResponse();

        if (request == null)
            return false;

        return Requests.isErrorPage(request, response);
    }

    @Override
    public final boolean hasPageExtension() {
        HttpServletRequest request = getServletRequest();

        if (request == null)
            return false;

        String uri = request.getRequestURI();

        return Requests.hasPageExtension(uri);
    }

    @Override
    public final boolean isInObserverThread() {
        return Thread.currentThread().getName().contains("observer-pool");
    }

    @Override
    public final boolean storeHeaderExists() {
        return getStoreFromHeader() != null;
    }

    @Override
    public final Id getStoreFromHeader() {
        HttpServletRequest request = getServletRequest();

        if (request == null)
            return null;

        String storeHeader = request.getHeader(STORE_HEADER);

        if (!Str.isEmpty(storeHeader)) {
            return Id.valueOf(storeHeader);
        } else {
            return null;
        }
    }

    @Override
    public final Store getStoreFromMerchant(Id storeId) {
        ApplicationContext appCtx = getApplicationContext();
        Merchant m = appCtx.getMerchant();

        return m.getStore(storeId);
    }

    @Override
    public final void setOriginalURI(final String uri) {
        registryPut(RegistryKey.ORIGINAL_URI, uri);
    }

    @Override
    public final String getOriginalURI() {
        return registryGet(RegistryKey.ORIGINAL_URI);
    }

    @Override
    public final void setOriginalQueryString(final String queryString) {
        registryPut(RegistryKey.ORIGINAL_QUERY_STRING, queryString);
    }

    @Override
    public final String getOriginalQueryString() {
        return registryGet(RegistryKey.ORIGINAL_QUERY_STRING);
    }

    @Override
    public final void setRewrittenURI(final String uri) {
        registryPut(RegistryKey.REWRITTEN_URI, uri);
    }

    @Override
    public final String getRewrittenURI() {
        return registryGet(RegistryKey.REWRITTEN_URI);
    }

    @Override
    public final UrlRewrite getUrlRewrite(final String uri) {
        UrlRewriteService urlRewriteService = urlRewriteService();

        UrlRewrite urlRewrite = urlRewriteService.findUrlRewrite(uri);

        if (urlRewrite == null) {
            urlRewrite = urlRewriteService.findUrlRewrite(Requests.stripLastURIPart(uri));

            if (urlRewrite != null) {
                setRewrittenURI(urlRewrite.getRequestURI().getClosestValue());
            }
        }

        return urlRewrite;
    }

    @Override
    public final void setCurrentModule(Module module) {
        registryPut(RegistryKey.CURRENT_MODULE, module);
    }

    @Override
    public final Module getCurrentModule() {
        return registryGet(RegistryKey.CURRENT_MODULE);
    }

    @Override
    public final void setActionURI(final String actionURI) {
        registryPut(RegistryKey.ACTION_URI, actionURI);
    }

    @Override
    public final String getActionURI() {
        return registryGet(RegistryKey.ACTION_URI);
    }

    @Override
    public final void setViewPath(final String viewPath) {
        registryPut(RegistryKey.VIEW_PATH, viewPath);
    }

    @Override
    public final String getViewPath() {
        return registryGet(RegistryKey.VIEW_PATH);
    }

    @Override
    public final void setQueryMetadata(final QueryMetadata queryMetadata) {
        registryPut(QueryMetadata.class.getName(), queryMetadata);
    }

    @Override
    public final QueryMetadata getLastQueryMetadata() {
        return registryGet(QueryMetadata.class.getName());
    }

    @Override
    public final void enableBatchMode() {
        JDBC.enableBatchMode();
    }

    @Override
    public final void disableBatchMode() {
        JDBC.disableBatchMode();
    }

    @Override
    public final void finalizeBatch() {
        JDBC.finalizeBatch();
    }

    @Override
    public final void publish(final String message, final String key, final Object value) {
        MessageBus.publish(message, Context.create(key, value));
    }

    @Override
    public final void publish(final String message, final String key1, final Object value1, final String key2, final Object value2) {
        MessageBus.publish(message, Context.create(key1, value1, key2, value2));
    }

    @Override
    public final void publish(final String message, final String key1, final Object value1, final String key2, final Object value2, final String key3, final Object value3) {
        MessageBus.publish(message, Context.create(key1, value1, key2, value2, key3, value3));
    }

    @Override
    public final void publish(final String message, final Context ctx) {
        MessageBus.publish(message, ctx);
    }

    @Override
    public final void storeProperty(final String key, final Object value, final Merchant merchant) {
        configurationService().storeProperty(key, value, merchant);
    }

    @Override
    public final void storeProperty(final String key, final Object value, final Store store) {
        configurationService().storeProperty(key, value, store);
    }

    @Override
    public final void storeProperty(final String key, final Object value, final RequestContext reqCtx) {
        configurationService().storeProperty(key, value, reqCtx);
    }

    @Override
    public final void storeProperty(final String key, final Object value) {
        configurationService().storeProperty(key, value);
    }

    @Override
    public final List<ConfigurationProperty> getConfigProperties(final String regex) {
        return configurationService().findProperties(regex);
    }

    @Override
    public final ConfigurationProperty getConfigProperty(final String key) {
        ConfigurationProperty property = null;

        try {
            if (previewHeaderExists() || refreshHeaderExists()) {
                String registryKey = new StringBuilder("cp/").append(key).toString();

                property = registryGet(registryKey);

                if (property == null) {
                    property = configurationService().findProperty(key);
                    registryPut(registryKey, property);
                }
            } else {
                property = configurationService().findProperty(key);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }

        return property;
    }

    @Override
    public final ConfigurationProperty getGlobalConfigProperty(final String key) {
        ConfigurationProperty property = null;

        try {
            if (previewHeaderExists() || refreshHeaderExists()) {
                String registryKey = new StringBuilder("cp-g/").append(key).toString();

                property = registryGet(registryKey);

                if (property == null) {
                    property = configurationService().findGlobalProperty(key);
                    registryPut(registryKey, property);
                }
            } else {
                property = configurationService().findGlobalProperty(key);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }

        return property;
    }

    @Override
    public final ConfigurationProperty getConfigProperty(final String key, final Store store) {
        ConfigurationProperty property = configurationService().findProperty(key, store);

        return property;
    }

    @Override
    public final Map<String, Object> getConfigMapShortKeys(final String prefix) {
        List<ConfigurationProperty> properties = configurationService().findProperties(prefix);

        if (properties == null)
            return null;

        Map<String, Object> map = new HashMap<>();

        for (ConfigurationProperty property : properties) {
            String key = property.getKey().substring(prefix.length());

            if (key.charAt(0) == DELIMITER)
                key = key.substring(1);

            map.put(key, property.getValue());
        }

        return map;
    }

    @Override
    public final Object cpVal(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null || cp.getValue() == null ? null : cp.getValue().getVal();
    }

    @Override
    public final String cpStr_(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getStringValue();
    }

    @Override
    public final String cpStr_(final String key, final String defaultValue) {
        String s = cpStr_(key);

        return s == null ? defaultValue : s;
    }

    @Override
    public final Double cpDouble_(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getDoubleValue();
    }

    @Override
    public final Double cpDouble_(final String key, final Double defaultValue) {
        Double d = cpDouble_(key);

        return d == null ? defaultValue : d;
    }

    @Override
    public final Long cpLong_(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getLongValue();
    }

    @Override
    public final Long cpLong_(final String key, final Long defaultValue) {
        Long l = cpLong_(key);

        return l == null ? defaultValue : l;
    }

    @Override
    public final Integer cpInt_(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getIntegerValue();
    }

    @Override
    public final Integer cpInt_(final String key, final Integer defaultValue) {
        Integer i = cpInt_(key);

        return i == null ? defaultValue : i;
    }

    @Override
    public final Float cpFloat_(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getFloatValue();
    }

    @Override
    public final Float cpFloat_(final String key, final Float defaultValue) {
        Float f = cpFloat_(key);

        return f == null ? defaultValue : f;
    }

    @Override
    public final Boolean cpBool_(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getBooleanValue();
    }

    @Override
    public final Boolean cpBool_(final String key, final Boolean defaultValue) {
        Boolean b = cpBool_(key);

        return b == null ? defaultValue : b;
    }

    @Override
    public final <E extends Enum<E>> E cpEnum_(final String key, final Class<E> enumType) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getEnumValue(enumType);
    }

    @Override
    public final <E extends Enum<E>> E cpEnum_(final String key, final Class<E> enumType, final E defaultValue) {
        E e = cpEnum_(key, enumType);

        return e == null ? defaultValue : e;
    }

    @Override
    public final Date cpDate_(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getDateValue();
    }

    @Override
    public final Date cpDate_(final String key, final Date defaultValue) {
        Date d = cpDate_(key);

        return d == null ? defaultValue : d;
    }

    @Override
    public final List<String> cpStrList_(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getStrings();
    }

    @Override
    public final List<String> cpStrList_(final String key, final List<String> defaultValue) {
        List<String> list = cpStrList_(key);

        return list == null ? defaultValue : list;
    }

    @Override
    public final Map<String, String> cpStrMap_(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getStringMap();
    }

    @Override
    public final Map<String, String> cpStrMap_(final String key, final Map<String, String> defaultValue) {
        Map<String, String> map = cpStrMap_(key);

        return map == null ? defaultValue : map;
    }

    @Override
    public final Map<String, Object> getMap(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getMap();
    }

    @Override
    public final Map<String, Object> getMap(final String key, final Map<String, Object> defaultValue) {
        Map<String, Object> map = getMap(key);

        return map == null ? defaultValue : map;
    }

    @Override
    public final List<Integer> cpIntList_(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getIntegerValues();
    }

    @Override
    public final List<Integer> cpIntList_(final String key, final List<Integer> defaultValue) {
        List<Integer> intList = cpIntList_(key);

        return intList == null ? defaultValue : intList;
    }

    @Override
    public final List<Long> cpLongList_(final String key) {
        ConfigurationProperty cp = getConfigProperty(key);

        return cp == null ? null : cp.getLongValues();
    }

    @Override
    public final Object cpVal__(final String key) {
        ConfigurationProperty cp = getGlobalConfigProperty(key);

        return cp == null ? null : cp.getValue();
    }

    @Override
    public final String cpStr__(final String key) {
        ConfigurationProperty cp = getGlobalConfigProperty(key);

        return cp == null ? null : cp.getStringValue();
    }

    @Override
    public final String cpStr__(final String key, final String defaultValue) {
        String s = cpStr__(key);

        return s == null ? defaultValue : s;
    }

    @Override
    public final Object cpVal_S(final String key, final Id storeId) {
        Store s = getApplicationContext().getMerchant().getStore(storeId);
        ConfigurationProperty cp = getConfigProperty(key, s);

        return cp == null ? null : cp.getValue();
    }

    @Override
    public final String cpStr_S(final String key, final Id storeId) {
        Store s = getApplicationContext().getMerchant().getStore(storeId);
        ConfigurationProperty cp = getConfigProperty(key, s);

        return cp == null ? null : cp.getStringValue();
    }

    @Override
    public final String cpStr_S(final String key, final String defaultValue, final Id storeId) {
        String s = cpStr_S(key, storeId);
        return s == null ? defaultValue : s;
    }

    @Override
    public final Boolean cpBool__(final String key) {
        ConfigurationProperty cp = getGlobalConfigProperty(key);

        return cp == null ? null : cp.getBooleanValue();
    }

    @Override
    public final Boolean cpBool__(final String key, final Boolean defaultValue) {
        Boolean b = cpBool__(key);

        return b == null ? defaultValue : b;
    }

    @Override
    public final Boolean cpBool_S(final String key, final Id storeId) {
        Store s = getApplicationContext().getMerchant().getStore(storeId);
        ConfigurationProperty cp = getConfigProperty(key, s);

        return cp == null ? null : cp.getBooleanValue();
    }

    @Override
    public final Boolean cpBool_S(final String key, final Boolean defaultValue, final Id storeId) {
        Boolean b = cpBool_S(key, storeId);
        return b == null ? defaultValue : b;
    }

    @Override
    public final Attribute attr(final Id attributeId) {
        return getRepository(Attributes.class).findById(Attribute.class, attributeId);
    }

    @Override
    public final Attribute attr(final String code, final String targetObjectCode) {
        AttributeTargetObject targetObject = getRepository(AttributeTargetObjects.class).havingCode(targetObjectCode);
        return getRepository(Attributes.class).havingCode(targetObject, code);
    }

    @Override
    public final Attribute attr(final String code, final Class<? extends AttributeSupport> modelClass) {
        AttributeTargetObject targetObject = getRepository(AttributeTargetObjects.class).forType(modelClass);
        return getRepository(Attributes.class).havingCode(targetObject, code);
    }

    @Override
    public final AttributeOption option(final Id optionId) {
        return getRepository(AttributeOptions.class).findById(AttributeOption.class, optionId);
    }

    @Override
    public final SystemConfig getSystemConfig() {
        return SystemConfig.GET;
    }

    @Override
    public final void setModuleLoader(final ModuleLoader moduleLoader) {
        registryPut(ModuleLoader.class.getName(), moduleLoader);
    }

    @Override
    public final ModuleLoader getModuleLoader() {
        return (ModuleLoader) registryGet(ModuleLoader.class.getName());
    }

    @Override
    public final void setTargetModule(final Module module) {
        registryPut(KEY_TARGET_MODULE, module);
    }

    @Override
    public final Module getTargetModule() {
        return (Module) registryGet(KEY_TARGET_MODULE);
    }

    @Override
    public final void setApplicationContext(final ApplicationContext applicationCtx) {
        AppRegistry.put(ApplicationContext.class.getName(), applicationCtx);
    }

    @Override
    public final ApplicationContext getApplicationContext() {
        return AppRegistry.get(ApplicationContext.class.getName());
    }

    @Override
    public final void setApplicationInitialized(Boolean isInitialized) {
        AppRegistry.put(KEY_IS_APP_INITIALIZED, isInitialized);
    }

    @Override
    public final boolean isApplicationInitialized() {
        return AppRegistry.get(KEY_IS_APP_INITIALIZED, false) == true && getApplicationContext() != null && getModuleLoader() != null;
    }

    @Override
    public final ContextTree getContextTree() {
        return getSystemService(SystemService.class).getContextTree();
    }

    @Override
    public final void registryPut(final String key, final Object value) {
        AppRegistry.put(key, value);
    }

    @Override
    public final void registryPut(final RegistryKey key, final Object value) {
        AppRegistry.put(key.key(), value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T registryGet(final String key) {
        return (T) AppRegistry.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T registryGet(final String key, final T defaultValue) {
        T val = (T) AppRegistry.get(key);
        return val == null ? defaultValue : val;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T registryGet(final RegistryKey key) {
        return (T) AppRegistry.get(key.key());
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T registryGet(final RegistryKey key, final T defaultValue) {
        T val = (T) AppRegistry.get(key.key());
        return val == null ? defaultValue : val;
    }

    @Override
    public final void registryRemove(final String key) {
        AppRegistry.remove(key);
    }

    @Override
    public final void registryRemove(final RegistryKey key) {
        AppRegistry.remove(key.key());
    }

    @Override
    public final void registryClear() {
        AppRegistry.clear();
    }

    @Override
    public final Set<String> registryKeys() {
        return AppRegistry.keySet();
    }

    @Override
    public final ConfigurationService configurationService() {
        return SystemInjector.get().getInstance(ConfigurationService.class);
    }

    @Override
    public final UrlRewriteService urlRewriteService() {
        return SystemInjector.get().getInstance(UrlRewriteService.class);
    }

    @Override
    public final String getSecureBasePath() {
        RequestContext requestCtx = getApplicationContext().getRequestContext();

        String httpsScheme = cpStr_(HTTPS_SCHEME_CONFIG_KEY, DEFAULT_HTTPS_SCHEME);

        return new StringBuilder(httpsScheme).append(Str.PROTOCOL_SUFFIX).append(requestCtx.getUrlPrefix()).toString();
    }

    @Override
    public final String getBasePath() {
        RequestContext requestCtx = getApplicationContext().getRequestContext();

        String httpScheme = cpStr_(HTTP_SCHEME_CONFIG_KEY, DEFAULT_HTTP_SCHEME);

        return new StringBuilder(httpScheme).append(Str.PROTOCOL_SUFFIX).append(requestCtx.getUrlPrefix()).toString();
    }

    @Override
    public final String getWebappSkinPath() {
        return new StringBuilder(getBaseWebappPath()).append(File.separatorChar).append(SKIN_DIR).toString();
    }

    @Override
    public final String getWebappJsPath() {
        return new StringBuilder(getBaseWebappPath()).append(File.separatorChar).append(JS_DIR).toString();
    }

    @Override
    public final String getWebappResourcePath() {
        return new StringBuilder(getBaseWebappPath()).append(File.separatorChar).append(RESOURCES_DIR).toString();
    }

    @Override
    public final String getProjectSkinPath() {
        return new StringBuilder(getProjectWebPath()).append(File.separatorChar).append(SKIN_DIR).toString();
    }

    @Override
    public final String getProjectJsPath() {
        return new StringBuilder(getProjectWebPath()).append(File.separatorChar).append(JS_DIR).toString();
    }

    @Override
    public final String getProjectResourcePath() {
        return new StringBuilder(getProjectWebPath()).append(File.separatorChar).append(RESOURCES_DIR).toString();
    }

    @Override
    public final String getProjectWebPath() {
        ApplicationContext appCtx = getApplicationContext();
        Merchant m = appCtx.getMerchant();

        return m.getWebPath();
    }

    @Override
    public final String getBaseWebappPath() {
        ServletContext servletCtx = getServletContext();

        String baseWebappPath = null;

        if (servletCtx != null) {
            baseWebappPath = servletCtx.getRealPath(Str.EMPTY);

        }
        // Non web-call made perhaps by unit-tests or main applications.
        else {
            baseWebappPath = SystemConfig.GET.val(SystemConfig.APPLICATION_WEBAPP_PATH);
        }

        return baseWebappPath;
    }

    @Override
    public final Id getModelIdIfExists() {
        if (getServletRequest() == null)
            return null;

        return (Id) getServletRequest().getAttribute("currentId");
    }

    @Override
    public final Class<?> getControllerClass() {
        if (getServletRequest() == null)
            return null;

        return (Class<?>) getServletRequest().getAttribute(GeemvcKey.CONTROLLER_CLASS);
    }

    @Override
    public final <T> T getCartFromSession() {
        return sessionGet(Constant.SESSION_KEY_CART);
    }

    @Override
    public final <T> void setLoggedInCustomer(final T customer) {
        sessionSet(Constant.SESSION_KEY_LOGGED_IN_CUTOMER, new SessionValue((Model) customer));
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T getLoggedInCustomer() {
        SessionValue loggedInCustomer = sessionGet(Constant.SESSION_KEY_LOGGED_IN_CUTOMER);

        return loggedInCustomer == null ? null : (T) loggedInCustomer.get();
    }

    @Override
    public final boolean isCustomerLoggedIn() {
        return sessionGet(Constant.SESSION_KEY_LOGGED_IN_CUTOMER) == null ? false : true;
    }

    @Override
    public final ClientSession getLoggedInUser() {
        Subject subject = SecurityUtils.getSubject();
        Session sess = null;

        if (subject != null && subject.isAuthenticated())
            sess = subject.getSession(false);

        return sess == null ? null : (ClientSession) sess.getAttribute(KEY_AUTHENTICATED_CLIENT);
    }

    @Override
    public final boolean isUserLoggedIn() {
        return getLoggedInUser() == null ? false : true;
    }

    @Override
    public final void cookieSet(String key, Object value) {
        cookieSet(key, value, null);
    }

    @Override
    public final void cookieSet(final String key, final Object value, final Integer maxAge) {
        HttpServletResponse response = getServletResponse();

        Cookie cookie = new Cookie(key, value.toString());
        cookie.setPath(Str.SLASH);

        if (maxAge != null) {
            cookie.setMaxAge(maxAge);
        }

        response.addCookie(cookie);
    }

    @Override
    public final void cookieUnset(final String key) {
        cookieSet(key, Str.EMPTY, 0);
    }

    @Override
    public final String cookieGet(final String key) {
        Cookie[] cookies = getServletRequest().getCookies();
        String value = null;

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    value = cookie.getValue();
                    break;
                }
            }
        }

        return value;
    }

    @Override
    public final void sessionInit() {
        if (getServletRequest() == null)
            return;

        getServletRequest().getSession(true);
    }

    @Override
    public final void sessionInvalidate() {
        if (getServletRequest() == null)
            return;

        HttpSession session = getServletRequest().getSession(false);

        if (session != null)
            session.invalidate();
    }

    @Override
    public final void sessionSet(final String key, final Object value) {
        sessionSet(key, value, true);
    }

    @Override
    public final void sessionSet(final String key, final Object value, boolean createSession) {
        if (getServletRequest() == null)
            return;

        HttpSession session = getServletRequest().getSession(createSession);

        if (session != null)
            session.setAttribute(key, value);
    }

    @Override
    public final void sessionRemove(final String key) {
        if (getServletRequest() == null)
            return;

        HttpSession session = getServletRequest().getSession(false);

        if (session != null)
            session.removeAttribute(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T sessionGet(final String key) {
        if (getServletRequest() == null)
            return null;

        HttpSession session = getServletRequest().getSession(false);

        if (session == null)
            return null;

        return (T) session.getAttribute(key);
    }

    @Override
    public final void setServletContext(final ServletContext servletContext) {
        registryPut(RegistryKey.SERVLET_CONTEXT, servletContext);
    }

    @Override
    public final ServletContext getServletContext() {
        return registryGet(RegistryKey.SERVLET_CONTEXT);
    }

    @Override
    public final void setServletRequest(final HttpServletRequest servletRequest) {
        registryPut(RegistryKey.SERVLET_REQUEST, servletRequest);
    }

    @Override
    public HttpServletRequest getServletRequest() {
        return registryGet(RegistryKey.SERVLET_REQUEST);
    }

    @Override
    public final void setServletResponse(final HttpServletResponse servletResponse) {
        registryPut(RegistryKey.SERVLET_RESPONSE, servletResponse);
    }

    @Override
    public HttpServletResponse getServletResponse() {
        return registryGet(RegistryKey.SERVLET_RESPONSE);
    }

    @Override
    public final boolean isDevToolbar() {
        return cpBool_(ConfigurationKey.DEV_TOOLBAR, false);
    }

    @Override
    public final boolean isDevPrintErrorMessages() {
        return cpBool_(ConfigurationKey.DEV_PRINT_ERRORS, false);
    }

    @Override
    public final void enableTestMode() {
        registryPut(ConfigurationKey.TEST_MODE, true);
    }

    @Override
    public final boolean isTestMode() {
        return registryGet(ConfigurationKey.TEST_MODE, false);
    }

    @Override
    public final boolean isDevMode() {
        return EnvProps.MODE_DEV.equals(EnvProps.GET.val(EnvProps.MODE));
    }

    @Override
    public final String getVersion() {
        return EnvProps.GET.val(EnvProps.VERSION);
    }

    @Override
    public final String getServer() {
        return EnvProps.GET.val(EnvProps.SERVER);
    }

    @Override
    public final String getClientIpAddress() {
        HttpServletRequest request = getServletRequest();
        if (request == null)
            return null;

        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
