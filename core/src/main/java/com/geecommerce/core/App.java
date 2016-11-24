package com.geecommerce.core;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.inject.SystemInjector;
import com.geecommerce.core.message.Context;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.QueryMetadata;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.Pojo;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.ConfigurationProperty;
import com.geecommerce.core.system.model.ContextMessage;
import com.geecommerce.core.system.model.ContextTree;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.service.ConfigurationService;
import com.geecommerce.core.system.service.UrlRewriteService;
import com.geecommerce.core.system.user.pojo.ClientSession;
import com.geecommerce.core.type.Id;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleLoader;

public interface App {

    public static App get() {
        return SystemInjector.get().getInstance(App.class);
    }

    Attribute attr(final Id attributeId);

    Attribute attr(final String code, final Class<? extends AttributeSupport> modelClass);

    Attribute attr(final String code, final String targetObjectCode);

    void bootstrap(final HttpServletRequest request, final HttpServletResponse response);

    <K, V> Cache<K, V> cache(String name);

    ConfigurationService configurationService();

    ContextMessage contextMessage(final String message);

    ContextMessage contextMessage(final String message, final String lang);

    ContextMessage contextMessage(final String message, final String lang, final String message2, final String lang2);

    ContextMessage contextMessage(final String message, final String lang, final String message2, final String lang2,
        final String message3, final String lang3);

    String cookieGet(final String key);

    void cookieSet(String key, Object value);

    void cookieSet(final String key, final Object value, final Integer maxAge);

    void cookieUnset(final String key);

    Boolean cpBool_(final String key);

    Boolean cpBool_(final String key, final Boolean defaultValue);

    Boolean cpBool__(final String key);

    Boolean cpBool__(final String key, final Boolean defaultValue);

    Boolean cpBool_S(final String key, final Boolean defaultValue, final Id storeId);

    Boolean cpBool_S(final String key, final Id storeId);

    Date cpDate_(final String key);

    Date cpDate_(final String key, final Date defaultValue);

    Double cpDouble_(final String key);

    Double cpDouble_(final String key, final Double defaultValue);

    <E extends Enum<E>> E cpEnum_(final String key, final Class<E> enumType);

    <E extends Enum<E>> E cpEnum_(final String key, final Class<E> enumType, final E defaultValue);

    Float cpFloat_(final String key);

    Float cpFloat_(final String key, final Float defaultValue);

    Integer cpInt_(final String key);

    Integer cpInt_(final String key, final Integer defaultValue);

    List<Integer> cpIntList_(final String key);

    List<Integer> cpIntList_(final String key, final List<Integer> defaultValue);

    Long cpLong_(final String key);

    Long cpLong_(final String key, final Long defaultValue);

    List<Long> cpLongList_(final String key);

    String cpStr_(final String key);

    String cpStr_(final String key, final String defaultValue);

    String cpStr__(final String key);

    String cpStr__(final String key, final String defaultValue);

    String cpStr_S(final String key, final Id storeId);

    String cpStr_S(final String key, final String defaultValue, final Id storeId);

    List<String> cpStrList_(final String key);

    List<String> cpStrList_(final String key, final List<String> defaultValue);

    Map<String, String> cpStrMap_(final String key);

    Map<String, String> cpStrMap_(final String key, final Map<String, String> defaultValue);

    Object cpVal(final String key);

    Object cpVal__(final String key);

    Object cpVal_S(final String key, final Id storeId);

    void disableBatchMode();

    boolean editAllowed();

    boolean editHeaderExists();

    void enableBatchMode();

    void enableTestMode();

    void finalizeBatch();

    String getActionURI();

    ApplicationContext context();

    String getBaseCurrency();

    String getBasePath();

    String getBaseWebappPath();

    <T> T getCartFromSession();

    String getClientIpAddress();

    Map<String, Object> getConfigMapShortKeys(final String prefix);

    List<ConfigurationProperty> getConfigProperties(final String regex);

    ConfigurationProperty getConfigProperty(final String key);

    ConfigurationProperty getConfigProperty(final String key, final Store store);

    ContextTree getContextTree();

    Class<?> getControllerClass();

    Locale getCurrentLocale();

    Module getCurrentModule();

    String getDefaultLanguage();

    String getDisplayCurrency();

    ConfigurationProperty getGlobalConfigProperty(final String key);

    <T extends Helper> T helper(final Class<T> helper);

    <T extends Helper> Class<T> helperType(final Class<T> helper);

    <T extends Injectable> T injectable(final Class<T> injectable);

    <T extends Injectable> Class<T> injectableType(final Class<T> injectable);

    QueryMetadata getLastQueryMetadata();

    <T> T getLoggedInCustomer();

    ClientSession getLoggedInUser();

    Map<String, Object> getMap(final String key);

