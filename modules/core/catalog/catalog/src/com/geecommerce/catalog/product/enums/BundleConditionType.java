package com.geecommerce.catalog.product.enums;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

import java.util.Map;

public enum BundleConditionType implements ModelEnum {
    VALID(1), NOT_VALID(2);

    private int id;

    private BundleConditionType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(BundleConditionType.class.getSimpleName())
            .append(".").append(name()).toString());
    }

    public static final BundleConditionType fromId(int id) {
        for (BundleConditionType bundleConditionType : values()) {
            if (bundleConditionType.toId() == id) {
                return bundleConditionType;
            }
        }
        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (BundleConditionType bundleConditionType : values()) {
            hrMap.put(
                App.get()
                    .message(new StringBuilder("enum.").append(BundleConditionType.class.getSimpleName())
                        .append(".").append(bundleConditionType.name()).toString()),
                    bundleConditionType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(BundleConditionType.class.getSimpleName())
            .append(".label").toString());
    }
}
