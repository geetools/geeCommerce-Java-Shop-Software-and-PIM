package com.geecommerce.core.system.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.model.SearchIndex;
import com.geecommerce.core.type.Id;

import java.util.List;

public interface SearchIndexes extends Repository {
    public SearchIndex forValues(Merchant merchant, Store store, RequestContext requestContext);

    public SearchIndex forValues(Id merchantId, Id storeId, Id requestContextId);

}
