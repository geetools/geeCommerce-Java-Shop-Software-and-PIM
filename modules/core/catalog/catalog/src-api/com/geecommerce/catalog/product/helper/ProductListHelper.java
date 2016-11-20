package com.geecommerce.catalog.product.helper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.index.query.FilterBuilder;

import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductListFilterRule;
import com.geecommerce.catalog.product.model.ProductListQueryNode;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.system.attribute.model.Attribute;

public interface ProductListHelper extends Helper {

    public List<FilterBuilder> getVisibilityFilters();

    public Map<String, Set<Object>> getFilterPartsFromParameters(Map<String, String[]> parameterMap,
        ProductListFilterRule navFilterURLRule, Map<String, Attribute> filterAttributes,
        Map<String, String> attributesAliasIndex);

    public Map<String, Set<Object>> getFilterPartsFromURI(String uri, ProductListFilterRule navFilterURLRule);

    public FilterBuilder buildQuery(ProductListQueryNode queryNode);

    public String getAttributeOrConfigProperty(ProductList productList, String attrName, String configPropertyName);

    public void fixProductListQuery(ProductList productList);
}
