package com.geecommerce.search.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.FacetBuilder;
import org.jsoup.Jsoup;

import com.geecommerce.catalog.product.elasticsearch.helper.SynonymsHelper;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.elasticsearch.enums.ElasticSearch;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchIndexHelper;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.system.attribute.TargetObjectCode;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Strings;
import com.geecommerce.search.helper.SearchHelper;
import com.geecommerce.search.model.AutocompleteMapping;
import com.geecommerce.search.model.Facet;
import com.geecommerce.search.model.SearchQuery;
import com.geecommerce.search.model.SearchResult;
import com.geecommerce.search.repository.AutocompleteMappings;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

@Service
public class DefaultSearchService implements SearchService {
    @Inject
    protected App app;

    protected static final String INDEX_TYPE_PRODUCT = "products";
    protected static final String SEARCH_TYPE_PRODUCT = "product";
    protected static final String AUTOCOMPLETE_SEARCH_FIELD = "att_name_raw";
    protected static final String AUTOCOMPLETE_SEARCH_FIELD_2 = "att_name2_raw";

    protected final SearchHelper searchHelper;
    protected final AttributeService attributeService;
    protected final AutocompleteMappings autocompleteMappings;
    protected final ElasticsearchHelper elasticsearchHelper;
    protected final ElasticsearchIndexHelper elasticsearchIndexHelper;

    protected static final Pattern articleNumberPattern = Pattern.compile("[0-9]{5,}[\\-\\.]{1}[0-9]{2,}");

    protected static final Logger LOG = LogManager.getLogger(DefaultSearchService.class);

    @Inject
    public DefaultSearchService(SearchHelper searchHelper, AttributeService attributeService,
        AutocompleteMappings autocompleteMappings, ElasticsearchHelper elasticsearchHelper,
        ElasticsearchIndexHelper elasticsearchIndexHelper) {
        this.searchHelper = searchHelper;
        this.attributeService = attributeService;
        this.autocompleteMappings = autocompleteMappings;
        this.elasticsearchHelper = elasticsearchHelper;
        this.elasticsearchIndexHelper = elasticsearchIndexHelper;
    }

    @Override
    public SearchResult autocomplete(SearchQuery searchQuery) {
        SearchResponse response = getSearchResponse(searchQuery, true);

        SearchResult result = getSearchResult(searchQuery, response);

        return result;
    }

    @Override
    public SearchResult query(SearchQuery searchQuery) {
        SearchResponse response = getSearchResponse(searchQuery, false);

        SearchResult result = getSearchResult(searchQuery, response);

        return result;
    }

    private SearchResponse getSearchResponse(SearchQuery searchQuery, boolean isAutocomplete) {
        Client client = ElasticSearch.CLIENT.get();

        // ---------------------------------------------------------------------
        // Search query
        // ---------------------------------------------------------------------

        QueryBuilder qb = null;
        String searchPhrase = elasticsearchHelper.sanitize(searchQuery.getSearchPhrase());

        if (searchQuery.getQueryParams() != null && searchQuery.getQueryParams().size() > 0) {
            qb = searchHelper.toAndQuery(searchQuery.getQueryParams(), searchQuery.isIgnoreStatusAndVisibilityFlags());
        } else if (searchPhrase != null && isAutocomplete) {
            if (searchQuery.isIgnoreStatusAndVisibilityFlags()) {
                qb = QueryBuilders.boolQuery()
                    .must(QueryBuilders.queryString(Strings.transliterate(searchPhrase))
                        .field(getAutocompleteSearchField()).field(getAutocompleteSearchField2()));
            } else {
                qb = QueryBuilders.boolQuery()
                    .must(QueryBuilders.queryString(Strings.transliterate(searchPhrase))
                        .field(getAutocompleteSearchField()).field(getAutocompleteSearchField2()))
                    .must(new TermQueryBuilder("is_visible", true))
                    .must(new TermQueryBuilder("is_visible_in_pl", true));
            }
        } else if (searchPhrase != null) {
            Matcher m = articleNumberPattern.matcher(searchPhrase.trim());

            if (m.matches()) {
                String slugSearchPhrase = new StringBuilder(Str.UNDERSCORE_2X).append(
                    Strings.slugify(Jsoup.parse(searchPhrase).text()).replaceAll(Str.MINUS_ESCAPED, Str.UNDERSCORE))
                    .append(Str.UNDERSCORE_2X).toString();

                if (searchQuery.isIgnoreStatusAndVisibilityFlags()) {
                    qb = QueryBuilders.boolQuery().must(new TermQueryBuilder("att_article_number_slug", slugSearchPhrase));
                } else {
                    qb = QueryBuilders.boolQuery().must(new TermQueryBuilder("att_article_number_slug", slugSearchPhrase))
                        .must(new TermQueryBuilder("is_visible", true));
                }
            } else {
                StringBuilder searchFor = new StringBuilder(Strings.transliterate(searchPhrase));

                if (searchFor.length() >= 3)
                    searchFor.append(Char.ASTERIX);

                QueryStringQueryBuilder queryBuilder = QueryBuilders.queryString(searchFor.toString());
                if (SynonymsHelper.isSynonymsEnabled()) {
                    queryBuilder.analyzer("synonym");
                }

                if (searchQuery.isIgnoreStatusAndVisibilityFlags()) {
                    qb = QueryBuilders.boolQuery().must(queryBuilder);
                } else {
                    qb = QueryBuilders.boolQuery().must(queryBuilder).must(new TermQueryBuilder("is_visible", true))
                        .must(new TermQueryBuilder("is_visible_in_pl", true));
                }
            }
        } else {
            throw new IllegalArgumentException(
                "You must provide either a search-phrase or query-parameters when searching.");
        }

        // ---------------------------------------------------------------------
        // Search filters
        // ---------------------------------------------------------------------

        FilterBuilder fb = getFilterBuilder(searchQuery);

        // ---------------------------------------------------------------------
        // Prepare search
        // ---------------------------------------------------------------------

        ApplicationContext appCtx = app.context();
        Merchant merchant = appCtx.getMerchant();
        Store store = appCtx.getStore();

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName(merchant.getId(), store.getId()))
            .setMinScore(0.5f).setTypes(Annotations.getIndexedCollectionName(Product.class))
            .setSearchType(SearchType.QUERY_THEN_FETCH).setFrom(searchQuery.getOffset())
            .setSize(searchQuery.getLimit())
            // .addSort(SortBuilders.fieldSort(getSortField(searchQuery.getSort())).order(SortOrder.ASC).ignoreUnmapped(true))
            .setQuery(qb).setPostFilter(fb);

