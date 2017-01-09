package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum ProductSubType implements ModelEnum {
    DEFAULT(1), SERVICE(2), VIRTUAL(3), DOWNLOADABLE(4);

    private int id;

    private ProductSubType(int id) {
        this.id = id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(ProductSubType.class.getSimpleName()).append(".")
            .append(name()).toString());
    }

    public static ProductSubType fromId(int id) {
        for (ProductSubType productSubType : values()) {
            if (productSubType.toId() == id) {
                return productSubType;
            }
        }

        return null;
    }

    public final int toId() {
        return this.id;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (ProductSubType productSubType : values()) {
            hrMap.put(productSubType.getLabel(), productSubType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(
            new StringBuilder("enum.").append(ProductSubType.class.getSimpleName()).append(".label").toString());
    }
}
