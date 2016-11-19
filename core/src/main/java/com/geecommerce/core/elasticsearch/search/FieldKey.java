package com.geecommerce.core.elasticsearch.search;

import java.io.Serializable;

public class FieldKey implements Serializable {
    private static final long serialVersionUID = -6989968414122139943L;

    private final String attributeCode;
    private final String attributeSlugValue;

    public FieldKey(String attributeCode, String attributeSlugValue) {
        this.attributeCode = attributeCode;
        this.attributeSlugValue = attributeSlugValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributeCode == null) ? 0 : attributeCode.hashCode());
        result = prime * result + ((attributeSlugValue == null) ? 0 : attributeSlugValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        FieldKey other = (FieldKey) obj;

        if (attributeCode == null) {
            if (other.attributeCode != null)
                return false;
        } else if (!attributeCode.equals(other.attributeCode))
            return false;

        if (attributeSlugValue == null) {
            if (other.attributeSlugValue != null)
                return false;
        } else if (!attributeSlugValue.equals(other.attributeSlugValue))
            return false;

        return true;
    }
}
