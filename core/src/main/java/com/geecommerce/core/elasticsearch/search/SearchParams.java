package com.geecommerce.core.elasticsearch.search;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchParams {
    private String searchPhrase = null;
    private Map<String, Object> queryParams = new HashMap<>();
    private Map<String, Object> filter = new HashMap<>();
    private Integer offset;
    private Integer limit;
    private String sort;

    public SearchParams() {
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public SearchParams setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
        return this;
    }

    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    public SearchParams setQueryParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public SearchParams addQueryParam(String key, Object value) {
        queryParams.put(key, value);
        return this;
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public SearchParams setFilter(Map<String, Object> filter) {
        this.filter = filter;
        return this;
    }

    public SearchParams addFilter(String key, Object value) {
        this.filter.put(key, value);
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public SearchParams setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public SearchParams setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public String getSort() {
        return sort;
    }

    public SearchParams setSort(String sort) {
        this.sort = sort;
        return this;
    }

    public FilterBuilder getFilterBuilder() {
        List<FilterBuilder> filterBuilders = new ArrayList<>();

        return filterBuilders.size() == 0 ? null : FilterBuilders.andFilter(filterBuilders.toArray(new FilterBuilder[filterBuilders.size()]));
    }

}
