package com.geecommerce.core.service.api;

import java.util.List;

import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;

public interface MultiContextModel extends Model {
    public List<Id> getMerchantIds();

    public Id getFirstMerchantId();

    public void setMerchantIds(List<Id> merchantIds);

    public void setMerchantId(Id merchantId);

    public void addMerchantId(Id merchantId);

    public void addMerchantIds(Id... merchantIds);

    public void addMerchant(Merchant merchant);

    public boolean isFor(Merchant merchant);

    public List<Id> getStoreIds();

    public Id getFirstStoreId();

    public void setStoreIds(List<Id> storeIds);

    public void setStoreId(Id storeId);

    public void addStoreId(Id storeId);

    public void addStoreIds(Id... storeIds);

    public void addStore(Store store);

    public boolean isFor(Store store);

    public List<Id> getRequestContextIds();

    public Id getFirstRequestContextId();

    public void setRequestContextIds(List<Id> requestContextIds);

    public void setRequestContextId(Id requestContextId);

    public void addRequestContextId(Id requestContextId);

    public void addRequestContextIds(Id... requestContextIds);

    public void addRequestContext(RequestContext reqCtx);

    public boolean isFor(RequestContext reqCtx);
}
