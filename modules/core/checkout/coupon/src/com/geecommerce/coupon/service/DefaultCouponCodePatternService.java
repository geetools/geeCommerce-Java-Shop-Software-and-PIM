package com.geecommerce.coupon.service;

import java.util.List;

import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.coupon.model.CouponCodePattern;
import com.geecommerce.coupon.repository.CouponCodePatterns;
import com.google.inject.Inject;

@Service
public class DefaultCouponCodePatternService implements CouponCodePatternService {
    private final CouponCodePatterns couponCodePatterns;

    @Inject
    public DefaultCouponCodePatternService(CouponCodePatterns couponCodePatterns) {
        this.couponCodePatterns = couponCodePatterns;
    }

    @Override
    public List<CouponCodePattern> getCouponCodePatterns() {
        return couponCodePatterns.findAll(CouponCodePattern.class);
    }
}
