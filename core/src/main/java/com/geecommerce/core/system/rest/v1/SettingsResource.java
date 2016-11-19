package com.geecommerce.core.system.rest.v1;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTimeZone;

import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.model.Country;
import com.geecommerce.core.system.model.Currency;
import com.geecommerce.core.system.model.Language;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.pojo.Settings;
import com.geecommerce.core.system.repository.RequestContexts;
import com.google.inject.Inject;

@Path("/v1/settings")
public class SettingsResource extends AbstractResource {
    private final RestService service;
    private final RequestContexts requestContexts;
    private static String DEFAULT_LOCAL_TIMEZONE = "Europe/Berlin";

    @Inject
    public SettingsResource(RestService service, RequestContexts requestContexts) {
        this.service = service;
        this.requestContexts = requestContexts;
    }

    @GET
    @Path("languages")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getLanguages(@FilterParam Filter filter) {
        return ok(checked(service.get(Language.class, filter.getParams(), queryOptions(filter))));
    }

    @GET
    @Path("countries")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getCountries(@FilterParam Filter filter) {
        return ok(checked(service.get(Country.class, filter.getParams(), queryOptions(filter))));
    }

    @GET
    @Path("timezone")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getTimeZoneOffset(@FilterParam Filter filter) {
        DateTimeZone timeZone = DateTimeZone.forID(DEFAULT_LOCAL_TIMEZONE);
        int offset = timeZone.getOffset(new Date().getTime()) / 1000 / 60;
        return ok(offset);
    }

    @GET
    @Path("currencies")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getCurrencies(@FilterParam Filter filter) {
        return ok(checked(service.get(Currency.class, filter.getParams(), queryOptions(filter))));
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response get() {
        List<String> _availableUserLaguages = app.cpStrList_(ConfigurationKey.I18N_CPANEL_AVAILABLE_USER_LANGUAGES);
        List<String> _availableLaguages = app.cpStrList_(ConfigurationKey.I18N_AVAILABLE_LANGUAGES);
        List<String> _availableCountries = app.cpStrList_(ConfigurationKey.I18N_AVAILABLE_COUNTRIES);
        List<String> _availableCurrencies = app.cpStrList_(ConfigurationKey.I18N_AVAILABLE_CURRENCIES);

        String defaultEditLanguage = app.cpStr_(ConfigurationKey.I18N_CPANEL_DEFAULT_EDIT_LANGUAGE);
        String fallbackEditLanguage = app.cpStr_(ConfigurationKey.I18N_CPANEL_FALLBACK_EDIT_LANGUAGE);
        String defaultUserLanguage = app.cpStr_(ConfigurationKey.I18N_CPANEL_DEFAULT_USER_LANGUAGE);
        String fallbackUserLanguage = app.cpStr_(ConfigurationKey.I18N_CPANEL_FALLBACK_USER_LANGUAGE);

        // Languages
        Map<String, Object> filter = new HashMap<>();
        filter.put(Language.Col.ISO639_1, _availableLaguages);
        List<Language> availableLanguages = service.get(Language.class, filter);

        // Control Panel Languages
        filter = new HashMap<>();
        filter.put(Language.Col.ISO639_1, _availableUserLaguages);
        List<Language> availableUserLanguages = service.get(Language.class, filter);

        // Countries
        filter = new HashMap<>();
        filter.put(Country.Col.CODE, _availableCountries);
        List<Country> availableCountries = service.get(Country.class, filter, QueryOptions.builder().sortBy(Country.Col.NAME).build());

        // Currencies
        filter = new HashMap<>();
        filter.put(Currency.Col.CODE, _availableCurrencies);
        List<Currency> availableCurrencies = service.get(Currency.class, filter);

        // Request contexts
        List<RequestContext> reqContexts = requestContexts.forMerchant(app.getApplicationContext().getMerchant());

        // TimezoneOffset
        DateTimeZone timeZone = DateTimeZone.forID(DEFAULT_LOCAL_TIMEZONE);
        int offset = timeZone.getOffset(new Date().getTime()) / 1000 / 60;

        String productImagesSubdomain = app.cpStr_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_SUBDOMAIN);
        String productImagesUriPrefix = app.cpStr_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_WEBPATH);
        List<String> preloadAttributes = app.cpStrList_(ConfigurationKey.CPANEL_DATA_PRELOAD_ATTRIBUTES);

        String logoURI = app.cpStr_(ConfigurationKey.CPANEL_LOGO, "/img/logo.png");
        String logoText = app.cpStr_(ConfigurationKey.CPANEL_LOGO_TEXT, "CommerceBoard");
        String loginLogoURI = app.cpStr_(ConfigurationKey.CPANEL_LOGIN_LOGO, "/img/logo-login.png");
        String loginLogoText = app.cpStr_(ConfigurationKey.CPANEL_LOGIN_LOGO_TEXT, "CommerceBoard");

        ApplicationContext appCtx = app.getApplicationContext();
        Merchant merchant = appCtx.getMerchant();

        List<Merchant> merchants = new ArrayList<>();
        merchants.add(merchant);

        Settings settings = new Settings()
            .setRequestContexts(reqContexts)
            .setAvailableUserLanguages(availableUserLanguages)
            .setAvailableLaguages(availableLanguages)
            .setAvailableCountries(availableCountries)
            .setAvailableCurrencies(availableCurrencies)
            .setDefaultEditLanguage(defaultEditLanguage)
            .setFallbackEditLanguage(fallbackEditLanguage)
            .setDefaultUserLanguage(defaultUserLanguage)
            .setFallbackUserLanguage(fallbackUserLanguage)
            .setProductImagesSubdomain(productImagesSubdomain)
            .setProductImagesUriPrefix(productImagesUriPrefix)
            .setPreloadAttributes(preloadAttributes)
            .setMerchants(merchants)
            .setStores(merchant.getStores())
            .setTimezoneOffset(offset)
            .setLogoURI(logoURI)
            .setLogoText(logoText)
            .setLoginLogoURI(loginLogoURI)
            .setLoginLogoText(loginLogoText);

        return ok("settings", settings);
    }
}
