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

@Widget(name = "cross_sell")
public class CrossSellsWidget extends AbstractWidgetController implements WidgetController {
    private final ProductService productService;
    private final String PARAM_NAME = "product_id";

    @Inject
    public CrossSellsWidget(ProductService productService) {
	this.productService = productService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
	String productIdString = widgetCtx.getParam(PARAM_NAME);

	if (productIdString != null) {
	    Product product = productService.getProduct(new Id(productIdString));

	    if (product != null && product.hasCrossSells()) {
		widgetCtx.setParam("crossSellProducts", product.getCrossSells());
	    }
	}

	widgetCtx.render("cross_sell/cross_sell");
    }
}
