package com.geecommerce.coupon.service;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.CouponCodePattern;
import com.geecommerce.coupon.repository.CouponCodePatterns;


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
