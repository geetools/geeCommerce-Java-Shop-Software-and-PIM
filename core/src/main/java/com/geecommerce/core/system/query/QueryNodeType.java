package com.geecommerce.core.system.query;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

import java.util.Map;

public enum QueryNodeType implements ModelEnum {
    BOOLEAN(1), ATTRIBUTE(2);

    private int id;

    private QueryNodeType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(QueryNodeType.class.getSimpleName())
            .append(".").append(name()).toString());
    }

    public static final QueryNodeType fromId(int id) {
        for (QueryNodeType queryNodeType : values()) {
            if (queryNodeType.toId() == id) {
                return queryNodeType;
            }
        }
        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (QueryNodeType queryNodeType : values()) {
            hrMap.put(
                App.get()
                    .message(new StringBuilder("enum.").append(QueryNodeType.class.getSimpleName())
                        .append(".").append(queryNodeType.name()).toString()),
                    queryNodeType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(QueryNodeType.class.getSimpleName())
            .append(".label").toString());
    }
}
