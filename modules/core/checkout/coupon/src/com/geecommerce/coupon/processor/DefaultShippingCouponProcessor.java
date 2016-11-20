package com.geecommerce.coupon.processor;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.ParamKey;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.CouponCode;

public class DefaultShippingCouponProcessor implements CouponProcessor {
    @Override
    public boolean canBeProcessed(CalculationContext calcCtx, CouponCode couponCode,
        CartAttributeCollection cartAttributeCollection) {
        if (couponCode != null && couponCode.getCoupon() != null && couponCode.getCoupon().getCouponAction() != null
            && couponCode.getCoupon().getCouponAction().getFreeShipping() != null
            && couponCode.getCoupon().getCouponAction().getFreeShipping())
            return true;
        return false;
    }

    @Override
    public void process(CalculationContext calcCtx, CouponCode couponCode,
        CartAttributeCollection cartAttributeCollection) {
        calcCtx.addParameter(ParamKey.SHIPPING_DISCOUNT_RATE, 100.0);
    }
}
