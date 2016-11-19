package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum ImageFilenameOrigin implements ModelEnum {
    ARTICLE_NUMBER(1), ID(2), PRODUCT_NAME(3), ORIGINAL_FILENAME(4), CUSTOM(5);

    private int id;

    private ImageFilenameOrigin(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final ImageFilenameOrigin fromId(int id) {
        for (ImageFilenameOrigin value : values()) {
            if (value.toId() == id) {
                return value;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (ImageFilenameOrigin value : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(ImageFilenameOrigin.class.getSimpleName()).append(".").append(value.name()).toString()), value.id);
        }

        return hrMap;
    }

    public static final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(ImageFilenameOrigin.class.getSimpleName()).append(".label").toString());
    }
}
