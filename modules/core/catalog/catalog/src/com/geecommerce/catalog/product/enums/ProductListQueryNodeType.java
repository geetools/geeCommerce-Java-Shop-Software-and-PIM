package com.geecommerce.catalog.product.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum ProductListQueryNodeType implements ModelEnum {
    BOOLEAN(1), ATTRIBUTE(2);

    private int id;

    private ProductListQueryNodeType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(ProductListQueryNodeType.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static final ProductListQueryNodeType fromId(int id) {
        for (ProductListQueryNodeType productListQueryNodeType : values()) {
            if (productListQueryNodeType.toId() == id) {
                return productListQueryNodeType;
            }
        }
        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (ProductListQueryNodeType productListQueryNodeType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(ProductListQueryNodeType.class.getSimpleName()).append(".").append(productListQueryNodeType.name()).toString()),
                productListQueryNodeType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(ProductListQueryNodeType.class.getSimpleName()).append(".label").toString());
    }
}
