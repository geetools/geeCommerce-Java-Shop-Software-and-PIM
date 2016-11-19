package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum BackendType implements ModelEnum {
    STRING(1), INTEGER(2), LONG(3), DOUBLE(4), BOOLEAN(5), DATE(6), FLOAT(7), SHORT(8);

    private int id;

    private BackendType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(BackendType.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static final BackendType fromId(int id) {
        for (BackendType backendType : values()) {
            if (backendType.toId() == id) {
                return backendType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (BackendType backendType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(BackendType.class.getSimpleName()).append(".").append(backendType.name()).toString()), backendType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(BackendType.class.getSimpleName()).append(".label").toString());
    }
}
