package com.geecommerce.catalog.product.repository;

import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductConnectionIndex;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;

public interface ProductConnectionIndexes extends Repository {
    public ProductConnectionIndex forProduct(Product product);

    public ProductConnectionIndex forProduct(Id productId);

    public Map<Id, ProductConnectionIndex> forProducts(List<Product> products);

    public Map<Id, ProductConnectionIndex> forProducts(Id... productIds);
}
