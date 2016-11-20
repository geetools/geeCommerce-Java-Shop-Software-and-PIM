package com.geecommerce.core.elasticsearch.search;

import java.util.Set;

import com.geecommerce.core.enums.FilterType;
import com.google.common.collect.Sets;

public class FilterValue {
    private final FilterType filterType;
    private final Set<Object> values = Sets.newLinkedHashSet();

    public FilterValue(FilterType filterType) {
        this.filterType = filterType;
    }

    public void add(Object value) {
        values.add(value);
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public Set<Object> getValues() {
        return values;
    }
}
