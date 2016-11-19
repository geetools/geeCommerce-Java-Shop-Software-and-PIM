package com.geecommerce.guiwidgets.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

import java.util.Map;

@Model("discount_promotion_subscriptions")
public class DefaultDiscountPromotionSubscription extends AbstractModel implements DiscountPromotionSubscription {

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.DISCOUNT_PROMOTION_ID)
    private Id discountPromotionId = null;

    @Column(Col.GIFT_ID)
    private Id giftId = null;

    @Column(Col.EMAIL)
    private String email = null;

    @Column(Col.COUPON_CODE)
    private String couponCode = null;

    @Column(Col.FORM)
    private Map<String, Object> form = null;

    @Override
    public DiscountPromotionSubscription setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public Id getDiscountPromotionId() {
	return discountPromotionId;
    }

    @Override
    public DiscountPromotionSubscription setDiscountPromotionId(Id discountPromotionId) {
	this.discountPromotionId = discountPromotionId;
	return this;
    }

    @Override
    public String getEmail() {
	return email;
    }

    @Override
    public DiscountPromotionSubscription setEmail(String email) {
	this.email = email;
	return this;
    }

    @Override
    public String getCouponCode() {
	return couponCode;
    }

    @Override
    public DiscountPromotionSubscription setCouponCode(String couponCode) {
	this.couponCode = couponCode;
	return this;
    }

    @Override
    public Map<String, Object> getForm() {
	return form;
    }

    @Override
    public DiscountPromotionSubscription setForm(Map<String, Object> form) {
	this.form = form;
	return this;
    }

    @Override
    public Id getGiftId() {
	return giftId;
    }

    @Override
    public DiscountPromotionSubscription setGiftId(Id giftId) {
	this.giftId = giftId;
	return this;
    }

    @Override
    public Id getId() {
	return id;
    }
}
