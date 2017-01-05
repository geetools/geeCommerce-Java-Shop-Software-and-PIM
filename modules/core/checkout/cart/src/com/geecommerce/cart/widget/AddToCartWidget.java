package com.geecommerce.cart.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

@Widget(name = "add_to_cart", js = true)
public class AddToCartWidget extends AbstractWidgetController implements WidgetController {
    private final String PARAM_PRODUCT_ID = "product_id";
    private final String PARAM_PICKUP_STORE = "pickup_store";
    private final String PARAM_PICKUP_ZIP = "pickup_zip";

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        String productIdParam = widgetCtx.getParam(PARAM_PRODUCT_ID);
        String pickupStore = widgetCtx.getParam(PARAM_PICKUP_STORE);
        String pickupZip = widgetCtx.getParam(PARAM_PICKUP_ZIP);

        if (productIdParam != null && !productIdParam.isEmpty()) {
            widgetCtx.setParam("productId", productIdParam);
            widgetCtx.setParam("pickupStore", pickupStore);
            widgetCtx.setParam("pickupZip", pickupZip);

            widgetCtx.setJsParam("productId", productIdParam);
        }

        widgetCtx.render();
    }
}
