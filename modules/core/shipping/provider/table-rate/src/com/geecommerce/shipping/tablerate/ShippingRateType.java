package com.geecommerce.shipping.tablerate;

import java.util.Map;

import com.geecommerce.core.App;
import com.google.common.collect.Maps;

public enum ShippingRateType {
    PRICE(1), WEIGHT(2), NUMBER_OF_ITEMS(3), FIXED(4);

    private int id;

    private ShippingRateType(int id) {
        this.id = id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(ShippingRateType.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static ShippingRateType fromId(int id) {
        for (ShippingRateType type : values()) {
            if (type.toId() == id) {
                return type;
            }
        }

        return null;
    }

    public int toId() {
        return this.id;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (ShippingRateType type : values()) {
            hrMap.put(type.getLabel(), type.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(ShippingRateType.class.getSimpleName()).append(".label").toString());
    }
}
