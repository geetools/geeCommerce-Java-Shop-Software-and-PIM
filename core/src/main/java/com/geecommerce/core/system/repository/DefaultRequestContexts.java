package com.geecommerce.core.system.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.mongodb.QueryBuilder;

public class DefaultRequestContexts extends AbstractRepository implements RequestContexts {

    @Override
    public List<RequestContext> forValues(Merchant merchant, Store store, String language, String country, View view) {
        Map<String, Object> filter = new LinkedHashMap<>();

        if (merchant != null)
            filter.put(RequestContext.Column.MERCHANT_ID, merchant.getId());

        if (store != null)
            filter.put(RequestContext.Column.STORE_ID, store.getId());

        if (language != null)
            filter.put(RequestContext.Column.LANGUAGE, language);

        if (country != null)
            filter.put(RequestContext.Column.COUNTRY, country);

        if (view != null)
            filter.put(RequestContext.Column.VIEW_ID, view.getId());

        return find(RequestContext.class, filter);
    }

    @Override
    public List<RequestContext> forHost(String host) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(RequestContext.Column.URL_PREFIX, Pattern.compile(new StringBuilder('^').append(host).toString()));

        return find(RequestContext.class, filter,
            QueryOptions.builder().sortBy(RequestContext.Column.SORT_INDEX).build());
    }

    @Override
    public RequestContext forUrlPrefix(String urlPrefix) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(RequestContext.Column.URL_PREFIX, urlPrefix);

        return findOne(RequestContext.class, filter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RequestContext> forScopes(List<Id> scopeIds) {
        QueryBuilder query = new QueryBuilder().or(QueryBuilder.start(RequestContext.Column.ID).in(scopeIds).get(),
            QueryBuilder.start(RequestContext.Column.STORE_ID).in(scopeIds).get(),
            QueryBuilder.start(RequestContext.Column.MERCHANT_ID).in(scopeIds).get());

        return find(RequestContext.class, query.get().toMap());
    }

    @Override
    public List<RequestContext> forStore(Store store) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(RequestContext.Column.STORE_ID, store.getId());

        return find(RequestContext.class, filter,
            QueryOptions.builder().sortBy(RequestContext.Column.SORT_INDEX).build());
    }

    @Override
    public List<RequestContext> forMerchant(Merchant merchant) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(RequestContext.Column.MERCHANT_ID, merchant.getId());

        return find(RequestContext.class, filter,
            QueryOptions.builder().sortBy(RequestContext.Column.SORT_INDEX).build());
    }
}
