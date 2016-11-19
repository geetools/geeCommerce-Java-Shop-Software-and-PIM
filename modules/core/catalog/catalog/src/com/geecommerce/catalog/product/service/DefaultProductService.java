package com.geecommerce.catalog.product.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.catalog.product.ProductConstant;
import com.geecommerce.catalog.product.helper.ProductHelper;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductConnectionIndex;
import com.geecommerce.catalog.product.model.ProductLinkType;
import com.geecommerce.catalog.product.repository.CatalogMedia;
import com.geecommerce.catalog.product.repository.ProductConnectionIndexes;
import com.geecommerce.catalog.product.repository.ProductLinkTypes;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.App;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.service.UrlRewriteService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.Ids;
import com.geecommerce.inventory.repository.Stocks;
import com.geecommerce.price.pojo.PriceResult;
import com.geecommerce.price.service.PriceService;
import com.google.inject.Inject;

@Service
public class DefaultProductService implements ProductService {
    @Inject
    protected App app;

    protected final Products products;
    protected final ProductHelper productHelper;
    protected final PriceService priceService;
    protected final Stocks stocks;
    protected final CatalogMedia catalogMedia;
    protected final ProductLinkTypes productLinkTypes;
    protected final UrlRewriteService urlRewriteService;
    protected final ProductConnectionIndexes productConnections;

    @Inject
    public DefaultProductService(Products products, ProductHelper productHelper, PriceService priceService, Stocks stocks, CatalogMedia catalogMedia, ProductLinkTypes productLinkTypes,
        UrlRewriteService urlRewriteService,
        ProductConnectionIndexes productConnections) {
        this.products = products;
        this.productHelper = productHelper;
        this.priceService = priceService;
        this.stocks = stocks;
        this.catalogMedia = catalogMedia;
        this.productLinkTypes = productLinkTypes;
        this.urlRewriteService = urlRewriteService;
        this.productConnections = productConnections;
    }

    // ----------------------------------------------------------------------
    // Product
    // ----------------------------------------------------------------------

    @Override
    public Product createProduct(Product product) {
        return products.add(product);
    }

    @Override
    public void update(Product product) {
        products.update(product);
    }

    @Override
    public Product getProduct(Id id) {
        Product product = products.findById(Product.class, id);

        if (product != null) {
            // Collect productIds so that we can preload some data for them.
            Set<Id> productIds = new HashSet<>();

            productIds.add(id);

            if (product.hasProgrammeProducts())
                productIds.addAll(product.getProgrammeProductIds());

            if (product.hasUpsells())
                productIds.addAll(product.getUpsellProductIds());

            if (product.hasCrossSells())
                productIds.addAll(product.getCrossSellProductIds());

            // Preload prices to make later retrieval faster.
            List<Product> productsList = new ArrayList<>();
            productsList.add(product);

            if (productIds.size() > 0) {
                List<Product> foundProducts = products.findByIds(Product.class, productIds.toArray(new Id[productIds.size()]));
                productsList.addAll(foundProducts);
            }

            preloadPrices(productsList);
            preloadStockdata(productsList);
            preloadProducts(productsList);
        }

        return product;
    }

    @Override
    public Product getProductById2(Id id2) {
        return products.havingId2(id2);
    }

    @Override
    public List<Product> getProducts(Collection<Id> productIds, QueryOptions queryOptions) {
        if (productIds == null || productIds.isEmpty())
            return null;

        // long start = System.currentTimeMillis();

        Id[] ids = productIds.toArray(new Id[productIds.size()]);

        List<Product> foundProducts = products.findByIds(Product.class, ids, queryOptions);

        // System.out.println("getProducts+++1++ " + (System.currentTimeMillis()
        // - start));

        if (foundProducts != null && foundProducts.size() > 0) {
            List<UrlRewrite> urlRewrites = urlRewriteService.findUrlRewritesForProducts(ids);

            // System.out.println("getProducts+++2++ " +
            // (System.currentTimeMillis() - start));

            // Preload images.
            catalogMedia.enabledImagesFor(ids);

            // System.out.println("getProducts+++3++ " +
            // (System.currentTimeMillis() - start));

            productHelper.completeProducts(foundProducts, urlRewrites);

            // System.out.println("getProducts+++4++ " +
            // (System.currentTimeMillis() - start));

            preloadPrices(foundProducts);
            // System.out.println("getProducts+++5++ " +
            // (System.currentTimeMillis() - start));

            preloadStockdata(foundProducts);
            // System.out.println("getProducts+++6++ " +
            // (System.currentTimeMillis() - start));
            preloadProducts(foundProducts);
            // System.out.println("getProducts+++end++ " +
            // (System.currentTimeMillis() - start));
        }

        return foundProducts;

    }