    Map<String, Object> getMap(final String key, final Map<String, Object> defaultValue);

    <T extends Model> T model(final Class<T> model);

    <T extends Model> Class<T> modelType(final Class<T> model);

    Id getModelIdIfExists();

    ModuleLoader moduleLoader();

    String getOriginalQueryString();

    String getOriginalURI();

    <T extends Pojo> T pojo(final Class<T> pojo);

    <T extends Pojo> Class<T> pojoType(final Class<T> pojo);

    String getProjectJsPath();

    String getProjectResourcePath();

    String getProjectSkinPath();

    String getProjectWebPath();

    <T extends Repository> T repository(final Class<T> repository);

    <T extends Repository> Class<T> repositoryType(final Class<T> repository);

    String getRewrittenURI();

    String getSecureBasePath();

    String getServer();

    <T extends Service> T service(final Class<T> service);

    <T extends Service> Class<T> serviceType(final Class<T> service);

    ServletContext servletContext();

    HttpServletRequest servletRequest();

    HttpServletResponse servletResponse();

    Id getStoreFromHeader();

    Store getStoreFromMerchant(Id storeId);

    String getSystemCharset();

    SystemConfig systemConfig();

    <T extends Service> T systemService(final Class<T> service);

    Module getTargetModule();

    UrlRewrite getUrlRewrite(final String uri);

    String getVersion();

    String getViewPath();

    String getWebappJsPath();

    String getWebappResourcePath();

    String getWebappSkinPath();

    boolean hasPageExtension();

    void init();

    void initRequestType();

    <T> T inject(final Class<T> type);

    void injectMembers(final Object instance);

    boolean isAjaxRequest();

    boolean isAPIRequest();

    boolean isApplicationInitialized();

    boolean isCustomerLoggedIn();

    boolean isDevMode();

    boolean isDevPrintErrorMessages();

    boolean isDevToolbar();

    boolean isErrorPage();

    boolean isExternalHost(String host);

    boolean isGetRequest();

    boolean isInObserverThread();

    boolean isMediaRequest();

    boolean isMultipartRequest();

    boolean isPostRequest();

    boolean isSecureRequest();

    boolean isTemplateRequest();

    boolean isTestMode();

    boolean isUserLoggedIn();

    boolean isWebRequest();

    String message(final String message);

    String message(final String message, final String lang);

    String message(final String defMessage, final String message, String lang);

    String message(final String message, final String lang, final String message2, final String lang2);

    String message(final String message, final String lang, final String message2, final String lang2,
        final String message3, final String lang3);

    Id nextId();

    <T> T nextIncrementId(String name);

    Id nextSequenceNumber(final String seqName);

    AttributeOption option(final Id optionId);

    boolean previewHeaderExists();

    void publish(final String message, final Context ctx);

    void publish(final String message, final String key, final Object value);

    void publish(final String message, final String key1, final Object value1, final String key2, final Object value2);

    void publish(final String message, final String key1, final Object value1, final String key2, final Object value2,
        final String key3, final Object value3);

    boolean refreshHeaderExists();

    void registryClear();

    <T> T registryGet(final RegistryKey key);

    <T> T registryGet(final RegistryKey key, final T defaultValue);

    <T> T registryGet(final String key);

    <T> T registryGet(final String key, final T defaultValue);

    Set<String> registryKeys();

    void registryPut(final RegistryKey key, final Object value);

    void registryPut(final String key, final Object value);

    void registryRemove(final RegistryKey key);

    void registryRemove(final String key);

    <T> T sessionGet(final String key);

    void sessionInit();

    void sessionInvalidate();

    void sessionRemove(final String key);

    void sessionSet(final String key, final Object value);

    void sessionSet(final String key, final Object value, boolean createSession);

    void setActionURI(final String actionURI);

    void setApplicationContext(final ApplicationContext applicationCtx);

    void setApplicationInitialized(Boolean isInitialized);

    void setCurrentModule(Module module);

    <T> void setLoggedInCustomer(final T customer);

    void setModuleLoader(final ModuleLoader moduleLoader);

    void setOriginalQueryString(final String queryString);

    void setOriginalURI(final String uri);

    void setQueryMetadata(final QueryMetadata queryMetadata);

    void setRewrittenURI(final String uri);

    void setServletContext(final ServletContext servletContext);

    void setServletRequest(final HttpServletRequest servletRequest);

    void setServletResponse(final HttpServletResponse servletResponse);

    void setTargetModule(final Module module);

    void setViewPath(final String viewPath);

    boolean storeHeaderExists();

    void storeProperty(final String key, final Object value);

    void storeProperty(final String key, final Object value, final Merchant merchant);

    void storeProperty(final String key, final Object value, final RequestContext reqCtx);

    void storeProperty(final String key, final Object value, final Store store);

    UrlRewriteService urlRewriteService();
}
