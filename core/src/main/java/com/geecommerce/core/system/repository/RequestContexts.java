package com.geecommerce.core.system.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;

public interface RequestContexts extends Repository {
    public List<RequestContext> forValues(Merchant merchant, Store store, String language, String country, View view);

    public List<RequestContext> forStore(Store store);

    public List<RequestContext> forMerchant(Merchant merchant);

    public List<RequestContext> forHost(String host);

    public RequestContext forUrlPrefix(String urlPrefix);

    public List<RequestContext> forScopes(List<Id> scopeIds);
}
