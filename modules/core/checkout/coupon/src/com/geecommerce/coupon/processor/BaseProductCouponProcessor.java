package com.geecommerce.coupon.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationItem;
import com.geecommerce.calculation.model.CalculationItemDiscount;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponAction;
import com.geecommerce.coupon.model.CouponCode;

public abstract class BaseProductCouponProcessor extends BaseCouponProcessor implements CouponProcessor {

    public abstract boolean canBeProcessed(CalculationContext calcCtx, CouponCode couponCode, CartAttributeCollection cartAttributeCollection);

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

        Integer applyDiscountTo = 0;
        Map<Id, Map<String, Object>> itemDiscounts = new HashMap<>();

        int discountQtyStep = couponAction.getDiscountQtyStep() != null ? couponAction.getDiscountQtyStep().intValue() : 0;
        int maxPossibleSteps = totalItems / (1 + discountQtyStep);

        if (couponAction.getMaximumQtyApplyTo() == null || couponAction.getMaximumQtyApplyTo() > maxPossibleSteps) {
            applyDiscountTo = maxPossibleSteps;
        } else {
            applyDiscountTo = couponAction.getMaximumQtyApplyTo();
        }

        while (applyDiscountTo != 0) {
            Id current = itemWithLowestPrice(itemPrices);

            Map<String, Object> itemDiscount = itemDiscounts.get(current);
            if (itemDiscount == null) {
                itemDiscount = new HashMap<>();
                itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_ARTICLE_ID, current);
                itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_QUANTITY, 0);
                setDiscount(itemDiscount, couponAction);
                setItemDiscountPrice(current, couponAction.getPriceTypeId(), itemDiscount);
                itemDiscounts.put(current, itemDiscount);
            }

            itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_QUANTITY, (Integer) itemDiscount.get(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_QUANTITY) + 1);
            applyDiscountTo -= 1; // 1 item;
            itemCounts.put(current, itemCounts.get(current) - 1);
            removeItemsByStep(itemCounts, itemPrices, discountQtyStep);
        }
        calcCtx.setItemDiscounts(itemDiscounts);
    }

    protected abstract void setDiscount(Map<String, Object> itemDiscount, CouponAction couponAction);

    private void removeItemsByStep(Map<Id, Integer> itemCounts, Map<Id, Double> itemPrices, int step) {
        Id current = itemWithLowestPrice(itemPrices);

        if (itemCounts.get(current) == 0) {
            itemCounts.remove(current);
            itemPrices.remove(current);
        }

        while (step != 0) {
            current = itemWithLowestPrice(itemPrices);
            if (itemCounts.get(current) <= step) {
                step -= itemCounts.get(current);
                itemCounts.remove(current);
                itemPrices.remove(current);
            } else {
                itemCounts.put(current, itemCounts.get(current) - step);
                step = 0;
            }
        }
    }
}
