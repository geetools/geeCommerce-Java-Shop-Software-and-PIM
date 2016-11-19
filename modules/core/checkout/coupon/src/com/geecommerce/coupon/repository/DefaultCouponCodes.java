package com.geecommerce.coupon.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponCode;

@Repository
public class DefaultCouponCodes extends AbstractRepository implements CouponCodes {
    @Override
    public CouponCode byCode(String code) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(CouponCode.Column.CODE, code);
        return findOne(CouponCode.class, filter);
    }

    @Override
    public List<CouponCode> thatBelongTo(Coupon coupon) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(CouponCode.Column.COUPON_ID, coupon.getId());
        List<CouponCode> couponCodes = find(CouponCode.class, filter, QueryOptions.builder().noLimit().build());
        return couponCodes;
    }

    @Override
    public CouponCode thatBelongTo(Coupon coupon, String email) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(CouponCode.Column.COUPON_ID, coupon.getId());
        filter.put(CouponCode.Column.EMAIL, email);
        return findOne(CouponCode.class, filter);
    }
}
