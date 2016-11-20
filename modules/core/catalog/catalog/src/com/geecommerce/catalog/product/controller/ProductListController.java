package com.geecommerce.catalog.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.index.query.FilterBuilder;

import com.geecommerce.catalog.product.FilterContext;
import com.geecommerce.catalog.product.PagingContext;
import com.geecommerce.catalog.product.ProductListConstant;
import com.geecommerce.catalog.product.helper.ProductHelper;
import com.geecommerce.catalog.product.helper.ProductListHelper;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductListFilterRule;
import com.geecommerce.catalog.product.service.ProductListService;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.catalog.search.ProductSearchParams;
import com.geecommerce.core.App;
import com.geecommerce.core.elasticsearch.api.search.SearchResult;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.elasticsearch.service.ElasticsearchService;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.system.attribute.TargetObjectCode;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.system.helper.TargetSupportHelper;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.util.Requests;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.PathParam;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;
import com.google.inject.Injector;

@Controller
@Request("/catalog/product-list")
public class ProductListController {
    @Inject
    protected App app;

    protected static final String LOAD_PRODUCT_ATTRIBUTES_CONFIG_KEY = "catalog/product_list/load_product_attributes";
    protected static final String CURRENT_PRODUCT_LIST_NAME = "cat";

    protected final ProductListService productListService;
    protected final ProductService productService;
    protected final ProductListHelper productListHelper;
    protected final AttributeService attributeService;
    protected final ProductHelper productHelper;
    protected final ElasticsearchService elasticsearchService;
    protected final ElasticsearchHelper elasticsearchHelper;

    @Inject
    protected Injector injector;

    @Inject
    public ProductListController(ProductListService productListService, ProductService productService,
        ProductListHelper productListHelper, AttributeService attributeService, ProductHelper productHelper,
        ElasticsearchService elasticsearchService, ElasticsearchHelper elasticsearchHelper) {
        this.productListService = productListService;
        this.productService = productService;
        this.productListHelper = productListHelper;
        this.productHelper = productHelper;
        this.attributeService = attributeService;
        this.elasticsearchService = elasticsearchService;
        this.elasticsearchHelper = elasticsearchHelper;
    }

    @Request("/view/{id}")
    public Result view(@PathParam("id") Id id, FilterContext filterCtx, PagingContext pagingCtx) {
        long start = System.currentTimeMillis();

        List<Product> products = null;
        SearchResult productListResult = null;

        // Get selected productList.
        ProductList productList = productListService.getProductList(id);

        Map<String, Set<Object>> requestFilters = new LinkedHashMap<>();

        if (productList != null) {
            // See if the current product listing has a specific filter-rule for
            // parsing the URI.
            ProductListFilterRule productListFilterRule = productListFilterRule(productList);

            // Get filter-parts from the URI (always the last part ->
            // /productList-URI/<--filter-part-->/)
            collectURIFilters(productList, productListFilterRule, requestFilters);

            collectParamFilters(productListFilterRule, requestFilters);

            // After having collected all the filter-attributes, we can now
            // carry out the search.
            ProductSearchParams searchParams = new ProductSearchParams();
            searchParams.setLimit(pagingCtx.getNumResultsPerPage());
            searchParams.setOffset(pagingCtx.getOffset());
            searchParams.setSort(filterCtx.getSort());
            searchParams.setPriceFrom(filterCtx.getPriceFrom()).setPriceTo(filterCtx.getPriceTo());

            Map<String, Object> queryMap = null;
            if (productList.getQuery() != null && !productList.getQuery().isEmpty()) {
                queryMap = Json.fromJson(productList.getQuery(), HashMap.class);
            }

            List<FilterBuilder> builders = productListHelper.getVisibilityFilters();
            builders.add(productListHelper.buildQuery(productList.getQueryNode()));

            Map<String, Attribute> filterAttributes = attributeService
                .getAttributesForSearchFilter(TargetObjectCode.PRODUCT_LIST, TargetObjectCode.PRODUCT_FILTER);

            productListResult = elasticsearchService.findItems(Product.class, builders, filterAttributes, null,
                requestFilters, searchParams);

            // If product document-ids have been returned, fetch their
            // respective products.
            if (productListResult != null && productListResult.getDocumentIds() != null
                && productListResult.getDocumentIds().size() > 0) {
                Id[] productIds = elasticsearchHelper.toIds(productListResult.getDocumentIds().toArray());

                // Only load a limited list of attributes if configured.
                List<String> attributes = app.cpStrList_(LOAD_PRODUCT_ATTRIBUTES_CONFIG_KEY);
                QueryOptions qb = null;

                if (attributes != null && !attributes.isEmpty()) {
                    qb = QueryOptions.builder().fetchAttributes(attributes.toArray(new String[attributes.size()]))
                        .build();
                }

                products = orderByProductIds(productService.getProducts(Arrays.asList(productIds), qb), productIds);

                productHelper.rememberCurrentProductList(CURRENT_PRODUCT_LIST_NAME, Arrays.asList(productIds));
            }
        }

        if ((System.currentTimeMillis() - start) > 5000)
            System.out.println("Product list took: " + (System.currentTimeMillis() - start));

        // Only tell varnish to cache if the user is not filtering.
        if (requestFilters.isEmpty()) {
            // return Views.forward("catalog/product/list", "1h");
            return Results.view("catalog/product/list").bind("products", products).bind("productList", productList)
                .bind("productListResult", productListResult).bind("filterCtx", filterCtx)
                .bind("pagingCtx", pagingCtx);
        } else {
            return Results.view("catalog/product/list").bind("products", products).bind("productList", productList)
                .bind("productListResult", productListResult).bind("filterCtx", filterCtx)
                .bind("pagingCtx", pagingCtx);
        }
    }

