package com.geecommerce.catalog.product.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.catalog.product.helper.ProductHelper;
import com.geecommerce.catalog.product.model.CatalogMediaAsset;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductLinkType;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductNavigationIndex;
import com.geecommerce.catalog.product.repository.ProductLists;
import com.geecommerce.catalog.product.repository.ProductNavigationIndexes;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.Str;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.util.Requests;
import com.geecommerce.core.web.BaseController;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.geecommerce.price.pojo.PriceResult;
import com.geecommerce.unit.converter.enums.DataAmountUnit;
import com.geecommerce.unit.converter.service.DataAmountConverter;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.Param;
import com.geemvc.bind.param.annotation.PathParam;
import com.geemvc.view.bean.Result;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@Controller
@Request("/catalog/product")
public class ProductController extends BaseController {

    protected final ProductService productService;
    protected final ProductHelper productHelper;
    protected final MediaAssetService mediaAssetService;
    protected final ProductLists productLists;
    protected final ProductNavigationIndexes productNavigationIndexes;

    protected static final String DEFAULT_ROOT_NAV_ID_KEY = "navigation/default/root_id";
    protected static final String CURRENT_PRODUCT_LIST_PARAM = "cpl";

    private static final Logger log = LogManager.getLogger(ProductController.class);

    @Inject
    public ProductController(ProductService productService, ProductHelper productHelper, MediaAssetService mediaAssetService, ProductLists productLists,
        ProductNavigationIndexes productNavigationIndexes) {
        this.productService = productService;
        this.productHelper = productHelper;
        this.mediaAssetService = mediaAssetService;
        this.productLists = productLists;
        this.productNavigationIndexes = productNavigationIndexes;

        // this.dm =
        // app.cpStr_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_SUBDOMAIN);
        // this.wp = app.cpStr_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_WEBPATH);
    }

    @Request("attr-bean-test-form")
    public Result attributeBeanTestForm() {

        return Results.view("catalog/product/attr-bean-test-form");
    }

    @Request("process-attr-bean-test")
    public Result createAttributeBeanTest(@Valid Product product) {

        return Results.view("catalog/product/attr-bean-test-form")
            .bind("product", product);
    }

    @Request("/view/{id}")
    public Result view(@PathParam("id") Id id, @Context HttpServletRequest request, @Context HttpServletResponse response) {

        List<LinkedProductView> linkedProductViews = new ArrayList<>();

        long start = System.currentTimeMillis();

        Product product = productService.getProduct(id);

        if (!isVisible(product, request, response))
            return null;

        Map<String, Object> variantsAsMap = productHelper.toVariantsMap(product);
        String variantsAsJSON = null;

        if (variantsAsMap != null && variantsAsMap.size() > 0) {
            variantsAsJSON = Json.toJson(variantsAsMap);
        } else {
            variantsAsJSON = Json.toJson(Maps.newHashMap());
        }

        if (product.getProductLinks() != null && !product.getProductLinks().isEmpty()) {
            fillLinkedProductView(product, linkedProductViews);
        }

        List<MediaAssetLinkView> mediaAssetLinkViews = new ArrayList<>();
        if (product.getAssets() != null && !product.getAssets().isEmpty()) {
            mediaAssetLinkViews = getMediaAssetLinkViews(product.getAssets());
        }

        String currentProductList = request.getParameter(CURRENT_PRODUCT_LIST_PARAM);

        if ((System.currentTimeMillis() - start) > 500)
            System.out.println("-- product-view time took: " + (System.currentTimeMillis() - start));

        return Results.view("catalog/product/view")
            .bind("product", product)
            .bind("variantsAsMap", variantsAsMap)
            .bind("variantsAsJSON", variantsAsJSON)
            .bind("mediaAssetLinkViews", mediaAssetLinkViews)
            .bind("currentProductList", currentProductList)
            .bind("linkedProductViews", linkedProductViews);
    }

    @Request(path = "/json/view/{id}", produces = MediaType.APPLICATION_JSON)
    public Product viewJSON(@PathParam("id") Id id, @Context HttpServletRequest request, @Context HttpServletResponse response) {

        List<LinkedProductView> linkedProductViews = new ArrayList<>();

        long start = System.currentTimeMillis();

        Product product = productService.getProduct(id);

        if (!isVisible(product, request, response))
            return null;

        Map<String, Object> variantsAsMap = productHelper.toVariantsMap(product);
        String variantsAsJSON = null;

        if (variantsAsMap != null && variantsAsMap.size() > 0) {
            variantsAsJSON = Json.toJson(variantsAsMap);
        } else {
            variantsAsJSON = Json.toJson(Maps.newHashMap());
        }

        if (product.getProductLinks() != null && !product.getProductLinks().isEmpty()) {
            fillLinkedProductView(product, linkedProductViews);
        }

        List<MediaAssetLinkView> mediaAssetLinkViews = new ArrayList<>();
        if (product.getAssets() != null && !product.getAssets().isEmpty()) {
            mediaAssetLinkViews = getMediaAssetLinkViews(product.getAssets());
        }

        String currentProductList = request.getParameter(CURRENT_PRODUCT_LIST_PARAM);

        if ((System.currentTimeMillis() - start) > 500)
            System.out.println("-- product-view time took: " + (System.currentTimeMillis() - start));

        return product;
    }

