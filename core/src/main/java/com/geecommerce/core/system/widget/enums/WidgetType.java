package com.geecommerce.core.system.widget.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum WidgetType implements ModelEnum {
    BACKEND(1), HTML_CLIENT(2), CKEDITOR_CLIENT(3);

    private int id;

    private WidgetType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(WidgetType.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static final WidgetType fromId(int id) {
        for (WidgetType type : values()) {
            if (type.toId() == id) {
                return type;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (WidgetType type : values()) {
            hrMap.put(type.getLabel(), type.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(WidgetType.class.getSimpleName()).append(".label").toString());
    }
}
