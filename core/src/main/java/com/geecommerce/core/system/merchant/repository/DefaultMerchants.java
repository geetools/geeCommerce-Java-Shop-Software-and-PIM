package com.geecommerce.core.system.merchant.repository;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.type.Id;

public class DefaultMerchants extends AbstractRepository implements Merchants {
    @Override
    public Merchant havingStoreId(Id storeId) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put("stores._id", storeId);

        return findOne(Merchant.class, filter);
    }

    @Override
    public Merchant havingViewId(Id viewId) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put("views._id", viewId);

        return findOne(Merchant.class, filter);
    }
}
