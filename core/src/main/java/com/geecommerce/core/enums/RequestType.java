package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum RequestType implements ModelEnum {
    FRONTEND(1), API(3);

    private int id;

    private RequestType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final RequestType fromId(int id) {
        for (RequestType requestType : values()) {
            if (requestType.toId() == id) {
                return requestType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (RequestType requestType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(RequestType.class.getSimpleName()).append(".").append(requestType.name()).toString()), requestType.id);
        }

        return hrMap;
    }

    public static final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(RequestType.class.getSimpleName()).append(".label").toString());
    }

}
