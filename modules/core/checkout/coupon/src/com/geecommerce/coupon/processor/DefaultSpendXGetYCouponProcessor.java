package com.geecommerce.coupon.processor;

import java.util.List;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.ParamKey;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.enums.CouponActionType;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponAction;
import com.geecommerce.coupon.model.CouponCode;

public class DefaultSpendXGetYCouponProcessor extends BaseCouponProcessor implements CouponProcessor {
    @Override
    public boolean canBeProcessed(CalculationContext calcCtx, CouponCode couponCode,
        CartAttributeCollection cartAttributeCollection) {
        if (nullCheck(couponCode)
            && couponCode.getCoupon().getCouponAction().getType().equals(CouponActionType.SPEND_X_GET_Y_FREE)
            && couponCode.getCoupon().getCouponAction().getDiscountQtyStep() != null
            && couponCode.getCoupon().getCouponAction().getDiscountAmount() != null)
            return true;
        return false;
    }

    @Override
    public void process(CalculationContext calcCtx, CouponCode couponCode,
        CartAttributeCollection cartAttributeCollection) {
        Coupon coupon = couponCode.getCoupon();
        CouponAction couponAction = coupon.getCouponAction();

        calcCtx.addParameter(ParamKey.DISCOUNT_SPEND_X, couponAction.getDiscountQtyStep());
        calcCtx.addParameter(ParamKey.DISCOUNT_GET_Y, couponAction.getDiscountAmount());
        calcCtx.addParameter(ParamKey.DISCOUNT_TIMES_APPLY, couponAction.getMaximumQtyApplyTo());

        List<Id> itemIds = filterService.passFilter(cartAttributeCollection, coupon);
        if (itemIds == null || itemIds.size() == 0)
            return;

        setDiscountPrices(calcCtx, couponAction, itemIds);
    }
}
