package com.geecommerce.core.service;

public class ColumnKey {
    protected String name = null;
    protected ColumnInfo columnInfo = null;

    public ColumnKey(String name, ColumnInfo columnInfo) {
        this.name = name;
        this.columnInfo = columnInfo;
    }

    public String name() {
        return name;
    }

    public ColumnInfo columnInfo() {
        return columnInfo;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        ColumnKey other = (ColumnKey) obj;

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "ColumnKey [name=" + name + ", columnInfo=" + columnInfo + "]";
    }
}
