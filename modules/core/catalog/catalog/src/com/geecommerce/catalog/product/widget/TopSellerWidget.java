package com.geecommerce.catalog.product.widget;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.index.query.FilterBuilder;

import com.geecommerce.catalog.product.helper.ProductHelper;
import com.geecommerce.catalog.product.helper.ProductListHelper;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.service.ProductListService;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.elasticsearch.api.search.SearchResult;
import com.geecommerce.core.elasticsearch.search.SearchParams;
import com.geecommerce.core.elasticsearch.service.ElasticsearchService;
import com.geecommerce.core.system.attribute.TargetObjectCode;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.google.inject.Inject;

@Widget(name = "topseller")
public class TopSellerWidget extends AbstractWidgetController implements WidgetController {
    private final ProductService productService;
    private final ProductListService productListService;
    private final ElasticsearchService elasticsearchService;
    private final ProductHelper productHelper;
    private final ProductListHelper productListHelper;
    private final AttributeService attributeService;

    private final String CURRENT_PRODUCT_LIST_NAME = "ts";
    private final int DEFAULT_SIZE = 10;
    private final String TOP_SELLER_TRUE_QUERY = "{\"att_topSeller_hash\":\"___156041024830434188__\"}";

    @Inject
    public TopSellerWidget(ProductService productService, ProductListService productListService, ElasticsearchService elasticsearchService, ProductHelper productHelper, ProductListHelper productListHelper, AttributeService attributeService) {
        this.productService = productService;
        this.productListService = productListService;
        this.elasticsearchService = elasticsearchService;
        this.productHelper = productHelper;
        this.productListHelper = productListHelper;

        this.attributeService = attributeService;
    }

    @SuppressWarnings("unchecked")
    private Id[] getProductIds(WidgetContext widgetCtx) {
        Id[] productIds = null;
        String products = widgetCtx.getParam("products");

        if (products != null && !products.isEmpty()) {
            productIds = Id.toIds(products);

            if (productIds != null && productIds.length > 0)
                return productIds;
        }

        String sizeStr = widgetCtx.getParam("size");
        Integer size = DEFAULT_SIZE;

        if (sizeStr != null && !sizeStr.isEmpty()) {
            size = Integer.parseInt(sizeStr);
        }

        Map<String, Object> query = Json.fromJson(TOP_SELLER_TRUE_QUERY, HashMap.class);

        String productListIdStr = widgetCtx.getParam("productListId");

        ProductList productList = null;

        if (productListIdStr != null && !productListIdStr.isEmpty())
            productList = productListService.getProductList(Id.toId(productListIdStr));

        List<FilterBuilder> builders = productListHelper.getVisibilityFilters();
        builders.add(productListHelper.buildQuery(productList.getQueryNode()));

        // List<AttributeGroup> attributeGroups = new ArrayList<>();
        // attributeGroups.add(AttributeGroup.PRODUCT_FILTER);
        // attributeGroups.add(AttributeGroup.PRODUCT);
        Map<String, Attribute> filterAttributes = attributeService.getAttributesForSearchFilter(TargetObjectCode.PRODUCT_LIST, TargetObjectCode.PRODUCT_FILTER);

        SearchResult productListResult = elasticsearchService.findItems(Product.class, builders, filterAttributes, query, null, new SearchParams(), 0, size, "name");

        if (productListResult == null)
            return null;

        productIds = Id.toIds(productListResult.getDocumentIds().toArray());

        return productIds;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String view = widgetCtx.getParam("view");
        String widgetName = "topseller";
        if (view != null && !view.isEmpty()) {
            widgetName += "-" + view;
        }

        List<Product> topSellerProducts = null;
        Id[] productIds = getProductIds(widgetCtx);

        productHelper.rememberCurrentProductList(CURRENT_PRODUCT_LIST_NAME, Arrays.asList(productIds));

        if (productIds != null) {
            topSellerProducts = productService.getProducts(productIds);
        }

        widgetCtx.setParam("topSellers", topSellerProducts);

        widgetCtx.render(String.format("topseller/%s", widgetName));
    }
}
