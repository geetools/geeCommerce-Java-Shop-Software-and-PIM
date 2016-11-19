package com.geecommerce.coupon.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.coupon.model.Coupon;

@Repository
public class DefaultCoupons extends AbstractRepository implements Coupons {
    @Override
    public List<Coupon> notDeletedCoupons() {

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Coupon.Col.DELETED, false);

        return simpleContextFind(Coupon.class, filter);
    }
}
