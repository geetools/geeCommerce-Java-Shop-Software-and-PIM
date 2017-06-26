package com.geecommerce.core.system.model;

import com.geecommerce.core.Char;
import com.geecommerce.core.enums.UrlType;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Cacheable
@Model("search_indexer")
public class DefaultSearchIndex extends AbstractModel implements SearchIndex {

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.MERCHANT_ID)
    private Id merchantId = null;

    @Column(Col.STORE_ID)
    private Id storeId = null;

    @Column(Col.REQUEST_CONTEXT_ID)
    private Id requstContextId = null;

    @Column(Col.ENABLED)
    private boolean enabled = false;

    @Override
    public Id getMerchantId() {
        return merchantId;
    }

    @Override
    public SearchIndex setMerchantId(Id merchantId) {
        this.merchantId = merchantId;
        return this;
    }

    @Override
    public Id getStoreId() {
        return storeId;
    }

    @Override
    public SearchIndex setStoreId(Id storeId) {
        this.storeId = storeId;
        return this;
    }

    @Override
    public Id getRequestContextId() {
        return requstContextId;
    }

    @Override
    public SearchIndex setRequestContextId(Id requestContextId) {
        this.requstContextId = requestContextId;
        return this;
    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }

    @Override
    public SearchIndex setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public SearchIndex setId(Id id) {
        this.id = id;
        return this;
    }

}
