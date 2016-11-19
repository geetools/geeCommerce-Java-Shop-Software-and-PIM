package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum FilterType implements ModelEnum {
    DEFAULT(1), RANGE(2);

    private int id;

    private FilterType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final FilterType fromId(int id) {
        for (FilterType filterType : values()) {
            if (filterType.toId() == id) {
                return filterType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (FilterType filterType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(FilterType.class.getSimpleName()).append(".").append(filterType.name()).toString()), filterType.id);
        }

        return hrMap;
    }

    public static final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(FilterType.class.getSimpleName()).append(".label").toString());
    }
}
