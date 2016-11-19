package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum FrontendOutput implements ModelEnum {
    TEXT(1), LIST(2);

    private int id;

    private FrontendOutput(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final FrontendOutput fromId(int id) {
        for (FrontendOutput frontendOutput : values()) {
            if (frontendOutput.toId() == id) {
                return frontendOutput;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (FrontendOutput frontendOutput : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(FrontendOutput.class.getSimpleName()).append(".").append(frontendOutput.name()).toString()), frontendOutput.id);
        }

        return hrMap;
    }

    public static final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(FrontendOutput.class.getSimpleName()).append(".label").toString());
    }
}
