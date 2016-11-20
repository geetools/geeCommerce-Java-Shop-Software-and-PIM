package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum FrontendInput implements ModelEnum {
    TEXT(1), RICHTEXT(2), SELECT(3), MULTISELECT(4), DATE(5), BOOLEAN(6), COMBOBOX(7), RADIO(8), CHECKBOX(
        9), BOOLEAN_OLD(
            10), IMAGE(11), FILE(12), LINK(13), MEDIALINK(14), FLASHOBJECT(15), HIDDEN(16), MEDIAASSET(17);

    private int id;

    private FrontendInput(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final FrontendInput fromId(int id) {
        for (FrontendInput frontendInput : values()) {
            if (frontendInput.toId() == id) {
                return frontendInput;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (FrontendInput frontendInput : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(FrontendInput.class.getSimpleName())
                .append(".").append(frontendInput.name()).toString()), frontendInput.id);
        }

        return hrMap;
    }

    public static final String getLabel() {
        return App.get().message(
            new StringBuilder("enum.").append(FrontendInput.class.getSimpleName()).append(".label").toString());
    }
}
