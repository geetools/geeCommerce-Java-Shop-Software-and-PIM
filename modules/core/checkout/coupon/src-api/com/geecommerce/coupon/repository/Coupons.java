package com.geecommerce.coupon.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.coupon.model.Coupon;
import java.util.List;

public interface Coupons extends Repository {

    public List<Coupon> notDeletedCoupons();

}
