package com.geecommerce.shipping.additional.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface AdditionalShippingRate extends MultiContextModel {
    public AdditionalShippingRate setId(Id id);

    public Double getRate();

    public AdditionalShippingRate setRate(Double rate);

    public String getGroup();

    public AdditionalShippingRate setGroup(String group);

    public String getCarrier();

    public AdditionalShippingRate setCarrier(String carrier);

    public ContextObject<String> getLabel();

    public AdditionalShippingRate setLabel(ContextObject<String> label);

    public ContextObject<String> getDescription();

    public AdditionalShippingRate setDescription(ContextObject<String> label);

    static final class Column {
	public static final String ID = "_id";
	public static final String RATE = "rate";
	public static final String LABEL = "label";
	public static final String DESCRIPTION = "descr";
	public static final String CARRIER = "carrier";
	public static final String GROUP = "group";
    }
}
