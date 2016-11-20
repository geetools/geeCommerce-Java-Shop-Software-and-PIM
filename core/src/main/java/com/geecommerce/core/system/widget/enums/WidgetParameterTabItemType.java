package com.geecommerce.core.system.widget.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum WidgetParameterTabItemType implements ModelEnum {
    TAB(1), PARAMETER(2);

    private int id;

    private WidgetParameterTabItemType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(WidgetParameterTabItemType.class.getSimpleName())
            .append(".").append(name()).toString());
    }

    public static final WidgetParameterTabItemType fromId(int id) {
        for (WidgetParameterTabItemType type : values()) {
            if (type.toId() == id) {
                return type;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (WidgetParameterTabItemType type : values()) {
            hrMap.put(type.getLabel(), type.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(WidgetParameterTabItemType.class.getSimpleName())
            .append(".label").toString());
    }
}
