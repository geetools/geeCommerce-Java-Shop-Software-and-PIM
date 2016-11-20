package com.geecommerce.core.utils;

import java.util.Collection;

public class Collections {

    public static <T> void addIfNotNull(Collection<T> collection, Collection<T> values) {
        for (T value : values) {
            org.apache.commons.collections.CollectionUtils.addIgnoreNull(collection, value);
        }
    }
}
