package com.geecommerce.core.json.genson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.rest.jersey.inject.FilterInjectableProvider;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Model;
import com.owlike.genson.Context;
import com.owlike.genson.reflect.BeanCreator;
import com.owlike.genson.reflect.BeanDescriptor;
import com.owlike.genson.reflect.BeanProperty;
import com.owlike.genson.reflect.PropertyAccessor;
import com.owlike.genson.reflect.PropertyMutator;
import com.owlike.genson.stream.ObjectWriter;

public class DefaultBeanDescriptor<T> extends BeanDescriptor<T> {
    final List<PropertyAccessor> accessibleProperties;

    // Do not ignore these fields.
    private static final List<String> FIELD_BLACK_LIST = new ArrayList<>();

    static {
        FIELD_BLACK_LIST.add("_id");
        FIELD_BLACK_LIST.add("id");
        FIELD_BLACK_LIST.add("__v");
        FIELD_BLACK_LIST.add("version");
    }

    private final static Comparator<BeanProperty> _readablePropsComparator = new Comparator<BeanProperty>() {
        public int compare(BeanProperty o1, BeanProperty o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };

    public DefaultBeanDescriptor(Class<T> forClass, Class<?> fromDeclaringClass, List<PropertyAccessor> readableBps, Map<String, PropertyMutator> writableBps, BeanCreator creator,
        boolean failOnMissingProperty) {
        super(forClass, fromDeclaringClass, readableBps, writableBps, creator, failOnMissingProperty);

        Collections.sort(readableBps, _readablePropsComparator);

        accessibleProperties = Collections.unmodifiableList(readableBps);
    }

    @Override
    public void serialize(T obj, ObjectWriter writer, Context ctx) {
        writer.beginObject();

        boolean isModel = isModel(getOfClass());

        for (PropertyAccessor accessor : accessibleProperties) {
            if (isModel) {
                if (!isIgnore(accessor.getName()))
                    accessor.serialize(obj, writer, ctx);
            } else {
                accessor.serialize(obj, writer, ctx);
            }
        }

        writer.endObject();
    }

    private boolean isIgnore(String propertyName) {
        boolean isIgnore = false;

        QueryOptions queryOptions = App.get().registryGet(FilterInjectableProvider.QUERY_OPTIONS_KEY);

        // If object property is from a model class, we check if any properties
        // have been included or excluded.
        if (queryOptions != null && !FIELD_BLACK_LIST.contains(propertyName)) {
            List<String> includeFields = queryOptions.fieldsToInclude();
            List<String> excludeFields = queryOptions.fieldsToExclude();

            // Only serialize these fields if they have been specified in the
            // include-list.
            if (includeFields != null && includeFields.size() > 0) {
                if (!includeFields.contains(propertyName)) {
                    isIgnore = true;
                }
            }
            // Otherwise, if an exclude-list exists, only serialize properties
            // that are not in this list.
            else if (excludeFields != null && excludeFields.size() > 0) {
                if (excludeFields.contains(propertyName)) {
                    isIgnore = true;
                }
            }
        }

        return isIgnore;
    }

    private boolean isModel(Class<?> type) {
        return Model.class.isAssignableFrom(type);
    }

}
