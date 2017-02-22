package com.geecommerce.catalog.product.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.elasticsearch.api.search.SearchResult;
import com.geecommerce.core.elasticsearch.service.ElasticsearchService;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;

import com.geecommerce.catalog.product.helper.ProductListHelper;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductListFilterRule;
import com.geecommerce.catalog.product.model.ProductListQueryNode;
import com.geecommerce.catalog.product.repository.ProductListFilterRules;
import com.geecommerce.catalog.product.repository.ProductLists;
import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.elasticsearch.enums.ElasticSearch;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchIndexHelper;
import com.geecommerce.core.elasticsearch.search.FilterValue;
import com.geecommerce.core.elasticsearch.search.SearchParams;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.system.attribute.TargetObjectCode;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Service
public class DefaultProductListService implements ProductListService {
    @Inject
    protected App app;

    protected static final String INDEX_TYPE_PRODUCT = "products";
    protected static final String SEARCH_TYPE_PRODUCT = "product";

    protected final ProductLists productLists;
    protected final ProductListHelper productListHelper;
    protected final ProductListFilterRules productListFilterRules;
    protected final AttributeService attributeService;
    protected final ElasticsearchIndexHelper elasticsearchIndexHelper;
    protected final ElasticsearchHelper elasticsearchHelper;
    protected final ElasticsearchService elasticsearchService;
    protected final ProductService productService;

    protected static final String CACHE_NAME = "gc/catalog/product_list/facets";
    protected static final String CACHE_KEY_PREFIX = "pl/";

    @Inject
    public DefaultProductListService(ProductLists productLists, ProductListHelper productListHelper,
                                     ProductListFilterRules productListFilterRules, AttributeService attributeService,
                                     ElasticsearchIndexHelper elasticsearchIndexHelper, ElasticsearchHelper elasticsearchHelper, ElasticsearchService elasticsearchService, ProductService productService) {
        this.productLists = productLists;
        this.productListHelper = productListHelper;
        this.productListFilterRules = productListFilterRules;
        this.attributeService = attributeService;
        this.elasticsearchIndexHelper = elasticsearchIndexHelper;
        this.elasticsearchHelper = elasticsearchHelper;
        this.elasticsearchService = elasticsearchService;
        this.productService = productService;
    }

    // ----------------------------------------------------------------------
    // Product List
    // ----------------------------------------------------------------------

    @Override
    public ProductList createProductList(ProductList productList) {
        return productLists.add(productList);
    }

    @Override
    public ProductList getProductList(Id id) {
        return productLists.findById(ProductList.class, id);
    }

    @Override
    public ProductList getProductListByKey(String key) {
        return productLists.havingKey(key);
    }

    @Override
    public Set<Id> getProductIds(ProductListQueryNode queryNode, Map<String, Object> navFilterParts,
        Map<String, Set<Object>> uriFilterParts, SearchParams searchParams) {
        Client client = ElasticSearch.CLIENT.get();

        ApplicationContext appCtx = app.context();
        Merchant merchant = appCtx.getMerchant();
        Store store = appCtx.getStore();

        Map<String, Attribute> filterAttributes = attributeService
            .getAttributesForSearchFilter(TargetObjectCode.PRODUCT_LIST, TargetObjectCode.PRODUCT_FILTER);

        Map<String, FilterValue> searchQueryFilter = elasticsearchHelper.toQueryMap(navFilterParts, filterAttributes);

        List<FilterBuilder> builders = productListHelper.getVisibilityFilters();
        builders.add(productListHelper.buildQuery(queryNode));

        QueryBuilder searchQuery = elasticsearchHelper.buildQuery(builders, searchQueryFilter);

        SearchRequestBuilder searchRequestBuilder = client
            .prepareSearch(elasticsearchIndexHelper.indexName(merchant.getId(), store.getId(), Product.class))
            .setTypes(Annotations.getIndexedCollectionName(Product.class))
            .setSearchType(SearchType.QUERY_THEN_FETCH).setFrom(0).setSize(100000).addFields("product_id")
            .setQuery(searchQuery).setExplain(false);

        SearchResponse response = searchRequestBuilder.execute().actionGet();

        SearchHits hits = response.getHits();

        Set<Id> uniqueDocumentIds = new HashSet<>();

        if (hits != null && hits.totalHits() > 0) {
            for (SearchHit searchHit : hits) {
                uniqueDocumentIds.add(Id.valueOf(searchHit.id()));
            }
        }

        return uniqueDocumentIds;
    }

