package com.geecommerce.catalog.product.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Model("product_link_types")
public class DefaultProductLinkType extends AbstractModel implements ProductLinkType {
    private static final long serialVersionUID = -1114570549944548153L;

    private Id id = null;

    private String code = null;

    private ContextObject<String> label = null;

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;
        this.id = id_(map.get(Column.ID));
        this.code = str_(map.get(Column.CODE));
        this.label = ctxObj_(map.get(Column.LABEL));
    }

    @Override
    public Map<String, Object> toMap() {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put(Column.ID, getId());
        map.put(Column.CODE, getCode());
        map.put(Column.LABEL, getLabels());
        return map;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ProductLinkType setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public ProductLinkType setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public ContextObject<String> getLabels() {
        return label;
    }

    @Override
    public String getLabel() {
        return label.getString();
    }

    @Override
    public ProductLinkType setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }
}
