package com.geecommerce.core.system.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.ContextTree;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;

public interface SystemService extends Service {
    public void createRequestContext(RequestContext requestContext);

    public RequestContext getRequestContext(Id requestContextId);

    public List<RequestContext> getAllRequestContexts();

    public List<RequestContext> getRequestContextsForScopes(List<Id> scopeIds);

    public RequestContext findRequestContext(Merchant merchant, Store store, String language, String country,
        View view);

    public List<RequestContext> findRequestContexts(Merchant merchant, Store store, String language, String country,
        View view);

    public List<RequestContext> findRequestContextsForHost(String host);

    public RequestContext findRequestContextsForUrlPrefix(String urlPrefix);

    public void createNewMerchant(Merchant merchant);

    public Merchant findMerchantBy(Id id);

    public Merchant findMerchantByStoreId(Id storeId);

    public Merchant findMerchantByViewId(Id viewId);

    public ContextTree getContextTree();

    public ContextTree getContextTree(List<RequestContext> requestContexts);
}
