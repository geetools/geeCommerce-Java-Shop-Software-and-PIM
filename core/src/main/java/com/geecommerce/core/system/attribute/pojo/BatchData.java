package com.geecommerce.core.system.attribute.pojo;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.system.query.model.QueryNode;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public class BatchData {
    protected String forType = null;
    protected Map<String, ContextObject<?>> attributes = null;
    protected Map<String, List<Id>> options = null;
    protected Map<String, ContextObject<List<Id>>> xOptions = null;
    protected Map<String, ContextObject<Boolean>> optOuts = null;

    protected String searchKeyword = null;
    protected QueryNode query = null;
    protected List<Id> ids = null;
    protected List<Id> idsToIgnore = null;

    public static BatchData from(String forType, Update update) {
        return new BatchData()
            .forType(forType)
            .attributes(update.getAttributes())
            .options(update.getOptions())
            .xOptions(update.getXOptions())
            .optOuts(update.getOptOuts())
            .searchKeyword(update.getSearchKeyword())
            .query(update.getQuery())
            .ids(update.getIds())
            .idsToIgnore(update.getIdsToIgnore());
    }

    public String forType() {
        return forType;
    }

    public BatchData forType(String forType) {
        this.forType = forType;
        return this;
    }

    public Map<String, ContextObject<?>> attributes() {
        return attributes;
    }

    public BatchData attributes(Map<String, ContextObject<?>> attributes) {
        this.attributes = attributes;
        return this;
    }

    public Map<String, List<Id>> options() {
        return options;
    }

    public BatchData options(Map<String, List<Id>> options) {
        this.options = options;
        return this;
    }

    public Map<String, ContextObject<List<Id>>> xOptions() {
        return xOptions;
    }

    public BatchData xOptions(Map<String, ContextObject<List<Id>>> xOptions) {
        this.xOptions = xOptions;
        return this;
    }

    public Map<String, ContextObject<Boolean>> optOuts() {
        return optOuts;
    }

    public BatchData optOuts(Map<String, ContextObject<Boolean>> optOuts) {
        this.optOuts = optOuts;
        return this;
    }

    public String searchKeyword() {
        return searchKeyword;
    }

    public BatchData searchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
        return this;
    }

    public QueryNode query() {
        return query;
    }

    public BatchData query(QueryNode query) {
        this.query = query;
        return this;
    }

    public List<Id> ids() {
        return ids;
    }

    public BatchData ids(List<Id> ids) {
        this.ids = ids;
        return this;
    }

    public List<Id> idsToIgnore() {
        return idsToIgnore;
    }

    public BatchData idsToIgnore(List<Id> idsToIgnore) {
        this.idsToIgnore = idsToIgnore;
        return this;
    }

}
