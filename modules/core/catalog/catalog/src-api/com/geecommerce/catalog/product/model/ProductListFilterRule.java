package com.geecommerce.catalog.product.model;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface ProductListFilterRule extends MultiContextModel {
    public Id getId();

    public ProductListFilterRule setId(Id id);

    public String getKey();

    public ProductListFilterRule setKey(String key);

    public ContextObject<String> getLabel();

    public ProductListFilterRule setLabel(ContextObject<String> label);

    public List<Map<String, Object>> getAttributes();

    public ProductListFilterRule addAttribute(String attributeCode, int position, boolean allowMultipleValues,
        ContextObject<String> prefix, Boolean prefixMatchEnabled);

    public Map<String, Object> findAttribute(String attributeCode);

    public Map<String, Object> findAttributeByPositionInURI(int position);

    public Map<String, Object> findAttributeByPrefix(String prefix);

    static final class Column {
        public static final String ID = "_id";
        public static final String KEY = "key";
        public static final String LABEL = "label";
        public static final String ATTRIBUTES = "attributes";
    }

    static final class AttributeField {
        public static final String ATTRIBUTE_CODE = "attr_code";
        public static final String POSITION_IN_URI = "uri_pos";
        public static final String PREFIX = "prefix";
        public static final String PREFIX_MATCH_ENABLED = "prefix_match_enabled";
    }
}
