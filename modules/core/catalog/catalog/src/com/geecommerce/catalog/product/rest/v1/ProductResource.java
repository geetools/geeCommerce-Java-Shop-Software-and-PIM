package com.geecommerce.catalog.product.rest.v1;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.elasticsearch.index.query.FilterBuilder;

import com.geecommerce.catalog.product.dao.ProductDao;
import com.geecommerce.catalog.product.helper.CatalogMediaHelper;
import com.geecommerce.catalog.product.helper.ProductHelper;
import com.geecommerce.catalog.product.helper.ProductUrlHelper;
import com.geecommerce.catalog.product.model.BundleProductItem;
import com.geecommerce.catalog.product.model.CatalogMediaAsset;
import com.geecommerce.catalog.product.model.CatalogMediaType;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.batch.dataimport.helper.ImportHelper;
import com.geecommerce.core.batch.dataimport.repository.ImportTokens;
import com.geecommerce.core.batch.service.ImportExportService;
import com.geecommerce.core.elasticsearch.api.search.SearchResult;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.elasticsearch.search.SearchParams;
import com.geecommerce.core.elasticsearch.service.ElasticsearchService;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.media.MimeType;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.jersey.inject.ModelParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.service.CopySupport;
import com.geecommerce.core.service.QueryMetadata;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.system.helper.UrlRewriteHelper;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.query.helper.QueryHelper;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.util.Strings;
import com.geecommerce.inventory.model.DefaultInventoryItem;
import com.geecommerce.inventory.model.InventoryItem;
import com.geecommerce.inventory.service.StockService;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.service.PriceService;
import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/v1/products")
public class ProductResource extends AbstractResource {
    protected final RestService service;
    protected final AttributeService attributeService;
    protected final CatalogMediaHelper catalogMediaHelper;
    protected final ProductHelper productHelper;
    protected final UrlRewrites urlRewrites;
    protected final UrlRewriteHelper urlRewriteHelper;
    protected final ImportExportService importExportService;
    protected final ImportHelper importHelper;
    protected final ImportTokens importTokens;
    private final QueryHelper queryHelper;
    private final ElasticsearchService elasticsearchService;
    private final ElasticsearchHelper elasticsearchHelper;
    private final Products productRepository;

