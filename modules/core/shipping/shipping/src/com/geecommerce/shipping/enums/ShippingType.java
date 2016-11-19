package com.geecommerce.shipping.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum ShippingType implements ModelEnum {
    DEFAULT(0), PICKUP(1), PACKAGE(2), BULKY(3), DELIVERY(4);

    private int id;

    private ShippingType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(ShippingType.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static final ShippingType fromId(int id) {
        for (ShippingType shippingPackageType : values()) {
            if (shippingPackageType.toId() == id) {
                return shippingPackageType;
            }
        }
        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (ShippingType shippingPackageType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(ShippingType.class.getSimpleName()).append(".").append(shippingPackageType.name()).toString()), shippingPackageType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(ShippingType.class.getSimpleName()).append(".label").toString());
    }

}
