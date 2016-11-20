package com.geecommerce.catalog.product.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.google.inject.Inject;

@Widget(name = "upsell", js = true, css = true)
public class UpsellWidget extends AbstractWidgetController implements WidgetController {
    private final ProductService productService;
    private static final String PARAM_NAME = "product_id";

    @Inject
    public UpsellWidget(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        String productIdString = widgetCtx.getParam(PARAM_NAME);

        if (productIdString != null) {
            Product product = productService.getProduct(new Id(productIdString));

            if (product != null && product.hasUpsells()) {
                widgetCtx.setParam("upsellProducts", product.getUpsells());
            }
        }

        widgetCtx.render();
    }
}
