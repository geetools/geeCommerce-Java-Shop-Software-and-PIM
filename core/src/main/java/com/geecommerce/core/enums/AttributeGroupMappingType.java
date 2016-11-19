package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum AttributeGroupMappingType implements ModelEnum {
    ATTRIBUTE(1), ATTRIBUTE_GROUP(2);

    private int id;

    private AttributeGroupMappingType(int id) {
        this.id = id;
    }

    public static final AttributeGroupMappingType fromId(int id) {
        for (AttributeGroupMappingType type : values()) {
            if (type.toId() == id) {
                return type;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (AttributeGroupMappingType attributeType : values()) {
            hrMap.put(attributeType.getLabel(), attributeType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(AttributeGroupMappingType.class.getSimpleName()).append(".label").toString());
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(AttributeGroupMappingType.class.getSimpleName()).append(".").append(name()).toString());
    }
}
