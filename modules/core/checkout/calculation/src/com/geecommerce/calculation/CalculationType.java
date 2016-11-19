package com.geecommerce.calculation;

import java.util.Map;

import com.geecommerce.core.App;
import com.google.common.collect.Maps;

public enum CalculationType {
    ITEM_SUBTOTAL(1), SUBTOTAL(2), DISCOUNT(3), SHIPPING(4), TAX(5), GRAND_TOTAL(6), OTHER(7);

    private int id;

    private CalculationType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(CalculationType.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static final CalculationType fromId(int id) {
        for (CalculationType calculationType : values()) {
            if (calculationType.toId() == id) {
                return calculationType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (CalculationType calculationType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(CalculationType.class.getSimpleName()).append(".").append(calculationType.name()).toString()), calculationType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(CalculationType.class.getSimpleName()).append(".label").toString());
    }

}
