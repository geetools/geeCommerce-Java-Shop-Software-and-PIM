package com.geecommerce.coupon.processor;

import java.util.Map;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationItemDiscount;
import com.geecommerce.coupon.enums.CouponActionType;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.CouponAction;
import com.geecommerce.coupon.model.CouponCode;

public class DefaultListFixedProductCouponProcessor extends BaseListProductCouponProcessor implements CouponProcessor {
    @Override
    public boolean canBeProcessed(CalculationContext calcCtx, CouponCode couponCode,
        CartAttributeCollection cartAttributeCollection) {
        if (nullCheck(couponCode)
            && couponCode.getCoupon().getCouponAction().getType().equals(CouponActionType.LIST_FIXED_PRODUCT))
            return true;
        return false;
    }

    @Override
    protected void setDiscount(Map<String, Object> itemDiscount, CouponAction couponAction, int itemIndex) {
        if ((Integer) itemDiscount.get(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_QUANTITY) == 0)
            itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_AMOUNT,
                couponAction.getDiscountAmounts().get(itemIndex));
        else {
            Double discountAmount = (Double) itemDiscount.get(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_AMOUNT);
            Integer discountedQuantity = (Integer) itemDiscount
                .get(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_QUANTITY);

            Double newDiscountAmount = (discountAmount * discountedQuantity
                + couponAction.getDiscountAmounts().get(itemIndex)) / (discountedQuantity + 1);

            itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_AMOUNT, newDiscountAmount);

        }
    }

}
