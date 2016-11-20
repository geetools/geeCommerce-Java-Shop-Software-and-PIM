package com.geecommerce.coupon.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.coupon.model.Coupon;

public interface Coupons extends Repository {

    public List<Coupon> notDeletedCoupons();

}
