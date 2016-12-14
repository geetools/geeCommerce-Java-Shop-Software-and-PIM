package com.geecommerce.catalog.product.batch.dataimport.helper;

import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Helper;

public interface ProductBeanHelper extends Helper {
    void setSaleable(Product product, Map<String, String> data);

    void setVisibility(Product product, Map<String, String> data);

    void setProductKeys(Product product, Map<String, String> data);

    void setTypeAndGroup(Product product, Map<String, String> data);
}
