package com.geecommerce.guiwidgets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.google.inject.Inject;

@Widget(name = "cms_product_variants", cms = true, css = true, js = true)
public class CmsProductVariantsWidget extends AbstractWidgetController implements WidgetController {

    private final String PARAM_PRODUCT = "product_id";
    private final ProductService productService;

    @Inject
    public CmsProductVariantsWidget(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String productId = widgetCtx.getParam(PARAM_PRODUCT);
        if (!StringUtils.isBlank(productId)) {
            Id id = Id.parseId(productId);
            Product product = productService.getProduct(id);
            widgetCtx.setParam("wProduct", product);
            widgetCtx.setJsParam("productId", productId);
        } else {
            if (widgetCtx.getParam("product") != null) {
                Product product = widgetCtx.getParam("product", Product.class);
                widgetCtx.setJsParam("productId", product.getId());
            }
            ;
        }

        widgetCtx.render();
    }
}
