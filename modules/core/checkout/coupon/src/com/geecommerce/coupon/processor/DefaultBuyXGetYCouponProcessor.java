package com.geecommerce.coupon.processor;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationItem;
import com.geecommerce.calculation.model.CalculationItemDiscount;
import com.geecommerce.calculation.model.ParamKey;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.enums.CouponActionType;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponAction;
import com.geecommerce.coupon.model.CouponCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultBuyXGetYCouponProcessor extends BaseCouponProcessor implements CouponProcessor {
    @Override
    public boolean canBeProcessed(CalculationContext calcCtx, CouponCode couponCode, CartAttributeCollection cartAttributeCollection) {
	if (nullCheck(couponCode) && couponCode.getCoupon().getCouponAction().getType().equals(CouponActionType.BUY_X_GET_Y_FREE) && couponCode.getCoupon().getCouponAction().getDiscountQtyStep() != null
		&& couponCode.getCoupon().getCouponAction().getDiscountAmount() != null)
	    return true;
	return false;
    }

    @Override
    public void process(CalculationContext calcCtx, CouponCode couponCode, CartAttributeCollection cartAttributeCollection) {
	Coupon coupon = couponCode.getCoupon();
	CouponAction couponAction = coupon.getCouponAction();

	List<Id> itemIds = filterService.passFilter(cartAttributeCollection, coupon);
	if (itemIds == null || itemIds.size() == 0)
	    return;

	Integer totalItems = 0;
	Map<Id, Integer> itemCounts = new HashMap<>();
	Map<Id, Double> itemPrices = new HashMap<>();
	for (Id itemId : itemIds) {
	    Map<String, Object> item = getItem(calcCtx, itemId);
	    itemCounts.put(itemId, (Integer) item.get(CalculationItem.FIELD.ITEM_QUANTITY));
	    itemPrices.put(itemId, (Double) item.get(CalculationItem.FIELD.ITEM_BASE_CALCULATION_PRICE));
	    totalItems += (Integer) item.get(CalculationItem.FIELD.ITEM_QUANTITY);
	}

	int applyDiscountTo = getMaxApplyCount(couponAction, totalItems);

	Map<Id, Map<String, Object>> itemDiscounts = new HashMap<>();
	while (applyDiscountTo != 0) {
	    Id current = itemWithLowestPrice(itemPrices);
	    Map<String, Object> itemDiscount = new HashMap<>();
	    itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_ARTICLE_ID, current);
	    if (itemCounts.get(current) <= applyDiscountTo) {
		applyDiscountTo -= itemCounts.get(current);
		itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_QUANTITY, itemCounts.get(current));
	    } else {
		itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_QUANTITY, applyDiscountTo);
		applyDiscountTo = 0;
	    }
	    itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_RATE, 100.0);
	    setItemDiscountPrice(current, couponAction.getPriceTypeId(), itemDiscount);
	    itemPrices.remove(current);
	    itemDiscounts.put(current, itemDiscount);
	}
	calcCtx.setItemDiscounts(itemDiscounts);

    }

    private int getMaxApplyCount(CouponAction couponAction, int totalItems) {
	int xy = couponAction.getDiscountQtyStep().intValue() + couponAction.getDiscountAmount().intValue();
	int xytimes = totalItems / xy;
	int xymod = totalItems % xy;
	int applyDiscountTo = xytimes * couponAction.getDiscountAmount().intValue();
	if (xymod > couponAction.getDiscountQtyStep()) {
	    applyDiscountTo += xymod - couponAction.getDiscountQtyStep();
	}
	if (couponAction.getMaximumQtyApplyTo() != null && applyDiscountTo > couponAction.getMaximumQtyApplyTo())
	    applyDiscountTo = couponAction.getMaximumQtyApplyTo();

	return applyDiscountTo;
    }
}
