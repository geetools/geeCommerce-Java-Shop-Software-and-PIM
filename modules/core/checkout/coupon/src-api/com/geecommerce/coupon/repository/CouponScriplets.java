package com.geecommerce.coupon.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.coupon.model.CouponScriptlet;

public interface CouponScriplets extends Repository {
    public CouponScriptlet byCode(String code);

}