    protected boolean isVisible(Product product, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        if (product == null) {
            System.out.println("Product not found for path '" + httpRequest.getRequestURI() + "'. Redirecting to /.");

            httpResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            httpResponse.setHeader("Location", Str.SLASH);

            return false;
        }

        boolean isDeleted = product.isDeleted();

        long start = System.currentTimeMillis();

        boolean isVisible = product.isVisible();

        // System.out.println("-- isVisible time took: " +
        // (System.currentTimeMillis() - start));

        boolean isInternalRequest = Requests.isInternalRequest(httpRequest);
        boolean previewHeaderExists = app.previewHeaderExists();
        boolean refreshHeaderExists = app.refreshHeaderExists();

        if ((!isDeleted && isVisible) || isInternalRequest || previewHeaderExists || refreshHeaderExists)
            return true;

        System.out.println("Product " + product.getId() + " / " + product.getArticleNumber() + " is not visible after evaluating parameters: [isDeleted=" + isDeleted + ", isVisible=" + isVisible
            + ", isInternalRequest=" + isInternalRequest
            + ", previewHeaderExists=" + previewHeaderExists + ", refreshHeaderExists=" + refreshHeaderExists + "].");

        Long rootId = app.cpLong_(DEFAULT_ROOT_NAV_ID_KEY);

        List<ProductNavigationIndex> pniList = productNavigationIndexes.forProduct(product, Id.valueOf(rootId), false);

        if (pniList == null || pniList.size() == 0)
            pniList = productNavigationIndexes.forProduct(product, false);

        if (pniList != null && pniList.size() > 0) {
            ProductNavigationIndex pni = pniList.get(0);
            Id productListId = pni.getProductListId();

            if (productListId != null) {
                ProductList productList = productLists.findById(ProductList.class, productListId);

                if (productList != null) {
                    String productListURI = ContextObjects.findCurrentLanguage(productList.getURI());

                    if (productListURI != null) {
                        httpResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                        httpResponse.setHeader("Location", productListURI);

                        System.out.println("Redirecting to: " + productListURI);

                        return false;
                    }
                }
            }
        }

        httpResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        httpResponse.setHeader("Location", Str.SLASH);

        System.out.println("Redirecting to: /");

        return false;
    }

    protected void fillLinkedProductView(Product product, List<LinkedProductView> linkedProductViews) {
        Set<String> productLinksKeys = product.getProductLinks().keySet();
        for (String productLinksKey : productLinksKeys) {
            List<Product> linkedProducts = new ArrayList<>();

            for (Id linkedProductId : product.getProductLinks().get(productLinksKey)) {
                linkedProducts.add(productService.getProduct(linkedProductId));
            }

            LinkedProductView linkedProductView = new LinkedProductView(productService.getProductLinkTypeFor(productLinksKey), linkedProducts);
            linkedProductViews.add(linkedProductView);
        }
    }

    protected List<MediaAssetLinkView> getMediaAssetLinkViews(List<Id> assets) {
        List<MediaAsset> assetList = mediaAssetService.get(assets);
        List<MediaAssetLinkView> mediaAssetView = new ArrayList<>();
        if (assetList != null && !assetList.isEmpty()) {
            for (MediaAsset mediaAsset : assetList) {
                MediaAssetLinkView mediaAssetLinkView = new MediaAssetLinkView();
                mediaAssetLinkView.setUrl(mediaAsset.getUrl());
                mediaAssetLinkView.setLabel(mediaAsset.getName().getStr());

                if (mediaAsset.getSize() != null) {
                    Double size = app.getService(DataAmountConverter.class).convert(Double.valueOf(mediaAsset.getSize()), DataAmountUnit.KILOBYTE);
                    mediaAssetLinkView.setSize(size.longValue());
                }

                if (mediaAsset.getMimeType() != null && !mediaAsset.getMimeType().isEmpty())
                    mediaAssetLinkView.setMimeType(mediaAsset.getMimeType());

                mediaAssetView.add(mediaAssetLinkView);
            }
        }
        return mediaAssetView;
    }

    @Request("/price-container/{id}")
    public Result priceContainer(@PathParam("id") Id id) {
        Product product = productService.getProduct(id);
        return Results.view("catalog/product/view_price_container").bind("product", product);
    }

