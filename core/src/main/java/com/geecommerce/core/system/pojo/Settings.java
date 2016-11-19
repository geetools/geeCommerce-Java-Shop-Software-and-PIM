package com.geecommerce.core.system.pojo;

import java.io.Serializable;
import java.util.List;

import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.Country;
import com.geecommerce.core.system.model.Currency;
import com.geecommerce.core.system.model.Language;
import com.geecommerce.core.system.model.RequestContext;

public class Settings implements Serializable {
    private static final long serialVersionUID = -3097385740607941608L;
    private List<RequestContext> requestContexts = null;
    private List<Language> availableUserLanguages = null;
    private List<Language> availableLaguages = null;
    private List<Country> availableCountries = null;
    private List<Currency> availableCurrencies = null;
    private String defaultEditLanguage = null;
    private String fallbackEditLanguage = null;
    private String defaultUserLanguage = null;
    private String fallbackUserLanguage = null;
    private String productImagesSubdomain = null;
    private String productImagesUriPrefix = null;
    private List<String> preloadAttributes = null;
    private List<Merchant> merchants = null;
    private List<Store> stores = null;
    private Integer timezoneOffset = null;
    private String logoURI = null;
    private String logoText = null;
    private String loginLogoURI = null;
    private String loginLogoText = null;

    public Settings() {

    }

    public List<RequestContext> getRequestContexts() {
        return requestContexts;
    }

    public Settings setRequestContexts(List<RequestContext> requestContexts) {
        this.requestContexts = requestContexts;
        return this;
    }

    public List<Language> getAvailableUserLanguages() {
        return availableUserLanguages;
    }

    public Settings setAvailableUserLanguages(List<Language> availableUserLanguages) {
        this.availableUserLanguages = availableUserLanguages;
        return this;
    }

    public List<Language> getAvailableLaguages() {
        return availableLaguages;
    }

    public Settings setAvailableLaguages(List<Language> availableLaguages) {
        this.availableLaguages = availableLaguages;
        return this;
    }

    public List<Country> getAvailableCountries() {
        return availableCountries;
    }

    public Settings setAvailableCountries(List<Country> availableCountries) {
        this.availableCountries = availableCountries;
        return this;
    }

    public List<Currency> getAvailableCurrencies() {
        return availableCurrencies;
    }

    public Settings setAvailableCurrencies(List<Currency> availableCurrencies) {
        this.availableCurrencies = availableCurrencies;
        return this;
    }

    public String getDefaultEditLanguage() {
        return defaultEditLanguage;
    }

    public Settings setDefaultEditLanguage(String defaultEditLanguage) {
        this.defaultEditLanguage = defaultEditLanguage;
        return this;
    }

    public String getFallbackEditLanguage() {
        return fallbackEditLanguage;
    }

    public Settings setFallbackEditLanguage(String fallbackEditLanguage) {
        this.fallbackEditLanguage = fallbackEditLanguage;
        return this;
    }

    public String getDefaultUserLanguage() {
        return defaultUserLanguage;
    }

    public Settings setDefaultUserLanguage(String defaultUserLanguage) {
        this.defaultUserLanguage = defaultUserLanguage;
        return this;
    }

    public String getFallbackUserLanguage() {
        return fallbackUserLanguage;
    }

    public Settings setFallbackUserLanguage(String fallbackUserLanguage) {
        this.fallbackUserLanguage = fallbackUserLanguage;
        return this;
    }

    public String getProductImagesSubdomain() {
        return productImagesSubdomain;
    }

    public Settings setProductImagesSubdomain(String productImagesSubdomain) {
        this.productImagesSubdomain = productImagesSubdomain;
        return this;
    }

    public String getProductImagesUriPrefix() {
        return productImagesUriPrefix;
    }

    public Settings setProductImagesUriPrefix(String productImagesUriPrefix) {
        this.productImagesUriPrefix = productImagesUriPrefix;
        return this;
    }

    public List<String> getPreloadAttributes() {
        return preloadAttributes;
    }

    public Settings setPreloadAttributes(List<String> preloadAttributes) {
        this.preloadAttributes = preloadAttributes;
        return this;
    }

    public List<Merchant> getMerchants() {
        return merchants;
    }

    public Settings setMerchants(List<Merchant> merchants) {
        this.merchants = merchants;
        return this;
    }

    public List<Store> getStores() {
        return stores;
    }

    public Settings setStores(List<Store> stores) {
        this.stores = stores;
        return this;
    }

    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    public Settings setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
        return this;
    }

    public String getLogoURI() {
        return logoURI;
    }

    public Settings setLogoURI(String logoURI) {
        this.logoURI = logoURI;
        return this;
    }

    public String getLogoText() {
        return logoText;
    }

    public Settings setLogoText(String logoText) {
        this.logoText = logoText;
        return this;
    }

    public String getLoginLogoURI() {
        return loginLogoURI;
    }

    public Settings setLoginLogoURI(String loginLogoURI) {
        this.loginLogoURI = loginLogoURI;
        return this;
    }

    public String getLoginLogoText() {
        return loginLogoText;
    }

    public Settings setLoginLogoText(String loginLogoText) {
        this.loginLogoText = loginLogoText;
        return this;
    }
}
