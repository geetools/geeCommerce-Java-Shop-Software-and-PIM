package com.geecommerce.core.system.widget.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum WidgetParameterInputType implements ModelEnum {
    STRING(1), ID(2), INTEGER(3), DOUBLE(4);

    private int id;

    private WidgetParameterInputType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(WidgetParameterInputType.class.getSimpleName())
            .append(".").append(name()).toString());
    }

    public static final WidgetParameterInputType fromId(int id) {
        for (WidgetParameterInputType type : values()) {
            if (type.toId() == id) {
                return type;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (WidgetParameterInputType type : values()) {
            hrMap.put(type.getLabel(), type.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(WidgetParameterInputType.class.getSimpleName())
            .append(".label").toString());
    }
}
