package com.geecommerce.coupon.processor;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.ParamKey;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.enums.CouponActionType;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponAction;
import com.geecommerce.coupon.model.CouponCode;

import java.util.List;

public class DefaultRangeFixedCartCouponProcessor extends BaseCouponProcessor implements CouponProcessor {
    @Override
    public boolean canBeProcessed(CalculationContext calcCtx, CouponCode couponCode, CartAttributeCollection cartAttributeCollection) {
	if (nullCheck(couponCode) && couponCode.getCoupon().getCouponAction().getType().equals(CouponActionType.RANGE_FIXED_CART))
	    return true;
	return false;
    }

    @Override
    public void process(CalculationContext calcCtx, CouponCode couponCode, CartAttributeCollection cartAttributeCollection) {
	Coupon coupon = couponCode.getCoupon();
	CouponAction couponAction = coupon.getCouponAction();

	calcCtx.addParameter(ParamKey.CART_DISCOUNT_AMOUNT, couponAction.getRangeDiscountAmount());

	List<Id> itemIds = filterService.passFilter(cartAttributeCollection, coupon);
	if (itemIds == null || itemIds.size() == 0)
	    return;

	setDiscountPrices(calcCtx, couponAction, itemIds);
    }
}
