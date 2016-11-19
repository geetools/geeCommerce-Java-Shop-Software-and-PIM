package com.geecommerce.catalog.product.elasticsearch.helper;

import java.util.Map;
import java.util.Set;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Helper;

public interface ElasticsearchProductHelper extends Helper {

    public  Map<String, Object> buildJsonProduct(String id, Product product);
}
