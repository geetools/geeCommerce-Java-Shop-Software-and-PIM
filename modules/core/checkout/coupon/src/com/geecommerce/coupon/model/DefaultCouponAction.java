package com.geecommerce.coupon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.enums.CouponActionType;
import com.geecommerce.coupon.enums.CouponDiscountOrder;
import com.google.common.collect.Maps;

@Cacheable
@Model
public class DefaultCouponAction extends AbstractModel implements CouponAction {

    @Column(Col.TYPE)
    public CouponActionType type;
    @Column(Col.DISCOUNT_AMOUNT)
    public Double discountAmount;
    @Column(Col.DISCOUNT_AMOUNTS)
    public List<Double> discountAmounts;
    @Column(Col.RANGE_DISCOUNT_AMOUNT)
    public List<CouponRangeDiscountAmount> rangeDiscountAmount;
    @Column(Col.DISCOUNT_QTY_STEP)
    public Double discountQtyStep;
    @Column(Col.MAX_QTY_APPLY_TO)
    public Integer maximumQtyApplyTo;
    @Column(Col.FREE_SHIPPING)
    public Boolean freeShipping;
    @Column(Col.FILTER)
    public CouponFilterNode filter;
    @Column(Col.PRICE_TYPE_ID)
    public Id priceTypeId;
    @Column(Col.DISCOUNT_ORDER)
    public CouponDiscountOrder couponDiscountOrder;

    @Override
    public CouponActionType getType() {
        return type;
    }

    @Override
    public CouponAction setType(CouponActionType type) {
        this.type = type;
        return this;
    }

    @Override
    public Double getDiscountAmount() {
        return discountAmount;
    }

    @Override
    public CouponAction setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    @Override
    public List<Double> getDiscountAmounts() {
        return discountAmounts;
    }

    @Override
    public CouponAction setDiscountAmounts(List<Double> amounts) {
        this.discountAmounts = amounts;
        return this;
    }

    @Override
    public List<CouponRangeDiscountAmount> getRangeDiscountAmount() {
        return rangeDiscountAmount;
    }

    @Override
    public CouponAction setRangeDiscountAmount(List<CouponRangeDiscountAmount> amount) {
        this.rangeDiscountAmount = amount;
        return this;
    }

    @Override
    public Double getDiscountQtyStep() {
        return discountQtyStep;
    }

    @Override
    public CouponAction setDiscountQtyStep(Double discountQtyStep) {
        this.discountQtyStep = discountQtyStep;
        return this;
    }

    @Override
    public Integer getMaximumQtyApplyTo() {
        return maximumQtyApplyTo;
    }

    @Override
    public CouponAction setMaximumQtyApplyTo(Integer maximumQtyApplyTo) {
        this.maximumQtyApplyTo = maximumQtyApplyTo;
        return this;
    }

    @Override
    public Boolean getFreeShipping() {
        return freeShipping;
    }

    @Override
    public CouponAction setFreeShipping(Boolean freeShipping) {
        this.freeShipping = freeShipping;
        return this;
    }

    @Override
    public CouponFilterNode getFilter() {
        return filter;
    }

    @Override
    public CouponAction setFilter(CouponFilterNode filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public Id getPriceTypeId() {
        return priceTypeId;
    }

    @Override
    public CouponAction setPriceTypeId(Id priceTypeId) {
        this.priceTypeId = priceTypeId;
        return this;
    }

    @Override
    public CouponDiscountOrder getDiscountOrder() {
        if (couponDiscountOrder == null)
            couponDiscountOrder = CouponDiscountOrder.DSC;
        return couponDiscountOrder;
    }

    @Override
    public CouponAction setDiscountOrder(CouponDiscountOrder couponDiscountOrder) {
        this.couponDiscountOrder = couponDiscountOrder;
        return this;
    }

    @Override
    public Id getId() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        map = normalize(map);
        this.type = enum_(CouponActionType.class, map.get(CouponAction.Col.TYPE));
        this.couponDiscountOrder = enum_(CouponDiscountOrder.class, map.get(Col.DISCOUNT_ORDER));
        this.discountAmount = double_(map.get(Col.DISCOUNT_AMOUNT));

        if (list_(map.get(Col.DISCOUNT_AMOUNTS)) != null && list_(map.get(Col.DISCOUNT_AMOUNTS)).size() > 0) {
            this.discountAmounts = new ArrayList<>();
            for (Object s : list_(map.get(Col.DISCOUNT_AMOUNTS))) {
                if (s instanceof Double)
                    discountAmounts.add((Double) s);
                else
                    discountAmounts.add(Double.valueOf(s.toString()));
            }
        }

        this.discountQtyStep = double_(map.get(Col.DISCOUNT_QTY_STEP));
        this.maximumQtyApplyTo = int_(map.get(Col.MAX_QTY_APPLY_TO));
        this.freeShipping = bool_(map.get(Col.FREE_SHIPPING));
        this.priceTypeId = id_(map.get(Col.PRICE_TYPE_ID));
        Map<String, Object> ff = map_(map.get(Col.FILTER));
        if (ff != null && ff.size() > 0) {

            this.filter = app.model(CouponFilterNode.class);
            this.filter.fromMap(ff);
        }

        List<Map<String, Object>> rangeDiscountAmounts = list_(map.get(Col.RANGE_DISCOUNT_AMOUNT));
        if (rangeDiscountAmounts != null && rangeDiscountAmounts.size() > 0) {
            this.rangeDiscountAmount = new ArrayList<>();
            for (Map<String, Object> range : rangeDiscountAmounts) {
                CouponRangeDiscountAmount r = app.model(CouponRangeDiscountAmount.class);
                r.fromMap(range);
                this.rangeDiscountAmount.add(r);
            }
        } else {
            this.rangeDiscountAmount = null;
        }

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = Maps.newLinkedHashMap(super.toMap());
        m.put(Col.TYPE, getType().toId());
        m.put(Col.DISCOUNT_ORDER, getDiscountOrder().toId());
        m.put(Col.DISCOUNT_AMOUNT, getDiscountAmount());
        m.put(Col.DISCOUNT_AMOUNTS, getDiscountAmounts());
        m.put(Col.DISCOUNT_QTY_STEP, getDiscountQtyStep());
        m.put(Col.MAX_QTY_APPLY_TO, getMaximumQtyApplyTo());
        m.put(Col.FREE_SHIPPING, getFreeShipping());
        m.put(Col.PRICE_TYPE_ID, getPriceTypeId());

        if (getFilter() != null) {
            m.put(Col.FILTER, getFilter().toMap());
        }

        if (getRangeDiscountAmount() != null && getRangeDiscountAmount().size() > 0) {
            List<Map<String, Object>> rangeList = new ArrayList<>();
            for (CouponRangeDiscountAmount range : getRangeDiscountAmount()) {
                rangeList.add(range.toMap());
            }
            m.put(Col.RANGE_DISCOUNT_AMOUNT, rangeList);
        }
        return m;
    }
}
