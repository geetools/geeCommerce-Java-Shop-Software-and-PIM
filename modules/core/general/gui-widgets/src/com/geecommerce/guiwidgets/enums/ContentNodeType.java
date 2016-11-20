package com.geecommerce.guiwidgets.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.google.common.collect.Maps;

public enum ContentNodeType {
    TEXT(1), WIDGET(2);

    private int id;

    private ContentNodeType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(ContentNodeType.class.getSimpleName()).append(".")
            .append(name()).toString());
    }

    public static final ContentNodeType fromId(int id) {
        for (ContentNodeType contentNodeType : values()) {
            if (contentNodeType.toId() == id) {
                return contentNodeType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (ContentNodeType contentNodeType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(ContentNodeType.class.getSimpleName())
                .append(".").append(contentNodeType.name()).toString()), contentNodeType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(
            new StringBuilder("enum.").append(ContentNodeType.class.getSimpleName()).append(".label").toString());
    }
}