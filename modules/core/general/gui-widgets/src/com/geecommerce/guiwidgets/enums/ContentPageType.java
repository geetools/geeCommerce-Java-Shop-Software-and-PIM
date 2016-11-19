package com.geecommerce.guiwidgets.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.google.common.collect.Maps;

public enum ContentPageType {
    STATIC(1), PRODUCT(2);

    private int id;

    private ContentPageType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(ContentPageType.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static final ContentPageType fromId(int id) {
        for (ContentPageType contentType : values()) {
            if (contentType.toId() == id) {
                return contentType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (ContentPageType contentType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(ContentPageType.class.getSimpleName()).append(".").append(contentType.name()).toString()), contentType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(ContentPageType.class.getSimpleName()).append(".label").toString());
    }
}