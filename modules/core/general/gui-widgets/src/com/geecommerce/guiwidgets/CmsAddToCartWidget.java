package com.geecommerce.guiwidgets;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Widget(name = "cms_add_to_cart", js = true, cms = true, css = false)
public class CmsAddToCartWidget  extends AbstractWidgetController implements WidgetController {
    private final String PARAM_PRODUCT_ID = "product_id";

    private final ProductService productService;

    @Inject
    public CmsAddToCartWidget(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String productId = widgetCtx.getParam(PARAM_PRODUCT_ID);

        if (!StringUtils.isBlank(productId)) {
            Id id = Id.parseId(productId);
            Product product = productService.getProduct(id);
            widgetCtx.setParam("wProduct", product);

            widgetCtx.setJsParam("productId", product.getId());
            widgetCtx.setJsParam("bundle", product.isBundle());
        } else {
            if(widgetCtx.getParam("product") != null && widgetCtx.getParam("product") instanceof Product){
                Product product = (Product) widgetCtx.getParam("product");

                widgetCtx.setJsParam("productId", product.getId());
                widgetCtx.setJsParam("bundle", product.isBundle());
            }
        }

        widgetCtx.render();
    }
}
