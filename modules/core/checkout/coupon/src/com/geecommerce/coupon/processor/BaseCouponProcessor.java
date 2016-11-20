package com.geecommerce.coupon.processor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationItem;
import com.geecommerce.calculation.model.CalculationItemDiscount;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.App;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.CouponAction;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.service.FilterService;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.model.PriceType;
import com.geecommerce.price.repository.PriceTypes;
import com.google.inject.Inject;

public class BaseCouponProcessor {
    @Inject
    protected App app;

    @Inject
    protected ProductService productService;

    @Inject
    protected PriceTypes priceTypes;

    @Inject
    protected FilterService filterService;

    protected boolean nullCheck(CouponCode couponCode) {
        if (couponCode != null && couponCode.getCoupon() != null && couponCode.getCoupon().getCouponAction() != null
            && couponCode.getCoupon().getCouponAction().getFreeShipping() != null
            && couponCode.getCoupon().getCouponAction().getType() != null)
            return true;
        return false;
    }

    protected void setItemDiscountPrice(Id productId, Id priceTypeId, Map<String, Object> itemDiscount) {
        Product product = productService.getProduct(productId);
        if (priceTypeId == null) {
            Price price = product.getPrice().getFinalPriceFor(1);
            itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_BASE_CALCULATION_PRICE, price.getFinalPrice());
            itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_BASE_CALCULATION_PRICE_TYPE,
                price.getPriceType().getCode());
        } else {
            PriceType priceType = priceTypes.findById(PriceType.class, priceTypeId);
            itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_BASE_CALCULATION_PRICE,
                product.getPrice().getPrice(priceType.getCode(), 1));
            itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_BASE_CALCULATION_PRICE_TYPE,
                priceType.getCode());
        }
    }

    protected void setDiscountPrices(CalculationContext calcCtx, CartAttributeCollection cartAttributeCollection,
        CouponAction couponAction) {
        Set<Id> allProducts = cartAttributeCollection.getProductAttributes().keySet();
        setDiscountPrices(calcCtx, couponAction, allProducts);
    }

    protected void setDiscountPrices(CalculationContext calcCtx, CouponAction couponAction, Collection<Id> products) {
        Map<Id, Map<String, Object>> itemDiscounts = new HashMap<>();
        for (Id id : products) {
            Map<String, Object> item = getItem(calcCtx, id);
            Map<String, Object> itemDiscount = new HashMap<>();
            itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_ARTICLE_ID, id);
            itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_QUANTITY,
                (Integer) item.get(CalculationItem.FIELD.ITEM_QUANTITY));
            itemDiscount.put(CalculationItemDiscount.FIELD.DISCOUNT_ITEM_RATE, 0.0);
            setItemDiscountPrice(id, couponAction.getPriceTypeId(), itemDiscount);
            itemDiscounts.put(id, itemDiscount);
        }
        calcCtx.setItemDiscounts(itemDiscounts);
    }

    protected Map<String, Object> getItem(CalculationContext calcCtx, Id id) {
        for (Map<String, Object> item : calcCtx.getItems()) {
            if (item.get(CalculationItem.FIELD.ITEM_ARTICLE_ID).equals(id))
                return item;
        }
        return null;
    }

    protected Id itemWithLowestPrice(Map<Id, Double> itemPrices) {
        Double lowestPrice = Double.MAX_VALUE;
        Id lowestId = null;
        for (Id id : itemPrices.keySet()) {
            if (itemPrices.get(id) < lowestPrice) {
                lowestId = id;
                lowestPrice = itemPrices.get(id);
            }
        }
        return lowestId;
    }

    protected Id itemWithBiggestPrice(Map<Id, Double> itemPrices) {
        Double biggestPrice = Double.MIN_VALUE;
        Id biggestId = null;
        for (Id id : itemPrices.keySet()) {
            if (itemPrices.get(id) > biggestPrice) {
                biggestId = id;
                biggestPrice = itemPrices.get(id);
            }
        }
        return biggestId;
    }
}
