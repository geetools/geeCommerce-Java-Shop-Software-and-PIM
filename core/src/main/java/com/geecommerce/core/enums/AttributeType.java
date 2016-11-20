package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum AttributeType implements ModelEnum {
    DEFAULT(1), VIRTUAL(2);

    private int id;

    private AttributeType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(AttributeType.class.getSimpleName()).append(".")
            .append(name()).toString());
    }

    public static final AttributeType fromId(int id) {
        for (AttributeType type : values()) {
            if (type.toId() == id) {
                return type;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (AttributeType attributeType : values()) {
            hrMap.put(attributeType.getLabel(), attributeType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(
            new StringBuilder("enum.").append(AttributeType.class.getSimpleName()).append(".label").toString());
    }
}
