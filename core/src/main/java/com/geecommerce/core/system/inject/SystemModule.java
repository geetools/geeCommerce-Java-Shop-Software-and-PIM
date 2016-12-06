package com.geecommerce.core.system.inject;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.apache.shiro.realm.Realm;

import com.geecommerce.core.App;
import com.geecommerce.core.DefaultApp;
import com.geecommerce.core.batch.dataimport.helper.DefaultImportHelper;
import com.geecommerce.core.batch.dataimport.helper.ImportHelper;
import com.geecommerce.core.batch.dataimport.model.DefaultImportField;
import com.geecommerce.core.batch.dataimport.model.DefaultImportFieldScriptlet;
import com.geecommerce.core.batch.dataimport.model.DefaultImportProfile;
import com.geecommerce.core.batch.dataimport.model.DefaultImportToken;
import com.geecommerce.core.batch.dataimport.model.ImportField;
import com.geecommerce.core.batch.dataimport.model.ImportFieldScriptlet;
import com.geecommerce.core.batch.dataimport.model.ImportProfile;
import com.geecommerce.core.batch.dataimport.model.ImportToken;
import com.geecommerce.core.batch.dataimport.repository.DefaultImportFieldScriptlets;
import com.geecommerce.core.batch.dataimport.repository.DefaultImportProfiles;
import com.geecommerce.core.batch.dataimport.repository.DefaultImportTokens;
import com.geecommerce.core.batch.dataimport.repository.ImportFieldScriptlets;
import com.geecommerce.core.batch.dataimport.repository.ImportProfiles;
import com.geecommerce.core.batch.dataimport.repository.ImportTokens;
import com.geecommerce.core.batch.service.DefaultImportExportService;
import com.geecommerce.core.batch.service.ImportExportService;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.cache.DefaultCache;
import com.geecommerce.core.cache.DefaultCacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.elasticsearch.api.search.Facet;
import com.geecommerce.core.elasticsearch.api.search.FacetEntry;
import com.geecommerce.core.elasticsearch.api.search.SearchResult;
import com.geecommerce.core.elasticsearch.helper.DefaultElasticsearchHelper;
import com.geecommerce.core.elasticsearch.helper.DefaultElasticsearchIndexHelper;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchIndexHelper;
import com.geecommerce.core.elasticsearch.search.DefaultFacet;
import com.geecommerce.core.elasticsearch.search.DefaultFacetEntry;
import com.geecommerce.core.elasticsearch.search.DefaultSearchResult;
import com.geecommerce.core.elasticsearch.service.DefaultElasticsearchService;
import com.geecommerce.core.elasticsearch.service.ElasticsearchService;
import com.geecommerce.core.interceptor.GuiceMethodInterceptor;
import com.geecommerce.core.interceptor.annotation.Interceptable;
import com.geecommerce.core.rest.repository.DefaultRestRepository;
import com.geecommerce.core.rest.repository.RestRepository;
import com.geecommerce.core.rest.service.DefaultRestService;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.security.DefaultRealm;
import com.geecommerce.core.security.PermissionInterceptor;
import com.geecommerce.core.service.TransactionInterceptor;
import com.geecommerce.core.service.annotation.Transactional;
import com.geecommerce.core.service.persistence.DefaultPersistenceProvider;
import com.geecommerce.core.service.persistence.PersistenceProvider;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeGroup;
import com.geecommerce.core.system.attribute.model.AttributeGroupMapping;
import com.geecommerce.core.system.attribute.model.AttributeInputCondition;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.attribute.model.DefaultAttribute;
import com.geecommerce.core.system.attribute.model.DefaultAttributeGroup;
import com.geecommerce.core.system.attribute.model.DefaultAttributeGroupMapping;
import com.geecommerce.core.system.attribute.model.DefaultAttributeInputCondition;
import com.geecommerce.core.system.attribute.model.DefaultAttributeOption;
import com.geecommerce.core.system.attribute.model.DefaultAttributeTargetObject;
import com.geecommerce.core.system.attribute.model.DefaultAttributeValue;
import com.geecommerce.core.system.attribute.repository.AttributeGroups;
import com.geecommerce.core.system.attribute.repository.AttributeOptions;
import com.geecommerce.core.system.attribute.repository.AttributeTargetObjects;
import com.geecommerce.core.system.attribute.repository.Attributes;
import com.geecommerce.core.system.attribute.repository.DefaultAttributeGroups;
import com.geecommerce.core.system.attribute.repository.DefaultAttributeOptions;
import com.geecommerce.core.system.attribute.repository.DefaultAttributeTargetObjects;
import com.geecommerce.core.system.attribute.repository.DefaultAttributes;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.system.attribute.service.DefaultAttributeService;
import com.geecommerce.core.system.cpanel.attribute.model.AttributeTab;
import com.geecommerce.core.system.cpanel.attribute.model.AttributeTabMapping;
import com.geecommerce.core.system.cpanel.attribute.model.DefaultAttributeTab;
import com.geecommerce.core.system.cpanel.attribute.model.DefaultAttributeTabMapping;
import com.geecommerce.core.system.cpanel.model.ControlPanel;
import com.geecommerce.core.system.cpanel.model.DefaultControlPanel;
import com.geecommerce.core.system.helper.ContextMessageHelper;
import com.geecommerce.core.system.helper.DefaultContextMessageHelper;
import com.geecommerce.core.system.helper.DefaultTargetSupportHelper;
import com.geecommerce.core.system.helper.DefaultUrlRewriteHelper;
import com.geecommerce.core.system.helper.TargetSupportHelper;
import com.geecommerce.core.system.helper.UrlRewriteHelper;
import com.geecommerce.core.system.merchant.model.Contact;
import com.geecommerce.core.system.merchant.model.DefaultContact;
import com.geecommerce.core.system.merchant.model.DefaultMerchant;
import com.geecommerce.core.system.merchant.model.DefaultStore;
import com.geecommerce.core.system.merchant.model.DefaultView;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.merchant.repository.DefaultMerchants;
import com.geecommerce.core.system.merchant.repository.Merchants;
import com.geecommerce.core.system.model.ConfigurationProperty;
import com.geecommerce.core.system.model.ContextMessage;
import com.geecommerce.core.system.model.Country;
import com.geecommerce.core.system.model.Currency;
import com.geecommerce.core.system.model.DefaultConfigurationProperty;
import com.geecommerce.core.system.model.DefaultContextMessage;
import com.geecommerce.core.system.model.DefaultCountry;
import com.geecommerce.core.system.model.DefaultCurrency;
import com.geecommerce.core.system.model.DefaultLanguage;
import com.geecommerce.core.system.model.DefaultRequestContext;
import com.geecommerce.core.system.model.DefaultUrlRewrite;
import com.geecommerce.core.system.model.Language;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.Configurations;
import com.geecommerce.core.system.repository.ContextMessages;
import com.geecommerce.core.system.repository.DefaultConfigurations;
import com.geecommerce.core.system.repository.DefaultContextMessages;
import com.geecommerce.core.system.repository.DefaultLanguages;
import com.geecommerce.core.system.repository.DefaultRequestContexts;
import com.geecommerce.core.system.repository.DefaultUrlRewrites;
import com.geecommerce.core.system.repository.Languages;
import com.geecommerce.core.system.repository.RequestContexts;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.system.service.ConfigurationService;
import com.geecommerce.core.system.service.ContextMessageService;
import com.geecommerce.core.system.service.DefaultConfigurationService;
import com.geecommerce.core.system.service.DefaultContextMessageService;
import com.geecommerce.core.system.service.DefaultSystemService;
import com.geecommerce.core.system.service.DefaultUrlRewriteService;
import com.geecommerce.core.system.service.SystemService;
import com.geecommerce.core.system.service.UrlRewriteService;
import com.geecommerce.core.system.user.model.DefaultPermission;
import com.geecommerce.core.system.user.model.DefaultRole;
import com.geecommerce.core.system.user.model.DefaultUser;
import com.geecommerce.core.system.user.model.Permission;
import com.geecommerce.core.system.user.model.Role;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.system.user.repository.DefaultPermissions;
import com.geecommerce.core.system.user.repository.DefaultRoles;
import com.geecommerce.core.system.user.repository.DefaultUsers;
import com.geecommerce.core.system.user.repository.Permissions;
import com.geecommerce.core.system.user.repository.Roles;
import com.geecommerce.core.system.user.repository.Users;
import com.geecommerce.core.system.user.service.DefaultUserService;
import com.geecommerce.core.system.user.service.UserService;
import com.geecommerce.core.system.widget.model.DefaultWidget;
import com.geecommerce.core.system.widget.model.DefaultWidgetGroup;
import com.geecommerce.core.system.widget.model.DefaultWidgetParameter;
import com.geecommerce.core.system.widget.model.DefaultWidgetParameterOption;
import com.geecommerce.core.system.widget.model.DefaultWidgetParameterTab;
import com.geecommerce.core.system.widget.model.DefaultWidgetParameterTabItem;
import com.geecommerce.core.system.widget.model.Widget;
import com.geecommerce.core.system.widget.model.WidgetGroup;
import com.geecommerce.core.system.widget.model.WidgetParameter;
import com.geecommerce.core.system.widget.model.WidgetParameterOption;
import com.geecommerce.core.system.widget.model.WidgetParameterTab;
import com.geecommerce.core.system.widget.model.WidgetParameterTabItem;
import com.geecommerce.core.system.widget.repository.DefaultWidgetParameterOptions;
import com.geecommerce.core.system.widget.repository.DefaultWidgetParameterTabs;
import com.geecommerce.core.system.widget.repository.DefaultWidgetParameters;
import com.geecommerce.core.system.widget.repository.WidgetParameterOptions;
import com.geecommerce.core.system.widget.repository.WidgetParameterTabs;
import com.geecommerce.core.system.widget.repository.WidgetParameters;
import com.geecommerce.core.web.geemvc.intercept.DefaultHandlerInterceptor;
import com.geecommerce.core.web.geemvc.intercept.HandlerInterceptor;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;

