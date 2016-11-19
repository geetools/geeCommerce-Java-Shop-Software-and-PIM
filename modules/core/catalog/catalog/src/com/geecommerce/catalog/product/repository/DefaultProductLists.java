package com.geecommerce.catalog.product.repository;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;

@Repository
public class DefaultProductLists extends AbstractRepository implements ProductLists {
    @Override
    public ProductList havingKey(String key) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(ProductList.Col.KEY, key);
        filter.put(ProductList.Col.ENABLED, true);

        return multiContextFindOne(ProductList.class, filter);
    }
}
