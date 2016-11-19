package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum FilterIndexField implements ModelEnum {
    NATIVE(1), ATT_RAW(2), ATT_HASH(3), ATT_SLUG(4);

    private int id;

    private FilterIndexField(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final FilterIndexField fromId(int id) {
        for (FilterIndexField filterIndexField : values()) {
            if (filterIndexField.toId() == id) {
                return filterIndexField;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (FilterIndexField filterIndexField : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(FilterIndexField.class.getSimpleName()).append(".").append(filterIndexField.name()).toString()), filterIndexField.id);
        }

        return hrMap;
    }

    public static final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(FilterIndexField.class.getSimpleName()).append(".label").toString());
    }
}
