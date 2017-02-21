package com.geecommerce.cart.controller;

import com.geecommerce.cart.helper.CartHelper;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.cart.service.CartService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.BaseController;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.model.CouponData;
import com.geecommerce.coupon.service.CouponService;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.Param;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmitr on 09.02.2017.
 */

@Controller
@Request("/coupon")
public class CouponController extends BaseController {

    private final CouponService couponService;
    private final CartHelper cartHelper;
    private final CartService cartService;

    @Inject
    public CouponController(CouponService couponService, CartHelper cartHelper, CartService cartService) {
        this.couponService = couponService;
        this.cartHelper = cartHelper;
        this.cartService = cartService;
    }


    @Request("add")
    public Result addCouponCode(@Param("couponCode") String couponCode) {
        CouponCode code = couponService.getCouponCode(couponCode);
        //Map<String, String> result = new HashMap<>();

        //TODO: notify user that coupon doesn't exist
        if (code == null) {
            //result.put("result", "unsuccess");
            //result.put("message", app.message("Coupon doesn't exist or not valid"));

        } else {
            Cart cart = cartHelper.getCart(true);
            CartAttributeCollection cartAttributeCollection = ((CouponData) cart).toCartAttributeCollection();

            if (couponService.isCouponApplicableToCart(code, cartAttributeCollection, true)) {
                cart.setCouponCode(code);
                cartService.updateCart(cart);
               // result.put("result", "success");
            } else {
               // result.put("result", "unsuccess");
               // result.put("message", app.message("Can't use coupon for this cart"));
            }
        }

        return redirect("/cart/view/");
    }

    @Request("remove")
    public Result removeCouponCode() {
        Cart cart = cartHelper.getCart(true);
        cart.setCouponCode(null);
        cartService.updateCart(cart);

        return redirect("/cart/view/");
    }


    @Request("set-autocoupon")
    public Result setAutoCoupon(@Param("couponId") Id couponId) {
        CouponCode couponCode = couponService.getCouponCode(couponId);

        if (couponCode != null && couponCode.getCoupon() != null && couponCode.getCoupon().getAuto() != null &&
                couponCode.getCoupon().getAuto()) {
            Cart cart = cartHelper.getCart(true);
            cart.setUseAutoCoupon(true);
            cart.setCouponCode(couponCode);
            cartService.updateCart(cart);
        }

        return redirect("/cart/view/");
    }

    @Request("switch-autocoupon")
    public Result switchAutoCoupon(@Param("couponId") Id couponId) {
        Cart cart = cartHelper.getCart(true);
        cart.setCouponCode(null);
        cart.setUseAutoCoupon(!cart.getUseAutoCoupon());
        if(!cart.getUseAutoCoupon() && cart.getCouponCode() != null && cart.getCouponCode().getCoupon().getAuto())
            cart.setCouponCode(null);

        cartService.updateCart(cart);

        return redirect("/cart/view/");
    }

}

