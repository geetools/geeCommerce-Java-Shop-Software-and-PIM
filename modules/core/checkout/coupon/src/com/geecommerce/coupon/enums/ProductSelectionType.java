package com.geecommerce.coupon.enums;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

import java.util.Map;

public enum ProductSelectionType implements ModelEnum {
    PRODUCT(1), LIST(2), QUERY(3);

    private int id;

    private ProductSelectionType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(ProductSelectionType.class.getSimpleName()).append(".")
            .append(name()).toString());
    }

    public static final ProductSelectionType fromId(int id) {
        for (ProductSelectionType productSelectionType : values()) {
            if (productSelectionType.toId() == id) {
                return productSelectionType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (ProductSelectionType productSelectionType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(ProductSelectionType.class.getSimpleName())
                .append(".").append(productSelectionType.name()).toString()), productSelectionType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(
            new StringBuilder("enum.").append(ProductSelectionType.class.getSimpleName()).append(".label").toString());
    }
}
