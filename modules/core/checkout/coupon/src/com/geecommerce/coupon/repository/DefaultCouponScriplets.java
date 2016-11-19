package com.geecommerce.coupon.repository;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.coupon.model.CouponScriptlet;

@Repository
public class DefaultCouponScriplets extends AbstractRepository implements CouponScriplets {
    @Override
    public CouponScriptlet byCode(String code) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(CouponScriptlet.Column.CODE, code);
        return findOne(CouponScriptlet.class, filter);
    }
}
