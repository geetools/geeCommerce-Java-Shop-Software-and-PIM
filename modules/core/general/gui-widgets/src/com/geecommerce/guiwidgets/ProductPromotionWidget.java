package com.geecommerce.guiwidgets;

import com.google.inject.Inject;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.guiwidgets.model.ProductPromotion;
import com.geecommerce.guiwidgets.model.Slide;
import com.geecommerce.guiwidgets.model.WebSlideShow;
import com.geecommerce.guiwidgets.service.ProductPromotionService;
import com.geecommerce.guiwidgets.service.WebSlideShowService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Widget(name = "product_promotion", cms = false)
public class ProductPromotionWidget extends AbstractWidgetController implements WidgetController {
    private final ProductPromotionService productPromotionService;
    private final String PARAM_KEY = "key";
    private final String PARAM_TEMPLATE = "template";

    @Inject
    public ProductPromotionWidget(ProductPromotionService productPromotionService) {
	this.productPromotionService = productPromotionService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
	String promotionKey = widgetCtx.getParam(PARAM_KEY);
	widgetCtx.setParam("productPromotionKey", promotionKey);
	String promotionTemplate = widgetCtx.getParam(PARAM_TEMPLATE);
	if (promotionKey != null && !promotionKey.isEmpty() && promotionTemplate != null && !promotionTemplate.isEmpty()) {
	    List<ProductPromotion> productPromotions = productPromotionService.getProductPromotionByKey(promotionKey);
	    if (productPromotions != null && productPromotions.size() != 0) {
		ProductPromotion productPromotion = productPromotions.get(0);
		List<Product> products = productPromotionService.getProducts(productPromotion);
		if (products != null && products.size() != 0) {
		    widgetCtx.setParam("products", products);

		}
		widgetCtx.setParam("productPromotion", productPromotion);
	    }
	}
	widgetCtx.render("product_promotion/" + promotionTemplate);
    }
}