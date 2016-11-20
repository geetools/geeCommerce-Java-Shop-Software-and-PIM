package com.geecommerce.coupon.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.coupon.model.CouponCodePattern;

public interface CouponCodePatternService extends Service {
    public List<CouponCodePattern> getCouponCodePatterns();
}
