package com.geecommerce.tax;

import java.util.Map;

import com.geecommerce.core.App;
import com.google.common.collect.Maps;

public enum TaxClassType {
    PRODUCT(1), CUSTOMER(2);

    private int id;

    private TaxClassType(int id) {
        this.id = id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(TaxClassType.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static TaxClassType fromId(int id) {
        for (TaxClassType type : values()) {
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

        for (TaxClassType type : values()) {
            hrMap.put(type.getLabel(), type.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(TaxClassType.class.getSimpleName()).append(".label").toString());
    }
}
