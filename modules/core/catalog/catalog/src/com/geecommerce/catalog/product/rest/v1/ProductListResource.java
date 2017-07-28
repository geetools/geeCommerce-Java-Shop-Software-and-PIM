package com.geecommerce.catalog.product.rest.v1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

import com.geecommerce.catalog.product.helper.ProductHelper;
import com.geecommerce.catalog.product.helper.ProductListHelper;
import com.geecommerce.catalog.product.helper.ProductListUrlHelper;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductListFilterRule;
import com.geecommerce.catalog.product.service.ProductListService;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.elasticsearch.service.ElasticsearchService;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.system.helper.UrlRewriteHelper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.query.model.QueryNode;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.util.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/product-lists")
public class ProductListResource extends AbstractResource {
    private final RestService service;
    private final ProductListService productListService;
    private final ProductHelper productHelper;
    private final ProductListHelper productListHelper;
    private final ProductService productService;
    private final UrlRewrites urlRewrites;
    private final UrlRewriteHelper urlRewriteHelper;
    private final ProductListUrlHelper productListUrlHelper;
    private final ElasticsearchHelper elasticsearchHelper;
    private final ElasticsearchService elasticsearchService;
    private final AttributeService attributeService;

    @Inject
    public ProductListResource(RestService service, ProductListService productListService, ProductHelper productHelper,
        ProductListHelper productListHelper, ProductService productService, UrlRewrites urlRewrites,
        UrlRewriteHelper urlRewriteHelper, ProductListUrlHelper productListUrlHelper,
        ElasticsearchHelper elasticsearchHelper, ElasticsearchService elasticsearchService,
        AttributeService attributeService) {
        this.service = service;
        this.productListService = productListService;
        this.productHelper = productHelper;
        this.productListHelper = productListHelper;
        this.productService = productService;
        this.urlRewrites = urlRewrites;
        this.urlRewriteHelper = urlRewriteHelper;
        this.productListUrlHelper = productListUrlHelper;
        this.elasticsearchHelper = elasticsearchHelper;
        this.elasticsearchService = elasticsearchService;
        this.attributeService = attributeService;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getProductLists(@FilterParam Filter filter) {
        return ok(service.get(ProductList.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ProductList getProductList(@PathParam("id") Id id) {
        return checked(service.get(ProductList.class, id));
    }

    @GET
    @Path("/filters")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getProductListFilterRules() {
        return ok(service.get(ProductListFilterRule.class, (Map<String, Object>) null));
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createProductList(Update update) {
        String queryNodeJson = (String) update.getFields().get("queryNode");
        Map<String, Object> queryNodeMap = Json.fromJson(queryNodeJson, HashMap.class);
        update.getFields().remove("queryNode");
        // update.getFields().put("queryNode", queryNodeMap);
        QueryNode queryNode = app.model(QueryNode.class);
        queryNode.fromMap(queryNodeMap);

        ProductList p = app.model(ProductList.class);
        p.fromMap(update.getFields());
        p.setQueryNode(queryNode);
        productListHelper.fixProductListQuery(p);

        p.putAttributes(update.getAttributes());
        p.setOptionAttributes(update.getOptions());

        p.setQuery(toQuery(p));
        setProductListKey(p);
        p = service.create(p);
        return created(p);
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ProductList updateProductList(@PathParam("id") Id id, Update update) {
        if (id != null && update != null) {
            String queryNodeJson = (String) update.getFields().get("queryNode");
            Map<String, Object> queryNodeMap = Json.fromJson(queryNodeJson, HashMap.class);
            update.getFields().remove("queryNode");
            // update.getFields().put("queryNode", queryNodeMap);
            QueryNode queryNode = app.model(QueryNode.class);
            queryNode.fromMap(queryNodeMap);

            ProductList p = checked(service.get(ProductList.class, id));

            p.getAttributes().clear();
            p.set(update.getFields());
            p.setQueryNode(queryNode);
            productListHelper.fixProductListQuery(p);
            p.putAttributes(update.getAttributes());
            p.setOptionAttributes(update.getOptions());
            p.setQuery(toQuery(p));

            setProductListKey(p);
            service.update(p);
        }
        return checked(service.get(ProductList.class, id));
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeProductList(@PathParam("id") Id id) {
        if (id != null) {
            ProductList p = checked(service.get(ProductList.class, id));

            if (p != null) {
                service.remove(p);
            }
        }
    }

    private void setProductListKey(ProductList productList) {
        String defaultLanguage = app.cpStr_(ConfigurationKey.I18N_CPANEL_DEFAULT_EDIT_LANGUAGE);
        if (productList.getKey() == null || productList.getKey().isEmpty()) {
            if (productList.getLabel() != null) {
                String name = productList.getLabel().getClosestValue(defaultLanguage);
                if (name != null && !name.isEmpty()) {
                    productList.setKey(Strings.slugify(name));
                }
            }
        }
    }

    /*
     * @GET
     * 
     * @Path("{id}/images")
     * 
     * @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML}) public
     * Response getProductListImages(@PathParam("id") Id id) { ProductList
     * productList = productListService.getProductList(id);
     * ProductListFilterRule filterRule = productList.getFilterRule();
     * 
     * Map<String, Object> queryMap = null; if (productList.getQuery() != null
     * && !productList.getQuery().isEmpty()) { queryMap =
     * Json.fromJson(productList.getQuery(), HashMap.class); }
     * 
     * SearchResult productListResult =
     * elasticsearchService.findItems(Product.class, queryMap, null, 0, 100,
     * null); List<Product> products = new ArrayList<>(); List<HashMap<String,
     * Object>> urls = new ArrayList<>();
     * 
     * // If product document-ids have been returned, fetch their respective
     * products. if (productListResult != null &&
     * productListResult.getDocumentIds() != null &&
     * productListResult.getDocumentIds().size() > 0) { Id[] productIds =
     * elasticsearchHelper.toIds(productListResult.getDocumentIds().toArray());
     * products = orderByProductIds(productService.getProducts(productIds),
     * productIds);
     * 
     * String webPath =
     * app.cpStr_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_WEBPATH); String domain
     * = app.cpStr_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_SUBDOMAIN); String
     * baseUrl = "http://" + domain + webPath; for (Product product : products)
     * { HashMap<String, Object> url = new HashMap<>(); url.put("id",
     * product.getId()); String finalUrl; if (product.getMainImageURI() == null)
     * { finalUrl = baseUrl + "/produkty/no_image.jpg"; } else { finalUrl =
     * "http://" + product.getMainImageURI(); } String urlParams =
     * "___s:180x200"; finalUrl =
     * finalUrl.replaceFirst("(.*)\\.(jpg|jpeg|png|gif)$", "$1" + urlParams +
     * ".$2"); url.put("url", finalUrl); urls.add(url); } }
     * 
     * return ok(checked(urls)); }
     */

    /*
     * @GET
     * 
     * @Path("{id}/products")
     * 
     * @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML}) public
     * Response getProductListProducts(@PathParam("id") Id id) { ProductList
     * productList = productListService.getProductList(id); List<Product>
     * products = new ArrayList<>(); if (productList.getQuery() != null &&
     * !productList.getQuery().isEmpty()) { ProductListFilterRule filterRule =
     * productList.getFilterRule();
     * 
     * Map<String, Object> queryMap = null; if (productList.getQuery() != null
     * && !productList.getQuery().isEmpty()) { queryMap =
     * Json.fromJson(productList.getQuery(), HashMap.class); }
     * 
     * SearchResult productListResult = null; try { productListResult =
     * productListService.findProducts(productList, queryMap, null, 0, 100,
     * null); } catch (Exception ex) { System.out.println(ex.getMessage()); }
     * 
     * // If product document-ids have been returned, fetch their respective
     * products. if (productListResult != null &&
     * productListResult.getDocumentIds() != null &&
     * productListResult.getDocumentIds().size() > 0) { Id[] productIds =
     * elasticsearchHelper.toIds(productListResult.getDocumentIds().toArray());
     * products = orderByProductIds(productService.getProducts(productIds),
     * productIds); }
     * 
     * } return ok(checked(products)); }
     */

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

    private String toQuery(ProductList productList) {
        List<AttributeValue> attributeValues = productList.getAttributes();
        HashMap<String, Object> queryMap = new HashMap<>();
        if (productList.isSale()) {
            queryMap.put("is_sale", true);
        }
        if (productList.isSpecial()) {
            queryMap.put("is_special", true);
        }
        if (queryMap.size() == 0)
            return null;
        return Json.toJson(queryMap);
    }

    @SuppressWarnings("unchecked")
    @POST
    @Path("{id}/url/validation")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getIsUrlUnique(@PathParam("id") Id id, Update update) {
        ContextObject<String> urls = (ContextObject<String>) update.getFields().get("rewriteUrl");
        return ok(checked(urlRewriteHelper.isUriUnique(ObjectType.PRODUCT_LIST, id, urls)));
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
    public Response getRewriteUrl(@PathParam("id") Id productListId) {
        UrlRewrite urlRewrite = urlRewrites.forProductList(productListId);
        if (urlRewrite == null) {
            urlRewrite = app.model(UrlRewrite.class);
        }
        return ok(checked(urlRewrite));
    }

    @SuppressWarnings("unchecked")
    @PUT
    @Path("{id}/url")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateRewriteUrl(@PathParam("id") Id productListId, Update update) {
        boolean autoGenerate = Boolean.parseBoolean((String) update.getFields().get("auto"));
        boolean empty = autoGenerate ? false : true;
        ContextObject<String> urls = (ContextObject<String>) update.getFields().get("rewriteUrl");

        ProductList productList = checked(service.get(ProductList.class, productListId));
        UrlRewrite urlRewrite = urlRewrites.forProductList(productListId);
        if (urlRewrite == null) {
            urlRewrite = app.model(UrlRewrite.class);
            urlRewrite.setRequestURI(new ContextObject<String>());
            urlRewrite.setEnabled(true);
            urlRewrite.setRequestMethod("GET");
            urlRewrite.setTargetObjectId(productListId);
            urlRewrite.setTargetObjectType(ObjectType.PRODUCT_LIST);
            urlRewrite.setTargetURL("/catalog/product-list/view/" + productListId);
        }

        urlRewrite.setRequestURI(urls);

        if (urlRewrite.getRequestURI() == null)
            urlRewrite.setRequestURI(new ContextObject<String>());

        productListUrlHelper.generateUniqueUri(productList, urlRewrite, empty);
        if (urlRewrite.getId() == null)
            urlRewrites.add(urlRewrite);
        else
            urlRewrites.update(urlRewrite);

        if (urlRewrite != null && (urlRewrite.getRequestURI() == null || urlRewrite.getRequestURI().size() == 0)
            && urlRewrite.getId() != null) {
            urlRewrites.remove(urlRewrite);
        }

        return ok(checked(urlRewrite));
    }

}
