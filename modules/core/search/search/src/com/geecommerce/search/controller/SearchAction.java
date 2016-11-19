package com.geecommerce.search.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.Str;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.system.helper.TargetSupportHelper;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.web.BasePagingActionBean;
import com.geecommerce.search.form.FilterForm;
import com.geecommerce.search.helper.SearchHelper;
import com.geecommerce.search.model.AutocompleteMapping;
import com.geecommerce.search.model.SearchQuery;
import com.geecommerce.search.model.SearchResult;
import com.geecommerce.search.service.SearchRewriteService;
import com.geecommerce.search.service.SearchService;
import com.google.inject.Inject;

import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding(value = "/catalog/search/{$event}/{k}")
public class SearchAction extends BasePagingActionBean {
    private String k = null;
    private String f = null;

    private SearchResult searchResult = null;
    private List<Product> products = null;
    private Map<String, Object> activeFilters = null;

    private final SearchService searchService;
    private final SearchHelper searchHelper;
    private final ProductService productService;
    private final SearchRewriteService searchRewriteService;
    private final ElasticsearchHelper elasticsearchHelper;

    private FilterForm filterForm = null;

    @Inject
    public SearchAction(SearchService searchService, SearchHelper searchHelper, ProductService productService, SearchRewriteService searchRewriteService, ElasticsearchHelper elasticsearchHelper) {
        this.searchService = searchService;
        this.searchHelper = searchHelper;
        this.productService = productService;
        this.searchRewriteService = searchRewriteService;
        this.elasticsearchHelper = elasticsearchHelper;
    }

    @HandlesEvent("query")
    public Resolution query() {
        if (f != null) {
            activeFilters = searchHelper.toFilterMap(f);
        }

        if (k != null) {
            String newUri = searchRewriteService.findUrl(k);
            if (newUri == null) {
                searchResult = searchService.query(new SearchQuery(k, activeFilters, getOffset(), getNumResultsPerPage(), getFilterForm().getSort(), getFilterForm().getPriceFrom(),
                    getFilterForm().getPriceTo(), getFilterForm().isShowEvent(),
                    getFilterForm().isShowSale()));

                if (searchResult != null && searchResult.getDocumentIds() != null && searchResult.getDocumentIds().size() > 0) {
                    Id[] productIds = elasticsearchHelper.toIds(searchResult.getDocumentIds().toArray());
                    products = productService.getProducts(productIds);
                    products = orderByProductIds(products, productIds);

                    // If we have an exact match after an articleNumber search,
                    // redirect directly to product.
                    if (products != null && products.size() == 1) {
                        Product p = products.get(0);
                        String articleNumber = p.getArticleNumber();

                        if (Str.trimEquals(k, articleNumber)) {
                            return redirect(app.getHelper(TargetSupportHelper.class).findURI(p));
                        }
                    }
                }
            } else {
                return redirect(newUri);
            }
        }

        return view("catalog/search/result");
    }

    public Resolution autocomplete() {
        String searchPhrase = getRequest().getParameter("term");

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
        return json(Json.toJson(resultList));
    }

    public void setK(String k) {
        this.k = k;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getSearchQuery() {
        return k;
    }

    public SearchResult getSearchResult() {
        return searchResult;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Map<String, Object> getActiveFilters() {
        return activeFilters;
    }

    public String getActiveFiltersAsJson() {
        return activeFilters != null && activeFilters.size() > 0 ? Json.toJson(activeFilters) : null;
    }

    // @Override
    public long getTotalNumResults() {
        return searchResult == null ? 0 : searchResult.getTotalNumResults();
    }

    @Override
    public int getDefaultNumResultsPerPage() {
        return 40;
    }

    @Override
    public int[] getNumResultsPerPageList() {
        return new int[] { 40, 100, 200 };
    }

    @Override
    public String getPagingURI() {
        return new StringBuilder("/catalog/search/").append(getContext().getEventName()).append("/").append(k).append("/").toString();
    }

    public FilterForm getFilterForm() {
        if (filterForm == null) {
            filterForm = new FilterForm();
        }
        return filterForm;
    }

    public void setFilterForm(FilterForm filterForm) {
        this.filterForm = filterForm;
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
