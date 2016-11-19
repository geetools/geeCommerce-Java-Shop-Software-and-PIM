package com.geecommerce.wishlist.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.wishlist.service.WishListService;
import com.google.inject.Inject;

@Widget(name = "add_to_wishlist")
public class AddToWishListWidget extends AbstractWidgetController implements WidgetController {
    private final String PARAM_PRODUCT_ID = "product_id";
    private WishListService wishListService = null;

    @Inject
    public AddToWishListWidget(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String productIdParam = widgetCtx.getParam(PARAM_PRODUCT_ID);
        if (productIdParam != null && !productIdParam.isEmpty())
            widgetCtx.setParam("productId", productIdParam);

        if (app.isCustomerLoggedIn()) {
            widgetCtx.setParam("wishLists", wishListService.getWishLists(((Customer) app.getLoggedInCustomer()).getId()));
        }
        widgetCtx.render("wishlist/add_to_wishlist");
    }
}
