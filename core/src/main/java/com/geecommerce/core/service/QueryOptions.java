package com.geecommerce.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.Str;
import com.geecommerce.core.type.Id;

public class QueryOptions {
    private final List<Id> limitToMerchants;
    private final List<Id> limitToStores;
    private final List<Id> limitToRequestContexts;
    private final Map<String, List<Id>> limitAttributesToStores;
    private final List<String> fieldsToInclude;
    private final List<String> fieldsToExclude;
    private final List<String> attributesToInclude;
    private final List<String> attributesToExclude;
    private final String distinctField;
    private final List<String> sortAsc;
    private final List<String> sortDesc;
    private final Long offset;
    private final Integer limit;
    private final Boolean fetchIdsOnly;
    private final Boolean provideCount;
    private final String singleQueryField;
    private int numFieldsSet = 0;
    private final Boolean refresh;
    private final Boolean noCache;
    private final Boolean fromCacheOnly;

    public QueryOptions(List<Id> limitToMerchants, List<Id> limitToStores, List<Id> limitToRequestContexts,
        Map<String, List<Id>> limitAttributesToStores, List<String> fieldsToInclude, List<String> fieldsToExclude,
        List<String> attributesToInclude, List<String> attributesToExclude, String distinctField,
        List<String> sortAsc, List<String> sortDesc, Long offset, Integer limit, Boolean fetchIdsOnly,
        Boolean provideCount, String singleQueryField, Boolean refresh, Boolean noCache, Boolean fromCacheOnly) {
        this.limitToMerchants = limitToMerchants;
        this.limitToStores = limitToStores;
        this.limitToRequestContexts = limitToRequestContexts;
        this.limitAttributesToStores = limitAttributesToStores;
        this.fieldsToInclude = fieldsToInclude;
        this.fieldsToExclude = fieldsToExclude;
        this.attributesToInclude = attributesToInclude;
        this.attributesToExclude = attributesToExclude;
        this.distinctField = distinctField;
        this.sortAsc = sortAsc;
        this.sortDesc = sortDesc;
        this.offset = offset;
        this.limit = limit;
        this.fetchIdsOnly = fetchIdsOnly;
        this.provideCount = provideCount;
        this.singleQueryField = singleQueryField;
        this.refresh = refresh;
        this.noCache = noCache;
        this.fromCacheOnly = fromCacheOnly;

        if (this.limitToMerchants != null)
            numFieldsSet++;

        if (this.limitToStores != null)
            numFieldsSet++;

        if (this.limitToRequestContexts != null)
            numFieldsSet++;

        if (this.limitAttributesToStores != null)
            numFieldsSet++;

        if (this.fieldsToInclude != null)
            numFieldsSet++;

        if (this.fieldsToExclude != null)
            numFieldsSet++;

        if (this.attributesToInclude != null)
            numFieldsSet++;

        if (this.attributesToExclude != null)
            numFieldsSet++;

        if (this.distinctField != null)
            numFieldsSet++;

        if (this.sortAsc != null)
            numFieldsSet++;

        if (this.sortDesc != null)
            numFieldsSet++;

        if (this.offset != null)
            numFieldsSet++;

        if (this.limit != null)
            numFieldsSet++;

        if (this.fetchIdsOnly != null)
            numFieldsSet++;

        if (this.provideCount != null)
            numFieldsSet++;

        if (this.singleQueryField != null)
            numFieldsSet++;

        if (this.refresh != null)
            numFieldsSet++;

        if (this.noCache != null)
            numFieldsSet++;

        if (this.fromCacheOnly != null)
            numFieldsSet++;
    }

    public List<Id> limitToMerchants() {
        return limitToMerchants;
    }

    public List<Id> limitToStores() {
        return limitToStores;
    }

    public Map<String, List<Id>> limitAttributesToStores() {
        return limitAttributesToStores;
    }

    public boolean hasAttributeStoreLimitFor(String code) {
        if (limitAttributesToStores == null || limitAttributesToStores.size() == 0)
            return false;

        return limitAttributesToStores.get(code) != null && limitAttributesToStores.get(code).size() > 0;
    }

    public List<Id> getStoreLimitForAttribute(String code) {
        if (limitAttributesToStores == null || limitAttributesToStores.size() == 0)
            return null;

        return limitAttributesToStores.get(code);
    }

    public Id getFirstStoreLimitForAttribute(String code) {
        if (limitAttributesToStores == null || limitAttributesToStores.size() == 0)
            return null;

        List<Id> storeIds = limitAttributesToStores.get(code);

        return storeIds == null || storeIds.size() == 0 ? null : storeIds.get(0);
    }

    public List<Id> limitToRequestContexts() {
        return limitToRequestContexts;
    }

    public List<String> fieldsToInclude() {
        return fieldsToInclude;
    }

    public List<String> fieldsToExclude() {
        return fieldsToExclude;
    }

    public List<String> attributesToInclude() {
        return attributesToInclude;
    }

    public List<String> attributesToExclude() {
        return attributesToExclude;
    }

    public String distinctField() {
        return distinctField;
    }

