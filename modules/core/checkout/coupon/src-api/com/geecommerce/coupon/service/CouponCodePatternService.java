package com.geecommerce.coupon.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.CouponCodePattern;

import java.util.List;

public interface CouponCodePatternService extends Service {
    public List<CouponCodePattern> getCouponCodePatterns();
}
