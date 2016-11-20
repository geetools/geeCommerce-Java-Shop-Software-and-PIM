package com.geecommerce.coupon.model;

import com.geecommerce.core.service.api.Model;

public interface CouponRangeDiscountAmount extends Model {

    public Double getDiscountAmount();

    public CouponRangeDiscountAmount setDiscountAmount(Double amount);

    public Double getFromAmount();

    public CouponRangeDiscountAmount setFromAmount(Double fromAmount);

    public Double getToAmount();

    public CouponRangeDiscountAmount setToAmount(Double toAmount);

    static final class Col {

        public static final String DISCOUNT_AMOUNT = "dsc_amount";
        public static final String FROM_AMOUNT = "from_amount";
        public static final String TO_AMOUNT = "to_amount";
    }
}
