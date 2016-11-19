package com.geecommerce.core.system.widget.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum WidgetParameterType implements ModelEnum {
    TEXT(1), MEDIASSET(2), PRODUCT(3), COLOR(4), SLIDER(5), SELECT(6), NUMBER(7);

    private int id;

    private WidgetParameterType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(WidgetParameterType.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static final WidgetParameterType fromId(int id) {
        for (WidgetParameterType type : values()) {
            if (type.toId() == id) {
                return type;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (WidgetParameterType type : values()) {
            hrMap.put(type.getLabel(), type.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(WidgetParameterType.class.getSimpleName()).append(".label").toString());
    }
}
