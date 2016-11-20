package com.geecommerce.core.system.model;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.geecommerce.core.Char;
import com.geecommerce.core.enums.UrlType;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Cacheable
@Model("request_contexts")
public class DefaultRequestContext extends AbstractModel implements RequestContext {
    private static final long serialVersionUID = 2082572472587143131L;
    private Id id = null;
    private Id merchantId = null;
    private Id storeId = null;
    private String language = null;
    private String country = null;
    private Id viewId = null;
    private String urlPrefix = null;
    private UrlType urlType = null;
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
    public Id getMerchantId() {
        return merchantId;
    }

    @Override
    public Id getStoreId() {
        return storeId;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public Id getViewId() {
        return viewId;
    }

    @Override
    public String getUrlPrefix() {
        return urlPrefix;
    }

    @Override
    public UrlType getUrlType() {
        return urlType;
    }

    @Override
    public Integer getSortIndex() {
        return sortIndex;
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

        this.id = id_(map.get(Column.ID));
        this.merchantId = id_(map.get(Column.MERCHANT_ID));
        this.storeId = id_(map.get(Column.STORE_ID));
        this.language = str_(map.get(Column.LANGUAGE));
        this.country = str_(map.get(Column.COUNTRY));
        this.viewId = id_(map.get(Column.VIEW_ID));
        this.urlPrefix = str_(map.get(Column.URL_PREFIX));
        this.sortIndex = int_(map.get(Column.SORT_INDEX));

        if (map.get(Column.URL_TYPE) != null)
            this.urlType = UrlType.fromId(int_(map.get(Column.URL_TYPE)));

        initCacheKey();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>(super.toMap());

        map.put(Column.ID, getId());
        map.put(Column.MERCHANT_ID, getMerchantId());
        map.put(Column.STORE_ID, getStoreId());
        map.put(Column.LANGUAGE, getLanguage());
        map.put(Column.COUNTRY, getCountry());
        map.put(Column.VIEW_ID, getViewId());
        map.put(Column.URL_PREFIX, getUrlPrefix());
        map.put(Column.SORT_INDEX, getSortIndex());

        if (getUrlType() != null)
            map.put(Column.URL_TYPE, getUrlType().toId());

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
