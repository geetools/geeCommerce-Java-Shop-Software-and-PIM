package com.geecommerce.checkout.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum AddressType implements ModelEnum {
    DELIVERY(1), INVOICE(2);

    private int id;

    private AddressType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(AddressType.class.getSimpleName()).append(".")
            .append(name()).toString());
    }

    public static final AddressType fromId(int id) {
        for (AddressType addressType : values()) {
            if (addressType.toId() == id) {
                return addressType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (AddressType addressType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(AddressType.class.getSimpleName()).append(".")
                .append(addressType.name()).toString()), addressType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(
            new StringBuilder("enum.").append(AddressType.class.getSimpleName()).append(".label").toString());
    }
}
