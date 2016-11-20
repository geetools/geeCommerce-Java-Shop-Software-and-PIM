package com.geecommerce.core.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.Char;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.util.Arr;

public class MultiContextCacheKey {
    private static final String CACHE_KEY_QUERY_PREFIX = "mctx-query/";
    private static final String CACHE_KEY_NULL_VALUE = "null";
    private static final int CUT_QUERY_VALUE_AT = 100;

    private static final char REQUEST_CONTEXT_PREFIX = 'R';
    private static final char STORE_PREFIX = 'S';
    private static final char MERCHANT_PREFIX = 'M';
    private static final char GLOBAL_PREFIX = 'G';

    private final String cacheKey;

    public <T extends Model> MultiContextCacheKey(Class<T> modelClass, Map<String, Object> filter,
        String distinctFieldName, QueryOptions queryOptions) {
        StringBuilder cacheKey = new StringBuilder(CACHE_KEY_QUERY_PREFIX).append(modelClass.getName())
            .append(Char.SLASH);

        cacheKey.append(GLOBAL_PREFIX).append(Char.SLASH);

        appendQueryPart(cacheKey, filter);

        cacheKey.append(Char.SLASH).append(distinctFieldName);

        if (queryOptions != null)
            cacheKey.append(Char.SLASH).append(queryOptions.toCacheKey());

        this.cacheKey = cacheKey.toString();
    }

    public <T extends Model> MultiContextCacheKey(Class<T> modelClass, RequestContext reqCtx,
        Map<String, Object> filter, String distinctFieldName, QueryOptions queryOptions) {
        StringBuilder cacheKey = new StringBuilder(CACHE_KEY_QUERY_PREFIX).append(modelClass.getName())
            .append(Char.SLASH);

        cacheKey.append(REQUEST_CONTEXT_PREFIX)
            .append(reqCtx == null || reqCtx.getId() == null ? 0 : reqCtx.getId().longValue()).append(Char.SLASH);

        appendQueryPart(cacheKey, filter);

        cacheKey.append(Char.SLASH).append(distinctFieldName);

        if (queryOptions != null)
            cacheKey.append(Char.SLASH).append(queryOptions.toCacheKey());

        this.cacheKey = cacheKey.toString();
    }

    public <T extends Model> MultiContextCacheKey(Class<T> modelClass, Store store, Map<String, Object> filter,
        String distinctFieldName, QueryOptions queryOptions) {
        StringBuilder cacheKey = new StringBuilder(CACHE_KEY_QUERY_PREFIX).append(modelClass.getName())
            .append(Char.SLASH);

        cacheKey.append(STORE_PREFIX).append(store.getId().longValue()).append(Char.SLASH);

        appendQueryPart(cacheKey, filter);

        cacheKey.append(Char.SLASH).append(distinctFieldName);

        if (queryOptions != null)
            cacheKey.append(Char.SLASH).append(queryOptions.toCacheKey());

        this.cacheKey = cacheKey.toString();
    }

    public <T extends Model> MultiContextCacheKey(Class<T> modelClass, Merchant merchant, Map<String, Object> filter,
        String distinctFieldName, QueryOptions queryOptions) {
        StringBuilder cacheKey = new StringBuilder(CACHE_KEY_QUERY_PREFIX).append(modelClass.getName())
            .append(Char.SLASH);

        cacheKey.append(MERCHANT_PREFIX).append(merchant.getId().longValue()).append(Char.SLASH);

        appendQueryPart(cacheKey, filter);

        cacheKey.append(Char.SLASH).append(distinctFieldName);

        if (queryOptions != null)
            cacheKey.append(Char.SLASH).append(queryOptions.toCacheKey());

        this.cacheKey = cacheKey.toString();
    }

    protected void appendQueryPart(StringBuilder key, Object query) {
        if (query == null) {
            key.append(CACHE_KEY_NULL_VALUE);
            return;
        }

        Class<?> c = query.getClass();

        // --MAP--
        if (query instanceof Map<?, ?>) {
            Map<String, Object> queryMap = (Map<String, Object>) query;
            if (queryMap.size() > 0) {
                Set<String> keys = queryMap.keySet();
                int y = 0;

                key.append(Char.CURLY_BRACKET_OPEN);

                for (String k : keys) {
                    if (y > 0)
                        key.append(Char.SEMI_COLON);

                    Object v = queryMap.get(k);
                    key.append(k).append(Char.EQUALS);
                    appendQueryPart(key, v);

                    y++;
                }

                key.append(Char.CURLY_BRACKET_CLOSE);

            } else {
                key.append(CACHE_KEY_NULL_VALUE);
                return;
            }
            // --COLLECTION--
        } else if (c.isArray() || query instanceof Collection<?>) {
            Collection<?> col = c.isArray() ? Arr.asCollection(query) : (Collection<?>) query;

            StringBuilder colVal = new StringBuilder();

            key.append(Char.SQUARE_BRACKET_OPEN);

            int y = 0;
            for (Object o : col) {
                if (y > 0)
                    colVal.append(Char.COMMA);

                appendQueryPart(colVal, o);

                y++;
            }

            if (colVal.length() > CUT_QUERY_VALUE_AT) {
                int hashCode = colVal.toString().hashCode();
                key.append(colVal.substring(0, CUT_QUERY_VALUE_AT)).append(Char.HASH).append(hashCode);
            } else {
                key.append(colVal);
            }

            key.append(Char.SQUARE_BRACKET_CLOSE);

        } else {
            String s = String.valueOf(query);

            if (s.length() > CUT_QUERY_VALUE_AT) {
                int hashCode = s.hashCode();
                key.append(s.substring(0, CUT_QUERY_VALUE_AT)).append(Char.HASH).append(hashCode);
            } else {
                key.append(s);
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cacheKey == null) ? 0 : cacheKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        MultiContextCacheKey other = (MultiContextCacheKey) obj;

        if (cacheKey == null) {
            if (other.cacheKey != null)
                return false;
        } else if (!cacheKey.equals(other.cacheKey))
            return false;

        return true;
    }

    @Override
    public String toString() {
        return cacheKey;
    }
}
