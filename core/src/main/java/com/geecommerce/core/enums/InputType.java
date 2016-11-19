package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum InputType implements ModelEnum {
    MANDATORY(1), OPTIONAL(2), OPTOUT(3);

    private int id;

    private InputType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final InputType fromId(int id) {
        for (InputType inputType : values()) {
            if (inputType.toId() == id) {
                return inputType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (InputType inputType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(InputType.class.getSimpleName()).append(".").append(inputType.name()).toString()), inputType.id);
        }

        return hrMap;
    }

    public static final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(InputType.class.getSimpleName()).append(".label").toString());
    }
}
