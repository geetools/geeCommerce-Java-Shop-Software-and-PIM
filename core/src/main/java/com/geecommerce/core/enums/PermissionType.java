package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum PermissionType implements ModelEnum {
    GUI(1), URI(2), API(3), MODEL(4), MODULE(5);

    private int id;

    private PermissionType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final PermissionType fromId(int id) {
        for (PermissionType permissionType : values()) {
            if (permissionType.toId() == id) {
                return permissionType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (PermissionType permissionType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(PermissionType.class.getSimpleName()).append(".").append(permissionType.name()).toString()), permissionType.id);
        }

        return hrMap;
    }

    public static final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(PermissionType.class.getSimpleName()).append(".label").toString());
    }
}
