package com.geecommerce.catalog.product.model;

import java.util.List;

import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.PageSupport;
import com.geecommerce.core.service.TargetSupport;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

public interface ProductList extends MultiContextModel, AttributeSupport, TargetSupport, PageSupport {
    public Id getId();

    public ProductList setId(Id id);

    public Id getId2();

    public ProductList setId2(Id id2);

    public String getKey();

    public ProductList setKey(String key);

    public ContextObject<String> getLabel();

    public ProductList setLabel(ContextObject<String> label);

    @JsonIgnore
    public ContextObject<String> getURI();

    public ProductList setEnabled(boolean enabled);

    public boolean isEnabled();

    public ProductList setSale(boolean sale);

    public boolean isSale();

    public ProductList setSpecial(boolean special);

    public boolean isSpecial();

    public ProductList setQuery(String query);

    public String getQuery();

    public ProductListQueryNode getQueryNode();

    public ProductList setQueryNode(ProductListQueryNode productListQueryNode);

    public Id getFilterRuleId();

    public ProductList setFilterRuleId(Id filterRuleId);

    public ProductListFilterRule getFilterRule();

    public ProductList setFilterRule(ProductListFilterRule filterRule);

    @JsonIgnore
    public List<Attribute> getFilterAttributes();

    public List<Id> getFilterAttributeIds();

    public ProductList setFilterAttributeIds(List<Id> ids);

    static final class Col {
        public static final String ID = "_id";
        public static final String ID2 = "id2";

        public static final String KEY = "key";
        public static final String LABEL = "label";
        public static final String QUERY = "query";
        public static final String QUERY_NODE = "query_node";
        public static final String FILTER_RULE_ID = "f_rule_id";
        public static final String ENABLED = "enabled";
        public static final String SALE = "sale";
        public static final String SPECIAL = "special";
        public static final String ATTRIBUTES = "attributes";
        public static final String FILTER_ATTRIBUTES = "filter_attrs";
    }
}
