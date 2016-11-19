package com.geecommerce.core.json.genson;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.core.rest.jersey.inject.FilterInjectableProvider;
import com.geecommerce.core.service.QueryOptions;

public class ConvertUtil {
    // Do not ignore these fields.
    private static final List<String> FIELD_BLACK_LIST = new ArrayList<>();
    static {
        FIELD_BLACK_LIST.add("_id");
        FIELD_BLACK_LIST.add("id");
        FIELD_BLACK_LIST.add("__v");
        FIELD_BLACK_LIST.add("version");
    }

    public static final boolean ignoreProperty(String name) {
        QueryOptions queryOptions = App.get().registryGet(FilterInjectableProvider.QUERY_OPTIONS_KEY);

        boolean ignore = true;

        // If object property is from a model class, we check if any properties
        // have been included or excluded.
        if (queryOptions != null && !FIELD_BLACK_LIST.contains(name)) {
            List<String> includeFields = queryOptions.fieldsToInclude();
            List<String> excludeFields = queryOptions.fieldsToExclude();

            // Only serialize these fields if they have been specified in the
            // include-list.
            if (includeFields != null && includeFields.size() > 0) {
                if (includeFields.contains(name)) {
                    ignore = false;
                }
            }
            // Otherwise, if an exclude-list exists, only serialize properties
            // that are not in this list.
            else if (excludeFields != null && excludeFields.size() > 0) {
                if (!excludeFields.contains(name)) {
                    ignore = false;
                }
            } else {
                // Default handling if no filtered fields have been specified.
                ignore = false;
            }
        } else {
            ignore = false;
        }

        return ignore;
    }
}
