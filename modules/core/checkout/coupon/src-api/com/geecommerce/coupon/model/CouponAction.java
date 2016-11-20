package com.geecommerce.coupon.model;

import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.enums.CouponActionType;
import com.geecommerce.coupon.enums.CouponDiscountOrder;

public interface CouponAction extends Model {

    public CouponActionType getType();

    public CouponAction setType(CouponActionType type);

    public Double getDiscountAmount();

    public CouponAction setDiscountAmount(Double amount);

    public List<Double> getDiscountAmounts();

    public CouponAction setDiscountAmounts(List<Double> amounts);

    public List<CouponRangeDiscountAmount> getRangeDiscountAmount();

    public CouponAction setRangeDiscountAmount(List<CouponRangeDiscountAmount> amount);

    public Double getDiscountQtyStep();

    public CouponAction setDiscountQtyStep(Double discountQtyStep);

    public Integer getMaximumQtyApplyTo();

    public CouponAction setMaximumQtyApplyTo(Integer maximumQtyApplyTo);

    public Boolean getFreeShipping();

    public CouponAction setFreeShipping(Boolean freeShipping);

    public CouponFilterNode getFilter();

    public CouponAction setFilter(CouponFilterNode filter);

    public Id getPriceTypeId();

    public CouponAction setPriceTypeId(Id priceTypeId);

    public CouponDiscountOrder getDiscountOrder();

    public CouponAction setDiscountOrder(CouponDiscountOrder couponDiscountOrder);

    static final class Col {
        public static final String TYPE = "type";
        public static final String DISCOUNT_AMOUNT = "dsc_amount";
        public static final String DISCOUNT_AMOUNTS = "dsc_amounts";
        public static final String RANGE_DISCOUNT_AMOUNT = "range_dsc_amount";
        public static final String DISCOUNT_QTY_STEP = "dsc_qty_step";
        public static final String MAX_QTY_APPLY_TO = "max_qty_apply";
        public static final String FREE_SHIPPING = "free_shipping";
        public static final String FILTER = "filter";
        public static final String PRICE_TYPE_ID = "prc_type_id";
        public static final String DISCOUNT_ORDER = "dsc_order";
    }
}
