package com.geecommerce.catalog.product.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

@Widget(name = "product")
public class ProductWidget extends AbstractWidgetController implements WidgetController {
    private final ProductService productService;

    @Inject
    public ProductWidget(ProductService productService) {
	this.productService = productService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
	Id productId = widgetCtx.getParam("id", Id.class);
	String varName = widgetCtx.getParam("var", String.class, "productObj");

	if (productId != null) {
	    Product p = productService.getProduct(productId);

	    if (p != null)
		widgetCtx.setParam(varName, p);
	}

	widgetCtx.invokeBody(widgetCtx.getOut());
    }
}
