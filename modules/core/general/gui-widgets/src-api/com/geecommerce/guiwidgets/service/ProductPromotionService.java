package com.geecommerce.guiwidgets.service;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.guiwidgets.model.ProductPromotion;

import java.util.List;

public interface ProductPromotionService extends Service {

    public List<ProductPromotion> getProductPromotionByKey(String key);

    public List<Product> getProducts(ProductPromotion productPromotion);

    public List<Product> getProducts(ProductList productList, int limit);
}
