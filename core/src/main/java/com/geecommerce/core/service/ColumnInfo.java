package com.geecommerce.core.service;

import java.lang.reflect.Type;

public class ColumnInfo {
    private final String name;
    private final Class<?> type;
    private final Type genericType;
    private final String property;
    private final boolean autoPopulate;

    public ColumnInfo(String name, Class<?> type, Type genericType, String property, boolean autoPopulate) {
        this.name = name;
        this.type = type;
        this.genericType = genericType;
        this.property = property;
        this.autoPopulate = autoPopulate;
    }

    public String name() {
        return name;
    }

    public Class<?> type() {
        return type;
    }

    public Type genericType() {
        return genericType;
    }

    public String property() {
        return property;
    }

    public boolean isAutoPopulate() {
        return autoPopulate;
    }

    @Override
    public String toString() {
        return "ColumnInfo [name=" + name + ", type=" + type + ", genericType=" + genericType + ", property=" + property + ", autoPopulate=" + autoPopulate + "]";
    }
}
