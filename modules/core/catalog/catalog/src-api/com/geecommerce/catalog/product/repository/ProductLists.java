package com.geecommerce.catalog.product.repository;

import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.core.service.api.Repository;

public interface ProductLists extends Repository {
    public ProductList havingKey(String key);
}