public class SystemModule extends AbstractModule {
    private static CacheManager cacheManager = null;

    @Override
    protected void configure() {
        super.bind(App.class).to(DefaultApp.class).in(Singleton.class);

        // Daos
        super.bind(PersistenceProvider.class).to(DefaultPersistenceProvider.class).in(Singleton.class);

        super.bind(Connections.class).in(Singleton.class);

        // Rest
        super.bind(RestRepository.class).to(DefaultRestRepository.class).in(Singleton.class);
        super.bind(RestService.class).to(DefaultRestService.class).in(Singleton.class);

        // Interceptors
        super.bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), new TransactionInterceptor());
        super.bindInterceptor(Matchers.any(), Matchers.annotatedWith(Interceptable.class), new GuiceMethodInterceptor());

        // super.bindInterceptor(Matchers.annotatedWith(Service.class),
        // Matchers.any(), new ProfilerInterceptor());
        // super.bindInterceptor(Matchers.annotatedWith(Repository.class),
        // Matchers.any(), new ProfilerInterceptor());
        // super.bindInterceptor(Matchers.annotatedWith(Dao.class),
        // Matchers.any(), new ProfilerInterceptor());
        // super.bindInterceptor(Matchers.annotatedWith(Helper.class),
        // Matchers.any(), new ProfilerInterceptor());
        // super.bindInterceptor(Matchers.annotatedWith(Profile.class),
        // Matchers.any(), new ProfilerInterceptor());

        PermissionInterceptor permissionInterceptor = new PermissionInterceptor();
        bindInterceptor(Matchers.annotatedWith(Path.class), Matchers.annotatedWith(GET.class), permissionInterceptor);
        bindInterceptor(Matchers.annotatedWith(Path.class), Matchers.annotatedWith(PUT.class), permissionInterceptor);
        bindInterceptor(Matchers.annotatedWith(Path.class), Matchers.annotatedWith(POST.class), permissionInterceptor);
        bindInterceptor(Matchers.annotatedWith(Path.class), Matchers.annotatedWith(DELETE.class), permissionInterceptor);
        bindInterceptor(Matchers.annotatedWith(Path.class), Matchers.annotatedWith(HEAD.class), permissionInterceptor);
        bindInterceptor(Matchers.annotatedWith(Path.class), Matchers.annotatedWith(OPTIONS.class), permissionInterceptor);

        // PermissionInterceptor permissionInterceptor = new
        // PermissionInterceptor();
        // bindInterceptor(Matchers.annotatedWith(Service.class),
        // Matchers.any(), permissionInterceptor);
        // bindInterceptor(Matchers.any(),
        // Matchers.annotatedWith(NotOnWeekends.class), permissionInterceptor);

        bind(HandlerInterceptor.class).to(DefaultHandlerInterceptor.class);

        // System Service
        super.bind(SystemService.class).to(DefaultSystemService.class).in(Singleton.class);

        // RequestContext
        super.bind(RequestContext.class).to(DefaultRequestContext.class);
        super.bind(RequestContexts.class).to(DefaultRequestContexts.class).in(Singleton.class);

        // Language
        super.bind(Language.class).to(DefaultLanguage.class);
        super.bind(Languages.class).to(DefaultLanguages.class).in(Singleton.class);

        // Country
        super.bind(Country.class).to(DefaultCountry.class);

        // Currency
        super.bind(Currency.class).to(DefaultCurrency.class);

        // Merchant
        super.bind(Merchant.class).to(DefaultMerchant.class);
        super.bind(Merchants.class).to(DefaultMerchants.class).in(Singleton.class);
        super.bind(Contact.class).to(DefaultContact.class);
        super.bind(Store.class).to(DefaultStore.class);
        super.bind(View.class).to(DefaultView.class);

        // UrlRewrite
        super.bind(UrlRewrite.class).to(DefaultUrlRewrite.class);
        super.bind(UrlRewriteService.class).to(DefaultUrlRewriteService.class).in(Singleton.class);
        super.bind(UrlRewrites.class).to(DefaultUrlRewrites.class).in(Singleton.class);
        super.bind(UrlRewriteHelper.class).to(DefaultUrlRewriteHelper.class).in(Singleton.class);

        // Context Messages
        super.bind(ContextMessage.class).to(DefaultContextMessage.class);
        super.bind(ContextMessageService.class).to(DefaultContextMessageService.class).in(Singleton.class);
        super.bind(ContextMessages.class).to(DefaultContextMessages.class).in(Singleton.class);
        super.bind(ContextMessageHelper.class).to(DefaultContextMessageHelper.class).in(Singleton.class);

        // Configuration
        super.bind(ConfigurationProperty.class).to(DefaultConfigurationProperty.class);
        super.bind(ConfigurationService.class).to(DefaultConfigurationService.class).in(Singleton.class);
        super.bind(Configurations.class).to(DefaultConfigurations.class).in(Singleton.class);

        // Attribute
        super.bind(Attribute.class).to(DefaultAttribute.class);
        super.bind(AttributeService.class).to(DefaultAttributeService.class).in(Singleton.class);
        super.bind(Attributes.class).to(DefaultAttributes.class).in(Singleton.class);

        // AttributeGroup
        super.bind(AttributeGroup.class).to(DefaultAttributeGroup.class);
        super.bind(AttributeGroups.class).to(DefaultAttributeGroups.class).in(Singleton.class);
        super.bind(AttributeGroupMapping.class).to(DefaultAttributeGroupMapping.class);

        // AttributeOption
        super.bind(AttributeOption.class).to(DefaultAttributeOption.class);
        super.bind(AttributeOptions.class).to(DefaultAttributeOptions.class).in(Singleton.class);

        // AttributeValue
        super.bind(AttributeValue.class).to(DefaultAttributeValue.class);

        // AttributeInputCondition
        super.bind(AttributeInputCondition.class).to(DefaultAttributeInputCondition.class);

        // AttributeTargetObject
        super.bind(AttributeTargetObject.class).to(DefaultAttributeTargetObject.class);
        super.bind(AttributeTargetObjects.class).to(DefaultAttributeTargetObjects.class).in(Singleton.class);

        // Widget
        super.bind(Widget.class).to(DefaultWidget.class);
        super.bind(WidgetGroup.class).to(DefaultWidgetGroup.class);
        super.bind(WidgetParameterTab.class).to(DefaultWidgetParameterTab.class);
        super.bind(WidgetParameterTabs.class).to(DefaultWidgetParameterTabs.class).in(Singleton.class);
        super.bind(WidgetParameterTabItem.class).to(DefaultWidgetParameterTabItem.class);
        super.bind(WidgetParameter.class).to(DefaultWidgetParameter.class);
        super.bind(WidgetParameters.class).to(DefaultWidgetParameters.class).in(Singleton.class);
        super.bind(WidgetParameterOption.class).to(DefaultWidgetParameterOption.class);
        super.bind(WidgetParameterOptions.class).to(DefaultWidgetParameterOptions.class).in(Singleton.class);

        // Helpers
        super.bind(TargetSupportHelper.class).to(DefaultTargetSupportHelper.class).in(Singleton.class);
        super.bind(ElasticsearchIndexHelper.class).to(DefaultElasticsearchIndexHelper.class).in(Singleton.class);
        super.bind(ElasticsearchHelper.class).to(DefaultElasticsearchHelper.class).in(Singleton.class);
        super.bind(ElasticsearchService.class).to(DefaultElasticsearchService.class).in(Singleton.class);

        super.bind(Facet.class).to(DefaultFacet.class);
        super.bind(FacetEntry.class).to(DefaultFacetEntry.class);
        super.bind(SearchResult.class).to(DefaultSearchResult.class);

        // Caching
        super.bind(CacheManager.class).to(DefaultCacheManager.class).in(Singleton.class);
        super.bind(Cache.class).to(DefaultCache.class);
        // super.bind(Cache.class).to(Ehcache.class);

        // ----------------------------------------------------
        // Control Panel
        // ----------------------------------------------------

        // Security Realm
        super.bind(Realm.class).to(DefaultRealm.class);

        // User
        super.bind(User.class).to(DefaultUser.class);
        super.bind(Users.class).to(DefaultUsers.class).in(Singleton.class);
        super.bind(Role.class).to(DefaultRole.class);
        super.bind(Roles.class).to(DefaultRoles.class).in(Singleton.class);
        super.bind(Permission.class).to(DefaultPermission.class);
        super.bind(Permissions.class).to(DefaultPermissions.class).in(Singleton.class);
        super.bind(UserService.class).to(DefaultUserService.class).in(Singleton.class);

        super.bind(ControlPanel.class).to(DefaultControlPanel.class);
        super.bind(AttributeTab.class).to(DefaultAttributeTab.class);
        super.bind(AttributeTabMapping.class).to(DefaultAttributeTabMapping.class);

        // ----------------------------------------------------
        // Import / Export
        // ----------------------------------------------------
        super.bind(ImportToken.class).to(DefaultImportToken.class);
        super.bind(ImportTokens.class).to(DefaultImportTokens.class);
        super.bind(ImportProfile.class).to(DefaultImportProfile.class);
        super.bind(ImportProfiles.class).to(DefaultImportProfiles.class);
        super.bind(ImportField.class).to(DefaultImportField.class);
        super.bind(ImportFieldScriptlet.class).to(DefaultImportFieldScriptlet.class);
        super.bind(ImportFieldScriptlets.class).to(DefaultImportFieldScriptlets.class);
        super.bind(ImportExportService.class).to(DefaultImportExportService.class);
        super.bind(ImportHelper.class).to(DefaultImportHelper.class);

    }

    // @Provides
    // CacheManager provideCacheManager() {
    // if (cacheManager == null) {
    // cacheManager = new DefaultCacheManager();
    // }
    //
    // return cacheManager;
    // }
}