    @Request("/view-header/{id}")
    public Result viewHeader(@PathParam("id") Id id) {
        Product product = productService.getProduct(id);
        return Results.view("catalog/product/view_header").bind("product", product);
    }

    @Request("/status/{id}")
    public Result status(@PathParam("id") Id id) {

        Product p = productService.getProduct(id);

        AttributeOption ao = productHelper.getDescriptionStatus(p, getStore());
        AttributeOption ao2 = productHelper.getImageStatus(p);

        return Results.stream("text/html", ao == null ? "null" : ao.toString() + " - " + ao2.toString());
    }

    @Request("/list/{id}")
    public Result list(@PathParam("id") Id id) {
        List<Product> products = productService.getProductsFor(getStore());
        return Results.view("product/list").bind("products", products);
    }

    protected Map<String, Object> getProductSaleData(Id productId) {
        Product p = productService.getProduct(productId);

        if (p == null)
            return null;

        Map<String, Object> saleData = new HashMap<>();

        PriceResult priceResult = p.getPrice();

        if (priceResult != null) {
            saleData.put("final_price", priceResult.getFinalPrice());

            Map<String, Double> prices = priceResult.getValidPrices();
            Set<String> priceTypes = prices.keySet();

            for (String priceType : priceTypes) {
                saleData.put(priceType, prices.get(priceType));
            }

            // saleData.put("qty", p.getQty()); We may not want to show the
            // outside world how much we actually have in
            // stock.
            saleData.put("saleable", p.isValidForSelling());
        } else {
            log.warn("Unable to find price data for product: " + productId);
        }

        return saleData;
    }

    protected Map<String, Object> getImageParams(Product p) {
        Map<String, Object> params = new HashMap<>();
        // URL information
        params.put("dm", app.cpStr_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_SUBDOMAIN));
        params.put("wp", app.cpStr_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_WEBPATH));

        // Main size
        params.put("mwidth", app.cpStr_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_VIEW_MAIN_WIDTH, ""));
        params.put("mheight", app.cpStr_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_VIEW_MAIN_HEIGHT, ""));

        // Thumbnail size
        params.put("twidth", app.cpStr_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_VIEW_THUMBNAIL_WIDTH, ""));
        params.put("theight", app.cpStr_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_VIEW_THUMBNAIL_HEIGHT, ""));

        List<CatalogMediaAsset> images = p.getImages();
        List<Map<String, Object>> imageMaps = new ArrayList<>();

        for (CatalogMediaAsset productImage : images) {
            Map<String, Object> imageMap = new HashMap<>();

            if (productImage.getPath() == null || "".equals(productImage.getPath().trim()))
                continue;

            imageMap.put("path", productImage.getWebPath());

            if (productImage.getTitle() != null) {
                imageMap.put("title", productImage.getTitle().getStr());
            }

            imageMap.put("pos", productImage.getPosition());

            imageMaps.add(imageMap);
        }

        params.put("images", imageMaps);

        return params;
    }

    @Request("/images/{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Result images(@PathParam("id") Id id) {
        if (id == null)
            return Results.stream("application/javascript", "An internal error occured. Please try again later.");

        Product p = productService.getProduct(id);

        if (p == null)
            return Results.stream("application/javascript", "An internal error occured. Please try again later.");
        // return jsonError("An internal error occured. Please try again
        // later.");

        Map<String, Object> params = getImageParams(p);

        // return json(params);
        return Results.stream("application/javascript", Json.toJson(params));
    }

    @Request("/fragment/{id}")
    public Result fragment(@PathParam("id") Id id, @Context HttpServletRequest request, @Context HttpServletResponse response, @Param("frg") String frg) {
        if (id == null || Str.isEmpty(frg))
            return null;

        Product product = productService.getProduct(id);

        if (!isVisible(product, request, response))
            return null;

        return Results.view("catalog/product/fragments/" + frg).bind("product", product);
    }

    public class LinkedProductView {

        ProductLinkType productLinkType = null;
        List<Product> products = new ArrayList<>();

        public LinkedProductView(ProductLinkType productLinkType, List<Product> products) {
            this.productLinkType = productLinkType;
            this.products = products;
        }

        public ProductLinkType getProductLinkType() {
            return productLinkType;
        }

        public LinkedProductView setProductLinkType(ProductLinkType productLinkType) {
            this.productLinkType = productLinkType;
            return this;
        }

        public List<Product> getProducts() {
            return products;
        }

        public LinkedProductView setProducts(List<Product> products) {
            this.products = products;
            return this;
        }

        public LinkedProductView addProduct(Product product) {
            if (this.products == null) {
                this.products = new ArrayList<>();
            }
            this.products.add(product);
            return this;
        }
    }

    public class MediaAssetLinkView {
        protected String url = null;
        private String mimeType = null;
        private String label = null;
        private Long size = null;

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

    }
}
