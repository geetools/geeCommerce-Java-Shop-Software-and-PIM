package com.geecommerce.catalog.product.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Model("product_list_filter_rules")
public class DefaultProductListFilterRule extends AbstractMultiContextModel implements ProductListFilterRule {
    private static final long serialVersionUID = 8296979884782859285L;
    private Id id = null;
    private String key = null;
    private ContextObject<String> label = null;

    private List<Map<String, Object>> attributes = new ArrayList<>();

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ProductListFilterRule setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public ProductListFilterRule setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public ProductListFilterRule setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public List<Map<String, Object>> getAttributes() {
        return attributes;
    }

    @Override
    public ProductListFilterRule addAttribute(String attributeCode, int positionInURI, boolean allowMultipleValues,
        ContextObject<String> prefix, Boolean prefixMatchEnabled) {
        boolean positionInURIExists = positionInURIExists(positionInURI);

        // Attribute already exists and may have changed.
        if (attributeExists(attributeCode)) {
            Map<String, Object> foundAttribute = findAttribute(attributeCode);

            if (positionInURIExists && ((int) foundAttribute.get(AttributeField.POSITION_IN_URI)) != positionInURI) {
                throw new RuntimeException(
                    "Unable to change uri-position for filter-url-attribute because the position is already taken by another attribute.");
            } else {
                foundAttribute.put(AttributeField.POSITION_IN_URI, positionInURI);

                ContextObject<String> existingPrefix = ctxObj_(foundAttribute.get(AttributeField.PREFIX));

                if (prefix != null) {
                    if (existingPrefix != null) {
                        prefix.merge(existingPrefix);
                    }

                    foundAttribute.put(AttributeField.PREFIX, prefix);
                    foundAttribute.put(AttributeField.PREFIX_MATCH_ENABLED,
                        prefixMatchEnabled == null ? false : prefixMatchEnabled.booleanValue());
                }
            }
        }
        // Attribute does not exist yet.
        else {
            // We cannot use a position that already exists.
            if (positionInURIExists) {
                throw new RuntimeException(
                    "Unable to add attribute to filter-url-rule because the uri-position already exists.");
            }
            // Add the new value, if all is OK.
            else {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put(AttributeField.ATTRIBUTE_CODE, attributeCode);
                map.put(AttributeField.POSITION_IN_URI, positionInURI);

                if (prefix != null) {
                    map.put(AttributeField.PREFIX, prefix);
                    map.put(AttributeField.PREFIX_MATCH_ENABLED,
                        prefixMatchEnabled == null ? false : prefixMatchEnabled.booleanValue());
                }

                this.attributes.add(map);
            }
        }

        return this;
    }

    @Override
    public Map<String, Object> findAttribute(String attributeCode) {
        if (this.attributes != null && !this.attributes.isEmpty()) {
            for (Map<String, Object> map : this.attributes) {
                String code = str_(map.get(AttributeField.ATTRIBUTE_CODE));

                if (code != null && code.equals(attributeCode))
                    return map;
            }
        }

        return null;
    }

    @Override
    public Map<String, Object> findAttributeByPositionInURI(int positionInURI) {
        if (this.attributes != null && !this.attributes.isEmpty()) {
            for (Map<String, Object> map : this.attributes) {
                int p = (int) map.get(AttributeField.POSITION_IN_URI);

                if (p == positionInURI)
                    return map;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> findAttributeByPrefix(String prefix) {
        if (this.attributes != null && !this.attributes.isEmpty()) {
            for (Map<String, Object> map : this.attributes) {
                ContextObject<String> p = ContextObject
                    .valueOf((List<Map<String, Object>>) map.get(AttributeField.PREFIX));

                if (p != null && p.getClosestValue() != null && p.getString().equals(prefix))
                    return map;
            }
        }

        return null;
    }

    protected boolean attributeExists(String attributeCode) {
        if (this.attributes != null && !this.attributes.isEmpty()) {
            for (Map<String, Object> map : this.attributes) {
                String code = str_(map.get(AttributeField.ATTRIBUTE_CODE));

                if (code != null && code.equals(attributeCode))
                    return true;
            }
        }

        return false;
    }

    protected boolean positionInURIExists(int positionInURI) {
        if (this.attributes != null && !this.attributes.isEmpty()) {
            for (Map<String, Object> map : this.attributes) {
                int pos = (int) map.get(AttributeField.POSITION_IN_URI);

                if (pos == positionInURI)
                    return true;
            }
        }

        return false;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.key = str_(map.get(Column.KEY));
        this.label = ctxObj_(map.get(Column.LABEL));
        this.attributes = list_(map.get(Column.ATTRIBUTES));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Column.ID, getId());
        map.put(Column.KEY, getKey());
        map.put(Column.LABEL, getLabel());
        map.put(Column.ATTRIBUTES, getAttributes());

        return map;
    }
}
