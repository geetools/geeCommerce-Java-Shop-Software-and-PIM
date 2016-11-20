package com.geecommerce.search.model;

import java.util.HashMap;
import java.util.Map;

public class SearchQuery {
    private String searchPhrase = null;
    private Map<String, Object> queryParams = new HashMap<>();
    private Map<String, Object> filter = new HashMap<>();
    private String sort;
    private Integer offset;
    private Integer limit;
    private Double priceFrom;
    private Double priceTo;
    private boolean showEvent;
    private boolean showSale;

    public SearchQuery(String searchPhrase) {
        this.searchPhrase = searchPhrase;
    }

    public SearchQuery(String searchPhrase, Integer offset, Integer limit) {
        this.searchPhrase = searchPhrase;
        this.offset = offset;
        this.limit = limit;
    }

    public SearchQuery(String searchPhrase, Map<String, Object> filter, Integer offset, Integer limit) {
        this.searchPhrase = searchPhrase;
        this.filter = filter;
        this.offset = offset;
        this.limit = limit;
    }

    public SearchQuery(String searchPhrase, Map<String, Object> filter, Integer offset, Integer limit, String sort,
        Double priceFrom, Double priceTo, boolean showEvent, boolean showSale) {
        this.searchPhrase = searchPhrase;
        this.filter = filter;
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.showEvent = showEvent;
        this.showSale = showSale;
    }

    public SearchQuery(Map<String, Object> queryParams, Integer offset, Integer limit) {
        this.queryParams = queryParams;
        this.offset = offset;
        this.limit = limit;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public SearchQuery setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
        return this;
    }

    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    public SearchQuery setQueryParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public SearchQuery addQueryParam(String key, Object value) {
        queryParams.put(key, value);
        return this;
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public SearchQuery setFilter(Map<String, Object> filter) {
        this.filter = filter;
        return this;
    }

    public SearchQuery addFilter(String key, Object value) {
        this.filter.put(key, value);
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public SearchQuery setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public SearchQuery setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Double getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(Double priceFrom) {
        this.priceFrom = priceFrom;
    }

    public Double getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(Double priceTo) {
        this.priceTo = priceTo;
    }

    public boolean isShowEvent() {
        return showEvent;
    }

    public boolean isShowSale() {
        return showSale;
    }
}
