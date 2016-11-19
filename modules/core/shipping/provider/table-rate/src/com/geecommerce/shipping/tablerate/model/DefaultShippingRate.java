package com.geecommerce.shipping.tablerate.model;

import java.util.Map;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.shipping.enums.ShippingType;
import com.geecommerce.shipping.tablerate.ShippingRateType;

@Cacheable
@Model("shipping_rates")
public class DefaultShippingRate extends AbstractMultiContextModel implements ShippingRate {
    private static final long serialVersionUID = -4897010166894675659L;

    private Id id = null;

    private String country = null;

    private String state = null;

    private String zip = null;

    private Double rate = null;

    private Double lowerBound = null;

    private ContextObject<String> label = null;

    private ContextObject<String> description = null;

    private ShippingRateType type = null;

    private ShippingType shippingType = null;

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public ShippingRate setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public String getCountry() {
	return country;
    }

    @Override
    public ShippingRate setCountry(String country) {
	this.country = country;
	return this;
    }

    @Override
    public String getState() {
	return state;
    }

    @Override
    public ShippingRate setState(String state) {
	this.state = state;
	return this;
    }

    @Override
    public String getZip() {
	return zip;
    }

    @Override
    public ShippingRate setZip(String zip) {
	this.zip = zip;
	return this;
    }

    @Override
    public Double getRate() {
	return rate;
    }

    @Override
    public ShippingRate setRate(Double rate) {
	this.rate = rate;
	return this;
    }

    @Override
    public Double getLowerBound() {
	return lowerBound;
    }

    @Override
    public ShippingRate setLowerBound(Double lowerBound) {
	this.lowerBound = lowerBound;
	return this;
    }

    @Override
    public ShippingRateType getType() {
	return type;
    }

    @Override
    public ShippingRate setType(ShippingRateType type) {
	this.type = type;
	return this;
    }

    @Override
    public ShippingType getShippingType() {
	return shippingType;
    }

    @Override
    public ShippingRate setShippingType(ShippingType type) {
	this.shippingType = type;
	return this;
    }

    @Override
    public ContextObject<String> getLabel() {
	return label;
    }

    @Override
    public ShippingRate setLabel(ContextObject<String> label) {
	this.label = label;
	return this;
    }

    @Override
    public ContextObject<String> getDescription() {
	return description;
    }

    @Override
    public ShippingRate setDescription(ContextObject<String> description) {
	this.description = description;
	return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	super.fromMap(map);

	this.id = id_(map.get(Column.ID));
	this.country = str_(map.get(Column.COUNTRY));
	this.state = str_(map.get(Column.STATE));
	this.zip = str_(map.get(Column.ZIP));
	this.rate = double_(map.get(Column.RATE));
	this.lowerBound = double_(map.get(Column.LOWER_BOUND));
	this.label = ctxObj_(map.get(Column.LABEL));
	this.description = ctxObj_(map.get(Column.DESCRIPTION));
	this.type = ShippingRateType.fromId(int_(map.get(Column.RATE_TYPE)));
	this.shippingType = enum_(ShippingType.class, map.get(Column.SHIPPING_TYPE), ShippingType.DEFAULT);
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

	map.put(Column.ID, getId());

	if (getCountry() != null)
	    map.put(Column.COUNTRY, getCountry());

	if (getState() != null)
	    map.put(Column.STATE, getState());

	if (getZip() != null)
	    map.put(Column.ZIP, getZip());

	map.put(Column.RATE, getRate());
	map.put(Column.LOWER_BOUND, getLowerBound());
	map.put(Column.LABEL, getLabel());
	map.put(Column.DESCRIPTION, getDescription());
	map.put(Column.RATE_TYPE, getType().toId());

	if (getShippingType() != null)
	    map.put(Column.SHIPPING_TYPE, getShippingType().toId());

	return map;
    }
}
