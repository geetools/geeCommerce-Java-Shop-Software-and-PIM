package com.geecommerce.catalog.product.rest.web.v1;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.catalog.product.dao.ProductDao;
import com.geecommerce.catalog.product.enums.BundleGroupType;
import com.geecommerce.catalog.product.helper.CatalogMediaHelper;
import com.geecommerce.catalog.product.helper.ProductHelper;
import com.geecommerce.catalog.product.helper.ProductUrlHelper;
import com.geecommerce.catalog.product.model.*;
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
import com.geecommerce.price.helper.PriceHelper;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.pojo.PricingContext;
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
    private final PriceService priceService;
    private final PriceHelper priceHelper;

    @Inject
    public ProductWebResource(RestService service, CatalogMediaHelper catalogMediaHelper, ProductHelper productHelper,
                              ProductUrlHelper productUrlHelper, UrlRewrites urlRewrites, ProductDao productDao,
                              UrlRewriteHelper urlRewriteHelper, PriceService priceService, PriceHelper priceHelper) {
        this.service = service;
        this.catalogMediaHelper = catalogMediaHelper;
        this.productHelper = productHelper;
        this.productUrlHelper = productUrlHelper;
        this.urlRewrites = urlRewrites;
        this.productDao = productDao;
        this.urlRewriteHelper = urlRewriteHelper;
        this.priceService = priceService;
        this.priceHelper = priceHelper;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getProducts(@FilterParam Filter filter) {
        QueryOptions queryOptions = queryOptions(filter);

        if (storeHeaderExists())
            queryOptions = QueryOptions.builder(queryOptions)
                .limitAttributeToStore("status_description", getStoreFromHeader())
                .limitAttributeToStore("status_article", getStoreFromHeader()).build();

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

        PriceService priceService = app.service(PriceService.class);

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

        StockService stockService = app.service(StockService.class);

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

        mediaAssets = service.get(CatalogMediaAsset.class, filter.getParams(),
            QueryOptions.builder().sortBy(CatalogMediaAsset.Col.POSITION).build());

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
            urlRewrite = app.model(UrlRewrite.class);
        }
        return ok(checked(urlRewrite));
    }

    @POST
    @Path("{id}/bundle-prices")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getBundlePrices(@PathParam("id") Id id, Map<String, List<String>> bundle) {
        Product bundleProduct = checked(service.get(Product.class, id));

        Map<Id, Integer> originalMap = collectItems(bundleProduct, bundle, null);
        Double basePrice = calculateBundlePrice(originalMap);

        Map<String, Map<Id, Double>> prices = new HashMap<>();

        prices.put("cart", new HashMap<>());
        prices.get("cart").put(Id.parseId("0"), basePrice);

        for(String groupIdStr : bundle.keySet()){

            Id groupId = Id.parseId(groupIdStr);
            Optional<BundleGroupItem> group = bundleProduct.getBundleGroups().stream().filter(g-> g.getId().equals(groupId)).findFirst();

            if(group.isPresent() && !group.get().getType().equals(BundleGroupType.LIST)) {
                Map<Id, Integer> bundleMap = collectItems(bundleProduct, bundle, groupId);

                prices.put(groupId.toString(), new HashMap<>());

                if(group.get().isMultiselect()) {
                    for(Product product: group.get().getProducts()){ //TODO: get valid products
                        Map<Id, Integer> bundleMapCurrent = new HashMap<Id, Integer>(bundleMap);
                        Map<Id, Integer> bundleMapRest = collectItems(bundleProduct, bundle, groupId);

                        if(bundleMap.get(product.getId()) == null) {
                            bundleMapCurrent.putAll(bundleMapRest);

                            BundleProductItem item = group.get().getItemByProduct(product.getId());
                            bundleMapCurrent.put(product.getId(), item.getQuantity());
                        } else {
                            bundleMapRest.remove(product.getId());
                            bundleMapCurrent.putAll(bundleMapRest);
                        }

                        Double currentPrice  = calculateBundlePrice(bundleMapCurrent);
                        Double difference = currentPrice - basePrice;

                        prices.get(groupId.toString()).put(product.getId(), difference);
                    }

                } else {
                    for(Product product: group.get().getProducts()){ //TODO: get valid products
                        Map<Id, Integer> bundleMapCurrent = new HashMap<Id, Integer>(bundleMap);

                        BundleProductItem item = group.get().getItemByProduct(product.getId());
                        bundleMapCurrent.put(product.getId(), item.getQuantity());

                        Double currentPrice  = calculateBundlePrice(bundleMapCurrent);
                        Double difference = currentPrice - basePrice;

                        prices.get(groupId.toString()).put(product.getId(), difference);
                    }

                    if(group.get().isOptional()){
                        Double currentPrice  = calculateBundlePrice(bundleMap);
                        Double difference = currentPrice - basePrice;

                        prices.get(groupId.toString()).put(null, difference);
                    }
                }

            }
        }



        return ok(prices);
    }


    private Map<Id, Integer> collectItems(Product bundleProduct, Map<String, List<String>> bundle, Id except){

        Map<Id, Integer> bundleMap = new HashMap<>();

        for(String groupIdStr : bundle.keySet()){

            Id groupId = Id.parseId(groupIdStr);

            Optional<BundleGroupItem> group = bundleProduct.getBundleGroups().stream().filter(g-> g.getId().equals(groupId)).findFirst();

            if(group.isPresent() && !group.get().getId().equals(except)) {
                List<String> products = bundle.get(groupIdStr);

                if(products!= null) {
                    for (String productIdStr : products) {
                        Id productId = Id.parseId(productIdStr);
                        BundleProductItem item = group.get().getItemByProduct(productId);

                        bundleMap.put(productId, item.getQuantity());
                    }
                }
            }
        }

        return bundleMap;
    }

    private Map<Id, Integer> collectItemsForGroup(Product bundleProduct, Map<String, List<String>> bundle, Id groupId){

        Map<Id, Integer> bundleMap = new HashMap<>();

        for(String groupIdStr : bundle.keySet()){

            Id id = Id.parseId(groupIdStr);

            Optional<BundleGroupItem> group = bundleProduct.getBundleGroups().stream().filter(g-> g.getId().equals(id)).findFirst();

            if(group.isPresent() && group.get().getId().equals(groupId)) {
                List<String> products = bundle.get(groupIdStr);
                if(products!= null) {
                    for (String productIdStr : products) {
                        Id productId = Id.parseId(productIdStr);
                        BundleProductItem item = group.get().getItemByProduct(productId);

                        bundleMap.put(productId, item.getQuantity());
                    }
                }
            }
        }

        return bundleMap;
    }

    private Double calculateBundlePrice( Map<Id, Integer> productQuantityMap){
        Double price = 0.0;

        //List<Id> withProducts = new ArrayList<>(productQuantityMap.keySet());
        Id[] withProducts = productQuantityMap.keySet().toArray(new Id[productQuantityMap.keySet().size()]);
        PricingContext pricingContext = priceHelper.getPricingContext(true);
        for (Id productId: productQuantityMap.keySet()){
            pricingContext.setLinkedProductIds(productId, withProducts);
        }

        for (Id productId: productQuantityMap.keySet()){

            Product p = checked(service.get(Product.class, productId));

            if(p != null && p.getPrice() != null && p.hasValidPrice()){


                price += p.getPrice().getFinalPrice(pricingContext) * productQuantityMap.get(productId);
            }

        }

        return price;
    }

}
