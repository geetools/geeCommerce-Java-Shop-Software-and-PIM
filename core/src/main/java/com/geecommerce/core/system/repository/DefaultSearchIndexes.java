package com.geecommerce.core.system.repository;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.model.SearchIndex;
import com.geecommerce.core.type.Id;
import com.mongodb.QueryBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DefaultSearchIndexes extends AbstractRepository implements SearchIndexes {

    @Override
    public SearchIndex forValues(Merchant merchant, Store store, RequestContext requestContext) {
        Map<String, Object> filter = new LinkedHashMap<>();

        if (merchant != null)
            filter.put(GlobalColumn.MERCHANT_ID, merchant.getId());

        if (store != null)
            filter.put(GlobalColumn.STORE_ID, store.getId());

        if (requestContext != null)
            filter.put(GlobalColumn.REQUEST_CONTEXT_ID, requestContext.getId());

        return findOne(SearchIndex.class, filter);
    }

    @Override
    public SearchIndex forValues(Id merchantId, Id storeId, Id requestContextId) {
        Map<String, Object> filter = new LinkedHashMap<>();

        if (merchantId != null)
            filter.put(GlobalColumn.MERCHANT_ID, merchantId);

        if (storeId != null)
            filter.put(GlobalColumn.STORE_ID, storeId);

        if (requestContextId != null)
            filter.put(GlobalColumn.REQUEST_CONTEXT_ID, requestContextId);

        return findOne(SearchIndex.class, filter);
    }

}
