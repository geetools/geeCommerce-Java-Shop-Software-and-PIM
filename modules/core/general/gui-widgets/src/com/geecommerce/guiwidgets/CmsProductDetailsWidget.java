package com.geecommerce.guiwidgets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.google.inject.Inject;

@Widget(name = "cms_product_details", cms = true)
public class CmsProductDetailsWidget extends AbstractWidgetController implements WidgetController {

    private final String PARAM_MODE = "mode";
    private final String PARAM_PRODUCT = "product_id";
    private final ProductService productService;
    private final Products products;

    @Inject
    public CmsProductDetailsWidget(ProductService productService, Products products) {
        this.productService = productService;
        this.products = products;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        String mode = widgetCtx.getParam(PARAM_MODE);
        String productId = widgetCtx.getParam(PARAM_PRODUCT);
        if (!StringUtils.isBlank(productId)) {
            Id id = Id.parseId(productId);
            Product product = products.findById(Product.class, id);
            widgetCtx.setParam("wProduct", product);
        }
        if (StringUtils.isNotBlank(mode)) {
            widgetCtx.setParam("wMode", mode.toLowerCase());
        }
        widgetCtx.render();
    }
}
