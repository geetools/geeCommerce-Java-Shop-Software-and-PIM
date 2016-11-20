package com.geecommerce.catalog.product.cron.helper;

import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.repository.Prices;
import com.google.inject.Inject;

@Helper
public class DefaultProductHelper implements ProductHelper {
    private final Products products;
    private final Prices prices;

    @Inject
    public DefaultProductHelper(Products products, Prices prices) {
        this.products = products;
        this.prices = prices;
    }

    @Override
    public List<Id> getProductIds() {
        return products.noneDeletedIdsForContext();
    }

    @Override
    public List<Id> getAllProductIds() {
        return products.allIdsForContext();
    }

    @Override
    public Map<String, Id> allArticleNumbers() {
        return products.allArticleNumbers();
    }

    @Override
    public Product getProduct(Id productId) {
        return products.findById(Product.class, productId);
    }

    @Override
    public Product findProductByArticleNumber(String articleNumber) {
        return products.havingArticleNumber(articleNumber);
    }

    @Override
    public void update(Product product) {
        products.update(product);
    }
}
