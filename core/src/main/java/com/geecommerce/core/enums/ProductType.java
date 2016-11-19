package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum ProductType implements ModelEnum {
    PHYSICAL(1), VARIANT_MASTER(2), BUNDLE(3), PROGRAMME(4), VIRTUAL(5), DOWNLOADABLE(6);

    private int id;

    private ProductType(int id) {
        this.id = id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(ProductType.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static ProductType fromId(int id) {
        for (ProductType productType : values()) {
            if (productType.toId() == id) {
                return productType;
            }
        }

        return null;
    }

    public final int toId() {
        return this.id;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (ProductType productType : values()) {
            hrMap.put(productType.getLabel(), productType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(ProductType.class.getSimpleName()).append(".label").toString());
    }
}
