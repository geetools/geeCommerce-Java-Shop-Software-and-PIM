package com.geecommerce.shipping.tablerate.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.shipping.enums.ShippingType;
import com.geecommerce.shipping.tablerate.ShippingRateType;

public interface ShippingRate extends MultiContextModel {
    public ShippingRate setId(Id id);

    public String getCountry();

    public ShippingRate setCountry(String country);

    public String getState();

    public ShippingRate setState(String state);

    public String getZip();

    public ShippingRate setZip(String zip);

    public Double getRate();

    public ShippingRate setRate(Double rate);

    public Double getLowerBound();

    public ShippingRate setLowerBound(Double lowerBound);

    public ShippingRateType getType();

    public ShippingRate setType(ShippingRateType type);

    public ShippingType getShippingType();

    public ShippingRate setShippingType(ShippingType type);

    public ContextObject<String> getLabel();

    public ShippingRate setLabel(ContextObject<String> label);

    public ContextObject<String> getDescription();

    public ShippingRate setDescription(ContextObject<String> label);

    static final class Column {
	public static final String ID = "_id";
	public static final String COUNTRY = "country";
	public static final String STATE = "state";
	public static final String ZIP = "zip";
	public static final String RATE = "rate";
	public static final String LABEL = "label";
	public static final String DESCRIPTION = "descr";
	public static final String RATE_TYPE = "type";
	public static final String LOWER_BOUND = "l_bound";
	public static final String SHIPPING_TYPE = "s_type";
    }
}
