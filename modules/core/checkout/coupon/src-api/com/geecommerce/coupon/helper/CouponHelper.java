package com.geecommerce.coupon.helper;

import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponCodePattern;

import java.util.List;

public interface CouponHelper extends Helper {

    public List<String> generateCodes(String prefix, String postfix, CouponCodePattern pattern, Integer length, Integer quantity);

    public void fixCouponFilters(Coupon coupon);

    public boolean hasPriceTypes(Id productId, List<Id> priceTypes);

}