    protected ProductListFilterRule productListFilterRule(ProductList productList) {
        ProductListFilterRule filterRule = productList.getFilterRule();

        if (filterRule != null) {
            // Used in the ProductListFilterWidget-Widget later.
            app.registryPut(ProductListConstant.FILTER_RULE, filterRule);
        }

        return filterRule;
    }

    protected void collectURIFilters(ProductList productList, ProductListFilterRule catFilterRule,
        Map<String, Set<Object>> requestFilters) {
        String originalURI = app.getOriginalURI();
        String productListURI = app.helper(TargetSupportHelper.class).findURI(productList);

        if (originalURI.equals(productListURI)) {
            return;
        }

        String filterPartURI = Requests.extractLastURIPart(originalURI);

        if (originalURI.equals(filterPartURI)) {
            return;
        }

        if (filterPartURI.length() > 1) {
            Map<String, Set<Object>> uriFilterParts = productListHelper.getFilterPartsFromURI(filterPartURI, catFilterRule);

            // Used in the Navigation-Widget later.
            app.registryPut(ProductListConstant.URI_FILTER_PARTS, uriFilterParts);

            requestFilters.putAll(uriFilterParts);
        }
    }

    protected void collectParamFilters(ProductListFilterRule navFilterRule, Map<String, Set<Object>> requestFilters) {
        Map<String, Attribute> filterAttributes = attributeService
            .getAttributesForSearchFilter(TargetObjectCode.PRODUCT_LIST, TargetObjectCode.PRODUCT_FILTER);

        Map<String, String> attributesAliasIndex = elasticsearchHelper.buildAttributeAliasIndex(filterAttributes);

        // Used in the Navigation-Widget later.
        app.registryPut(ProductListConstant.FILTER_ATTRIBUTES, filterAttributes);
        app.registryPut(ProductListConstant.FILTER_ATTRIBUTE_ALIAS_INDEX, attributesAliasIndex);

        Map<String, Set<Object>> paramFilterParts = productListHelper.getFilterPartsFromParameters(
            app.servletRequest().getParameterMap(), navFilterRule, filterAttributes, attributesAliasIndex);

        if (paramFilterParts != null && paramFilterParts.size() > 0) {
            app.registryPut(ProductListConstant.PARAM_FILTER_PARTS, paramFilterParts);

            requestFilters.putAll(paramFilterParts);
        }
    }

    protected List<Product> orderByProductIds(List<Product> products, Id[] productIds) {
        List<Product> productList = new LinkedList<Product>();
        for (Id id : productIds) {
            for (Product product : products) {
                if (id.equals(product.getId())) {
                    productList.add(product);
                }
            }
        }

        return productList;
    }
}
