package com.geecommerce.tax.model;

import java.util.Map;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.tax.TaxClassType;
import com.google.common.collect.Maps;

@Cacheable
@Model("tax_classes")
public class DefaultTaxClass extends AbstractMultiContextModel implements TaxClass {
    private static final long serialVersionUID = 2476188775998051433L;

    private Id id = null;

    private String code = null;

    private TaxClassType taxClassType = null;

    private ContextObject<String> label = null;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public TaxClass setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public TaxClass setCode(String code) {
        this.code = code;
        return this;
    }

    public TaxClassType getTaxClassType() {
        return taxClassType;
    }

    public TaxClass setTaxClassType(TaxClassType taxClassType) {
        this.taxClassType = taxClassType;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public TaxClass setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.code = str_(map.get(Column.CODE));
        this.taxClassType = TaxClassType.fromId(int_(map.get(Column.TAX_CLASS_TYPE)));
        this.label = ctxObj_(map.get(Column.LABEL));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Column.ID, getId());
        map.put(Column.CODE, getCode());
        map.put(Column.TAX_CLASS_TYPE, getTaxClassType().toId());
        map.put(Column.LABEL, getLabel());

        return map;
    }
}