        // System.out.println(qb.toString());

        // ---------------------------------------------------------------------
        // Build facets
        // ---------------------------------------------------------------------

        Map<String, Attribute> facetAttributes = attributeService
            .getAttributesForSearchFilter(TargetObjectCode.PRODUCT_LIST, TargetObjectCode.PRODUCT_FILTER);

        List<FacetBuilder> facetBuilders = searchHelper.toFacetBuilders(facetAttributes);

        for (FacetBuilder facetBuilder : facetBuilders) {
            searchRequestBuilder.addFacet(facetBuilder);
        }

        // ---------------------------------------------------------------------
        // Execute search
        // ---------------------------------------------------------------------

        // client.close(); TODO!!! CLOSE ES CONNECTION

        return searchRequestBuilder.execute().actionGet();
    }

    private SearchResult getSearchResult(SearchQuery searchQuery, SearchResponse response) {
        SearchResult result = app.injectable(SearchResult.class);

        SearchHits hits = response.getHits();

        List<Facet> facets = searchHelper.buildFacets(response);

        if (hits != null && hits.totalHits() > 0) {
            Set<String> uniqueDocumentIds = Sets.newLinkedHashSet();

            for (SearchHit searchHit : hits) {
                uniqueDocumentIds.add((String) searchHit.getSource().get("product_id"));
            }

            result.setTotalNumResults(uniqueDocumentIds.size());

            int x = 0;
            for (String docId : uniqueDocumentIds) {
                if (x >= searchQuery.getOffset() && x < (searchQuery.getOffset() + searchQuery.getLimit())) {
                    result.addDocumentId(docId);
                }

                x++;
            }

            result.setFacets(facets);
        }

        return result;
    }

    protected String indexName(Id merchantId, Id storeId) {
        return elasticsearchIndexHelper.indexName(merchantId, storeId, Product.class);
    }

    @Override
    public List<AutocompleteMapping> getAutocompleteMappingsByKeyword(String keyword) {
        return autocompleteMappings.thatBelongsTo(keyword);
    }

    private String getAutocompleteSearchField() {
        ApplicationContext appCtx = app.context();
        RequestContext reqCtx = appCtx.getRequestContext();
        String auto = AUTOCOMPLETE_SEARCH_FIELD + "_" + reqCtx.getLanguage();
        return auto;
    }

    private String getAutocompleteSearchField2() {
        ApplicationContext appCtx = app.context();
        RequestContext reqCtx = appCtx.getRequestContext();
        String auto = AUTOCOMPLETE_SEARCH_FIELD_2 + "_" + reqCtx.getLanguage();
        return auto;
    }

    private String getSortField(String sort) {
        if ("name".equals(sort)) {
            return getAutocompleteSearchField();
        }
        if ("price".equals(sort)) {
            return "price";
        }
        return getAutocompleteSearchField();
    }

    private FilterBuilder getFilterBuilder(SearchQuery searchQuery) {
        Map<String, Object> filterMap = searchQuery.getFilter();
        List<FilterBuilder> filterBuilders = new ArrayList<>();

        if (filterMap != null && filterMap.size() > 0) {
            for (String key : filterMap.keySet()) {
                filterBuilders.add(FilterBuilders.termFilter(key, filterMap.get(key)));
            }
        }

        if (searchQuery.getPriceFrom() != null || searchQuery.getPriceTo() != null)
            filterBuilders.add(
                FilterBuilders.rangeFilter("price").from(searchQuery.getPriceFrom()).to(searchQuery.getPriceTo()));

        if (searchQuery.isShowEvent()) {
            filterBuilders.add(FilterBuilders.termFilter("is_special", true));
        }

        if (searchQuery.isShowSale()) {
            filterBuilders.add(FilterBuilders.termFilter("is_sale", true));
        }

        return FilterBuilders.andFilter(filterBuilders.toArray(new FilterBuilder[filterBuilders.size()]));
    }
}
