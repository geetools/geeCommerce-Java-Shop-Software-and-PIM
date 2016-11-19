package com.geecommerce.catalog.product.rest.web.v1;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.catalog.product.dao.ProductDao;
import com.geecommerce.catalog.product.helper.CatalogMediaHelper;
import com.geecommerce.catalog.product.helper.ProductHelper;
import com.geecommerce.catalog.product.helper.ProductUrlHelper;
import com.geecommerce.catalog.product.model.CatalogMediaAsset;
import com.geecommerce.catalog.product.model.CatalogMediaType;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.media.MimeType;
import com.geecommerce.core.rest.AbstractWebResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.system.helper.UrlRewriteHelper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.Id;
import com.geecommerce.inventory.model.InventoryItem;
import com.geecommerce.inventory.service.StockService;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.service.PriceService;
import com.google.inject.Inject;

@Path("/v1/web/products")
public class ProductWebResource extends AbstractWebResource {
    private final RestService service;
    private final CatalogMediaHelper catalogMediaHelper;
    private final ProductHelper productHelper;
    private final ProductUrlHelper productUrlHelper;
    private final UrlRewrites urlRewrites;
    private final UrlRewriteHelper urlRewriteHelper;
    private final ProductDao productDao;

    @Inject
    public ProductWebResource(RestService service, CatalogMediaHelper catalogMediaHelper, ProductHelper productHelper, ProductUrlHelper productUrlHelper, UrlRewrites urlRewrites,
        ProductDao productDao, UrlRewriteHelper urlRewriteHelper) {
        this.service = service;
        this.catalogMediaHelper = catalogMediaHelper;
        this.productHelper = productHelper;
        this.productUrlHelper = productUrlHelper;
        this.urlRewrites = urlRewrites;
        this.productDao = productDao;
        this.urlRewriteHelper = urlRewriteHelper;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getProducts(@FilterParam Filter filter) {
        QueryOptions queryOptions = queryOptions(filter);

        if (storeHeaderExists())
            queryOptions = QueryOptions.builder(queryOptions).limitAttributeToStore("status_description", getStoreFromHeader()).limitAttributeToStore("status_article", getStoreFromHeader()).build();

        return ok(service.get(Product.class, filter.getParams(), queryOptions));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Product getProduct(@PathParam("id") Id id) {
        return checked(service.get(Product.class, id));
    }

    @GET
    @Path("{id}/variants")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getVariants(@PathParam("id") Id id) {
        Product p = checked(service.get(Product.class, id));
        Map<String, Object> variantsMap = productHelper.toVariantsMap(p);

        return ok(variantsMap);
    }

    @GET
    @Path("{id}/upsells")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getUpsells(@PathParam("id") Id id) {
        Product p = checked(service.get(Product.class, id));
        List<Product> upsells = p.getUpsells();

        return ok(upsells);
    }

    @GET
    @Path("{id}/programme-products")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getProgrammeProducts(@PathParam("id") Id id) {
        Product p = checked(service.get(Product.class, id));
        List<Product> programmeProducts = p.getProgrammeProducts();

        return ok(programmeProducts);
    }

    @GET
    @Path("{id}/prices")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getPrices(@PathParam("id") Id productId) {
        Product p = checked(service.get(Product.class, productId));

        PriceService priceService = app.getService(PriceService.class);

        List<Price> prices = null;

        Set<Id> productIds = new HashSet<>();
        productIds.add(p.getId());

        if (p.isVariantMaster()) {
            productIds.addAll(p.getVariantProductIds());
        }

        prices = priceService.getPrices(productIds.toArray(new Id[productIds.size()]));

        return ok(prices);
    }

    @GET
    @Path("{id}/stock")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getStockInventoryItems(@PathParam("id") Id productId) {
        Product p = checked(service.get(Product.class, productId));

        StockService stockService = app.getService(StockService.class);

        List<InventoryItem> inventoryItems = null;

        Set<Id> productIds = new HashSet<>();
        productIds.add(p.getId());

        if (p.isVariantMaster()) {
            productIds.addAll(p.getVariantProductIds());
        }

        inventoryItems = stockService.getInventoryItems(productIds.toArray(new Id[productIds.size()]));

        return ok(inventoryItems);
    }

    @GET
    @Path("{id}/media-assets")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getMediaAssets(@PathParam("id") Id productId, @FilterParam Filter filter) {
        Product p = checked(service.get(Product.class, productId));

        List<CatalogMediaAsset> mediaAssets = null;

        Set<Id> productIds = new HashSet<>();
        productIds.add(p.getId());

        if (p.isVariantMaster()) {
            productIds.addAll(p.getVariantProductIds());
        }

        List<String> mimeTypes = filter.getStrings("mimeType");

        if (mimeTypes != null && mimeTypes.size() > 0) {
            Set<String> expandedMimeTypes = MimeType.expandWildcards(mimeTypes);

            if (expandedMimeTypes.size() > 0) {
                // Override the request mimeTypes with the expanded ones.
                filter.append("mimeType", expandedMimeTypes);
            }
        }

        filter.append("productId", productIds);

        mediaAssets = service.get(CatalogMediaAsset.class, filter.getParams(), QueryOptions.builder().sortBy(CatalogMediaAsset.Col.POSITION).build());

        return ok(mediaAssets);
    }

    @GET
    @Path("media-types")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getMediaTypes() {
        return ok(service.get(CatalogMediaType.class));
    }

    @GET
    @Path("{id}/url")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getRewriteUrl(@PathParam("id") Id productId) {
        UrlRewrite urlRewrite = urlRewrites.forProduct(productId);
        if (urlRewrite == null) {
            urlRewrite = app.getModel(UrlRewrite.class);
        }
        return ok(checked(urlRewrite));
    }

}
