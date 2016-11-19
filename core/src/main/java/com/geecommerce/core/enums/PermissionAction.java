package com.geecommerce.core.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum PermissionAction implements ModelEnum {
    VIEW(1), ADD(2), UPDATE(3), DELETE(4), GET(5), PUT(6), POST(7), PATCH(8), USE(9), HEAD(10), OPTIONS(11);

    private int id;

    private PermissionAction(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public static final PermissionAction fromId(int id) {
        for (PermissionAction permissionAction : values()) {
            if (permissionAction.toId() == id) {
                return permissionAction;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (PermissionAction permissionAction : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(PermissionAction.class.getSimpleName()).append(".").append(permissionAction.name()).toString()), permissionAction.id);
        }

        return hrMap;
    }

    public static final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(PermissionAction.class.getSimpleName()).append(".label").toString());
    }
}
