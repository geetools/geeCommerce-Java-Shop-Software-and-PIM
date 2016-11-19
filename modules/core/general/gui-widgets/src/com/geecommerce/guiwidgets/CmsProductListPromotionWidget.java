package com.geecommerce.guiwidgets;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Widget(name = "cms_product_list_promotion", cms = false, css = true)
public class CmsProductListPromotionWidget  extends AbstractWidgetController implements WidgetController {

    private final String PARAM_PRODUCT_LIST = "product_list_id";
    private final String PARAM_TITLE = "title";
    private final String PARAM_LIMIT = "limit";

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String productListId = widgetCtx.getParam(PARAM_PRODUCT_LIST);
        if (productListId != null && !productListId.isEmpty()){
            //List<Product> products = productPromotionService.getProducts(productPromotion);
        }


/*
* String promotionKey = widgetCtx.getParam(PARAM_KEY);
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
	}*/
    }
}
