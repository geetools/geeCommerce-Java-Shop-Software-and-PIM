package com.geecommerce.cart.widget;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductNavigationIndex;
import com.geecommerce.catalog.product.repository.ProductLists;
import com.geecommerce.catalog.product.repository.ProductNavigationIndexes;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.google.inject.Inject;

/**
 * Created by korsar on 18.12.2015.
 */

@Widget(name = "product_category_widget")
public class ProductCategoryWidget extends AbstractWidgetController implements WidgetController {

    private final String PARAM_PRODUCT = "product";

    private final ProductLists productLists;
    private final ProductNavigationIndexes productNavigationIndexes;
    private final ProductService productService;

    Product product = null;
    String productId = null;

    @Inject
    public ProductCategoryWidget(ProductLists productLists, ProductNavigationIndexes productNavigationIndexes,
        ProductService productService) {
        this.productLists = productLists;
        this.productNavigationIndexes = productNavigationIndexes;
        this.productService = productService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {

        ProductList productList = null;
        productId = widgetCtx.getParam(PARAM_PRODUCT);
        String category = "";

        product = productService.getProduct(Id.parseId(productId));

        if (productList == null) {
            List<ProductNavigationIndex> pniList = productNavigationIndexes.forProduct(product);
            if (pniList != null && pniList.size() > 0) {
                ProductNavigationIndex pni = pniList.get(0);
                Id productListId = pni.getProductListId();

                if (productListId != null)
                    productList = productLists.findById(ProductList.class, productListId);
            }
        }
        if (productList != null) {
            category = String.valueOf(productList.getLabel().getVal());
        }

        widgetCtx.renderContent(category);
    }

}
