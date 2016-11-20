package com.geecommerce.core.json.genson;

import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.convert.ContextualFactory;
import com.owlike.genson.reflect.BeanProperty;

public class IgnoreFieldContextualFactory implements ContextualFactory<Object> {
    @Override
    public Converter<Object> create(BeanProperty property, Genson genson) {
        return useRuntimePropertyFilter(property.getRawClass()) ? new RuntimePropertyFilter(property)
            : genson.provideConverter(property.getType());
    }

    private boolean useRuntimePropertyFilter(Class<?> type) {
        return true;
        // return !type.isArray() && !List.class.isAssignableFrom(type) &&
        // !Map.class.isAssignableFrom(type) &&
        // !Set.class.isAssignableFrom(type);
    }
}
