package com.geecommerce.catalog.product;

import java.util.Map;
import java.util.Set;

import com.geecommerce.catalog.product.model.ProductListFilterRule;
import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.core.system.attribute.model.Attribute;

public interface ProductListURIBuilder extends Injectable {
    public ProductListURIBuilder newURI(String baseURI);

    public ProductListURIBuilder usingFilterRule(ProductListFilterRule filterRule);

    public ProductListURIBuilder withAttributeMetaData(Map<String, Attribute> attributeMetaData);

    public ProductListURIBuilder withAttributeAliasIndex(Map<String, Attribute> attributeAliasIndex);

    public ProductListURIBuilder havingURIFilterParts(Map<String, Set<Object>> uriFilterParts);

    public ProductListURIBuilder havingParamFilterParts(Map<String, Set<Object>> paramFilterParts);

    public ProductListURIBuilder havingValue(String attributeCode, Object... value);

    public ProductListURIBuilder removeValue(String attributeCode, String filterLabel);

    public ProductListURIBuilder havingNewValue(String fieldKey, Object value);

    public ProductListURIBuilder havingRangeValue(String attrCode, Double fromRange, Double toRange);

    public ProductListURIBuilder havingBasicFilterParts(Map<String, Set<Object>> basicFilterParts);

    public String build();
}
