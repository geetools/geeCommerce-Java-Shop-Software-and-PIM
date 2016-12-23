package com.geecommerce.catalog.product.enums;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

import java.util.Map;

public enum BundleGroupType implements ModelEnum {
    SELECT(1), MULTISELECT(2), CHECKBOX(3), RADIOBUTTON(4), VARIANT(5);

    private int id;

    private BundleGroupType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(BundleGroupType.class.getSimpleName())
            .append(".").append(name()).toString());
    }

    public static final BundleGroupType fromId(int id) {
        for (BundleGroupType bundleGroupType : values()) {
            if (bundleGroupType.toId() == id) {
                return bundleGroupType;
            }
        }
        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (BundleGroupType bundleGroupType : values()) {
            hrMap.put(
                App.get()
                    .message(new StringBuilder("enum.").append(BundleGroupType.class.getSimpleName())
                        .append(".").append(bundleGroupType.name()).toString()),
                    bundleGroupType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(BundleGroupType.class.getSimpleName())
            .append(".label").toString());
    }
}