    @Override
    public Map<Id, Boolean> getProductIdsAndVisibility(ProductListQueryNode queryNode,
        Map<String, Object> navFilterParts, Map<String, Set<Object>> uriFilterParts, SearchParams searchParams) {
        Client client = ElasticSearch.CLIENT.get();

        ApplicationContext appCtx = app.context();
        Merchant merchant = appCtx.getMerchant();
        Store store = appCtx.getStore();

        Map<String, Attribute> filterAttributes = attributeService
            .getAttributesForSearchFilter(TargetObjectCode.PRODUCT_LIST, TargetObjectCode.PRODUCT_FILTER);

        Map<String, FilterValue> searchQueryFilter = elasticsearchHelper.toQueryMap(navFilterParts, filterAttributes);

        List<FilterBuilder> builders = new ArrayList<>();
        builders.add(productListHelper.buildQuery(queryNode));

        QueryBuilder searchQuery = elasticsearchHelper.buildQuery(builders, searchQueryFilter);

        SearchRequestBuilder searchRequestBuilder = client
            .prepareSearch(elasticsearchIndexHelper.indexName(merchant.getId(), store.getId(), Product.class))
            .setTypes(Annotations.getIndexedCollectionName(Product.class))
            .setSearchType(SearchType.QUERY_THEN_FETCH).setFrom(0).setSize(100000)
            .addFields("product_id", "is_visible").setQuery(searchQuery).setExplain(false);

        SearchResponse response = searchRequestBuilder.execute().actionGet();

        SearchHits hits = response.getHits();

        Map<Id, Boolean> uniqueDocumentIds = new HashMap<>();

        boolean error = false;

        if (hits != null && hits.totalHits() > 0) {
            for (SearchHit searchHit : hits) {
                Map<String, SearchHitField> fields = searchHit.getFields();
                SearchHitField field = fields.get("is_visible");

                try {
                    Id id = Id.valueOf(searchHit.id());
                    Boolean isVisible = Boolean.valueOf(field.getValue().toString());

                    uniqueDocumentIds.put(id, field == null || isVisible == null ? false : isVisible);
                } catch (Throwable t) {
                    error = true;
                    System.out.println("getProductIdsAndVisibility error: id=" + searchHit.id() + ", isVisible="
                        + field.getValue());
                }
            }
        }

        if (error)
            throw new IllegalStateException();

        return uniqueDocumentIds;
    }

    @Override
    public Map<String, Object> findProductById(Id productId) {
        Client client = ElasticSearch.CLIENT.get();

        ApplicationContext appCtx = app.context();
        Merchant merchant = appCtx.getMerchant();
        Store store = appCtx.getStore();

        IdsQueryBuilder qb = QueryBuilders.idsQuery(SEARCH_TYPE_PRODUCT);
        qb.addIds(productId.str());

        SearchRequestBuilder searchRequestBuilder = client
            .prepareSearch(elasticsearchIndexHelper.indexName(merchant.getId(), store.getId(), Product.class))
            .setTypes(Annotations.getIndexedCollectionName(Product.class))
            .setSearchType(SearchType.QUERY_THEN_FETCH).setQuery(qb).setExplain(false);

        SearchResponse response = searchRequestBuilder.execute().actionGet();

        SearchHits hits = response.getHits();

        Map<String, Object> productData = null;

        if (hits.getTotalHits() > 0) {
            SearchHit searchHit = hits.getAt(0);

            if (searchHit != null)
                productData = searchHit.getSource();
        }

        return productData;
    }

    @Override
    public Id[] getProductIds(ProductList productList, Integer limit) {
        SearchResult productListResult = null;
        try {
            if(limit == null)
                limit = 10000;
            List<FilterBuilder> builders = productListHelper.getVisibilityFilters();
            builders.add(productListHelper.buildQuery(productList.getQueryNode()));

            Map<String, Attribute> filterAttributes = attributeService
                    .getAttributesForSearchFilter(TargetObjectCode.PRODUCT_LIST, TargetObjectCode.PRODUCT_FILTER);

            productListResult = elasticsearchService.findItems(Product.class, builders, filterAttributes, null, null,
                    new SearchParams(), 0, limit, null);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        if (productListResult != null && productListResult.getDocumentIds() != null
                && productListResult.getDocumentIds().size() > 0) {
            List<Id> ids = new ArrayList<>();
            return elasticsearchHelper.toIds(productListResult.getDocumentIds().toArray());
        }
        return null;
    }

    @Override
    public List<Product> getProducts(ProductList productList, Integer limit) {
        Id[] productIds = getProductIds(productList, limit);
        return productService.getProducts(productIds);
    }

    @Override
    public ProductListFilterRule createProductListFilterRule(ProductListFilterRule filterRule) {
        return productListFilterRules.add(filterRule);
    }

    @Override
    public ProductListFilterRule getProductListFilterRule(String key) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(ProductListFilterRule.Column.KEY, key);

        return productListFilterRules.findOne(ProductListFilterRule.class, filter);
    }

    protected Cache<String, SearchResponse> getCache() {
        CacheManager cm = app.inject(CacheManager.class);
        return cm.getCache(CACHE_NAME);
    }
}
