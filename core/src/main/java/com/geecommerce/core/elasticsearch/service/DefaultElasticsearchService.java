package com.geecommerce.core.elasticsearch.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.elasticsearch.api.search.Facet;
import com.geecommerce.core.elasticsearch.api.search.SearchResult;
import com.geecommerce.core.elasticsearch.enums.ElasticSearch;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchIndexHelper;
import com.geecommerce.core.elasticsearch.search.FacetCount;
import com.geecommerce.core.elasticsearch.search.FieldKey;
import com.geecommerce.core.elasticsearch.search.FieldValue;
import com.geecommerce.core.elasticsearch.search.FilterValue;
import com.geecommerce.core.elasticsearch.search.SearchParams;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

@Service
public class DefaultElasticsearchService implements ElasticsearchService {
    @Inject
    protected App app;

    protected final ElasticsearchIndexHelper elasticsearchIndexHelper;
    protected final ElasticsearchHelper elasticsearchHelper;
    protected final AttributeService attributeService;

    @Inject
    public DefaultElasticsearchService(ElasticsearchIndexHelper elasticsearchIndexHelper,
        ElasticsearchHelper elasticsearchHelper, AttributeService attributeService) {
        this.elasticsearchIndexHelper = elasticsearchIndexHelper;
        this.elasticsearchHelper = elasticsearchHelper;
        this.attributeService = attributeService;
    }

    @Override
    public <T extends Model> SearchResult findItems(Class<T> modelClass, List<FilterBuilder> filterBuilders,
        Map<String, Attribute> filterAttributes, Map<String, Object> navFilterParts,
        Map<String, Set<Object>> uriFilterParts, SearchParams searchParams, Integer offset, Integer limit,
        String sort) {
        searchParams.setOffset(offset).setLimit(limit).setSort(sort);
        return findItems(modelClass, filterBuilders, filterAttributes, navFilterParts, uriFilterParts, searchParams);
    }