    @Override
    public List<Product> getProducts(Id... ids) {
        return getProducts(true, ids);
    }

    @Override
    public List<Product> getProducts(boolean fetchEntities, Id... ids) {
        List<Product> foundProducts = products.findByIds(Product.class, ids);

        if (foundProducts != null && foundProducts.size() > 0 && fetchEntities) {
            List<UrlRewrite> urlRewrites = urlRewriteService.findUrlRewritesForProducts(ids);

            // Preload images.
            catalogMedia.enabledImagesFor(ids);

            productHelper.completeProducts(foundProducts, urlRewrites);

            preloadPrices(foundProducts);
            preloadStockdata(foundProducts);
            preloadProducts(foundProducts);
        }

        return foundProducts;
    }

    @Override
    public List<Product> getEnabledProducts() {
        return products.enabledForContext();
    }

    @Override
    public List<Id> getEnabledProductIds() {
        return products.enabledIdsForContext();
    }

    @Override
    public List<Id> getAllProductIds() {
        return products.allIdsForContext();
    }

    @Override
    public List<Product> getProductsFor(Merchant merchant) {
        return products.thatBelongTo(merchant);
    }

    @Override
    public List<Product> getProductsFor(Store store) {
        return products.thatBelongTo(store);
    }

    // ----------------------------------------------------------------------
    // Product Links
    // ----------------------------------------------------------------------

    @Override
    public ProductLinkType createProductLinkType(ProductLinkType productLinkType) {
        return productLinkTypes.add(productLinkType);
    }

    @Override
    public void updateProductLinkType(ProductLinkType productLinkType) {
        productLinkTypes.update(productLinkType);
    }

    @Override
    public ProductLinkType getProductLinkTypeFor(String code) {
        return productLinkTypes.thatBelongTo(code);
    }

    @Override
    public List<ProductLinkType> getProductLinkTypes() {
        return productLinkTypes.thatBelongTo();
    }

    protected void preloadPrices(List<Product> productList) {
        if (productList == null || productList.size() == 0)
            return;

        // long start = System.currentTimeMillis();

        String currencyCode = app.getBaseCurrency();

        Set<Id> programmeProductIds = new HashSet<>();

        for (Product product : productList) {
            if (product.isProgramme() && product.getProgrammeProductIds() != null && !product.getProgrammeProductIds().isEmpty())
                programmeProductIds.addAll(product.getProgrammeProductIds());
        }

        // System.out.println(" preloadPrices***1** " +
        // (System.currentTimeMillis() - start));

        List<Product> allProducts = new ArrayList<>();
        allProducts.addAll(productList);

        if (programmeProductIds.size() > 0) {
            List<Product> foundProducts = products.findByIds(Product.class, programmeProductIds.toArray(new Id[programmeProductIds.size()]));
            allProducts.addAll(foundProducts);
        }

        // System.out.println(" preloadPrices***2** " +
        // (System.currentTimeMillis() - start));

        // Attempt to find indexed connections for products.
        Map<Id, ProductConnectionIndex> pConnections = productConnections.forProducts(Ids.toIds(allProducts));

        // System.out.println(" preloadPrices***3** " +
        // (System.currentTimeMillis() - start));

        Map<Id, Id[]> productIdMap = new HashMap<>();

        for (Product product : allProducts) {
            if (product == null || product.getId() == null)
                continue;

            Id productId = product.getId();

            ProductConnectionIndex pci = null;

            if (pConnections != null)
                pci = pConnections.get(product.getId());

            // If we do not have any indexed connections for this product, just
            // add its own id.
            if (pci == null && !product.isVariantMaster() && !product.isProgramme() && !product.isBundle()) {
                productIdMap.put(productId, new Id[0]);
            }
            // Otherwise add the child (programme, variant-master, variant)
            // connections.
            else if (pci != null) {
                Set<Id> productChildIds = pci.getSellableChildConnections();

                // Do not add an empty child list if the product is of type
                // programme, variant-master or bundle.
                if ((productChildIds == null || productChildIds.size() == 0) && (product.isVariantMaster() || product.isProgramme() || product.isBundle()))
                    continue;

                productIdMap.put(productId, productChildIds == null ? new Id[0] : productChildIds.toArray(new Id[productChildIds.size()]));
            }
        }

        // System.out.println(" preloadPrices***4** " +
        // (System.currentTimeMillis() - start));

        if (productIdMap.size() > 0) {
            // Once we have collected all the ids, we can pre-fetch the prices
            // for them.
            Map<Id, PriceResult> priceResults = priceService.getPricesFor(productIdMap, currencyCode);

            // System.out.println(" preloadPrices***5** " +
            // (System.currentTimeMillis() - start));

            // If prices have already been preloaded, we add to the existing
            // map.
            Map<Id, PriceResult> preloadedPriceResults = app.registryGet(ProductConstant.PRELOADED_PRODUCT_PRICES);

            if (preloadedPriceResults != null && priceResults != null) {
                preloadedPriceResults.putAll(priceResults);
            }
            // If price-results exist, add them to the registry for usage later
            // in the thread.
            else if (priceResults != null) {
                app.registryPut(ProductConstant.PRELOADED_PRODUCT_PRICES, priceResults);
            }
        }

        // System.out.println(" preloadPrices***end** " +
        // (System.currentTimeMillis() - start));
    }

