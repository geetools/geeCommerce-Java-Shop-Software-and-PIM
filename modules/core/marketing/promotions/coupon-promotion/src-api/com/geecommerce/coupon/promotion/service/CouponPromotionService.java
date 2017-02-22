package com.geecommerce.coupon.promotion.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.promotion.model.CouponPromotion;
import com.geecommerce.coupon.promotion.model.ProductPromotionPriceIndex;

import java.util.List;

public interface CouponPromotionService extends Service {

	List<CouponPromotion> getPromotions();

	List<CouponPromotion> getPromotions(List<Id> ids);

	CouponPromotion getPromotion(Id id);

	ProductPromotionPriceIndex createPromotionPriceIndex(ProductPromotionPriceIndex priceIndex);

	CouponPromotion addPromotion(CouponPromotion promotion);

	void updatePromotion(CouponPromotion promotion);

	List<Coupon> getCoupons();
}
