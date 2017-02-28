package com.geecommerce.guiwidgets.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.FilterBuilder;

import com.geecommerce.catalog.product.helper.ProductHelper;
import com.geecommerce.catalog.product.helper.ProductListHelper;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.service.ProductListService;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.elasticsearch.api.search.SearchResult;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.elasticsearch.search.SearchParams;
import com.geecommerce.core.elasticsearch.service.ElasticsearchService;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.system.attribute.TargetObjectCode;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.guiwidgets.model.ProductPromotion;
import com.geecommerce.guiwidgets.repository.ProductPromotions;
import com.google.inject.Inject;

@Service
public class DefaultProductPromotionService implements ProductPromotionService {
    private final ProductPromotions productPromotions;
    private final ProductListHelper productListHelper;
    private final ProductListService productListService;
    private final ProductService productService;
    private final ElasticsearchService elasticsearchService;
    private final ProductHelper productHelper;
    private final AttributeService attributeService;
    private final ElasticsearchHelper elasticsearchHelper;

    @Inject
    public DefaultProductPromotionService(ProductPromotions productPromotions, ProductListHelper productListHelper,
        ProductListService productListService, ProductService productService,
        ElasticsearchService elasticsearchService, ProductHelper productHelper, AttributeService attributeService,
        ElasticsearchHelper elasticsearchHelper) {
        this.productPromotions = productPromotions;
        this.productListHelper = productListHelper;
        this.productListService = productListService;
        this.productService = productService;
        this.elasticsearchService = elasticsearchService;
        this.productHelper = productHelper;
        this.attributeService = attributeService;
        this.elasticsearchHelper = elasticsearchHelper;
    }

    @Override
    public List<ProductPromotion> getProductPromotionByKey(String key) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(ProductPromotion.Col.KEY, key);
        return productPromotions.find(ProductPromotion.class, filter);
    }

    @Override
    public List<Product> getProducts(ProductPromotion productPromotion) {
        List<Product> products = new ArrayList<>();

        if (productPromotion.getTargetObjectType() == null)
            return null;

        if (productPromotion.isForProduct()) {
            Id productId = productPromotion.getTargetObjectId();
            if (productId != null) {
                Product product = productService.getProduct(productId);
                if (product != null) {
                    products.add(product);
                }
            }
        }

        if (productPromotion.isForProductList()) {
            if (productPromotion.getTargetObjectId() == null)
                return products;

            ProductList productList = productListService.getProductList(productPromotion.getTargetObjectId());
            if (productList == null)
                return products;

            Map<String, Object> queryMap = null;
            if (productList.getQuery() != null && !productList.getQuery().isEmpty()) {
                queryMap = Json.fromJson(productList.getQuery(), HashMap.class);
            }

            products = productListService.getProducts(productList, productPromotion.getLimit() == null || productPromotion.getLimit() == 0
                ? 100 : productPromotion.getLimit());
        }
        return products;
    }


}
