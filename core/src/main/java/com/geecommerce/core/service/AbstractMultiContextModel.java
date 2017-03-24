package com.geecommerce.core.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractMultiContextModel extends AbstractModel implements MultiContextModel {
    private static final long serialVersionUID = 36664335972546427L;

    @Column(GlobalColumn.MERCHANT_ID)
    protected List<Id> merchantIds = null;

    @Column(GlobalColumn.STORE_ID)
    protected List<Id> storeIds = null;

    @Column(GlobalColumn.REQUEST_CONTEXT_ID)
    protected List<Id> requestContextIds = null;

    @Override
    public List<Id> getMerchantIds() {
        return merchantIds;
    }

    @Override
    public Id getFirstMerchantId() {
        return merchantIds == null || merchantIds.size() == 0 ? null : merchantIds.get(0);
    }

    @Override
    public void setMerchantIds(List<Id> merchantIds) {
        this.merchantIds = merchantIds;
    }

    @Override
    public void setMerchantId(Id merchantId) {
        this.merchantIds = Lists.newArrayList(merchantId);
    }

    @Override
    public void addMerchantId(Id merchantId) {
        if (this.merchantIds == null) {
            this.merchantIds = new ArrayList<>();
        }

        if (!this.merchantIds.contains(merchantId)) {
            this.merchantIds.add(merchantId);
        }
    }

    @Override
    public void addMerchantIds(Id... merchantIds) {
        if (merchantIds == null || merchantIds.length == 0)
            return;

        if (this.merchantIds == null) {
            this.merchantIds = new ArrayList<>();
        }

        for (Id merchantId : merchantIds) {
            if (!this.merchantIds.contains(merchantId)) {
                this.merchantIds.add(merchantId);
            }
        }
    }

    @Override
    public void addMerchant(Merchant merchant) {
        if (merchant == null || merchant.getId() == null)
            return;

        addMerchantId(merchant.getId());
    }

    @Override
    public boolean isFor(Merchant merchant) {
        if (merchant == null || merchant.getId() == null || this.merchantIds == null || this.merchantIds.size() == 0)
            return false;

        return this.merchantIds.contains(merchant.getId());
    }

    @Override
    public List<Id> getStoreIds() {
        return storeIds;
    }

    @Override
    public Id getFirstStoreId() {
        return storeIds == null || storeIds.size() == 0 ? null : storeIds.get(0);
    }

    @Override
    public void setStoreIds(List<Id> storeIds) {
        this.storeIds = storeIds;
    }

    @Override
    public void setStoreId(Id storeId) {
        this.storeIds = Lists.newArrayList(storeId);
    }

    @Override
    public void addStoreId(Id storeId) {
        if (this.storeIds == null) {
            this.storeIds = new ArrayList<>();
        }

        if (!this.storeIds.contains(storeId)) {
            this.storeIds.add(storeId);
        }
    }

    @Override
    public void addStoreIds(Id... storeIds) {
        if (storeIds == null || storeIds.length == 0)
            return;

        if (this.storeIds == null) {
            this.storeIds = new ArrayList<>();
        }

        for (Id storeId : storeIds) {
            if (!this.storeIds.contains(storeId)) {
                this.storeIds.add(storeId);
            }
        }
    }

    @Override
    public void addStore(Store store) {
        if (store == null || store.getId() == null)
            return;

        addStoreId(store.getId());
    }

    @Override
    public boolean isFor(Store store) {
        if (store == null || store.getId() == null || this.storeIds == null || this.storeIds.size() == 0)
            return false;

        return this.storeIds.contains(store.getId());
    }

    @Override
    public List<Id> getRequestContextIds() {
        return requestContextIds;
    }

    @Override
    public Id getFirstRequestContextId() {
        return requestContextIds == null || requestContextIds.size() == 0 ? null : requestContextIds.get(0);
    }

    @Override
    public void setRequestContextIds(List<Id> requestContextIds) {
        this.requestContextIds = requestContextIds;
    }

    @Override
    public void setRequestContextId(Id requestContextId) {
        this.requestContextIds = Lists.newArrayList(requestContextId);
    }

    @Override
    public void addRequestContextId(Id requestContextId) {
        if (this.requestContextIds == null) {
            this.requestContextIds = new ArrayList<>();
        }

        if (!this.requestContextIds.contains(requestContextId)) {
            this.requestContextIds.add(requestContextId);
        }
    }

    @Override
    public void addRequestContextIds(Id... requestContextIds) {
        if (requestContextIds == null || requestContextIds.length == 0)
            return;

        if (this.requestContextIds == null) {
            this.requestContextIds = new ArrayList<>();
        }

        for (Id requestContextId : requestContextIds) {
            if (!this.requestContextIds.contains(requestContextId)) {
                this.requestContextIds.add(requestContextId);
            }
        }
    }

    @Override
    public void addRequestContext(RequestContext reqCtx) {
        if (reqCtx == null || reqCtx.getId() == null)
            return;

        addRequestContextId(reqCtx.getId());
    }

    @Override
    public boolean isFor(RequestContext reqCtx) {
        if (reqCtx == null || reqCtx.getId() == null || this.requestContextIds == null
            || this.requestContextIds.size() == 0)
            return false;

        return this.requestContextIds.contains(reqCtx.getId());
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        if (map == null || map.size() == 0)
            return;

        this.merchantIds = idList_(map.get(GlobalColumn.MERCHANT_ID));
        this.storeIds = idList_(map.get(GlobalColumn.STORE_ID));
        this.requestContextIds = idList_(map.get(GlobalColumn.REQUEST_CONTEXT_ID));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>(super.toMap());

        if (getMerchantIds() != null && getMerchantIds().size() > 0)
            map.put(GlobalColumn.MERCHANT_ID, getMerchantIds());
        else {
            map.put(GlobalColumn.MERCHANT_ID, null);
        }

        if (getStoreIds() != null && getStoreIds().size() > 0)
            map.put(GlobalColumn.STORE_ID, getStoreIds());
        else
            map.put(GlobalColumn.STORE_ID, null);


        if (getRequestContextIds() != null && getRequestContextIds().size() > 0)
            map.put(GlobalColumn.REQUEST_CONTEXT_ID, getRequestContextIds());
        else
            map.put(GlobalColumn.REQUEST_CONTEXT_ID, null);

        return map;
    }
}
