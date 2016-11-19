package com.geecommerce.coupon.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponCode;

import java.util.List;

public interface CouponCodes extends Repository {
    public CouponCode byCode(String code);

    public List<CouponCode> thatBelongTo(Coupon coupon);

    public CouponCode thatBelongTo(Coupon coupon, String email);
}
