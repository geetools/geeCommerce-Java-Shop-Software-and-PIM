package com.geecommerce.coupon.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.Coupon;

import java.util.List;

public interface FilterService extends Service {

    public boolean fitCondition(CartAttributeCollection cartAttributeCollection, Coupon coupon);

    public List<Id> passFilter(CartAttributeCollection cartAttributeCollection, Coupon coupon);

}