    @Inject
    public ProductResource(RestService service, CatalogMediaHelper catalogMediaHelper, ProductHelper productHelper,
                           ProductUrlHelper productUrlHelper, UrlRewrites urlRewrites, ProductDao productDao,
                           UrlRewriteHelper urlRewriteHelper, QueryHelper queryHelper, ElasticsearchService elasticsearchService, ElasticsearchHelper elasticsearchHelper, Products productRepository, AttributeService attributeService, CatalogMediaHelper catalogMediaHelper1, ProductHelper productHelper1, UrlRewrites urlRewrites1, UrlRewriteHelper urlRewriteHelper1, ImportExportService importExportService, ImportHelper importHelper, ImportTokens importTokens, QueryHelper queryHelper1, ElasticsearchService elasticsearchService1, ElasticsearchHelper elasticsearchHelper1, Products productRepository1) {
        this.service = service;

        this.attributeService = attributeService;
        this.catalogMediaHelper = catalogMediaHelper1;
        this.productHelper = productHelper1;
        this.urlRewrites = urlRewrites1;
        this.urlRewriteHelper = urlRewriteHelper1;
        this.importExportService = importExportService;
        this.importHelper = importHelper;
        this.importTokens = importTokens;
        this.queryHelper = queryHelper1;
        this.elasticsearchService = elasticsearchService1;
        this.elasticsearchHelper = elasticsearchHelper1;
        this.productRepository = productRepository1;
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
    @Path("query")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getProductsByQuery(@FilterParam Filter filter) {

        QueryOptions queryOptions = queryOptions(filter);

        if (storeHeaderExists())
            queryOptions = QueryOptions.builder(queryOptions)
                    .limitAttributeToStore("status_description", getStoreFromHeader())
                    .limitAttributeToStore("status_article", getStoreFromHeader()).build();


        FilterBuilder filterBuilder = queryHelper.buildQuery(filter.getQuery());
        List<FilterBuilder> builders = new ArrayList<>();
        builders.add(filterBuilder);
        SearchParams searchParams = new SearchParams();
        searchParams.setLimit(filter.getLimit());
        searchParams.setOffset(filter.getOffset().intValue());
        SearchResult productsResult = elasticsearchService.findItems(Product.class, builders, searchParams);

        Id[] ids = elasticsearchHelper.toIds(productsResult.getDocumentIds().toArray());

        List<Product> products = new ArrayList<>();
        if(ids != null && ids.length > 0) {
            products = productRepository.findByIds(Product.class, ids, queryOptions);
        }

        app.setQueryMetadata(QueryMetadata.builder().count(productsResult.getTotalNumResults()).build());
        return ok(products);
    }


    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Product getProduct(@PathParam("id") Id id) {

        try {
            Product p = Json.fromJson(
                "{\"type\":\"PRODUCT\",\"attributes\":[{\"attributeId\":\"11306950695210100\",\"optionIds\":[\"11365129740710100\"]}]}",
                app.modelType(Product.class));
            System.out.println("DESERIALIZED PRODUCT: " + p);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return checked(service.get(Product.class, id));
    }

    @GET
    @Path("{id}/snapshots")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getProductSnapshots(@PathParam("id") Id id, @FilterParam Filter filter) {
        QueryOptions queryOptions = queryOptions(filter);

        return ok(service.getSnapshots(Product.class, id, queryOptions));
    }

    @GET
    @Path("{id}/snapshots/{versions}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getProductSnapshots(@PathParam("id") Id id, @PathParam("versions") List<Integer> versions) {
        return ok(service.getSnapshots(Product.class, id, versions.toArray(new Integer[versions.size()])));
    }

    @GET
    @Path("{id}/variants")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getVariants(@PathParam("id") Id id) {
        Product p = checked(service.get(Product.class, id));
        List<Product> variants = p.getVariants();

        return ok(variants);
    }

    @GET
    @Path("{id}/upsells")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getUpsells(@PathParam("id") Id id) {
        Product p = checked(service.get(Product.class, id));
        List<Product> upsells = p.getUpsells();

        return ok(upsells);
    }

    @PUT
    @Path("{productId}/upsells/{upsellProductId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void addUpsell(@PathParam("productId") Id id, @PathParam("upsellProductId") Id upsellProductId) {
        if (id != null && upsellProductId != null) {
            // Get main and upsell product.,
            Product product = checked(service.get(Product.class, id));
            Product upsellProduct = checked(service.get(Product.class, upsellProductId));

            // Add child product to main product.
            product.addUpsellProduct(upsellProduct);

            // Save main product with the new upsell-productId.
            service.update(product);
        } else {
            throwBadRequest(
                "productId and upsellProductId cannot be null in requestURI. Expecting: products/{productId}/upsells/{upsellProductId}");
        }
    }

    @DELETE
    @Path("{productId}/upsells/{upsellProductId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeUpsell(@PathParam("productId") Id id, @PathParam("upsellProductId") Id upsellProductId) {
        if (id != null && upsellProductId != null) {
            // Get main and child product.
            Product product = checked(service.get(Product.class, id));
            Product upsellProduct = checked(service.get(Product.class, upsellProductId));

            // Remove child from main product.
            product.removeUpsellProduct(upsellProduct);

            // Save main product with the removed variant-productId.
            service.update(product);
        } else {
            throwBadRequest(
                "productId and upsellProductId cannot be null in requestURI. Expecting: products/{productId}/upsells/{upsellProductId}");
        }
    }

    @GET
    @Path("{id}/cross-sells")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getCrossSells(@PathParam("id") Id id) {
        Product p = checked(service.get(Product.class, id));
        List<Product> crossSells = p.getCrossSells();

        return ok(crossSells);
    }

    @PUT
    @Path("{productId}/cross-sells/{crossSellProductId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void addCrossSell(@PathParam("productId") Id id, @PathParam("crossSellProductId") Id crossSellProductId) {
        if (id != null && crossSellProductId != null) {
            // Get main and cross-sell product.,
            Product product = checked(service.get(Product.class, id));
            Product crossSellProduct = checked(service.get(Product.class, crossSellProductId));

            // Add child product to main product.
            product.addCrossSellProduct(crossSellProduct);

            // Save main product with the new cross-sell-productId.
            service.update(product);
        } else {
            throwBadRequest(
                "productId and crossSellProductId cannot be null in requestURI. Expecting: products/{productId}/cross-sells/{crossSellProductId}");
        }
    }

    @DELETE
    @Path("{productId}/cross-sells/{crossSellProductId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeCrossSell(@PathParam("productId") Id id, @PathParam("crossSellProductId") Id crossSellProductId) {
        if (id != null && crossSellProductId != null) {
            // Get main and child product.
            Product product = checked(service.get(Product.class, id));
            Product crossSellProduct = checked(service.get(Product.class, crossSellProductId));

            // Remove child from main product.
            product.removeCrossSellProduct(crossSellProduct);

            // Save main product with the removed variant-productId.
            service.update(product);
        } else {
            throwBadRequest(
                "productId and crossSellProductId cannot be null in requestURI. Expecting: products/{productId}/cross-sells/{crossSellProductId}");
        }
    }

    @GET
    @Path("{id}/programme-products")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getProgrammeProducts(@PathParam("id") Id id) {
        Product p = checked(service.get(Product.class, id));
        List<Product> programmeProducts = p.getProgrammeProducts();

        return ok(programmeProducts);
    }

    @PUT
    @Path("{programmeProductId}/programme-products/{childProductId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void addProductToProgramme(@PathParam("programmeProductId") Id id,
        @PathParam("childProductId") Id childProductId) {
        if (id != null && childProductId != null) {
            // Get main and child product.,
            Product programmeProduct = checked(service.get(Product.class, id));
            Product childProduct = checked(service.get(Product.class, childProductId));

            // Add child product to main product.
            programmeProduct.addProgrammeProduct(childProduct);

            // Save main product with the new child-productId.
            service.update(programmeProduct);
        } else {
            throwBadRequest(
                "programmeProductId and childProductId cannot be null in requestURI. Expecting: products/{programmeProductId}/programme-products/{childProductId}");
        }
    }

    @DELETE
    @Path("{programmeProductId}/programme-products/{childProductId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeProductFromProgramme(@PathParam("programmeProductId") Id programmeProductId,
        @PathParam("childProductId") Id childProductId) {
        if (programmeProductId != null && childProductId != null) {
            // Get main and child product.
            Product programmeProduct = checked(service.get(Product.class, programmeProductId));
            Product childProduct = checked(service.get(Product.class, childProductId));

            // Remove child from main product.
            programmeProduct.removeProgrammeProduct(childProduct);

            // Save main product with the removed variant-productId.
            service.update(programmeProduct);
        } else {
            throwBadRequest(
                "programmeProductId and childProductId cannot be null in requestURI. Expecting: products/{programmeProductId}/programme-products/{childProductId}");
        }
    }

    @GET
    @Path("{id}/bundle-products")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getBundleProducts(@PathParam("id") Id id) {
        Product p = checked(service.get(Product.class, id));
        List<BundleProductItem> bundleProducts = p.getBundleProductItems();

        return ok(bundleProducts);
    }

    @PUT
    @Path("{bundleProductId}/bundle-products/{childProductId}/{qty}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void addProductToBundle(@PathParam("bundleProductId") Id id, @PathParam("childProductId") Id childProductId, @PathParam("qty") int quantity) {
        if (id != null && childProductId != null) {
            // Get main and child product.,
            Product bundleProduct = checked(service.get(Product.class, id));
            Product childProduct = checked(service.get(Product.class, childProductId));

            // Add child product to main product.
            bundleProduct.addBundleProduct(childProduct, quantity);

            // Save main product with the new child-productId.
            service.update(bundleProduct);
        } else {
            throwBadRequest(
                    "bundleProductId and childProductId cannot be null in requestURI. Expecting: products/{bundleProductId}/bundle-products/{childProductId}/{qty}");
        }
    }

    @DELETE
    @Path("{bundleProductId}/bundle-products/{childProductId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeProductFromBundle(@PathParam("bundleProductId") Id bundleProductId, @PathParam("childProductId") Id childProductId) {
        if (bundleProductId != null && childProductId != null) {
            // Get main and child product.
            Product bundleProduct = checked(service.get(Product.class, bundleProductId));
            Product childProduct = checked(service.get(Product.class, childProductId));

            // Remove child from main product.
            bundleProduct.removeBundleProduct(childProduct);

            // Save main product with the removed variant-productId.
            service.update(bundleProduct);
        } else {
            throwBadRequest(
                    "bundleProductId and childProductId cannot be null in requestURI. Expecting: products/{bundleProductId}/bundle-products/{childProductId}");
        }
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

    @POST
    @Path("{id}/prices")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createPrice(@PathParam("id") Id productId, @ModelParam Price price) {
        Product p = checked(service.get(Product.class, productId));

        if (p != null) {
            PriceService priceService = app.service(PriceService.class);

            if (price != null && productId.equals(price.getProductId())) {
                Price newPrice = priceService.createPrice(price);

                if (newPrice != null && newPrice.getId() != null) {
                    p = service.get(Product.class, productId, QueryOptions.builder().refresh(true).build());
                    setStatuses(p);
                    service.update(p);

                    return created(newPrice.getId());
                }
            }
        }

        return response(Status.INTERNAL_SERVER_ERROR, "Failed to create price for product " + productId);
    }

    @PUT
    @Path("{id}/prices/{priceId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updatePrice(@PathParam("id") Id productId, @PathParam("priceId") Id priceId, @ModelParam Price price) {
        Product p = checked(service.get(Product.class, productId));

        if (p != null) {
            PriceService priceService = app.service(PriceService.class);

            if (price != null && productId.equals(price.getProductId())) {
                Price dbPrice = priceService.getPrice(priceId);
                dbPrice.setPrice(price.getPrice());

                priceService.updatePrice(dbPrice);

                p = service.get(Product.class, productId, QueryOptions.builder().refresh(true).build());
                setStatuses(p);
                service.update(p);
            }
        }
    }

    @DELETE
    @Path("{id}/prices/{priceId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void deletePrice(@PathParam("id") Id productId, @PathParam("priceId") Id priceId) {
        Product p = checked(service.get(Product.class, productId));

        if (p != null) {
            PriceService priceService = app.service(PriceService.class);
            Price price = priceService.getPrice(priceId);

            if (price != null && productId.equals(price.getProductId())) {
                priceService.removePrice(price);

                p = service.get(Product.class, productId, QueryOptions.builder().refresh(true).build());
                setStatuses(p);
                service.update(p);
            }
        }
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

    @PUT
    @Path("{id}/stock")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @SuppressWarnings({ "rawtypes", "unchecked" })
    // TODO replace DefaultInventoryItem with interface when there is enough
    // time to write custom jersey/genson code.
    public void updateStockInventoryItems(@PathParam("id") Id productId, List<DefaultInventoryItem> inventoryItems) {
        StockService stockService = app.service(StockService.class);

        if (inventoryItems != null && inventoryItems.size() > 0) {
            stockService.updateInventoryItems((List) inventoryItems);
        }
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createProduct(@ModelParam Product product) {
        product.setVisible(ContextObjects.global(false))
            .setVisibleInProductList(ContextObjects.global(true));

        setStatuses(product);

        return created(service.create(product));
    }

    @SuppressWarnings("unchecked")
    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Id updateProduct(@PathParam("id") Id id, Update update) {
        if (id != null && update != null) {
            Product p = checked(service.get(Product.class, id, QueryOptions.builder().refresh(true).build()));

            if (update.isSaveAsNewCopy()) {
                Product copy = ((CopySupport<Product>) p).makeCopy();

                copy.set(update.getFields());
                copy.putAttributes(update.getAttributes());
                copy.setOptionAttributes(update.getOptions());
                copy.setXOptionAttributes(update.getXOptions());
                copy.setOptOuts(update.getOptOuts());
                copy.setMerchantIds(update.getMerchantIds());
                copy.setStoreIds(update.getStoreIds());
                copy.setRequestContextIds(update.getRequestContextIds());

                setStatuses(copy);

                copy = service.create(copy);

                updateRewriteURL(copy.getId(), update);
                copy.setURI(null);

                return copy.getId();
            } else {
                p.set(update.getFields());
                p.putAttributes(update.getAttributes());
                p.setOptionAttributes(update.getOptions());
                p.setXOptionAttributes(update.getXOptions());
                p.setOptOuts(update.getOptOuts());
                p.setMerchantIds(update.getMerchantIds());
                p.setStoreIds(update.getStoreIds());
                p.setRequestContextIds(update.getRequestContextIds());

                setStatuses(p);

                service.update(p);

                updateRewriteURL(p.getId(), update);
                p.setURI(null);

                return p.getId();
            }
        }

        return null;
    }

    private void setStatuses(Product p) {
        if (p == null)
            return;

        ApplicationContext appCtx = app.context();
        Merchant m = appCtx.getMerchant();

        for (Store store : m.getStores()) {
            AttributeOption descStatusStore = productHelper.getDescriptionStatus(p, store);

            if (descStatusStore != null && descStatusStore.getId() != null)
                p.setXOptionAttribute("status_description", descStatusStore.getId(), store);
        }

        AttributeOption imageStatus = productHelper.getImageStatus(p);

        if (imageStatus != null && imageStatus.getId() != null)
            p.setAttribute("status_image", imageStatus.getId());
    }

    @PUT
    @Path("{id}/attributes/{attributeId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateAttribute(@PathParam("id") Id id, @PathParam("attributeId") Id attributeId,
        @ModelParam AttributeValue attributeValue) {
        if (id != null && attributeId != null && attributeValue != null) {
            Product p = checked(service.get(Product.class, id, QueryOptions.builder().refresh(true).build()));

            if (attributeValue.getAttributeId() != null && !attributeId.equals(attributeValue.getAttributeId()))
                throwBadRequest("The attributeId in the URI and object to update do not match");

            AttributeValue av = p.getAttribute(attributeId);

            if (av != null) {
                if (attributeValue.getValue() != null)
                    av.setValue(attributeValue.getValue());

                if (attributeValue.getOptionIds() != null && !attributeValue.getOptionIds().isEmpty()) {
                    av.setOptionIds(attributeValue.getOptionIds());
                    av.setProperties(attributeValue.getProperties());
                }

                av.setSortOrder(attributeValue.getSortOrder());

                setStatuses(p);
                service.update(p);
            }
        }
    }

    @PUT
    @Path("{id}/variants/{variantProductId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void addVariant(@PathParam("id") Id id, @PathParam("variantProductId") Id variantProductId) {
        if (id != null && variantProductId != null) {
            // Get main and variant product.,
            Product product = checked(service.get(Product.class, id));
            Product variantProduct = checked(service.get(Product.class, variantProductId));

            // Add variant product to main product.
            product.addVariant(variantProduct);

            // Save main product with the new variant-productId.
            service.update(product);
            // Save variant-product with the new parent-productId.
            service.update(variantProduct);
        } else {
            throwBadRequest(
                "ProductId and variantProductId cannot be null in requestURI. Expecting: products/{id}/variants/{variantProductId}");
        }
    }

    @DELETE
    @Path("{id}/variants/{variantProductId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeVariant(@PathParam("id") Id id, @PathParam("variantProductId") Id variantProductId) {
        if (id != null && variantProductId != null) {
            // Get main and variant product.
            Product product = checked(service.get(Product.class, id));
            Product variantProduct = checked(service.get(Product.class, variantProductId));

            // Add variant product to main product.
            product.removeVariant(variantProduct);

            // Save main product with the removed variant-productId.
            service.update(product);
            // Save variant-product with the removed parent-productId.
            service.update(variantProduct);
        } else {
            throwBadRequest(
                "ProductId and variantProductId cannot be null in requestURI. Expecting: products/{id}/variants/{variantProductId}");
        }
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

    @POST
    @Path("/image-media-assets")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getImageMediaAssets(List<Id> productIds) {
        Map<Id, List<CatalogMediaAsset>> mediaAssetsMap = new LinkedHashMap<>();
        if (productIds != null && productIds.size() > 0) {
            for (Id productId : productIds) {

                Product p = checked(service.get(Product.class, productId));

                List<CatalogMediaAsset> mediaAssets = null;

                Set<Id> variantProductIds = new HashSet<>();
                variantProductIds.add(p.getId());

                if (p.isVariantMaster())
                    variantProductIds.addAll(p.getVariantProductIds());

                List<String> mimeTypes = new ArrayList<>();
                mimeTypes.add("image/*");
                mimeTypes.add("video/*");

                Filter filter = new Filter();
                if (mimeTypes != null && mimeTypes.size() > 0) {
                    Set<String> expandedMimeTypes = MimeType.expandWildcards(mimeTypes);
                    if (expandedMimeTypes.size() > 0)
                        // Override the request mimeTypes with the expanded
                        // ones.
                        filter.append("mimeType", expandedMimeTypes);
                }

                filter.append("productId", variantProductIds);
                mediaAssets = service.get(CatalogMediaAsset.class, filter.getParams(),
                    QueryOptions.builder().sortBy(CatalogMediaAsset.Col.POSITION).build());
                mediaAssetsMap.put(productId, mediaAssets);
            }
        }

        return ok(mediaAssetsMap);
    }

    @POST
    @Path("{id}/media-assets")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response newMediaAsset(@PathParam("id") Id id, @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataBodyPart formDataBodyPart) {
        CatalogMediaAsset newMediaAsset = null;

        if (id != null) {
            // Get product and image.
            Product product = checked(service.get(Product.class, id));

            FormDataContentDisposition fileDetails = formDataBodyPart.getFormDataContentDisposition();
            String mimeType = MimeType.fromFilename(fileDetails.getFileName());

            System.out.println(fileDetails.getFileName() + " -> " + mimeType + " -> " + fileDetails.getName());

            String absSystemPath = null;

            if (MimeType.isImage(fileDetails.getFileName())) {
                absSystemPath = catalogMediaHelper.getNewAbsoluteFilePath(fileDetails.getFileName(), null, mimeType,
                    product, app.getStoreFromHeader());
            } else {
                absSystemPath = catalogMediaHelper.getNewAbsoluteFilePath(fileDetails.getFileName(),
                    fileDetails.getFileName(), mimeType, product, app.getStoreFromHeader());
            }

            System.out.println(absSystemPath);

            File savedFile = catalogMediaHelper.saveToDisk(uploadedInputStream, absSystemPath, product);

            System.out.println(savedFile.getAbsolutePath());

            // We need to get the relative path from the saved file in case a
            // version number has been added.
            String relativePath = catalogMediaHelper.toRelativeAssetPath(savedFile.getAbsolutePath());

            System.out.println(relativePath);

            CatalogMediaAsset cma = app.model(CatalogMediaAsset.class).belongsTo(product)
                .setPath(catalogMediaHelper.toWebURI(relativePath)).setMimeType(mimeType).setPosition(99)
                .setEnabled(true);

            if (app.storeHeaderExists())
                cma.setStoreId(app.getStoreFromHeader());

            newMediaAsset = service.create(cma);

            product = service.get(Product.class, id, QueryOptions.builder().refresh(true).build());
            setStatuses(product);
            service.update(product);
        } else {
            throwBadRequest("ProductId cannot be null in requestURI. Expecting: products/{id}/media-assets");
        }

        if (newMediaAsset == null || newMediaAsset.getId() == null) {
            throwInternalServerError("Unable to save media-asset");
        }

        return created(newMediaAsset);
    }

    @PUT
    @Path("{id}/media-assets/{mediaAssetId}/preview-image")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response newMediaAssetPreviewImage(@PathParam("id") Id id, @PathParam("mediaAssetId") Id mediaAssetId,
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataBodyPart formDataBodyPart) {
        CatalogMediaAsset mediaAsset = null;

        if (id != null && mediaAssetId != null) {
            // Get product and media-asset.
            Product product = checked(service.get(Product.class, id));
            mediaAsset = checked(service.get(CatalogMediaAsset.class, mediaAssetId));

            // Just make sure that the objects match.
            if (product.getId().equals(mediaAsset.getProductId())) {
                FormDataContentDisposition fileDetails = formDataBodyPart.getFormDataContentDisposition();

                String videoSysPath = mediaAsset.getSystemPath();

                if (videoSysPath != null) {
                    String absSystemPath = videoSysPath.substring(0, videoSysPath.lastIndexOf(File.separatorChar) + 1)
                        + fileDetails.getFileName();

                    File savedFile = catalogMediaHelper.saveToDisk(uploadedInputStream, absSystemPath, product);

                    // We need to get the relative path from the saved file in
                    // case a version number has been added.
                    String relativePath = catalogMediaHelper.toRelativeAssetPath(savedFile.getAbsolutePath());
                    mediaAsset.setPreviewImagePath(catalogMediaHelper.toWebURI(relativePath));

                    service.update(mediaAsset);
                }
            }
        } else {
            throwBadRequest(
                "ProductId and mediaAssetId cannot be null in requestURI. Expecting: products/{id}/media-assets/{mediaAssetId}/preview-image");
        }

        if (mediaAsset == null || mediaAsset.getId() == null) {
            throwInternalServerError();
        }

        return ok(mediaAsset);
    }

    @DELETE
    @Path("{id}/media-assets/{mediaAssetId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeMediaAsset(@PathParam("id") Id id, @PathParam("mediaAssetId") Id mediaAssetId) {
        if (id != null && mediaAssetId != null) {
            // Get product and media-asset.
            Product product = checked(service.get(Product.class, id));
            CatalogMediaAsset mediaAsset = checked(service.get(CatalogMediaAsset.class, mediaAssetId));

            // Just make sure that the objects match.
            if (product.getId().equals(mediaAsset.getProductId())) {
                // Remove media-asset from DB.
                service.remove(mediaAsset);

                // Make sure that no other media-asset is using the file before
                // marking it as deleted.
                Map<String, Object> filter = new HashMap<>();
                filter.put(CatalogMediaAsset.Col.PATH, mediaAsset.getPath());

                List<CatalogMediaAsset> mediaAssets = service.get(CatalogMediaAsset.class, filter);

                // if (mediaAssets == null || mediaAssets.size() == 0) {
                // Rename media-asset to .deleted (file is not physically being
                // deleted from the system).
                catalogMediaHelper.markAsDeleted(mediaAsset.getSystemPath());

                catalogMediaHelper.removeFromCaches(mediaAsset);
                // }

                product = service.get(Product.class, id, QueryOptions.builder().refresh(true).build());
                setStatuses(product);
                service.update(product);
            }
        } else {
            throwBadRequest(
                "ProductId and mediaAssetId cannot be null in requestURI. Expecting: products/{id}/media-assets/{mediaAssetId}");
        }
    }

    @PUT
    @Path("{id}/media-assets")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateMediaAssets(@PathParam("id") Id id, List<Update> updates) {
        Product p = checked(service.get(Product.class, id));

        if (updates != null && updates.size() > 0) {
            for (Update update : updates) {
                if (update != null && update.getId() != null && update.getFields() != null
                    && update.getFields().size() > 0) {
                    CatalogMediaAsset cma = service.get(CatalogMediaAsset.class, update.getId());
                    cma.set(update.getFields());

                    if (id.equals(cma.getProductId())) {
                        service.update(cma);

                        p = service.get(Product.class, id, QueryOptions.builder().refresh(true).build());
                        setStatuses(p);
                        service.update(p);
                    }
                }
            }
        }
    }

    @PUT
    @Path("{id}/media-assets/positions")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateMediaAssetPositions(@PathParam("id") Id id, HashMap<String, Integer> positionsMap) {
        if (id != null && positionsMap != null && positionsMap.size() > 0) {
            Product product = checked(service.get(Product.class, id));

            Set<String> keys = positionsMap.keySet();

            for (String key : keys) {
                Id catMediaAssetId = Id.valueOf(key);
                Integer pos = positionsMap.get(key);

                CatalogMediaAsset mediaAsset = checked(service.get(CatalogMediaAsset.class, catMediaAssetId));

                if (mediaAsset.getProductId().equals(product.getId())) {
                    mediaAsset.setPosition(pos);
                    service.update(mediaAsset);
                }
            }
        }
    }

    @GET
    @Path("media-types")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getMediaTypes() {

        return ok(service.get(CatalogMediaType.class));
    }

    protected static final Pattern containsRandomPattern = Pattern.compile("\\-[A-za-z0-9]{4}\\.html$");

    @SuppressWarnings("unchecked")
    @PUT
    @Path("{id}/url")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateRewriteURL(@PathParam("id") Id productId, Update update) {
        ContextObject<String> urls = (ContextObject<String>) update.getVar("friendlyURL");

        Product product = checked(service.get(Product.class, productId));
        UrlRewrite urlRewrite = urlRewrites.forProduct(productId);

        List<Integer> entriesToRemove = new ArrayList<>();

        for (Map<String, Object> entry : urls) {
            String requestURI = (String) entry.get(ContextObject.VALUE);

            if (Str.isEmpty(requestURI))
                entriesToRemove.add(urls.entryHashCode(entry));

            // we'll try without random numbers and letters first.
            boolean addRandom = false;

            while (urlRewrites.contains(requestURI, ObjectType.PRODUCT, productId)) {
                String articleNumber = product.getArticleNumber();
                String sluggedArticleNumber = Strings.slugify(articleNumber);
                String rnd = Strings.random(4);
                int extPos = requestURI.lastIndexOf(".html");

                String _reqURI = null;

                Matcher containsRandomMatcher = containsRandomPattern.matcher(requestURI);

                // First clean url in case random number has previously been
                // added.
                if (!Str.isEmpty(sluggedArticleNumber)
                    && ((requestURI.contains(sluggedArticleNumber) && containsRandomMatcher.matches())
                        || requestURI.endsWith(sluggedArticleNumber + ".html"))) {
                    _reqURI = requestURI.substring(0, requestURI.indexOf(sluggedArticleNumber));
                } else if ((requestURI.contains(productId.str()) && containsRandomMatcher.matches())
                    || requestURI.endsWith(productId.str() + ".html")) {
                    _reqURI = requestURI.substring(0, requestURI.indexOf(productId.str()));
                } else {
                    _reqURI = requestURI.substring(0, extPos) + Str.MINUS;
                }

                // Now we'll build a new one with the clean base URI.
                if (!Str.isEmpty(sluggedArticleNumber)) {
                    StringBuilder sb = new StringBuilder(_reqURI).append(sluggedArticleNumber);

                    if (addRandom)
                        sb.append(Char.MINUS).append(rnd);

                    sb.append(requestURI.substring(extPos)).toString();

                    requestURI = sb.toString();
                } else {
                    StringBuilder sb = new StringBuilder(_reqURI).append(productId.str());

                    if (addRandom)
                        sb.append(Char.MINUS).append(rnd);

                    sb.append(requestURI.substring(extPos));

                    requestURI = sb.toString();
                }

                // If we need to go for another round, we'll add some random
                // chars next time.
                addRandom = true;
            }

            entry.put(ContextObject.VALUE, requestURI);
        }

        for (Integer hash : entriesToRemove) {
            urls.remove(hash);
        }

        if (urlRewrite == null) {
            urlRewrite = app.model(UrlRewrite.class);
            urlRewrite.setEnabled(true);
            urlRewrite.setRequestMethod("GET");
            urlRewrite.setTargetObjectId(productId);
            urlRewrite.setTargetObjectType(ObjectType.PRODUCT);
            urlRewrite.setTargetURL("/catalog/product/view/" + productId);
            urlRewrite.setRequestURI(urls);

            urlRewrites.add(urlRewrite);
        } else {
            urlRewrite.setRequestURI(urls);
            urlRewrites.update(urlRewrite);
        }

        return ok(checked(urlRewrite));
    }

    @SuppressWarnings("unchecked")
    @POST
    @Path("{id}/valid-url")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ContextObject<String> validRewriteURL(@PathParam("id") Id productId, Update update) {
        ContextObject<String> urls = (ContextObject<String>) update.getVar("friendlyURL");

        Product product = checked(service.get(Product.class, productId));

        List<Integer> entriesToRemove = new ArrayList<>();

        for (Map<String, Object> entry : urls) {
            String requestURI = (String) entry.get(ContextObject.VALUE);

            if (Str.isEmpty(requestURI))
                entriesToRemove.add(urls.entryHashCode(entry));

            // we'll try without random numbers and letters first.
            boolean addRandom = false;

            while (urlRewrites.contains(requestURI, ObjectType.PRODUCT, productId)) {
                String articleNumber = product.getArticleNumber();
                String sluggedArticleNumber = Strings.slugify(articleNumber);
                String rnd = Strings.random(4);
                int extPos = requestURI.lastIndexOf(".html");

                String _reqURI = null;

                Matcher containsRandomMatcher = containsRandomPattern.matcher(requestURI);

                // First clean url in case random number has previously been
                // added.
                if (!Str.isEmpty(sluggedArticleNumber)
                    && ((requestURI.contains(sluggedArticleNumber) && containsRandomMatcher.matches())
                        || requestURI.endsWith(sluggedArticleNumber + ".html"))) {
                    _reqURI = requestURI.substring(0, requestURI.indexOf(sluggedArticleNumber));
                } else if ((requestURI.contains(productId.str()) && containsRandomMatcher.matches())
                    || requestURI.endsWith(productId.str() + ".html")) {
                    _reqURI = requestURI.substring(0, requestURI.indexOf(productId.str()));
                } else {
                    _reqURI = requestURI.substring(0, extPos) + Str.MINUS;
                }

                // Now we'll build a new one with the clean base URI.
                if (!Str.isEmpty(sluggedArticleNumber)) {
                    StringBuilder sb = new StringBuilder(_reqURI).append(sluggedArticleNumber);

                    if (addRandom)
                        sb.append(Char.MINUS).append(rnd);

                    sb.append(requestURI.substring(extPos)).toString();

                    requestURI = sb.toString();
                } else {
                    StringBuilder sb = new StringBuilder(_reqURI).append(productId.str());

                    if (addRandom)
                        sb.append(Char.MINUS).append(rnd);

                    sb.append(requestURI.substring(extPos));

                    requestURI = sb.toString();
                }

                // If we need to go for another round, we'll add some random
                // chars next time.
                addRandom = true;
            }

            entry.put(ContextObject.VALUE, requestURI);
        }

        for (Integer hash : entriesToRemove) {
            urls.remove(hash);
        }

        return urls;
    }

    @SuppressWarnings("unchecked")
    @POST
    @Path("{id}/url/validation")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getIsUrlUnique(@PathParam("id") Id id, Update update) {
        ContextObject<String> urls = (ContextObject<String>) update.getFields().get("rewriteUrl");
        return ok(checked(urlRewriteHelper.isUriUnique(ObjectType.PRODUCT, id, urls)));
    }

    @SuppressWarnings("unchecked")
    @POST
    @Path("/url/validation")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getIsUrlUnique(Update update) {
        ContextObject<String> urls = (ContextObject<String>) update.getFields().get("rewriteUrl");
        return ok(checked(urlRewriteHelper.isUriUnique(urls)));
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
}
