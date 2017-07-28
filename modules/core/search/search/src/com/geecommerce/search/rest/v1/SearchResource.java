package com.geecommerce.search.rest.v1;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.search.helper.SearchHelper;
import com.geecommerce.search.model.SearchQuery;
import com.geecommerce.search.model.SearchResult;
import com.geecommerce.search.service.SearchService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/search")
public class SearchResource extends AbstractResource {
    private final RestService service;
    private final SearchService searchService;
    private final SearchHelper searchHelper;
    private final ProductService productService;
    private final ElasticsearchHelper elasticsearchHelper;

    @Inject
    public SearchResource(RestService service, SearchService searchService, SearchHelper searchHelper,
        ProductService productService, ElasticsearchHelper elasticsearchHelper) {
        this.service = service;
        this.searchService = searchService;
        this.searchHelper = searchHelper;
        this.productService = productService;
        this.elasticsearchHelper = elasticsearchHelper;
    }

    @GET
    @Path("products/{query}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response findProducts(@PathParam("query") String query, @FilterParam Filter filter) {
        SearchResult searchResult = searchService
            .query(new SearchQuery(query, null, filter.getOffset() == null ? 0 : filter.getOffset().intValue(),
                filter.getLimit() == null ? 25 : filter.getLimit(), true));

        List<Product> products = null;

        if (searchResult != null && searchResult.getDocumentIds() != null && searchResult.getDocumentIds().size() > 0) {
            QueryOptions queryOptions = queryOptions(filter);

            products = productService.getProducts(
                Arrays.asList(elasticsearchHelper.toIds(searchResult.getDocumentIds().toArray())), queryOptions);
        }

        return ok(products);
    }
}
