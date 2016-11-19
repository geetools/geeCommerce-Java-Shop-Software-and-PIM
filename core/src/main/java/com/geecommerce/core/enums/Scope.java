package com.geecommerce.core.enums;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;

public enum Scope implements ModelEnum {
    GLOBAL(1), MERCHANT(2), STORE(3), REQUEST_CONTEXT(4);

    private int id;

    private Scope(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final Scope fromId(int id) {
        for (Scope scope : values()) {
            if (scope.toId() == id) {
                return scope;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = new LinkedHashMap<>();

        for (Scope scope : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(Scope.class.getSimpleName()).append(".").append(scope.name()).toString()), scope.id);
        }

        return hrMap;
    }
}