    protected void preloadStockdata(List<Product> productList) {
        if (productList == null || productList.size() == 0)
            return;

        long start = System.currentTimeMillis();

        Id[] ids = Ids.toIds(productList);

        // Attempt to find indexed connections for products.
        Map<Id, ProductConnectionIndex> pConnections = productConnections.forProducts(ids);

        Set<Id> allProductIds = new HashSet<>();

        for (Product product : productList) {
            if (product == null || product.getId() == null)
                continue;

            ProductConnectionIndex pci = null;

            if (pConnections != null)
                pci = pConnections.get(product.getId());

            if (pci != null) {
                Set<Id> connectedProductIds = pci.getConnections();

                if (connectedProductIds == null || connectedProductIds.size() == 0)
                    continue;

                allProductIds.addAll(connectedProductIds);
            }
        }

        if (allProductIds.size() > 0) {
            stocks.preloadStockData(allProductIds, app.getApplicationContext().getStore().getId());
        }

        // System.out.println("*** preload stock took: " +
        // (System.currentTimeMillis() - start));
    }

    protected void preloadProducts(List<Product> productList) {
        if (productList == null || productList.size() == 0)
            return;

        long start = System.currentTimeMillis();

        Id[] ids = Ids.toIds(productList);

        // Attempt to find indexed connections for products.
        Map<Id, ProductConnectionIndex> pConnections = productConnections.forProducts(ids);

        Set<Id> allProductIds = new HashSet<>();

        for (Product product : productList) {
            if (product == null || product.getId() == null)
                continue;

            ProductConnectionIndex pci = null;

            if (pConnections != null)
                pci = pConnections.get(product.getId());

            if (pci != null) {
                Set<Id> connectedProductIds = pci.getConnections();

                // Do not add an empty child list if the product is of type
                // programme, variant-master or bundle.
                if (connectedProductIds == null || connectedProductIds.size() == 0)
                    continue;

                allProductIds.addAll(connectedProductIds);
            }
        }

        if (allProductIds.size() > 0) {
            List<Product> foundProducts = products.findByIds(Product.class, allProductIds.toArray(new Id[allProductIds.size()]));

            if (foundProducts != null && foundProducts.size() > 0) {
                List<UrlRewrite> urlRewrites = urlRewriteService.findUrlRewritesForProducts(ids);

                catalogMedia.enabledImagesFor(ids);

                productHelper.completeProducts(foundProducts, urlRewrites);
            }

        }

        // System.out.println("*** preload products took: " +
        // (System.currentTimeMillis() - start));
    }
}
