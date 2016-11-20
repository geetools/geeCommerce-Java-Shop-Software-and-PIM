package com.geecommerce.core.utils;

import org.apache.commons.lang3.StringUtils;

public class Classes {
    public static boolean exists(String fqn) {
        boolean exists = false;

        try {
            Class<?> clazz = Class.forName(fqn);
            if (clazz != null) {
                exists = true;
            }
        } catch (ClassNotFoundException e) {
            // ignore
        }

        return exists;
    }

    public static String ensureSafePackagePath(String fqn) {
        fqn = Regex.normalize(fqn);
        if (StringUtils.isNumeric(fqn.substring(0, 1))) {
            fqn = "_" + fqn;
        }

        return Regex.replaceAll(fqn, "[^a-zA-Z0-9_]", "").toLowerCase();
    }
}
