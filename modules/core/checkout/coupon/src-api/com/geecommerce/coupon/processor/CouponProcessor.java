package com.geecommerce.coupon.processor;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.CouponCode;

public interface CouponProcessor {

    public boolean canBeProcessed(CalculationContext calcCtx, CouponCode couponCode,
        CartAttributeCollection cartAttributeCollection);

    public void process(CalculationContext calcCtx, CouponCode couponCode,
        CartAttributeCollection cartAttributeCollection);
}
