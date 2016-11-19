package com.geecommerce.guiwidgets.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.google.common.collect.Maps;

public enum ContentType {
    PAGE(1), PARTIAL(2);

    private int id;

    private ContentType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(ContentType.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static final ContentType fromId(int id) {
        for (ContentType contentType : values()) {
            if (contentType.toId() == id) {
                return contentType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (ContentType contentType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(ContentType.class.getSimpleName()).append(".").append(contentType.name()).toString()), contentType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(ContentType.class.getSimpleName()).append(".label").toString());
    }
}