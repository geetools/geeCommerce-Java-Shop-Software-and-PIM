package com.geecommerce.core.system.model;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.geecommerce.core.Char;
import com.geecommerce.core.enums.UrlType;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Cacheable
@Model("request_contexts")
public class DefaultRequestContext extends AbstractModel implements RequestContext {
    private static final long serialVersionUID = 2082572472587143131L;
    @Column(Col.ID)
    private Id id = null;
    @Column(Col.MERCHANT_ID)
    private Id merchantId = null;
    @Column(Col.STORE_ID)
    private Id storeId = null;
    @Column(Col.LANGUAGE)
    private String language = null;
    @Column(Col.COUNTRY)
    private String country = null;
    @Column(Col.VIEW_ID)
    private Id viewId = null;
    @Column(Col.URL_PREFIX)
    private String urlPrefix = null;
    @Column(Col.URL_TYPE)
    private UrlType urlType = null;
    @Column(Col.SORT_INDEX)
    private Integer sortIndex = null;

    private String cacheKey = null;

    public DefaultRequestContext() {
        super();
    }

    public DefaultRequestContext(Id id, Id merchantId, Id storeId, String language, String country, Id viewId,
        String urlPrefix, UrlType urlType, Integer sortIndex) {
        super();
        this.id = id;
        this.merchantId = merchantId;
        this.storeId = storeId;
        this.language = language;
        this.country = country;
        this.viewId = viewId;
        this.urlPrefix = urlPrefix;
        this.urlType = urlType;
        this.sortIndex = sortIndex;

        initCacheKey();
    }

    public DefaultRequestContext(Id id, Id merchantId, Id storeId, String language, String country, Id viewId) {
        super();
        this.id = id;
        this.merchantId = merchantId;
        this.storeId = storeId;
        this.language = language;
        this.country = country;
        this.viewId = viewId;

        initCacheKey();
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public RequestContext setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getMerchantId() {
        return merchantId;
    }

    @Override
    public RequestContext setMerchantId(Id merchantId) {
        this.merchantId = merchantId;
        return this;
    }

    @Override
    public Id getStoreId() {
        return storeId;
    }

    @Override
    public RequestContext setStoreId(Id storeId) {
        this.storeId = storeId;
        return this;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public RequestContext setLanguage(String language) {
        this.language = language;
        return this;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public RequestContext setCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public Id getViewId() {
        return viewId;
    }

    @Override
    public RequestContext setViewId(Id viewId) {
        this.viewId = viewId;
        return this;
    }

    @Override
    public String getUrlPrefix() {
        return urlPrefix;
    }

    @Override
    public RequestContext setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
        return this;
    }

    @Override
    public UrlType getUrlType() {
        return urlType;
    }

    @Override
    public RequestContext setUrlType(UrlType urlType) {
        this.urlType = urlType;
        return this;
    }

    @Override
    public Integer getSortIndex() {
        return sortIndex;
    }

    @Override
    public RequestContext setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
        return this;
    }

    public Locale getLocale() {
        if (language == null)
            return null;

        if (country == null) {
            return new Locale(language);
        } else {
            return new Locale(language, country);
        }
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.merchantId = id_(map.get(Col.MERCHANT_ID));
        this.storeId = id_(map.get(Col.STORE_ID));
        this.language = str_(map.get(Col.LANGUAGE));
        this.country = str_(map.get(Col.COUNTRY));
        this.viewId = id_(map.get(Col.VIEW_ID));
        this.urlPrefix = str_(map.get(Col.URL_PREFIX));
        this.sortIndex = int_(map.get(Col.SORT_INDEX));

        if (map.get(Col.URL_TYPE) != null)
            this.urlType = enum_(UrlType.class, map.get(Col.URL_TYPE));

        initCacheKey();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>(super.toMap());

        map.put(Col.ID, getId());
        map.put(Col.MERCHANT_ID, getMerchantId());
        map.put(Col.STORE_ID, getStoreId());
        map.put(Col.LANGUAGE, getLanguage());
        map.put(Col.COUNTRY, getCountry());
        map.put(Col.VIEW_ID, getViewId());
        map.put(Col.URL_PREFIX, getUrlPrefix());
        map.put(Col.SORT_INDEX, getSortIndex());

        if (getUrlType() != null)
            map.put(Col.URL_TYPE, getUrlType().toId());

        return map;
    }

    private void initCacheKey() {
        if (this.cacheKey == null) {
            if (id == null) {
                StringBuilder cacheKey = new StringBuilder();

                if (this.merchantId != null)
                    cacheKey.append(this.merchantId.str());

                if (this.storeId != null)
                    cacheKey.append(Char.UNDERSCORE).append(this.storeId.str());

                if (this.viewId != null)
                    cacheKey.append(Char.UNDERSCORE).append(this.viewId.str());

                if (this.language != null)
                    cacheKey.append(Char.UNDERSCORE).append(this.language);

                if (this.country != null)
                    cacheKey.append(Char.UNDERSCORE).append(this.country);

                if (this.urlPrefix != null)
                    cacheKey.append(Char.AT).append(this.urlPrefix);

                this.cacheKey = cacheKey.toString();
            } else {
                this.cacheKey = new StringBuilder(id.str()).append(Char.AT).append(urlPrefix).toString();
            }
        }
    }

    @Override
    public String toKey() {
        if (id == null)
            return null;

        return this.cacheKey == null ? id.str() : this.cacheKey;
    }

    @Override
    public String toString() {
        return "DefaultRequestContext [id=" + id + ", merchantId=" + merchantId + ", storeId=" + storeId + ", language="
            + language + ", country=" + country + ", viewId=" + viewId + ", urlPrefix=" + urlPrefix + ", urlType="
            + urlType + "]";
    }
}
