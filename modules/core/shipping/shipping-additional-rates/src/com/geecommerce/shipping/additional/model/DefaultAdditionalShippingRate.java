package com.geecommerce.shipping.additional.model;

import java.util.Map;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Cacheable
@Model("add_shipping_rates")
public class DefaultAdditionalShippingRate extends AbstractMultiContextModel implements AdditionalShippingRate {
    private static final long serialVersionUID = -4897010166894675659L;

    private Id id = null;

    private String carrier = null;

    private String group = null;

    private Double rate = null;

    private ContextObject<String> label = null;

    private ContextObject<String> description = null;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public AdditionalShippingRate setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Double getRate() {
        return rate;
    }

    @Override
    public AdditionalShippingRate setRate(Double rate) {
        this.rate = rate;
        return this;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public AdditionalShippingRate setGroup(String group) {
        this.group = group;
        return this;
    }

    @Override
    public String getCarrier() {
        return carrier;
    }

    @Override
    public AdditionalShippingRate setCarrier(String carrier) {
        this.carrier = carrier;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public AdditionalShippingRate setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public ContextObject<String> getDescription() {
        return description;
    }

    @Override
    public AdditionalShippingRate setDescription(ContextObject<String> description) {
        this.description = description;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.carrier = str_(map.get(Column.CARRIER));
        this.group = str_(map.get(Column.GROUP));
        this.rate = double_(map.get(Column.RATE));
        this.label = ctxObj_(map.get(Column.LABEL));
        this.description = ctxObj_(map.get(Column.DESCRIPTION));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Column.ID, getId());

        if (getCarrier() != null)
            map.put(Column.CARRIER, getCarrier());

        if (getGroup() != null)
            map.put(Column.GROUP, getGroup());

        map.put(Column.RATE, getRate());
        map.put(Column.LABEL, getLabel());
        map.put(Column.DESCRIPTION, getDescription());

        return map;
    }
}
