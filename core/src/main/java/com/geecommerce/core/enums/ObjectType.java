package com.geecommerce.core.enums;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;

public enum ObjectType implements ModelEnum {
    PRODUCT_LIST(1), PRODUCT(2), RETAIL_STORE(3), CMS(4), LINK(5);

    private int id;

    private ObjectType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final ObjectType fromId(int id) {
        for (ObjectType objectType : values()) {
            if (objectType.toId() == id) {
                return objectType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = new LinkedHashMap<>();

        for (ObjectType objectType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(ObjectType.class.getSimpleName()).append(".").append(objectType.name()).toString()), objectType.id);
        }

        return hrMap;
    }
}
