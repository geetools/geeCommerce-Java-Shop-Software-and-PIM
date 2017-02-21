package com.geecommerce.cart.widget;

import com.geecommerce.cart.helper.CartHelper;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.model.CouponData;
import com.geecommerce.coupon.service.CouponService;
import com.google.inject.Inject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by dmitr on 09.02.2017.
 */
@Widget(name = "coupons", js = true, css = true)
public class CouponsWidget extends AbstractWidgetController implements WidgetController {

    private final CouponService couponService;
    private final CartHelper cartHelper;

    @Inject
    public CouponsWidget(CouponService couponService, CartHelper cartHelper) {
        this.couponService = couponService;
        this.cartHelper = cartHelper;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        Cart cart = cartHelper.getCart();
        CartAttributeCollection cartAttributeCollection = ((CouponData) cart).toCartAttributeCollection();
        List<CouponCode> autoCoupons = couponService.getAutoCoupons(cartAttributeCollection);

        widgetCtx.setParam("wCart", cart);
        widgetCtx.setParam("wAutoCoupons", autoCoupons);

        widgetCtx.render();
    }
}
