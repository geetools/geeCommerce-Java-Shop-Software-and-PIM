package com.geecommerce.core.system.service;

import java.util.List;

import com.google.inject.Inject;
import com.geecommerce.core.enums.Scope;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.merchant.repository.Merchants;
import com.geecommerce.core.system.model.ContextNode;
import com.geecommerce.core.system.model.ContextTree;
import com.geecommerce.core.system.model.Language;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.repository.Languages;
import com.geecommerce.core.system.repository.RequestContexts;
import com.geecommerce.core.type.Id;

public class DefaultSystemService implements SystemService {
    private final RequestContexts requestContexts;
    private final Merchants merchants;
    private final Languages languages;

    @Inject
    public DefaultSystemService(RequestContexts requestContexts, Merchants merchants, Languages languages) {
        this.requestContexts = requestContexts;
        this.merchants = merchants;
        this.languages = languages;
    }

    /* RequestContext */

    @Override
    public void createRequestContext(RequestContext requestContext) {
        requestContexts.add(requestContext);
    }

    @Override
    public RequestContext getRequestContext(Id requestContextId) {
        return requestContexts.findById(RequestContext.class, requestContextId);
    }

    @Override
    public List<RequestContext> getAllRequestContexts() {
        return requestContexts.findAll(RequestContext.class);
    }

    @Override
    public List<RequestContext> getRequestContextsForScopes(List<Id> scopeIds) {
        return requestContexts.forScopes(scopeIds);
    }

    @Override
    public RequestContext findRequestContext(Merchant merchant, Store store, String language, String country, View view) {
        List<RequestContext> requestContexts = findRequestContexts(merchant, store, language, country, view);

        if (requestContexts == null || requestContexts.isEmpty())
            return null;

        if (requestContexts.size() > 1)
            throw new IllegalStateException("Unable to return a unique request-context. More than 1 found for the parameters [merchant=" + (merchant == null ? null : merchant.getId()) + ", store="
                + (store == null ? null : store.getId())
                + ", language=" + language + ", country=" + country + ", view=" + (view == null ? null : view.getId()) + "]");

        return requestContexts.get(0);
    }

    @Override
    public List<RequestContext> findRequestContexts(Merchant merchant, Store store, String language, String country, View view) {
        return requestContexts.forValues(merchant, store, language, country, view);
    }

    @Override
    public List<RequestContext> findRequestContextsForHost(String host) {
        return requestContexts.forHost(host);
    }

    @Override
    public RequestContext findRequestContextsForUrlPrefix(String urlPrefix) {
        return requestContexts.forUrlPrefix(urlPrefix);
    }

    /* Merchant */

    @Override
    public void createNewMerchant(Merchant merchant) {
        merchants.add(merchant);
    }

    @Override
    public Merchant findMerchantBy(Id id) {
        return merchants.findById(Merchant.class, id);
    }

    @Override
    public Merchant findMerchantByStoreId(Id storeId) {
        return merchants.havingStoreId(storeId);
    }

    @Override
    public Merchant findMerchantByViewId(Id viewId) {
        return merchants.havingViewId(viewId);
    }

    /**
     * Get a tree representation of all request-contexts.
     */
    @Override
    public ContextTree getContextTree() {
        return getContextTree(getAllRequestContexts());
    }

    /**
     * Get a tree representation of all request-contexts.
     */
    @Override
    public ContextTree getContextTree(List<RequestContext> requestContexts) {
        ContextNode rootNode = new ContextNode();

        for (RequestContext reqCtx : requestContexts) {
            Merchant m = findMerchantBy(reqCtx.getMerchantId());

            ContextNode merchantNode = rootNode.addChild(reqCtx.getMerchantId(), Scope.MERCHANT, m.getCompanyName());

            Store s = m.getStoreFor(reqCtx);

            ContextNode storeNode = merchantNode.addChild(reqCtx.getStoreId(), Scope.STORE, s.getName());

            storeNode.addChild(reqCtx.getId(), Scope.REQUEST_CONTEXT, reqCtx.getUrlPrefix());
        }

        return new ContextTree(rootNode);
    }

    public List<Language> getLanguages() {
        return languages.findAll(Language.class);
    }
}
