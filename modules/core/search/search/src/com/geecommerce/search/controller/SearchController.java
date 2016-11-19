package com.geecommerce.search.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.FilterContext;
import com.geecommerce.catalog.product.PagingContext;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.Str;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.system.helper.TargetSupportHelper;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.BaseController;
import com.geecommerce.search.form.FilterForm;
import com.geecommerce.search.helper.SearchHelper;
import com.geecommerce.search.model.AutocompleteMapping;
import com.geecommerce.search.model.SearchQuery;
import com.geecommerce.search.model.SearchResult;
import com.geecommerce.search.service.SearchRewriteService;
import com.geecommerce.search.service.SearchService;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.Param;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Controller
@Request("/catalog/search")
// @UrlBinding(value = "/catalog/search/{$event}/{k}")
public class SearchController extends BaseController {
    /*
     * private String k = null;
     * private String f = null;
     */

    /*
     * private SearchResult searchResult = null;
     * private List<Product> products = null;
     * private Map<String, Object> activeFilters = null;
     */
    private final SearchService searchService;
    private final SearchHelper searchHelper;
    private final ProductService productService;
    private final SearchRewriteService searchRewriteService;
    private final ElasticsearchHelper elasticsearchHelper;

    private FilterForm filterForm = null;

    @Inject
    public SearchController(SearchService searchService, SearchHelper searchHelper, ProductService productService, SearchRewriteService searchRewriteService, ElasticsearchHelper elasticsearchHelper) {
        this.searchService = searchService;
        this.searchHelper = searchHelper;
        this.productService = productService;
        this.searchRewriteService = searchRewriteService;
        this.elasticsearchHelper = elasticsearchHelper;
    }

    // @HandlesEvent("query")
    @Request("/query")
    public Result query(@Param("q") String query, @Param("f") String filter, FilterForm filterForm, FilterContext filterCtx, PagingContext pagingCtx) {
        Map<String, Object> activeFilters = null;
        SearchResult searchResult = null;
        List<Product> products = null;

        if (filterForm == null) {
            filterForm = new FilterForm();
        }

        if (filter != null) {
            activeFilters = searchHelper.toFilterMap(filter);
        }

        if (query != null) {
            String newUri = searchRewriteService.findUrl(query);
            if (newUri == null) {
                searchResult = searchService.query(new SearchQuery(query, activeFilters, pagingCtx.getOffset(), pagingCtx.getNumResultsPerPage(),
                    filterForm.getSort(), filterForm.getPriceFrom(), filterForm.getPriceTo(), filterForm.isShowEvent(), filterForm.isShowSale()));

                if (searchResult != null && searchResult.getDocumentIds() != null && searchResult.getDocumentIds().size() > 0) {
                    Id[] productIds = elasticsearchHelper.toIds(searchResult.getDocumentIds().toArray());
                    products = productService.getProducts(productIds);
                    products = orderByProductIds(products, productIds);

                    // If we have an exact match after an articleNumber search,
                    // redirect directly to product.
                    if (products != null && products.size() == 1) {
                        Product p = products.get(0);
                        String articleNumber = p.getArticleNumber();

                        if (Str.trimEquals(query, articleNumber)) {
                            return Results.redirect(app.getHelper(TargetSupportHelper.class).findURI(p));
                            /*
                             * .bind("products", products)
                             * .bind("form", filterForm)
                             * .bind("filterCtx", filterCtx)
                             * .bind("pagingCtx", pagingCtx);
                             */
                        }
                    }
                }
            } else {
                return Results.redirect(newUri);/*
                                                 * .bind("products", products)
                                                 * .bind("form", filterForm)
                                                 * .bind("filterCtx", filterCtx)
                                                 * .bind("pagingCtx",
                                                 * pagingCtx);
                                                 */
            }
        }

        return Results.view("catalog/search/result")
            .bind("products", products)
            .bind("form", filterForm)
            .bind("filterCtx", filterCtx)
            .bind("pagingCtx", pagingCtx);
    }

    @Request("/autocomplete")
    public Result autocomplete() {
        String searchPhrase = app.getServletRequest().getParameter("term");

        SearchResult searchResult = null;
        List<Product> products = null;

        List<HashMap<String, String>> resultList = new ArrayList<>();
        if (searchPhrase != null && !searchPhrase.isEmpty()) {
            searchPhrase = searchPhrase.toLowerCase();
            int autocompleteMappingSize = 0;
            List<AutocompleteMapping> resultMappings = searchService.getAutocompleteMappingsByKeyword(searchPhrase);
            if (resultMappings != null && resultMappings.size() > 0) {
                autocompleteMappingSize = resultMappings.size();
                for (AutocompleteMapping resultMapping : resultMappings) {
                    HashMap<String, String> result = new HashMap<>();
                    // result.put("id", resultMapping.getId().str());
                    // result.put("value", resultMapping.getLabel());
                    result.put("label", resultMapping.getLabel());
                    result.put("uri", resultMapping.getDisplayURI());
                    resultList.add(result);
                }

            }
            searchResult = searchService.autocomplete(new SearchQuery(searchPhrase + "*", 0, 10 - autocompleteMappingSize));
            if (searchResult != null && searchResult.getDocumentIds() != null && searchResult.getDocumentIds().size() > 0) {
                products = productService.getProducts(false, elasticsearchHelper.toIds(searchResult.getDocumentIds().toArray()));
                for (Product product : products) {
                    HashMap<String, String> resultProduct = new HashMap<>();
                    resultProduct.put("id", product.getId().str());
                    resultProduct.put("value", product.attr("name2").getStr() + " " + product.attr("name").getStr());
                    resultProduct.put("label", product.attr("name2").getStr() + " " + product.attr("name").getStr());
                    resultProduct.put("uri", app.getHelper(TargetSupportHelper.class).findURI(product));
                    resultList.add(resultProduct);
                }
            }
        }
        return json(resultList);
    }

    private List<Product> orderByProductIds(List<Product> products, Id[] productIds) {
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