    public List<String> sortAsc() {
        return sortAsc;
    }

    public List<String> sortDesc() {
        return sortDesc;
    }

    public Long offset() {
        return offset;
    }

    public Integer limit() {
        return limit;
    }

    public boolean isFetchIdsOnly() {
        return fetchIdsOnly == null ? false : fetchIdsOnly.booleanValue();
    }

    public boolean isProvideCount() {
        return provideCount == null ? false : provideCount.booleanValue();
    }

    public String singleQueryField() {
        return singleQueryField;
    }

    public boolean isEmpty() {
        return numFieldsSet == 0;
    }

    public int numSetFields() {
        return numFieldsSet;
    }

    public boolean isRefresh() {
        return refresh == null ? false : refresh.booleanValue();
    }

    public boolean isNoCache() {
        return noCache == null ? false : noCache.booleanValue();
    }

    public boolean isFromCacheOnly() {
        return fromCacheOnly == null ? false : fromCacheOnly.booleanValue();
    }

    public String toCacheKey() {
        return sortAsc + ", " + sortDesc + "," + offset + "," + limit + ", " + limitToMerchants + ", " + limitToStores
            + ", " + limitToRequestContexts + ", " + limitAttributesToStores + ", " + fetchIdsOnly;
    }

    @Override
    public String toString() {
        return "QueryOptions [limitToMerchants=" + limitToMerchants + ", limitToStores=" + limitToStores
            + ", limitToRequestContexts=" + limitToRequestContexts + ", limitAttributesToStores="
            + limitAttributesToStores + ", fieldsToInclude=" + fieldsToInclude + ", fieldsToExclude="
            + fieldsToExclude + ", attributesToInclude=" + attributesToInclude + ", attributesToExclude="
            + attributesToExclude + ", distinctField=" + distinctField + ", sortAsc=" + sortAsc + ", sortDesc="
            + sortDesc + ", offset=" + offset + ", limit=" + limit + ", fetchIdsOnly=" + fetchIdsOnly
            + ", provideCount=" + provideCount + ", singleQueryField=" + singleQueryField + ", numFieldsSet="
            + numFieldsSet + ", refresh=" + refresh + ", noCache=" + noCache + ", fromCacheOnly=" + fromCacheOnly
            + "]";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(QueryOptions queryOptions) {
        return new Builder(queryOptions);
    }

    public static class Builder {
        private List<Id> limitToMerchants = null;
        private List<Id> limitToStores = null;
        private List<Id> limitToRequestContexts = null;
        private Map<String, List<Id>> limitAttributesToStores = null;
        private List<String> fieldsToInclude = null;
        private List<String> fieldsToExclude = null;
        private List<String> attributesToInclude = null;
        private List<String> attributesToExclude = null;
        private String distinctField = null;
        private List<String> sortAsc = null;
        private List<String> sortDesc = null;
        private Long offset = null;
        private Integer limit = null;
        private Boolean fetchIdsOnly = null;
        private Boolean provideCount = null;
        private String singleQueryField = null;
        private Boolean refresh;
        private Boolean noCache;
        private Boolean fromCacheOnly;

        private static final String EMPTY = "-";

        public Builder() {

        }

        public Builder(QueryOptions queryOptions) {
            if (queryOptions != null) {
                this.limitToMerchants = queryOptions.limitToMerchants();
                this.limitToStores = queryOptions.limitToStores();
                this.limitToRequestContexts = queryOptions.limitToRequestContexts();
                this.limitAttributesToStores = queryOptions.limitAttributesToStores();
                this.fieldsToInclude = queryOptions.fieldsToInclude();
                this.fieldsToExclude = queryOptions.fieldsToExclude();
                this.attributesToInclude = queryOptions.attributesToInclude();
                this.attributesToExclude = queryOptions.attributesToExclude();
                this.distinctField = queryOptions.distinctField();
                this.sortAsc = queryOptions.sortAsc();
                this.sortDesc = queryOptions.sortDesc();
                this.offset = queryOptions.offset();
                this.limit = queryOptions.limit();
                this.fetchIdsOnly = queryOptions.isFetchIdsOnly();
                this.provideCount = queryOptions.isProvideCount();
                this.singleQueryField = queryOptions.singleQueryField();
                this.refresh = queryOptions.isRefresh();
                this.noCache = queryOptions.isNoCache();
                this.fromCacheOnly = queryOptions.isFromCacheOnly();
            }
        }

        public Builder limitToMerchants(Id... limitToMerchants) {
            return limitToMerchants(Arrays.asList(limitToMerchants));
        }

        public Builder limitToMerchants(List<Id> limitToMerchants) {
            this.limitToMerchants = limitToMerchants;
            return this;
        }

        public Builder limitToStores(Id... limitToStores) {
            return limitToStores(Arrays.asList(limitToStores));
        }

        public Builder limitToStores(List<Id> limitToStores) {
            this.limitToStores = limitToStores;
            return this;
        }

        public Builder limitToRequestContexts(Id... limitToRequestContexts) {
            return limitToRequestContexts(Arrays.asList(limitToRequestContexts));
        }

        public Builder limitToRequestContexts(List<Id> limitToRequestContexts) {
            this.limitToRequestContexts = limitToRequestContexts;
            return this;
        }

        public Builder limitAttributeToStore(String attributeCode, Id storeId) {
            if (Str.isEmpty(attributeCode) || storeId == null)
                return this;

            if (this.limitAttributesToStores == null)
                this.limitAttributesToStores = new LinkedHashMap<>();

            List<Id> limitToStores = this.limitAttributesToStores.get(attributeCode);

            if (limitToStores == null) {
                limitToStores = new ArrayList<>();
                this.limitAttributesToStores.put(attributeCode, limitToStores);
            }

            if (!limitToStores.contains(storeId))
                limitToStores.add(storeId);

            return this;
        }

        public Builder fetchFields(String... fields) {
            return fetchFields(Arrays.asList(fields));
        }

        public Builder fetchFields(List<String> fields) {
            if (fields != null && fields.size() > 0) {
                for (String fieldName : fields) {
                    if (!isExcludeField(fieldName)) {
                        if (fieldsToInclude == null)
                            fieldsToInclude = new ArrayList<>();

                        fieldsToInclude.add(fieldName.trim());
                    } else {
                        if (fieldsToExclude == null)
                            fieldsToExclude = new ArrayList<>();

                        fieldsToExclude.add(fieldName.trim().substring(1));
                    }
                }
            }

            return this;
        }

        public Builder fetchAttributes(String... attributes) {
            return fetchAttributes(Arrays.asList(attributes));
        }

        public Builder fetchAttributes(List<String> attributes) {
            if (attributes != null && attributes.size() > 0) {
                for (String attributeName : attributes) {
                    if (!isExcludeAttribute(attributeName)) {
                        if (attributesToInclude == null)
                            attributesToInclude = new ArrayList<>();

                        attributesToInclude.add(attributeName.trim());
                    } else {
                        if (attributesToExclude == null)
                            attributesToExclude = new ArrayList<>();

                        attributesToExclude.add(attributeName.trim().substring(1));
                    }
                }
            }

            return this;
        }

        public Builder distinctField(String distinctField) {
            this.distinctField = distinctField;
            return this;
        }

        public Builder sortBy(List<String> sortBy) {
            if (sortBy != null && sortBy.size() > 0) {
                sortBy(sortBy.toArray(new String[sortBy.size()]));
            }

            return this;
        }

        public Builder sortBy(String... sortBy) {
            if (sortBy != null && sortBy.length > 0) {
                for (String fieldName : sortBy) {
                    if (!isSortDesc(fieldName)) {
                        if (sortAsc == null)
                            sortAsc = new ArrayList<>();

                        sortAsc.add(fieldName.trim());
                    } else {
                        if (sortDesc == null)
                            sortDesc = new ArrayList<>();

                        sortDesc.add(fieldName.trim().substring(1));
                    }
                }
            }

            return this;
        }

        public Builder sortByDesc(String sortBy) {
            if (sortBy == null || EMPTY.equals(sortBy.trim()))
                return this;

            String descSortBy = sortBy.trim();

            if (!descSortBy.startsWith(Str.MINUS))
                descSortBy = Str.MINUS + descSortBy;

            sortBy(descSortBy);

            return this;
        }

        public Builder fromOffset(Number offset) {
            this.offset = offset == null ? null : offset.longValue();
            return this;
        }

        public Builder limitTo(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder noLimit() {
            this.limit = -1;
            return this;
        }

        public Builder fetchIdsOnly(boolean fetchIdsOnly) {
            this.fetchIdsOnly = fetchIdsOnly;
            return this;
        }

        public Builder provideCount(boolean provideCount) {
            this.provideCount = provideCount;
            return this;
        }

        public Builder singleQueryField(String singleQueryField) {
            this.singleQueryField = singleQueryField;
            return this;
        }

        private boolean isExcludeField(String fieldName) {
            return fieldName != null && fieldName.trim().startsWith(Str.MINUS);
        }

        private boolean isExcludeAttribute(String attributeName) {
            return attributeName != null && attributeName.trim().startsWith(Str.MINUS);
        }

        private boolean isSortDesc(String fieldName) {
            return fieldName != null && fieldName.trim().startsWith(Str.MINUS);
        }

        public Builder refresh(boolean refresh) {
            this.refresh = refresh;
            return this;
        }

        public Builder noCache(boolean noCache) {
            this.noCache = noCache;
            return this;
        }

        public Builder fromCacheOnly(boolean fromCacheOnly) {
            this.fromCacheOnly = fromCacheOnly;
            return this;
        }

        public QueryOptions build() {
            return new QueryOptions(limitToMerchants, limitToStores, limitToRequestContexts, limitAttributesToStores,
                fieldsToInclude, fieldsToExclude, attributesToInclude, attributesToExclude, distinctField, sortAsc,
                sortDesc, offset, limit, fetchIdsOnly, provideCount, singleQueryField, refresh, noCache,
                fromCacheOnly);
        }
    }
}
