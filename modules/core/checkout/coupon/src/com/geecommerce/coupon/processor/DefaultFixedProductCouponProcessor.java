package com.geecommerce.coupon.processor;

import java.util.Map;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationItemDiscount;
import com.geecommerce.coupon.enums.CouponActionType;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.CouponAction;
import com.geecommerce.coupon.model.CouponCode;

public class DefaultFixedProductCouponProcessor extends BaseProductCouponProcessor implements CouponProcessor {
    @Override
    public boolean canBeProcessed(CalculationContext calcCtx, CouponCode couponCode,
        CartAttributeCollection cartAttributeCollection) {
        if (nullCheck(couponCode)
            && couponCode.getCoupon().getCouponAction().getType().equals(CouponActionType.FIXED_PRODUCT))
            return true;
        return false;
    }

    @Override
    protected void setDiscount(Map<String, Object> itemDiscount, CouponAction couponAction) {
        itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_AMOUNT, couponAction.getDiscountAmount());
    }

}
