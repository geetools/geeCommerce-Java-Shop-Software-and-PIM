package com.geecommerce.coupon.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.coupon.enums.CouponFilterAttributeType;
import com.geecommerce.coupon.model.CouponFilterAttribute;

import java.util.List;

public interface CouponFilterAttributes extends Repository {

    public List<CouponFilterAttribute> byType(CouponFilterAttributeType type);

}
