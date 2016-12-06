package com.geecommerce.core.batch.dataimport.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum ImportStage implements ModelEnum {
    UPLOAD(1), PLAN(2), PROCESS(3), INDEX(4);

    private int id;

    private ImportStage(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final ImportStage fromId(int id) {
        for (ImportStage importStage : values()) {
            if (importStage.toId() == id) {
                return importStage;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (ImportStage importStage : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(ImportStage.class.getSimpleName())
                .append(".").append(importStage.name()).toString()), importStage.id);
        }

        return hrMap;
    }

    public static final String getLabel() {
        return App.get().message(
            new StringBuilder("enum.").append(ImportStage.class.getSimpleName()).append(".label").toString());
    }
}