    @Override
    public <T extends Model> SearchResult findItems(Class<T> modelClass, List<FilterBuilder> filterBuilders,
        Map<String, Attribute> filterAttributes, Map<String, Object> navFilterParts,
        Map<String, Set<Object>> uriFilterParts, SearchParams searchParams) {
        Client client = ElasticSearch.CLIENT.get();

        ApplicationContext appCtx = app.context();
        Merchant merchant = appCtx.getMerchant();
        Store store = appCtx.getStore();

        List<FacetBuilder> facetBuilders = elasticsearchHelper.toFacetBuilders(filterAttributes);

        // String[] fields = productListHelper.toFieldList(filterAttributes);

        // ---------------------------------------------------------------------
        // Get a limited set of fields from all the results, so that we can
        // get hold of all the labels when building the facets.
        // ---------------------------------------------------------------------

        Map<String, FilterValue> searchQueryFilter = elasticsearchHelper.toQueryMap(navFilterParts, filterAttributes);

        QueryBuilder searchQuery = elasticsearchHelper.buildQuery(filterBuilders, searchQueryFilter);

        String sortField = searchParams.getSort();
        /*
         * getNameSearchField(); if ("price".equals(searchParams.getSort())) {
         * sortField = "price"; }
         */
        FilterBuilder fb = searchParams.getFilterBuilder();

        // Cache<String, SearchResponse> cache = getCache();

        // String cacheKey = CACHE_KEY_PREFIX + productList.getId();

        SearchResponse responseAll = null;

        // SearchResponse responseAll = cache.get(cacheKey);

        SearchRequestBuilder searchRequestBuilder = null;

        if (responseAll == null) {
            searchRequestBuilder = client
                .prepareSearch(elasticsearchIndexHelper.indexName(merchant.getId(), store.getId(), modelClass))
                .setTypes(Annotations.getIndexedCollectionName(modelClass))
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                // .setFetchSource(fields, null)
                // .addFields(fields)
                .setFrom(0).setSize(10000) // TODO: improve static value
                .setQuery(searchQuery).setPostFilter(fb).setExplain(false);

            if (!Strings.isNullOrEmpty(sortField)) {
                searchRequestBuilder
                    .addSort(SortBuilders.fieldSort(sortField).order(SortOrder.ASC).ignoreUnmapped(true));
            }

            for (FacetBuilder facetBuilder : facetBuilders) {
                searchRequestBuilder.addFacet(facetBuilder);
            }

            responseAll = searchRequestBuilder.execute().actionGet();

            // cache.put(cacheKey, responseAll);
        }

        // ---------------------------------------------------------------------
        // Now we remove the multi-selectable filterdefShowAlls so that we know
        // which
        // other values are potentially available.
        // ---------------------------------------------------------------------
        Map<String, Set<Object>> nonMultipleFilterParts = getNonMultipleFilterParts(uriFilterParts, filterAttributes);

        searchQueryFilter = elasticsearchHelper.toQueryMap(navFilterParts, nonMultipleFilterParts, filterAttributes);
        searchQuery = elasticsearchHelper.buildQuery(filterBuilders, searchQueryFilter);

        searchRequestBuilder = client
            .prepareSearch(elasticsearchIndexHelper.indexName(merchant.getId(), store.getId(), modelClass))
            .setTypes(Annotations.getIndexedCollectionName(modelClass)).setSearchType(SearchType.QUERY_THEN_FETCH)
            // .addFields(fields)
            .setFrom(0).setSize(10000) // TODO: improve
            // static value
            .setQuery(searchQuery).setPostFilter(fb).setFetchSource(false).setExplain(false);

        if (!Strings.isNullOrEmpty(sortField)) {
            searchRequestBuilder.addSort(SortBuilders.fieldSort(sortField).order(SortOrder.ASC).ignoreUnmapped(true));
        }

        for (FacetBuilder facetBuilder : facetBuilders) {
            searchRequestBuilder.addFacet(facetBuilder);
        }

        SearchResponse responseNonMulti = searchRequestBuilder.execute().actionGet();

        // -----------------------------------------------------------------------
        // Now we do the actual query which will return complete source
        // documents.
        // -----------------------------------------------------------------------
        searchQueryFilter = elasticsearchHelper.toQueryMap(navFilterParts, uriFilterParts, filterAttributes);
        searchQuery = elasticsearchHelper.buildQuery(filterBuilders, searchQueryFilter);

        // System.out.println(new String(searchQuery.buildAsBytes().toBytes()));

        searchRequestBuilder = client
            .prepareSearch(elasticsearchIndexHelper.indexName(merchant.getId(), store.getId(), modelClass))
            .setTypes(Annotations.getIndexedCollectionName(modelClass)).setSearchType(SearchType.QUERY_THEN_FETCH)
            .setFrom(0).setSize(100000).setQuery(searchQuery).setPostFilter(fb).setFetchSource(false)
            .setExplain(false);

        if (!Strings.isNullOrEmpty(sortField)) {
            searchRequestBuilder.addSort(SortBuilders.fieldSort(sortField).order(SortOrder.ASC).ignoreUnmapped(true));
        }

        for (FacetBuilder facetBuilder : facetBuilders) {
            searchRequestBuilder.addFacet(facetBuilder);
        }

        SearchResponse response = searchRequestBuilder.execute().actionGet();

        // Create flat field and facet-count index for better performance.
        Map<FieldKey, FieldValue> fieldIndex = elasticsearchHelper.toFlatFieldIndex(response, responseAll,
            filterAttributes);
        Map<FieldKey, FacetCount> facetCountIndex = elasticsearchHelper.toFlatFacetCountIndex(response,
            responseNonMulti, responseAll);

        // ---------------------------------------------------------------------
        // Process response
        // ---------------------------------------------------------------------

        SearchHits hits = response.getHits();

        SearchResult result = app.injectable(SearchResult.class);

        if (hits != null && hits.totalHits() > 0) {
            Set<String> uniqueDocumentIds = Sets.newLinkedHashSet();

            for (SearchHit searchHit : hits) {
                uniqueDocumentIds.add((String) searchHit.getId());
            }

            result.setTotalNumResults(uniqueDocumentIds.size());

            int x = 0;
            for (String docId : uniqueDocumentIds) {
                if (x >= searchParams.getOffset() && x < (searchParams.getOffset() + searchParams.getLimit())) {
                    result.addDocumentId(docId);
                }

                x++;
            }

            List<Facet> facets = elasticsearchHelper.retrieveFacets(responseAll, fieldIndex, facetCountIndex,
                filterAttributes);

            result.setFacets(facets);
        }

        return result;
    }

