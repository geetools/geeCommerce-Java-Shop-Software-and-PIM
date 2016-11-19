package com.geecommerce.coupon.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.coupon.enums.CouponFilterAttributeType;
import com.geecommerce.coupon.model.CouponFilterAttribute;

@Repository
public class DefaultCouponFilterAttributes extends AbstractRepository implements CouponFilterAttributes {
    @Override
    public List<CouponFilterAttribute> byType(CouponFilterAttributeType type) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(CouponFilterAttribute.Column.TYPE, type.toId());
        return find(CouponFilterAttribute.class, filter);
    }
}
