package com.geecommerce.core.json.genson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.core.rest.jersey.inject.FilterInjectableProvider;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.GenericType;
import com.owlike.genson.reflect.BeanProperty;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

/**
 * Ensures that only the following properties are serialized to json: <i>All
 * fields if include or exclude list is empty.</i> <i>Fields that are in an
 * include-list.</i> <i>Fields that are not in an
 * exclude-list.</i>
 * 
 * Fields can be passed to the REST-API using the fields parameter: e.g.: GET
 * /api/call/object?fields=a,b,c or GET /api/call/object?fields=-d. Fields that
 * are prepended with a hyphen are excluded,
 * otherwise
 * 
 * @author Michael
 * 
 */
@Profile
public class RuntimePropertyFilter implements Converter<Object> {
    // Do not ignore these fields.
    private static final List<String> FIELD_BLACK_LIST = new ArrayList<>();
    static {
        FIELD_BLACK_LIST.add("_id");
        FIELD_BLACK_LIST.add("id");
        FIELD_BLACK_LIST.add("__v");
        FIELD_BLACK_LIST.add("version");
    }

    private final BeanProperty property;

    public RuntimePropertyFilter(BeanProperty property) {
        this.property = property;
    }

    @Override
    public void serialize(Object object, ObjectWriter writer, Context ctx) throws IOException {
        if ("parent".equals(property.getName()) || "getParent".equals(property.getName())) {
            return;
        }

        // We only want to do the property check for model objects.
        if ((!isModel(property.getDeclaringClass()) && !isInjectable(property.getDeclaringClass())) || AttributeValue.class.isAssignableFrom(property.getDeclaringClass())) {
            ctx.genson.serialize(object, property.getType(), writer, ctx);
            return;
        }

        QueryOptions queryOptions = App.get().registryGet(FilterInjectableProvider.QUERY_OPTIONS_KEY);

        // If object property is from a model class, we check if any properties
        // have been included or excluded.
        if (queryOptions != null && !FIELD_BLACK_LIST.contains(property.getName())) {
            List<String> includeFields = queryOptions.fieldsToInclude();
            List<String> excludeFields = queryOptions.fieldsToExclude();

            // Only serialize these fields if they have been specified in the
            // include-list.
            if (includeFields != null && includeFields.size() > 0) {
                if (includeFields.contains(property.getName())) {
                    ctx.genson.serialize(object, property.getType(), writer, ctx);
                } else {
                    ctx.genson.serialize(null, property.getType(), writer, ctx);
                }
            }
            // Otherwise, if an exclude-list exists, only serialize properties
            // that are not in this list.
            else if (excludeFields != null && excludeFields.size() > 0) {
                if (!excludeFields.contains(property.getName())) {
                    ctx.genson.serialize(object, property.getType(), writer, ctx);
                } else {
                    ctx.genson.serialize(null, property.getType(), writer, ctx);
                }
            } else {
                // Default handling if no filtered fields have been specified.
                ctx.genson.serialize(object, property.getType(), writer, ctx);
            }
        } else {
            // Default handling if no filtered fields have been specified.
            ctx.genson.serialize(object, property.getType(), writer, ctx);
        }

    }

    private boolean isModel(Class<?> type) {
        return Model.class.isAssignableFrom(type);
    }

    private boolean isInjectable(Class<?> type) {
        return Injectable.class.isAssignableFrom(type);
    }

    @Override
    public Object deserialize(ObjectReader reader, Context ctx) throws IOException {
        return ctx.genson.deserialize(GenericType.of(property.getType()), reader, ctx);
    }
}
