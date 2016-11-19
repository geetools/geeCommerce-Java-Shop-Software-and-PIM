package com.geecommerce.coupon.model;

import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Cacheable
@Model
public class DefaultCouponRangeDiscountAmount extends AbstractModel implements CouponRangeDiscountAmount {

    @Column(Col.DISCOUNT_AMOUNT)
    public Double discountAmount;
    @Column(Col.FROM_AMOUNT)
    public Double fromAmount;
    @Column(Col.TO_AMOUNT)
    public Double toAmount;

    @Override
    public Double getDiscountAmount() {
        return discountAmount;
    }

    @Override
    public CouponRangeDiscountAmount setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    @Override
    public Double getFromAmount() {
        return fromAmount;
    }

    @Override
    public CouponRangeDiscountAmount setFromAmount(Double fromAmount) {
        this.fromAmount = fromAmount;
        return this;
    }

    @Override
    public Double getToAmount() {
        return toAmount;
    }

    @Override
    public CouponRangeDiscountAmount setToAmount(Double toAmount) {
        this.toAmount = toAmount;
        return this;
    }

    @Override
    public Id getId() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        map = normalize(map);
        this.discountAmount = double_(map.get(Col.DISCOUNT_AMOUNT));
        this.fromAmount = double_(map.get(Col.FROM_AMOUNT));
        this.toAmount = double_(map.get(Col.TO_AMOUNT));

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = Maps.newLinkedHashMap(super.toMap());
        ;
        m.put(Col.DISCOUNT_AMOUNT, getDiscountAmount());
        m.put(Col.FROM_AMOUNT, getFromAmount());
        m.put(Col.TO_AMOUNT, getToAmount());
        return m;
    }
}
