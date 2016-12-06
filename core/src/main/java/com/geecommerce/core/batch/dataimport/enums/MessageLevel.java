package com.geecommerce.core.batch.dataimport.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum MessageLevel implements ModelEnum {
    INFO(1), WARN(2), ERROR(3), FATAL(4);

    private int id;

    private MessageLevel(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final MessageLevel fromId(int id) {
        for (MessageLevel messageLevel : values()) {
            if (messageLevel.toId() == id) {
                return messageLevel;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (MessageLevel messageLevel : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(MessageLevel.class.getSimpleName())
                .append(".").append(messageLevel.name()).toString()), messageLevel.id);
        }

        return hrMap;
    }

    public static final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(MessageLevel.class.getSimpleName()).append(".label").toString());
    }
}
