package com.geecommerce.price.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.dao.PriceDao;
import com.geecommerce.price.model.Price;

@Repository
public class DefaultPrices extends AbstractRepository implements Prices {
    private final PriceDao priceDao;

    @Inject
    public DefaultPrices(PriceDao priceDao) {
        this.priceDao = priceDao;
    }

    @Override
    public Dao dao() {
        return this.priceDao;
    }

    @Override
    public Map<String, Object> getPriceData(Id productId, RequestContext requestCtx) {
        return priceDao.getPriceData(productId, requestCtx == null ? null : requestCtx.getId());
    }

    @Override
    public List<Price> belongingToProduct(Id productId, String currencyCode) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Price.Col.PRODUCT_ID, productId);
        filter.put(Price.Col.CURRENCY, currencyCode);

        return find(Price.class, filter);
    }

    @Override
    public List<Price> belongingToProducts(Id[] productIds, String currencyCode) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Price.Col.PRODUCT_ID, productIds);
        filter.put(Price.Col.CURRENCY, currencyCode);

        return find(Price.class, filter);
    }

    @Override
    public List<Price> belongingToProduct(Id productId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Price.Col.PRODUCT_ID, productId);

        return find(Price.class, filter);
    }

    @Override
    public List<Price> belongingToProducts(Id... productIds) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Price.Col.PRODUCT_ID, productIds);

        return find(Price.class, filter);
    }
}
