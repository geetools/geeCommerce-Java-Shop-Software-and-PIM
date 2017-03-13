package com.geecommerce.catalog.product.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductListFilterRule;
import com.geecommerce.catalog.product.model.ProductListQueryNode;
import com.geecommerce.core.elasticsearch.search.SearchParams;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;

public interface ProductListService extends Service {
    public ProductList createProductList(ProductList productList);

    public ProductList getProductList(Id id);

    public ProductList getProductListByKey(String key);

    public ProductListFilterRule createProductListFilterRule(ProductListFilterRule filterRule);

    public ProductListFilterRule getProductListFilterRule(String key);

    public Set<Id> getProductIds(ProductListQueryNode queryNode, Map<String, Object> navFilterParts,
        Map<String, Set<Object>> uriFilterParts, SearchParams searchParams);

    public Map<Id, Boolean> getProductIdsAndVisibility(ProductListQueryNode queryNode,
        Map<String, Object> navFilterParts, Map<String, Set<Object>> uriFilterParts, SearchParams searchParams);

    public Map<String, Object> findProductById(Id productId);

    public Id[] getProductIds(ProductList productList, boolean checkVisibility, Integer limit);

    public List<Product> getProducts(ProductList productList, boolean checkVisibility, Integer limit);
}
