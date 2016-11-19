package com.geecommerce.catalog.product.cron.helper;

import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.type.Id;

public interface ProductHelper extends Helper {
    public List<Id> getProductIds();

    public List<Id> getAllProductIds();

    public Product getProduct(Id productId);

    public Product findProductByArticleNumber(String article);

    public Map<String, Id> allArticleNumbers();

    public void update(Product product);
}
