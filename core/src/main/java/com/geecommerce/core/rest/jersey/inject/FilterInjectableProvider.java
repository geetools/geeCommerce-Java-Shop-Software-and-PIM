package com.geecommerce.core.rest.jersey.inject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.core.App;
import com.geecommerce.core.rest.RestHelper;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.system.query.model.QueryNode;
import com.geecommerce.core.type.TypeConverter;
import com.geecommerce.core.util.Json;
import com.google.inject.Singleton;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

@Profile
@Provider
@Singleton
public class FilterInjectableProvider extends AbstractHttpContextInjectable<Filter>
    implements InjectableProvider<FilterParam, Type> {
    public static final String QUERY_OPTIONS_KEY = "gc/api/queryOptions";

    public FilterInjectableProvider() {
    }

    protected static enum FilterKey {
        FIELDS, ATTRIBUTES, SORT, LIMIT, OFFSET, NOCACHE, QUERY, SEARCH_KEYWORD, IDS, IGNORE_IDS;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Undefined;
    }

    @Override
    public Injectable<Filter> getInjectable(ComponentContext ic, FilterParam fp, Type type) {
        if (type instanceof Class<?> && Filter.class.isAssignableFrom((Class<?>) type)) {
            return new FilterInjectableProvider();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Filter getValue(HttpContext ctx) {
        HttpRequestContext request = ctx.getRequest();

        MultivaluedMap<String, String> parameters = request.getQueryParameters();

        // if (!HttpMethod.GET.equals(request.getMethod()) || parameters == null
        // || parameters.size() == 0)
        // {
        // return null;
        // }

        Filter filter = new Filter();

        Set<String> keys = parameters.keySet();

        for (String key : keys) {
            if (FilterKey.FIELDS.name().equalsIgnoreCase(key)) {
                List<String> fieldList = parameters.get(key);

                if (fieldList != null && fieldList.size() > 0) {
                    filter.setFields(toStringList(fieldList));
                }
            } else if (FilterKey.ATTRIBUTES.name().equalsIgnoreCase(key)) {
                List<String> attributeList = parameters.get(key);

                if (attributeList != null && attributeList.size() > 0) {
                    filter.setAttributes(toStringList(attributeList));
                }
            } else if (FilterKey.SORT.name().equalsIgnoreCase(key)) {
                List<String> sortList = parameters.get(key);

                if (sortList != null && sortList.size() > 0)
                    filter.setSort(toStringList(sortList));
            } else if (FilterKey.LIMIT.name().equalsIgnoreCase(key)) {
                filter.setLimit(TypeConverter.asInteger(parameters.getFirst(key)));
            } else if (FilterKey.OFFSET.name().equalsIgnoreCase(key)) {
                filter.setOffset(TypeConverter.asLong(parameters.getFirst(key)));
            } else if (FilterKey.NOCACHE.name().equalsIgnoreCase(key)) {
                filter.setNoCache(TypeConverter.asBoolean(parameters.getFirst(key)));
            } else if (FilterKey.QUERY.name().equalsIgnoreCase(key)) {
                String queryString = parameters.getFirst(key);
                if (!StringUtils.isEmpty(queryString)) {
                    Map<String, Object> queryNodeMap = Json.fromJson(queryString, HashMap.class);
                    QueryNode queryNode = App.get().model(QueryNode.class);
                    queryNode.fromMap(queryNodeMap);
                    filter.setQuery(queryNode);
                }
            } else {
                List<Object> valueList = toTypedList(parameters.get(key));
                filter.append(key, valueList.size() == 1 ? valueList.get(0) : valueList);
            }
        }

        List<String> checkedFields = checkFields(filter.getFields(), filter.getAttributes());

        QueryOptions queryOptions = QueryOptions.builder().fetchFields(checkedFields).sortBy(filter.getSort())
            .limitTo(filter.getLimit()).fromOffset(filter.getOffset()).noCache(filter.isNoCache()).build();

        App.get().registryPut(QUERY_OPTIONS_KEY, queryOptions);

        return filter;
    }

    protected List<String> checkFields(List<String> fields, List<String> attributes) {
        if (attributes != null && !attributes.isEmpty()) {
            if (fields == null)
                fields = new ArrayList<>();

            if (!fields.contains("attributes")) {
                fields.add("attributes");

                if (!fields.contains("attributeId"))
                    fields.add("attributeId");

                if (!fields.contains("optOut"))
                    fields.add("optOut");

                if (!fields.contains("optionIds"))
                    fields.add("optionIds");

                if (!fields.contains("properties"))
                    fields.add("properties");

                if (!fields.contains("sortOrder"))
                    fields.add("sortOrder");

                if (!fields.contains("value"))
                    fields.add("value");

                if (!fields.contains("xOptionIds"))
                    fields.add("xOptionIds");

                if (!fields.contains("attributeOptions"))
                    fields.add("attributeOptions");

                if (!fields.contains("label"))
                    fields.add("label");
            }
        }

        return fields;
    }

    public List<String> toStringList(List<String> value) {
        List<String> valueList = new ArrayList<>();
        for (String v : value) {
            if (v.indexOf(',') != -1) {
                String[] csv = v.split(",");
                for (String s : csv) {
                    valueList.add(s.trim());
                }
            } else {
                valueList.add(v);
            }
        }

        return valueList;
    }

    public List<Object> toTypedList(List<String> value) {
        List<Object> valueList = new ArrayList<>();
        for (String v : value) {
            if (v.indexOf(',') != -1) {
                String[] csv = v.split(",");
                for (String s : csv) {
                    valueList.add(RestHelper.guessType(s.trim()));
                }
            } else {
                valueList.add(RestHelper.guessType(v));
            }
        }

        return valueList;
    }
}
