package com.geecommerce.coupon.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.CartAttributeCollection;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponCode;

public interface CouponService extends Service {
    public Coupon getCoupon(Id couponId);

    public CouponCode getCouponCode(String code);

    public CouponCode getCouponCode(Id id);

    public Boolean isCouponApplicableToCart(CouponCode couponCode, CartAttributeCollection cartAttributeCollection, boolean checkConditions);

    public boolean couponCouldBeUsedCustomerWithGroups(Coupon coupon);

    public Map<String, AttributeValue> getProductAttributes(Product product);

    public Map<String, AttributeValue> getCartAttributes(Model cart);

    public Map<String, AttributeValue> getCartItemAttributes(Model cartItem);

    public Map<String, AttributeValue> getOrderAttributes(Model order);

    public Map<String, AttributeValue> getOrderItemAttributes(Model orderItem);

    public CouponCode maintainCouponCodesList(CouponCode cartCoupon, CartAttributeCollection cartAttributeCollection, boolean useAutoCoupon);

    public void applyDiscount(CalculationContext calculationContext, CouponCode couponCode, CartAttributeCollection cartAttributeCollection);

    public List<CouponCode> getAutoCoupons();

    public CouponCode getAutoCoupon(CouponCode cartCoupon, CartAttributeCollection cartAttributeCollection);

    public List<CouponCode> getAutoCoupons(CartAttributeCollection cartAttributeCollection);

    public void useCoupon(CouponCode couponCode, Id orderId, Id customerId);

    public void useCoupon(CouponCode couponCode, Id orderId, String email);

    public CouponCode generateCode(Coupon coupon, String email, Integer duration);

}
