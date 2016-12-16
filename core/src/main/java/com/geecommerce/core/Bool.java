package com.geecommerce.core;

public class Bool {
    public static Boolean toBoolean(Object o) {
        if (o == null)
            return null;

        if (o instanceof Boolean)
            return (boolean) o;

        if (o instanceof Number) {
            if (((Number) o).intValue() == 1) {
                return true;
            } else {
                return false;
            }
        }

        if (o instanceof String) {
            if (True.matches((String) o))
                return true;

            if (False.matches((String) o))
                return false;
        }

        throw new IllegalStateException("Unable to convert '" + o + "' to a boolean value.");
    }
}