    @Override
    public <T extends Model> SearchResult findItems(Class<T> modelClass, String query, SearchParams searchParams,
        List<Id> attributeTargetObjectIds) {
        Client client = ElasticSearch.CLIENT.get();

        // ---------------------------------------------------------------------
        // Search query
        // ---------------------------------------------------------------------

        QueryBuilder qb = null;
        String searchPhrase = elasticsearchHelper.sanitize(query);

        if (searchParams.getQueryParams() != null && searchParams.getQueryParams().size() > 0) {
            qb = elasticsearchHelper.toAndQuery(searchParams.getQueryParams());
        } else if (searchPhrase != null) {
            StringBuilder searchFor = new StringBuilder(com.geecommerce.core.util.Strings.transliterate(searchPhrase));

            if (searchFor.length() > 4)
                searchFor.append(Char.ASTERIX);

            QueryStringQueryBuilder queryBuilder = QueryBuilders.queryString(searchFor.toString());

            qb = QueryBuilders.boolQuery().must(queryBuilder);
        } else {
            throw new IllegalArgumentException(
                "You must provide either a search-phrase or query-parameters when searching.");
        }

        // ---------------------------------------------------------------------
        // Search filters
        // ---------------------------------------------------------------------

        FilterBuilder fb = searchParams.getFilterBuilder();

        // ---------------------------------------------------------------------
        // Prepare search
        // ---------------------------------------------------------------------

        ApplicationContext appCtx = app.context();
        Merchant merchant = appCtx.getMerchant();
        Store store = appCtx.getStore();

        SearchRequestBuilder searchRequestBuilder = client
            .prepareSearch(elasticsearchIndexHelper.indexName(merchant.getId(), store.getId(), modelClass))
            .setMinScore(0.5f).setTypes(Annotations.getIndexedCollectionName(modelClass))
            .setSearchType(SearchType.QUERY_THEN_FETCH).setFrom(searchParams.getOffset())
            .setSize(searchParams.getLimit()).setQuery(qb).setPostFilter(fb);

        // ---------------------------------------------------------------------
        // Build facets
        // ---------------------------------------------------------------------

        Map<String, Attribute> facetAttributes = attributeService
            .getAttributesForSearchFilter(attributeTargetObjectIds);

        List<FacetBuilder> facetBuilders = elasticsearchHelper.toFacetBuilders(facetAttributes);

        for (FacetBuilder facetBuilder : facetBuilders) {
            searchRequestBuilder.addFacet(facetBuilder);
        }

        // ---------------------------------------------------------------------
        // Execute search
        // ---------------------------------------------------------------------

        // client.close(); TODO!!! CLOSE ES CONNECTION

        SearchResponse response = searchRequestBuilder.execute().actionGet();

        SearchHits hits = response.getHits();

        SearchResult result = app.injectable(SearchResult.class);

        if (hits != null && hits.totalHits() > 0) {
            Set<String> uniqueDocumentIds = Sets.newLinkedHashSet();

            for (SearchHit searchHit : hits) {
                uniqueDocumentIds.add((String) searchHit.getId());
            }

            result.setTotalNumResults(uniqueDocumentIds.size());

            int x = 0;
            for (String docId : uniqueDocumentIds) {
                if (x >= searchParams.getOffset() && x < (searchParams.getOffset() + searchParams.getLimit())) {
                    result.addDocumentId(docId);
                }

                x++;
            }

            // List<Facet> facets =
            // elasticsearchHelper.retrieveFacets(responseAll, fieldIndex,
            // facetCountIndex, filterAttributes);

            // result.setFacets(facets);
        }

        return result;// return searchRequestBuilder.execute().actionGet();
    }

    protected Map<String, Set<Object>> getNonMultipleFilterParts(Map<String, Set<Object>> allFilterParts,
        Map<String, Attribute> filterAttributes) {
        if (allFilterParts == null || filterAttributes == null)
            return null;

        Set<String> keys = allFilterParts.keySet();

        Map<String, Set<Object>> allFilterPartsCopy = Maps.newLinkedHashMap(allFilterParts);

        for (String attrCode : keys) {
            Attribute attr = filterAttributes.get(attrCode);

            if (attr != null && attr.isProductListFilterMulti()) {
                allFilterPartsCopy.remove(attrCode);
            }
        }

        return allFilterPartsCopy;
    }

}
