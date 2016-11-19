package com.geecommerce.catalog.product;

import java.util.Map;

import com.geecommerce.core.App;
import com.google.common.collect.Maps;

public enum ProductStatus {
    ENABLED(1), DISABLED(0), DELETED(2);

    int id = 0;

    ProductStatus(int id) {
        this.id = id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(ProductStatus.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static ProductStatus fromId(int id) {
        for (ProductStatus productStatus : values()) {
            if (productStatus.toId() == id) {
                return productStatus;
            }
        }

        return null;
    }

    public int toId() {
        return this.id;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (ProductStatus productStatus : values()) {
            hrMap.put(productStatus.getLabel(), productStatus.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(ProductStatus.class.getSimpleName()).append(".label").toString());
    }
}
