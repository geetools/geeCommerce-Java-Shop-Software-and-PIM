package com.geecommerce.coupon.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationItem;
import com.geecommerce.calculation.model.CalculationItemDiscount;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.enums.CouponActionType;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponAction;
import com.geecommerce.coupon.model.CouponCode;

public class DefaultBuyXGetYSameCouponProcessor extends BaseCouponProcessor implements CouponProcessor {
    private boolean spreadDiscount = true;

    @Override
    public boolean canBeProcessed(CalculationContext calcCtx, CouponCode couponCode,
        CartAttributeCollection cartAttributeCollection) {
        if (nullCheck(couponCode)
            && couponCode.getCoupon().getCouponAction().getType().equals(CouponActionType.BUY_X_GET_Y_FREE_SAME)
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

        if (couponAction.getMaximumQtyApplyTo() != null) {
            couponAction.setMaximumQtyApplyTo(Integer.MAX_VALUE);
        }

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

        int appliedDiscountTo = 0;

        int xy = couponAction.getDiscountQtyStep().intValue() + couponAction.getDiscountAmount().intValue();

        Map<Id, Map<String, Object>> itemDiscounts = new HashMap<>();

        Integer maximumQtyApplyTo = couponAction.getMaximumQtyApplyTo();
        if (maximumQtyApplyTo == null)
            maximumQtyApplyTo = Integer.MAX_VALUE;

        while (appliedDiscountTo < maximumQtyApplyTo) {
            if (itemPrices.size() == 0)
                break;

            Id current = itemWithLowestPrice(itemPrices);

            if (itemCounts.get(current) < xy) {
                itemCounts.remove(current);
                itemPrices.remove(current);
                continue;
            } else {
                int xytimes = itemCounts.get(current) / xy;
                int applyDiscountTimes = xytimes;

                if (applyDiscountTimes > maximumQtyApplyTo - appliedDiscountTo) {
                    applyDiscountTimes = maximumQtyApplyTo - appliedDiscountTo;
                }

                Map<String, Object> itemDiscount = new HashMap<>();
                itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_ARTICLE_ID, current);

                if (spreadDiscount) {
                    itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_QUANTITY, applyDiscountTimes * xy);
                    itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_RATE, 100.0 / xy);
                } else {
                    itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_QUANTITY, applyDiscountTimes);
                    itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_RATE, 100.0);
                }

                setItemDiscountPrice(current, couponAction.getPriceTypeId(), itemDiscount);
                itemDiscounts.put(current, itemDiscount);

                itemCounts.remove(current);
                itemPrices.remove(current);
            }

        }
        calcCtx.setItemDiscounts(itemDiscounts);

    }

}
