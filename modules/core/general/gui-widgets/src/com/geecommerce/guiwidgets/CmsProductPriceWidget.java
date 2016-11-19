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

@Widget(name = "cms_product_price", cms = true, css = true)
public class CmsProductPriceWidget extends AbstractWidgetController implements WidgetController {

    private final String PARAM_PRODUCT = "product_id";
    private final ProductService productService;

    @Inject
    public CmsProductPriceWidget(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String productId = widgetCtx.getParam(PARAM_PRODUCT);
        if (!StringUtils.isBlank(productId)) {
            Id id = Id.parseId(productId);
            Product product = productService.getProduct(id);
            widgetCtx.setParam("wProduct", product);
        }

        widgetCtx.render();
    }
}
