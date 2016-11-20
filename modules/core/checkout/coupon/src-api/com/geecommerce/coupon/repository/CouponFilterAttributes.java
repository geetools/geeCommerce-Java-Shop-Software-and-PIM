package com.geecommerce.coupon.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.coupon.enums.CouponFilterAttributeType;
import com.geecommerce.coupon.model.CouponFilterAttribute;

public interface CouponFilterAttributes extends Repository {

    public List<CouponFilterAttribute> byType(CouponFilterAttributeType type);

}
